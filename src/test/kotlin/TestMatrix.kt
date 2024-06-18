import LinearAlgebra.Matrix
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

fun assertArrayEqualsIgnoreSign(expected: Array<Float>, actual: Array<Float>) {
    if (expected.size != actual.size) {
        fail<String>("Arrays are of different sizes, expected: ${expected.size}, actual: ${actual.size}")
    }
    for (i in expected.indices) {
        if (expected[i] != actual[i] && !(expected[i] == 0.0f && actual[i] == -0.0f) && !(expected[i] == -0.0f && actual[i] == 0.0f)) {
            fail<String>("Array contents differ at index [$i], expected: <${expected[i]}> but was: <${actual[i]}>")
        }
    }
}

class MatrixInitTest {
    @Test
    fun testMatrixInitialization() {
        val rows = 2
        val cols = 3
        val matrix = Matrix(rows, cols)
        assertEquals(rows, matrix.rows)
        assertEquals(cols, matrix.cols)
        assertTrue(matrix.data.all { it == 0f })
    }

    @Test
    fun testMatrixInitializationWithData() {
        val rows = 2
        val cols = 3
        val data = arrayOf(1f, 2f, 3f, 4f, 5f, 6f)
        val matrix = Matrix(rows, cols, data)
        assertEquals(rows, matrix.rows)
        assertEquals(cols, matrix.cols)
        assertArrayEquals(data, matrix.data)
    }

    @Test
    fun testMatrixInitializationWithFunction() {
        val rows = 2
        val cols = 3
        val matrix = Matrix(rows, cols) { row, col -> (row * cols + col).toFloat() }
        val expectedData = arrayOf(0f, 1f, 2f, 3f, 4f, 5f)
        assertEquals(rows, matrix.rows)
        assertEquals(cols, matrix.cols)
        assertArrayEquals(expectedData, matrix.data)
    }

    @Test
    fun testMatrixInitializationWithIncorrectDataSize() {
        val rows = 2
        val cols = 3
        val data = arrayOf(1f, 2f, 3f, 4f) // Incorrect data size
        try {
            Matrix(rows, cols, data)
            fail("Expected IllegalArgumentException due to incorrect data size")
        } catch (e: Exception) {
            assertTrue(e is IllegalArgumentException)
        }
    }

    @Test
    fun testMatrixInitializationWithNegativeDimensions() {
        try {
            Matrix(-1, 3)
            fail("Expected IllegalArgumentException due to negative dimensions")
        } catch (e: Exception) {
            assertTrue(e is NegativeArraySizeException)
        }
    }
}

class MatrixAddTest {
    @Test
    fun testMatrixAddition() {
        val matrix1 = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val matrix2 = Matrix(2, 3) { row, col -> (row * 3 + col + 1).toFloat() }

        val result = matrix1 + matrix2
        val expectedData = arrayOf(1f, 3f, 5f, 7f, 9f, 11f)

        assertEquals(matrix1.rows, result.rows)
        assertEquals(matrix1.cols, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testMatrixAdditionWithZeros() {
        val matrix1 = Matrix(2, 3) { _, _ -> 0f }
        val matrix2 = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }

        val result = matrix1 + matrix2
        val expectedData = arrayOf(0f, 1f, 2f, 3f, 4f, 5f)

        assertEquals(matrix1.rows, result.rows)
        assertEquals(matrix1.cols, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testMatrixAdditionWithNegativeNumbers() {
        val matrix1 = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val matrix2 = Matrix(2, 3) { row, col -> -(row * 3 + col).toFloat() }

        val result = matrix1 + matrix2
        val expectedData = arrayOf(0f, 0f, 0f, 0f, 0f, 0f)

        assertEquals(matrix1.rows, result.rows)
        assertEquals(matrix1.cols, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testMatrixAdditionWithDifferentDimensions() {
        val matrix1 = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val matrix2 = Matrix(3, 2) { row, col -> (row * 2 + col).toFloat() }

        try {
            matrix1 + matrix2
            fail("Expected IllegalArgumentException due to different dimensions")
        } catch (e: IllegalArgumentException) {
            assertEquals("Failed requirement.", e.message)
        }
    }

    @Test
    fun testMatrixAdditionResultIsNewInstance() {
        val matrix1 = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val matrix2 = Matrix(2, 3) { row, col -> (row * 3 + col + 1).toFloat() }

        val result = matrix1 + matrix2

        // Ensure the result is a new instance and not modifying the original matrices
        assertNotSame(matrix1, result)
        assertNotSame(matrix2, result)
    }
}

class MatrixSubTest {
    @Test
    fun testMatrixSubtraction() {
        val matrix1 = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val matrix2 = Matrix(2, 3) { row, col -> (row * 3 + col + 1).toFloat() }

        val result = matrix1 - matrix2
        val expectedData = arrayOf(-1f, -1f, -1f, -1f, -1f, -1f)

        assertEquals(matrix1.rows, result.rows)
        assertEquals(matrix1.cols, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testMatrixSubtractionWithZeros() {
        val matrix1 = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val matrix2 = Matrix(2, 3) { _, _ -> 0f }

        val result = matrix1 - matrix2
        val expectedData = arrayOf(0f, 1f, 2f, 3f, 4f, 5f)

        assertEquals(matrix1.rows, result.rows)
        assertEquals(matrix1.cols, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testMatrixSubtractionResultingInNegative() {
        val matrix1 = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val matrix2 = Matrix(2, 3) { row, col -> (row * 3 + col + 2).toFloat() }

        val result = matrix1 - matrix2
        val expectedData = arrayOf(-2f, -2f, -2f, -2f, -2f, -2f)

        assertEquals(matrix1.rows, result.rows)
        assertEquals(matrix1.cols, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testMatrixSubtractionWithDifferentDimensions() {
        val matrix1 = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val matrix2 = Matrix(3, 2) { row, col -> (row * 2 + col).toFloat() }

        try {
            matrix1 - matrix2
            fail("Expected IllegalArgumentException due to different dimensions")
        } catch (e: IllegalArgumentException) {
            assertEquals("Failed requirement.", e.message)
        }
    }

    @Test
    fun testMatrixSubtractionResultIsNewInstance() {
        val matrix1 = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val matrix2 = Matrix(2, 3) { row, col -> (row * 3 + col + 1).toFloat() }

        val result = matrix1 - matrix2

        // Ensure the result is a new instance and not modifying the original matrices
        assertNotSame(matrix1, result)
        assertNotSame(matrix2, result)
    }
}

class MatrixTimesTest {
    @Test
    fun testMatrixMultiplicationByScalar() {
        val matrix = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val scalar = 2.5f

        val result = matrix * scalar
        val expectedData = arrayOf(0f, 2.5f, 5f, 7.5f, 10f, 12.5f)

        assertEquals(matrix.rows, result.rows)
        assertEquals(matrix.cols, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testMatrixMultiplicationByZero() {
        val matrix = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val scalar = 0f

        val result = matrix * scalar
        val expectedData = arrayOf(0f, 0f, 0f, 0f, 0f, 0f)

        assertEquals(matrix.rows, result.rows)
        assertEquals(matrix.cols, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testMatrixMultiplicationByNegativeScalar() {
        val matrix = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val scalar = -1.5f

        val result = matrix * scalar
        val expectedData = arrayOf(0f, -1.5f, -3f, -4.5f, -6f, -7.5f)

        assertEquals(matrix.rows, result.rows)
        assertEquals(matrix.cols, result.cols)
        assertArrayEqualsIgnoreSign(expectedData, result.data)
    }

    @Test
    fun testMatrixMultiplicationByOne() {
        val matrix = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val scalar = 1f

        val result = matrix * scalar
        val expectedData = arrayOf(0f, 1f, 2f, 3f, 4f, 5f)

        assertEquals(matrix.rows, result.rows)
        assertEquals(matrix.cols, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testMatrixMultiplicationResultIsNewInstance() {
        val matrix = Matrix(2, 3) { row, col -> (row * 3 + col).toFloat() }
        val scalar = 2f

        val result = matrix * scalar

        // Ensure the result is a new instance and not modifying the original matrix
        assertNotSame(matrix, result)
    }
}

class MatrixMultiplicationTest {
    @Test
    fun testMatrixMultiplication() {
        val matrix1 = Matrix(2, 3) { row, col -> (row * 3 + col + 1).toFloat() }
        val matrix2 = Matrix(3, 2) { row, col -> (row * 2 + col + 1).toFloat() }

        val result = matrix1 * matrix2
        val expectedData = arrayOf(22f, 28f, 49f, 64f)

        assertEquals(2, result.rows)
        assertEquals(2, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testMatrixMultiplicationWithIdentityMatrix() {
        val matrix = Matrix(3, 3) { row, col -> (row * 3 + col + 1).toFloat() }
        val identityMatrix = Matrix(3, 3) { row, col -> if (row == col) 1f else 0f }

        val result = matrix * identityMatrix
        val expectedData = arrayOf(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f)

        assertEquals(3, result.rows)
        assertEquals(3, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testMatrixMultiplicationWithIncompatibleDimensions() {
        val matrix1 = Matrix(2, 3) { row, col -> (row * 3 + col + 1).toFloat() }
        val matrix2 = Matrix(2, 2) { row, col -> (row * 2 + col + 1).toFloat() }

        try {
            matrix1 * matrix2
        }catch (e: Exception){
            assertEquals(e.message, "Matrix dimensions do not match for multiplication")
        }
    }

    @Test
    fun testMatrixMultiplicationResultIsNewInstance() {
        val matrix1 = Matrix(2, 3) { row, col -> (row * 3 + col + 1).toFloat() }
        val matrix2 = Matrix(3, 2) { row, col -> (row * 2 + col + 1).toFloat() }

        val result = matrix1 * matrix2

        // Ensure the result is a new instance and not modifying the original matrices
        assertNotSame(matrix1, result)
        assertNotSame(matrix2, result)
    }
}

class MatrixTransposeTest{
    @Test
    fun testMatrixTranspose() {
        val matrix = Matrix(2, 3) { row, col -> (row * 3 + col + 1).toFloat() }

        val result = matrix.transpose()
        val expectedData = arrayOf(1f, 4f, 2f, 5f, 3f, 6f)

        assertEquals(3, result.rows)
        assertEquals(2, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testSquareMatrixTranspose() {
        val matrix = Matrix(2, 2) { row, col -> (row * 2 + col + 1).toFloat() }

        val result = matrix.transpose()
        val expectedData = arrayOf(1f, 3f, 2f, 4f)

        assertEquals(2, result.rows)
        assertEquals(2, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testSingleElementMatrixTranspose() {
        val matrix = Matrix(1, 1) { _, _ -> 42f }

        val result = matrix.transpose()
        val expectedData = arrayOf(42f)

        assertEquals(1, result.rows)
        assertEquals(1, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testMatrixTransposeResultIsNewInstance() {
        val matrix = Matrix(2, 3) { row, col -> (row * 3 + col + 1).toFloat() }

        val result = matrix.transpose()

        // Ensure the result is a new instance and not modifying the original matrix
        assertNotSame(matrix, result)
    }
}

class MatrixDeterminantTest {

    @Test
    fun testDeterminantOf2x2Matrix() {
        val matrix = Matrix(2, 2) { row, col -> (row * 2 + col + 1).toFloat() }
        val result = matrix.determinant()
        val expected = -2f
        assertEquals(expected, result)
    }

    @Test
    fun testDeterminantOf3x3Matrix() {
        val matrix = Matrix(3, 3) { row, col -> (row * 3 + col + 1).toFloat() }
        val result = matrix.determinant()
        val expected = 0f
        assertEquals(expected, result)
    }

    @Test
    fun testDeterminantOfIdentityMatrix() {
        val matrix = Matrix(3, 3) { row, col -> if (row == col) 1f else 0f }
        val result = matrix.determinant()
        val expected = 1f
        assertEquals(expected, result)
    }

    @Test
    fun testDeterminantOfNonSquareMatrix() {
        val matrix = Matrix(2, 3) { row, col -> (row * 3 + col + 1).toFloat() }
        try {
            matrix.determinant()
        }catch (e: Exception){
            assertEquals(e.message, "Matrix must be square to calculate determinant")
        }
    }

    @Test
    fun testDeterminantOfLargerMatrix() {
        val matrix = Matrix(4, 4) { row, col -> (row * 4 + col + 1).toFloat() }
        val result = matrix.determinant()
        val expected = 0f
        assertEquals(expected, result)
    }
}

class MatrixInverseTest {

    @Test
    fun testInverseOf2x2Matrix() {
        val matrix = Matrix(2, 2) { row, col -> (row * 2 + col + 1).toFloat() }
        val result = matrix.inverse()
        val expectedData = arrayOf(-2.0f, 1.0f, 1.5f, -0.5f)
        assertEquals(2, result.rows)
        assertEquals(2, result.cols)
        assertArrayEquals(expectedData, result.data)
    }

    @Test
    fun testInverseOfIdentityMatrix() {
        val matrix = Matrix.getIdentityMatrix(3)
        val result = matrix.inverse()
        val expectedData = arrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        assertEquals(3, result.rows)
        assertEquals(3, result.cols)
        assertArrayEqualsIgnoreSign(expectedData, result.data)
    }

    @Test
    fun testInverseOfNonSquareMatrix() {
        val matrix = Matrix(2, 3) { row, col -> (row * 3 + col + 1).toFloat() }
        try {
            matrix.inverse()
        } catch (e: Exception){
            assertEquals("Matrix must be square to calculate inverse", e.message)
        }

    }

    @Test
    fun testInverseOfSingularMatrix() {
        val matrix = Matrix(2, 2) { _, _ -> 1f }
        try {
            matrix.inverse()
        }catch (e: Exception){
            assertEquals("Matrix is singular and cannot be inverted", e.message)
        }
    }

    @Test
    fun testInverseOfLargerMatrix() {
        val matrix = Matrix(3, 3, data = arrayOf(
            1F, 2f, -1f,
            2F, 1F, 2F,
            -1F, 2F, 1F
        ))
        val inverse = matrix.inverse()

        assertEquals(3, inverse.rows)
        assertEquals(3, inverse.cols)

        val result = matrix * inverse
        //test if all elements are the same
        assertArrayEqualsIgnoreSign(Matrix.getIdentityMatrix(3).data, result.data)
    }
}