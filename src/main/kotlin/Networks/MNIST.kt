package Networks

import LinearAlgebra.Matrix
import kotlinx.coroutines.coroutineScope
import kotlin.math.log
import kotlin.math.pow
import kotlin.random.Random

// Activation functions and their derivatives
fun relu(x: Float): Float = if (x > 0F) x else 0F
fun reluDerivative(x: Float): Float = if (x > 0) 1.0F else 0.0F


class MNIST(val inputLayer: Int, val hiddenLayer: Int, val outputLayer: Int) {
    var weights: Array<Matrix> = arrayOf(
        Matrix(rows = inputLayer, cols = hiddenLayer) { i, j -> 0F },
        Matrix(rows = hiddenLayer, cols = outputLayer) { i, j -> 0F }
    )
    var biases: Array<Matrix> = arrayOf(
        Matrix(rows = 1, cols = hiddenLayer) { i, j -> 0F },
        Matrix(rows = 1, cols = outputLayer) { i, j -> 0F }
    )

    fun forward(input: Matrix): Pair<Array<Matrix>, Array<Matrix>> {
        var current = input
        val zValues = Array(weights.size) { Matrix.zero(input.rows, weights[it].cols) } //sums
        val aValues = Array(weights.size + 1) {
            Matrix.zero(
                input.rows,
                if (it == weights.size) outputLayer else weights[it].cols
            )
        } //activations

        aValues[0] = input
        weights.forEachIndexed { index, weight ->
            val z = current * weight + biases[index]
            zValues[index] = z
            current = z.forEach { relu(it) }
            aValues[index + 1] = current
        }
        current = current.softMax()
        aValues[aValues.size - 1] = current
        return Pair(zValues, aValues)
    }

    fun epoch(train: DatasetInput, progress: (Float) -> Unit) {
        train.images.forEachIndexed { index, value ->
            iterate(value.toMatrix(normalize = true), train.lable[index].toMatrix())
            progress(index / train.images.size.toFloat())
        }
    }

    suspend fun calcError(input: DatasetInput): Double {
        var totalError = 0.0
        coroutineScope {
            input.images.forEachIndexed { index, value ->
                val (z, a) = forward(value.toMatrix(normalize = true))
                val prediction = a.last()
                val actual = input.lable[index].toMatrix()

                totalError += (actual - prediction).absolute()
            }
        }
        return totalError / input.numberOfImages
    }

    fun iterate(image: Matrix, label: Matrix, learningRate: Float = 0.0005F): Matrix {
        val (zValues, aValues) = forward(image)
        val (dWeights, dBiases) = backward(zValues, aValues, label)
        dWeights.forEachIndexed { index, value ->
            weights[index] += value * learningRate
        }
        dBiases.forEachIndexed { index, value ->
            biases[index] += value * learningRate
        }
        return aValues.last()
    }

    fun train(input: DatasetInput, epochs: Int, progress: (Float) -> Unit, epochFinish: (Int) -> Unit) {
        repeat(epochs) {
            epoch(input, progress = progress)
            epochFinish(it + 1)
        }
    }

    fun backward(zValues: Array<Matrix>, aValues: Array<Matrix>, labels: Matrix): Pair<Array<Matrix>, Array<Matrix>> {
        val dWeights = Array(weights.size) { Matrix.zero(weights[it].rows, weights[it].cols) }
        val dBiases = Array(biases.size) { Matrix.zero(biases[it].rows, biases[it].cols) }

        // Calculate the error at the output layer
        val dZLast = aValues.last() - labels
        dWeights[dWeights.size - 1] = aValues[aValues.size - 2].transpose() * dZLast
        dBiases[dBiases.size - 1] = dZLast

        // Propagate the error backward
        var dA = dZLast * weights.last().transpose()
        for (i in weights.size - 2 downTo 0) {
            val dZ = dA.forEachIndexed { row, col, value -> value * reluDerivative(zValues[i].get(row, col)) }
            dWeights[i] = aValues[i].transpose() * dZ
            dBiases[i] = dZ
            if (i > 0) {
                dA = dZ * weights[i].transpose()
            }
        }
        return Pair(dWeights, dBiases)
    }

}