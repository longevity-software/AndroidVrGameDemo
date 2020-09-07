package longevity.software.vrgamedemo

import android.content.Context

class ModelLoader(context: Context) {

    val SUN_MODEL = "sun.obj"
    val GRASS_MODEL = "Grass.obj"
    val TREE_MODEL = "Tree.obj"
    val WALL_MODEL = "Wall.obj"

    private val mModelMap = HashMap<String, ModelData>()
    private val mDefaultModel: ModelData

    init {
        // load all the models
        mDefaultModel = ObjectFileParser(context, "cube.obj").getModelData()

        mModelMap.put(SUN_MODEL, ObjectAndMaterialFileParser(context, SUN_MODEL).getModelData())
        mModelMap.put(GRASS_MODEL, ObjectAndMaterialFileParser(context, GRASS_MODEL).getModelData())
        mModelMap.put(TREE_MODEL, ObjectAndMaterialFileParser(context, TREE_MODEL).getModelData())
        mModelMap.put(WALL_MODEL, ObjectAndMaterialFileParser(context, WALL_MODEL).getModelData())
    }

    /**
     * gets the model data from the model map if it exists
     */
    fun getModelData(model: String): ModelData {
        return mModelMap.getOrDefault(model, mDefaultModel)
    }
}