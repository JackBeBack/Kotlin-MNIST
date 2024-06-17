class ByteImage(val width: Int, val height: Int, val byteArray: ByteArray) {
    fun get(x: Int, y: Int) = byteArray[y * width + x]

}