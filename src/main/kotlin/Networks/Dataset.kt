package Networks

import LinearAlgebra.Matrix

class Dataset {
}

data class DatasetInput(val magicNumber: Int = 0, val numberOfImages: Int = 0, val rows: Int = 0, val cols: Int = 0, val images: List<ByteImage> = emptyList(), val lable: List<Int> = emptyList())

fun Int.toMatrix(): Matrix{
    val ret = Array<Float>(10) { i -> if (this == i) 1F else 0F }
    return Matrix(rows = 1, cols = 10, ret)
}