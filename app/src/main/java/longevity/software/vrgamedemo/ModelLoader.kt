package longevity.software.vrgamedemo

import android.content.Context

class ModelLoader(context: Context) {

    val SUN_MODEL = "sun.obj"
    val GRASS_MODEL = "Grass.obj"
    val TREE_MODEL = "Tree.obj"
    val WALL_MODEL = "Wall.obj"

    val TILE_EMPTY = "emptyTile.obj"

    val ROCKS_MODEL = "Rocks.obj"

    val TREE_TRANSPARENT_MODEL = "TreeTransparent.obj"
    val ROCKS_TRANSPARENT_MODEL = "RocksTransparent.obj"

    private val mModelMap = HashMap<String, ModelData>()
    private val mDefaultModel: ModelData

    init {
        // load all the models
        mDefaultModel = ObjectFileParser(context, "cube.obj").getModelData()

        mModelMap.put(SUN_MODEL, ObjectAndMaterialFileParser(context, SUN_MODEL).getModelData())
        mModelMap.put(GRASS_MODEL, ObjectAndMaterialFileParser(context, GRASS_MODEL).getModelData())
        mModelMap.put(TREE_MODEL, ObjectAndMaterialFileParser(context, TREE_MODEL).getModelData())
        mModelMap.put(WALL_MODEL, ObjectAndMaterialFileParser(context, WALL_MODEL).getModelData())

        mModelMap.put(TILE_EMPTY, ObjectAndMaterialFileParser(context, TILE_EMPTY).getModelData())

        mModelMap.put(ROCKS_MODEL, ObjectAndMaterialFileParser(context, ROCKS_MODEL).getModelData())

        // transparent models
        mModelMap.put(TREE_TRANSPARENT_MODEL, ObjectAndMaterialFileParser(context, TREE_TRANSPARENT_MODEL).getModelData())
        mModelMap.put(ROCKS_TRANSPARENT_MODEL, ObjectAndMaterialFileParser(context, ROCKS_TRANSPARENT_MODEL).getModelData())
    }

    /**
     * gets the model data from the model map if it exists
     */
    fun getModelData(model: String): ModelData {
        return mModelMap.getOrDefault(model, mDefaultModel)
    }
}