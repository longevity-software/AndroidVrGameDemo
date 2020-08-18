package longevity.software.vrgamedemo

data class MaterialData(val mMaterialName: String) {

    var mAmbientColour = floatArrayOf(1.0f, 0.2f, 0.3f)
    var mDiffuseColour = floatArrayOf(1.0f, 0.2f, 0.3f)
    var mSpecularColour = floatArrayOf(1.0f, 0.2f, 0.3f)
    var mSpecularExponent: Float = 32.0f
    var mTransparency: Float = 1.0f
}