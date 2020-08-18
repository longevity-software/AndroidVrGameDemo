package longevity.software.vrgamedemo

data class ModelData(val mVertices: FloatArray,
                     val mNormals: FloatArray,
                     val mUVs: FloatArray,
                     val mIndices: ShortArray,
                     val mAmbientColour: FloatArray,
                     val mDiffuseColour: FloatArray,
                     val mSpecularColour: FloatArray,
                     val mSpecularExponent: FloatArray,
                     val mTransparency: FloatArray) {

}