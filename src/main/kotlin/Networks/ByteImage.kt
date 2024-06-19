package Networks

import LinearAlgebra.Matrix
import kotlin.collections.map
import kotlin.collections.toTypedArray

class ByteImage(val width: Int, val height: Int, val byteArray: ByteArray) {
    fun get(x: Int, y: Int) = byteArray[y * width + x]

    fun toMatrix(normalize: Boolean = false): Matrix{
        return Matrix(
            rows = 1,
            cols = width * height,
            data = this.byteArray.map { if (normalize) it.toFloat() / 255F else it.toFloat() }.toTypedArray()
        )
    }
}