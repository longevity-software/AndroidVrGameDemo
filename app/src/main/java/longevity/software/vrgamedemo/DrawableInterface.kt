package longevity.software.vrgamedemo

interface DrawableInterface {

    fun draw(vpMatrix: FloatArray, lightPos: Triple<Float, Float, Float>, lightColour: Triple<Float, Float, Float>, cameraPos: Triple<Float, Float, Float>)

    fun initialise()
}