

fun main(){
    val tensor = Tensor(
        shape = intArrayOf(2, 2, 2, 2, 2, 2),
        data = IntArray(64){ it }
    )
    println(tensor)
}