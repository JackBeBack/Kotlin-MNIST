class Dataset {
}

data class DatasetInput(val magicNumber: Int = 0, val numberOfImages: Int = 0, val rows: Int = 0, val cols: Int = 0, val images: List<ByteImage> = emptyList())