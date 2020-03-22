package longevity.software.vrgamedemo

class TriangleGameObject() : AbstractGameObject() {

    private val vertexCoordsArray: FloatArray = floatArrayOf(    // in counterclockwise order:
        0.0f, 0.622008459f, 0.0f,      // top
        -0.5f, -0.311004243f, 0.0f,    // bottom left
        0.5f, -0.311004243f, 0.0f      // bottom right
    )

    private val indicesArray: ShortArray = shortArrayOf(
        0, 1, 2
    )

    private val colourRGBA = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    /**
     * TriangleGameObject init block which sets the parameters in the abstract base class
     */
    init {
        super.SetParameters(vertexCoordsArray, indicesArray, colourRGBA)
    }
}