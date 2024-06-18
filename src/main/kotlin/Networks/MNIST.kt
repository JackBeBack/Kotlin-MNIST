package Networks

import LinearAlgebra.Matrix
import kotlin.random.Random

// Activation functions and their derivatives
fun relu(x: Float): Float = if (x > 0F) x else 0F
fun reluDerivative(x: Float): Float = if (x > 0) 1.0F else 0.0F


class MNIST(val inputLayer: Int, val hiddenLayer: Int, val outputLayer: Int) {
    var weights: Array<Matrix> = arrayOf(
        Matrix(rows = inputLayer, cols = hiddenLayer){ i, j -> Random.nextFloat() },
        Matrix(rows = hiddenLayer, cols = outputLayer){i, j -> Random.nextFloat()}
    )
    var biases: Array<Matrix> = arrayOf(
        Matrix(rows = 1, cols = hiddenLayer){ i, j -> Random.nextFloat() },
        Matrix(rows = 1, cols = outputLayer){i, j -> Random.nextFloat()}
    )

    fun forward(input: Matrix): Pair<Array<Matrix>, Array<Matrix>> {
        var current = input
        val zValues = Array(weights.size) { Matrix.zero(2) }        //sums
        val aValues = Array(weights.size + 1) { Matrix.zero(2) }    //activations

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


    fun iterate(image: Matrix, label: Matrix){
        val (zValues, aValues) = forward(image)
        val (dWeights, dBiases) = backward(image, zValues, aValues, label)
        dWeights.forEachIndexed { index, value ->
            weights[index] += value
        }
        dBiases.forEachIndexed { index, value ->
            biases[index] += value
        }
    }

    fun backward(input: Matrix, zValues: Array<Matrix>, aValues: Array<Matrix>, labels: Matrix): Pair<Array<Matrix>, Array<Matrix>> {
        val dWeights = Array(weights.size) { Matrix.zero(weights[0].rows, weights[0].cols) }
        val dBiases = Array(biases.size) { Matrix.zero(biases[0].rows, biases[0].cols) }

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