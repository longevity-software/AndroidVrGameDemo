package longevity.software.vrgamedemo

import android.content.Context

class ModelLoader(context: Context) {

    val SUZANNE_MODEL = "suzanne.obj"
    val TOROID_MODEL = "smoothTorus.obj"

    private val mModelMap = HashMap<String, ModelData>()
    private val mDefaultModel: ModelData

    init {
        // load all the models
        mDefaultModel = ObjectFileParser(context, "cube.obj").getModelData()

        mModelMap.put(SUZANNE_MODEL, ObjectFileParser(context, SUZANNE_MODEL).getModelData())
        mModelMap.put(TOROID_MODEL, ObjectFileParser(context, TOROID_MODEL).getModelData())
    }

    /**
     * gets the model data from the model map if it exists
     */
    fun getModelData(model: String): ModelData {
        return mModelMap.getOrDefault(model, mDefaultModel)
    }
}