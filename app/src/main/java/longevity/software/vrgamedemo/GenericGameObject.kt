package longevity.software.vrgamedemo

class GenericGameObject(colour: Int, vertices: FloatArray, indices: ShortArray, normals: FloatArray) :AbstractGameObject() {

    private val mGreenRGBA = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)
    private val mBlueRGBA = floatArrayOf(0.22265625f, 0.63671875f, 0.76953125f, 1.0f)

    /**
     * sets the parameters based on the passed colour value
     */
    init {
        if ( 0 == colour ) {
            super.SetParameters(vertices, indices, normals, mBlueRGBA)
        }
        else {
            super.SetParameters(vertices, indices, normals, mGreenRGBA)
        }
    }
}