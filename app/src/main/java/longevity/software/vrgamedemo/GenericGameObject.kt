package longevity.software.vrgamedemo

class GenericGameObject(colour: Int, vertices: FloatArray, indices: ShortArray) :AbstractGameObject() {

    private val greenRGBA = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)
    private val blueRGBA = floatArrayOf(0.22265625f, 0.63671875f, 0.76953125f, 1.0f)

    init {

        if ( 0 == colour ) {
            super.SetParameters(vertices, indices, blueRGBA)
        }
        else {
            super.SetParameters(vertices, indices, greenRGBA)
        }
    }
}