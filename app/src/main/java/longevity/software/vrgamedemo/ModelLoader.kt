package longevity.software.vrgamedemo

import android.content.Context

class ModelLoader(context: Context) {

    val HOUSE_MODEL = "House.obj"
    val SHELTER_MODEL = "shelter.obj"

    //val PLANE_MODEL = "Plane.obj"
    //val STATUE_MODEL = "Statue.obj"
    //val WELL_MODEL = "Well.obj"

    private val mModelMap = HashMap<String, ModelData>()
    private val mDefaultModel: ModelData

    init {
        // load all the models
        mDefaultModel = ObjectFileParser(context, "cube.obj").getModelData()

        mModelMap.put(HOUSE_MODEL, ObjectAndMaterialFileParser(context, HOUSE_MODEL).getModelData())
        mModelMap.put(SHELTER_MODEL, ObjectAndMaterialFileParser(context, SHELTER_MODEL).getModelData())
        //mModelMap.put(PLANE_MODEL, ObjectFileParser(context, PLANE_MODEL).getModelData())
        //mModelMap.put(STATUE_MODEL, ObjectFileParser(context, STATUE_MODEL).getModelData())
        //mModelMap.put(WELL_MODEL, ObjectFileParser(context, WELL_MODEL).getModelData())
    }

    /**
     * gets the model data from the model map if it exists
     */
    fun getModelData(model: String): ModelData {
        return mModelMap.getOrDefault(model, mDefaultModel)
    }
}