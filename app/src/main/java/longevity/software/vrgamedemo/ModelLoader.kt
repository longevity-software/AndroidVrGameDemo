package longevity.software.vrgamedemo

import android.content.Context

class ModelLoader(context: Context) {

    val SUN_MODEL = "sun.obj"
    val GRASS_MODEL = "Grass.obj"
    val TREE_MODEL = "Tree.obj"
    val WALL_MODEL = "Wall.obj"

    val TILE_ONE_MODEL = "TileOne.obj"
    val TILE_TWO_MODEL = "TileTwo.obj"
    val TILE_THREE_MODEL = "TileThree.obj"
    val TILE_FOUR_MODEL = "TileFour.obj"
    val TILE_FIVE_MODEL = "TileFive.obj"
    val TILE_SIX_MODEL = "TileSix.obj"
    val TILE_SEVEN_MODEL = "TileSeven.obj"
    val TILE_EIGHT_MODEL = "TileEight.obj"
    val TILE_NINE_MODEL = "TileNine.obj"

    val TILE_EMPTY = "emptyTile.obj"

    val TILE_STRAIGHT = "Straight.obj"
    val TILE_CORNER = "Corner.obj"
    val TILE_CROSSROADS = "Crossroads.obj"
    val TILE_JUNCTION = "Junction.obj"

    val ROCKS_MODEL = "Rocks.obj"

    private val mModelMap = HashMap<String, ModelData>()
    private val mDefaultModel: ModelData

    init {
        // load all the models
        mDefaultModel = ObjectFileParser(context, "cube.obj").getModelData()

        mModelMap.put(SUN_MODEL, ObjectAndMaterialFileParser(context, SUN_MODEL).getModelData())
        mModelMap.put(GRASS_MODEL, ObjectAndMaterialFileParser(context, GRASS_MODEL).getModelData())
        mModelMap.put(TREE_MODEL, ObjectAndMaterialFileParser(context, TREE_MODEL).getModelData())
        mModelMap.put(WALL_MODEL, ObjectAndMaterialFileParser(context, WALL_MODEL).getModelData())

        mModelMap.put(TILE_ONE_MODEL, ObjectAndMaterialFileParser(context, TILE_ONE_MODEL).getModelData())
        mModelMap.put(TILE_TWO_MODEL, ObjectAndMaterialFileParser(context, TILE_TWO_MODEL).getModelData())
        mModelMap.put(TILE_THREE_MODEL, ObjectAndMaterialFileParser(context, TILE_THREE_MODEL).getModelData())
        mModelMap.put(TILE_FOUR_MODEL, ObjectAndMaterialFileParser(context, TILE_FOUR_MODEL).getModelData())
        mModelMap.put(TILE_FIVE_MODEL, ObjectAndMaterialFileParser(context, TILE_FIVE_MODEL).getModelData())
        mModelMap.put(TILE_SIX_MODEL, ObjectAndMaterialFileParser(context, TILE_SIX_MODEL).getModelData())
        mModelMap.put(TILE_SEVEN_MODEL, ObjectAndMaterialFileParser(context, TILE_SEVEN_MODEL).getModelData())
        mModelMap.put(TILE_EIGHT_MODEL, ObjectAndMaterialFileParser(context, TILE_EIGHT_MODEL).getModelData())
        mModelMap.put(TILE_NINE_MODEL, ObjectAndMaterialFileParser(context, TILE_NINE_MODEL).getModelData())
        mModelMap.put(TILE_EMPTY, ObjectAndMaterialFileParser(context, TILE_EMPTY).getModelData())

        mModelMap.put(TILE_STRAIGHT, ObjectAndMaterialFileParser(context, TILE_STRAIGHT).getModelData())
        mModelMap.put(TILE_CORNER, ObjectAndMaterialFileParser(context, TILE_CORNER).getModelData())
        mModelMap.put(TILE_CROSSROADS, ObjectAndMaterialFileParser(context, TILE_CROSSROADS).getModelData())
        mModelMap.put(TILE_JUNCTION, ObjectAndMaterialFileParser(context, TILE_JUNCTION).getModelData())

        mModelMap.put(ROCKS_MODEL, ObjectAndMaterialFileParser(context, ROCKS_MODEL).getModelData())
    }

    /**
     * gets the model data from the model map if it exists
     */
    fun getModelData(model: String): ModelData {
        return mModelMap.getOrDefault(model, mDefaultModel)
    }
}