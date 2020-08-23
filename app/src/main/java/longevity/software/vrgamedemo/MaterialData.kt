package longevity.software.vrgamedemo

data class MaterialData(val mMaterialName: String) {

    var mAmbientColour = floatArrayOf(1.0f, 1.0f, 1.0f)
    var mDiffuseColour = floatArrayOf(1.0f, 1.0f, 1.0f)
    var mSpecularColour = floatArrayOf(1.0f, 1.0f, 1.0f)
    var mSpecularExponent: Float = 32.0f
    var mDissolve: Float = 1.0f
}