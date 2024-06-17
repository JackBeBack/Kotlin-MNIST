import java.nio.ByteBuffer
import java.nio.ByteOrder

class Utility {

}

fun ByteArray.toInt(order: ByteOrder = ByteOrder.BIG_ENDIAN): Int {
    if (this.size != 4) {
        throw IllegalArgumentException("Byte array must be exactly 4 bytes long to convert to an Int.")
    }
    val byteBuffer = ByteBuffer.wrap(this)
    byteBuffer.order(order)
    return byteBuffer.int
}