package LinearAlgebra

import androidx.compose.ui.unit.IntSize

class Matrix(val rows: Int, val cols: Int, val data: Array<Float> = Array(rows*cols){0f}) {
    //An mxn Matrix has
    // m Rows
    // n Columns
    init {
        require(data.size == (rows*cols))
    }

    fun get(row: Int, col: Int): Float{
        return data[col + row * cols]
    }

    companion object{
        fun getIdentityMatrix(size: Int): Matrix{
            return Matrix(size, size){x, y ->
                if (x == y) 1F else 0F
            }
        }
        fun zero(rows: Int, cols: Int = rows): Matrix{
            return Matrix(rows, cols){x, y ->
                0F
            }
        }
    }

    val size: IntSize
        get() = IntSize(cols, rows)

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                stringBuilder.append(String.format("%6.2f", data[i * cols + j]))
                if (j < cols - 1) stringBuilder.append(" ")
            }
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }

    constructor(rows: Int, cols: Int, init: (row: Int, col: Int) -> Float) : this(rows, cols, Array(rows * cols) { 0f }) {
        for (y in 0 until rows) {
            for (x in 0 until cols) {
                data[x + y * cols] = init(y, x)
            }
        }
    }

    // Element-wise addition
    operator fun plus(other: Matrix): Matrix {
        return elementWiseOperation(other) { a, b -> a + b }
    }

    // Element-wise subtraction
    operator fun minus(other: Matrix): Matrix {
        return elementWiseOperation(other) { a, b -> a - b }
    }

    fun elementWiseOperation(other: Matrix, operation: (Float, Float) -> Float): Matrix {
        require(this.rows == other.rows && this.cols == other.cols)
        val ret = Array(rows * cols) { 0f }
        data.forEachIndexed { index, value ->
            ret[index] = operation(value, other.data[index])
        }
        return Matrix(this.rows, this.cols, ret)
    }

    fun forEach(operation: (Float) -> Float): Matrix {
        val ret = Array(rows * cols) { 0f }
        data.forEachIndexed { index, value ->
            ret[index] = operation(value)
        }
        return Matrix(this.rows, this.cols, ret)
    }

    fun forEachIndexed(operation: (Int, Int, Float) -> Float): Matrix {
        val ret = Array(rows * cols) { 0f }
        for (y in 0 until rows){
            for (x in 0 until cols){
                ret[x + y *cols] = operation(y, x, this.data[x + y *cols])
            }
        }
        return Matrix(this.rows, this.cols, ret)
    }

    operator fun times(other: Number): Matrix{
        val ret = Array(rows * cols) { 0f }
        data.forEachIndexed { index, value ->
            ret[index] = value * other.toFloat()
        }
        return Matrix(this.rows, this.cols, ret)
    }

    // Matrix multiplication
    operator fun times(other: Matrix): Matrix {
        require(this.cols == other.rows) { "Matrix dimensions do not match for multiplication" }
        val resultData = Array(this.rows * other.cols) { 0f }
        for (i in 0 until this.rows) {
            for (j in 0 until other.cols) {
                var sum = 0f
                for (k in 0 until this.cols) {
                    sum += this.data[i * this.cols + k] * other.data[k * other.cols + j]
                }
                resultData[i * other.cols + j] = sum
            }
        }
        return Matrix(this.rows, other.cols, resultData)
    }

    // Transpose function
    fun transpose(): Matrix {
        val resultData = Array(this.cols * this.rows) { 0f }
        for (i in 0 until this.rows) {
            for (j in 0 until this.cols) {
                resultData[j * this.rows + i] = this.data[i * this.cols + j]
            }
        }
        return Matrix(this.cols, this.rows, resultData)
    }

    fun determinant(): Float {
        require(rows == cols) { "Matrix must be square to calculate determinant" }
        if (this.rows == 2 && this.cols == 2) {
            return this.data[0] * this.data[3] - this.data[1] * this.data[2]
        }

        var det = 0f
        for (col in 0 until this.cols) {
            det += this.data[col] * cofactor(this, 0, col)
        }
        return det
    }

    // Calculate the inverse
    fun inverse(): Matrix {
        require(rows == cols) { "Matrix must be square to calculate inverse" }
        val det = determinant()
        require(det != 0f) { "Matrix is singular and cannot be inverted" }
        if (rows == 2) {
            return Matrix(2, 2, arrayOf(
                data[3] / det,
                -data[1] / det,
                -data[2] / det,
                data[0] / det
            ))
        }

        val adjoin = Matrix(rows, cols) { row, col -> cofactor(this, row, col) }
        return adjoin.transpose() * (1 / det)
    }

    private fun subMatrix(matrix: Matrix, row: Int, col: Int): Matrix {
        val subData = Array((matrix.rows - 1) * (matrix.cols - 1)) { 0f }
        var index = 0
        for (i in 0 until matrix.rows) {
            if (i == row) continue
            for (j in 0 until matrix.cols) {
                if (j == col) continue
                subData[index++] = matrix.data[i * matrix.cols + j]
            }
        }
        return Matrix(matrix.rows - 1, matrix.cols - 1, subData)
    }

    fun softMax(): Matrix{
        require(this.rows == 1)
        var total = 0F
        this.data.forEach { total += it }
        return Matrix(this.rows, this.cols, data = this.data.map { it / total }.toTypedArray())
    }

    private fun cofactor(matrix: Matrix, row: Int, col: Int): Float {
        val subMatrix = subMatrix(matrix, row, col)
        val sign = if ((row + col) % 2 == 0) 1 else -1
        return sign * subMatrix.determinant()
    }

}

