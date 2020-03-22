package longevity.software.vrgamedemo

class TexturedQuad(leftNotRight: Boolean) : AbstractTexturedGameObject() {

    private val leftVertexArray: FloatArray = floatArrayOf(
        -1.0f, 1.0f, 0.0f,      // top left
        -1.0f, -1.0f, 0.0f,     // bottom left
        0.0f, -1.0f, 0.0f,      // bottom right
        0.0f, 1.0f, 0.0f        // top right
    )

    private val rightVertexArray: FloatArray = floatArrayOf(
        0.0f, 1.0f, 0.0f,       // top left
        0.0f, -1.0f, 0.0f,      // bottom left
        1.0f, -1.0f, 0.0f,      // bottom right
        1.0f, 1.0f, 0.0f        // top right
    )

    private val indicesArray: ShortArray = shortArrayOf(
        0, 1, 2, 0, 2, 3
    )

    private val uvArray: FloatArray = floatArrayOf(
        0.0f, 1.0f,     // top left
        0.0f, 0.0f,     // bottom left
        1.0f, 0.0f,     // bottom right
        1.0f, 1.0f      // top right
    )

    /**
     * TexturedQuad init block which sets the parameters in the abstract
     * base class based on the leftNotRight flag
     */
    init {
        if (leftNotRight) {
            super.SetParameters(leftVertexArray, indicesArray, uvArray)
        }
        else {
            super.SetParameters(rightVertexArray, indicesArray, uvArray)
        }
    }
}