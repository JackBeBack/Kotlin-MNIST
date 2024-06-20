import LinearAlgebra.Matrix
import Networks.ByteImage
import Networks.DatasetInput
import Networks.MNIST
import Networks.toMatrix
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.io.File
import java.io.IOException
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

fun loadDataset(path: String, test: Boolean = true): DatasetInput {
    val imagePath = if (test) "$path/images.idx3-ubyte" else "$path/images.idx3-ubyte"
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


    val labelPath = if (test) "$path/labels.idx1-ubyte" else "$path/labels.idx1-ubyte"
    //[offset] [type]          [value]          [description]
    //0000     32 bit integer  0x00000801(2049) magic number (MSB first)
    //0004     32 bit integer  10000            number of items
    //0008     unsigned byte   ??               label
    //0009     unsigned byte   ??               label
    //........
    //xxxx     unsigned byte   ??               label
    //The labels values are 0 to 9.

    val imageBytes = readBytesFromFile(imagePath)

    var index = 0
    val magicNumber = imageBytes.copyOfRange(index, index + 4).toInt()
    index += 4
    val numberOfImages = imageBytes.copyOfRange(index, index + 4).toInt()
    index += 4
    val rows = imageBytes.copyOfRange(index, index + 4).toInt()
    index += 4
    val columns = imageBytes.copyOfRange(index, index + 4).toInt()

    val numBytesPerImage = rows * columns
    val images = mutableListOf<ByteImage>()
    repeat(numberOfImages) {
        images.add(ByteImage(rows, columns, imageBytes.copyOfRange(index, index + numBytesPerImage)))
        index += numBytesPerImage
    }
    val labels = mutableListOf<Int>()
    val labelBytes = readBytesFromFile(labelPath)
    index = 4
    val numberOfLabels = labelBytes.copyOfRange(index, index + 4).toInt()
    index += 4
    repeat(numberOfLabels) {
        labels.add(labelBytes[index].toInt())
        index += 1
    }
    return DatasetInput(magicNumber, numberOfImages, rows, columns, images, labels)
}


@Composable
@Preview
fun App(train: DatasetInput, test: DatasetInput) {
    val mnist = remember { MNIST(784, 150, 10) }
    var index by remember { mutableStateOf(0) }
    var numEpochs by remember { mutableStateOf(0) }
    var isTraining by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0F) }
    var prediction by remember { mutableStateOf(Matrix.zero(10, 1)) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(isTraining) {
        if (isTraining) {
            scope.launch(Dispatchers.IO){
                mnist.train(train,
                    5,
                    progress = { it ->
                        progress = it
                    },
                    epochFinish = { epoch ->
                        numEpochs = epoch
                    }
                )
            }
        }else{
            println("Cancel")
        }
    }
    Box(Modifier.fillMaxSize().padding(16.dp)) {
        if (train.images.size > index) {
            MaterialTheme {
                GrayscaleImage(Modifier.align(Alignment.CenterStart), train.images[index])
                Button(onClick = {
                    prediction =
                        mnist.iterate(train.images[index].toMatrix(normalize = true), train.lable[index].toMatrix())
                }, modifier = Modifier.align(Alignment.Center)) {
                    Text("Forward pass")
                }
                Column(modifier = Modifier.align(Alignment.CenterEnd).width(150.dp)) {
                    prediction.data.forEachIndexed { index, value ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${value * 100}%")
                            Text("${index}")
                        }
                    }
                }
            }
        }
        Button(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = { index = Random.nextInt(train.numberOfImages) }) {
            Text("Random")
        }
        Button(modifier = Modifier.align(Alignment.BottomEnd), onClick = {
            scope.launch(Dispatchers.IO) {
                error = "${mnist.calcError(test)}"
            }
        }) {
            Text(if (error.isEmpty()) "Calc Error" else error)
        }
        Button(modifier = Modifier.align(Alignment.BottomStart), onClick = {
            isTraining = !isTraining
        }) {
            Text(if (isTraining) {
                String.format("%.2f%% in Epoch %d", progress * 100, numEpochs)
            } else {
                "Start Training"
            })

        }
    }


}

@Composable
fun GrayscaleImage(modifier: Modifier, img: ByteImage) {
    Column(modifier) {
        for (y in 0 until img.height) {
            Row {
                for (x in 0 until img.width) {
                    Pixel(img.get(x, y))
                }
            }
        }
    }

}

@Composable
fun Pixel(color: Byte) {
    val grayscale = color.toInt()
    Box(Modifier.size(5.dp).background(Color(grayscale, grayscale, grayscale, 255)))
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        var test by remember { mutableStateOf(DatasetInput()) }
        var train by remember { mutableStateOf(DatasetInput()) }
        LaunchedEffect(Unit) {
            val testPath = "src/main/resources/MNIST Dataset/test"
            val trainPath = "src/main/resources/MNIST Dataset/train"
            test = loadDataset(testPath)
            train = loadDataset(testPath)
        }
        App(train, test)
    }
}
