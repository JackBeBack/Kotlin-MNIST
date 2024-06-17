import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.random.Random

fun readBytesFromFile(filePath: String): ByteArray {
    return try {
        val file = File(filePath)
        file.readBytes()
    } catch (e: IOException) {
        e.printStackTrace()
        throw IOException("File $filePath does not exist")
    }
}

fun readImages(path: String): DatasetInput{
    //TRAINING SET IMAGE FILE (train-images-idx3-ubyte):
    //[offset] [type]          [value]          [description]
    //0000     32 bit integer  0x00000803(2051) magic number
    //0004     32 bit integer  60000            number of images
    //0008     32 bit integer  28               number of rows
    //0012     32 bit integer  28               number of columns
    //0016     unsigned byte   ??               pixel
    //0017     unsigned byte   ??               pixel
    //........
    //xxxx     unsigned byte   ??               pixel
    //Pixels are organized row-wise. Pixel values are 0 to 255. 0 means background (white), 255 means foreground (black).
    val bytes = readBytesFromFile(path)

    var index = 0
    val magicNumber = bytes.copyOfRange(index, index+4).toInt()
    index += 4
    val numberOfImages = bytes.copyOfRange(index, index+4).toInt()
    index += 4
    val rows = bytes.copyOfRange(index, index+4).toInt()
    index += 4
    val columns = bytes.copyOfRange(index, index+4).toInt()
    println( magicNumber)
    println(numberOfImages)
    println(rows)
    println(columns)
    val numBytesPerImage = rows * columns
    val images = mutableListOf<ByteImage>()
    repeat(numberOfImages){
        images.add(ByteImage(rows, columns, bytes.copyOfRange(index, index+numBytesPerImage)))
        index += numBytesPerImage
    }
    println(images.size)
    return DatasetInput(magicNumber, numberOfImages, rows, columns, images)
}


@Composable
@Preview
fun App(input: DatasetInput) {

    var index by remember { mutableStateOf(0) }
    Box(Modifier.fillMaxSize()){
        if (input.images.size>index){
            MaterialTheme {
                GrayscaleImage(Modifier.align(Alignment.CenterStart), input.images[index])
            }
        }
        Button(modifier = Modifier.align(Alignment.Center), onClick = {index = Random.nextInt(input.numberOfImages)}){
            Text("Random")
        }
    }


}

@Composable
fun GrayscaleImage(modifier: Modifier, img: ByteImage){
    Column(modifier) {
        for (y in 0 until img.height){
            Row {
                for (x in 0 until img.width){
                    Pixel(img.get(x, y))
                }
            }
        }
    }

}

@Composable
fun Pixel(color: Byte){
    val grayscale = color.toInt()
    Box(Modifier.size(5.dp).background(Color(grayscale, grayscale, grayscale, 255)))
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        var input by remember { mutableStateOf(DatasetInput()) }
        LaunchedEffect(Unit){
            val filePath = "src/main/resources/MNIST Dataset/t10k-images-idx3-ubyte/t10k-images-idx3-ubyte"
            input = readImages(filePath)
        }
        App(input)
    }
}
