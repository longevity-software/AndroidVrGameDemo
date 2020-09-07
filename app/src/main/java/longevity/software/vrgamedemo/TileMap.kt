package longevity.software.vrgamedemo

class TileMap(modelLoader: ModelLoader) : DrawableInterface {

    private val NUMBER_OF_TILES = 9

    private var mTiles = Array<Tile?>(9) {null}

    /**
     * Initialise the tiles to default ones for now
     */
    init {

        val models = arrayOf<String>(modelLoader.GRASS_MODEL)

        // Create all the tiles
        for (i in 0 until NUMBER_OF_TILES) {
            mTiles[i] = Tile(models, modelLoader)
        }
    }

    /**
     * Function to initialise the Tiles models
     */
    override fun initialise() {

        // add all the models to the arraylist of model data
        for (i in 0 until NUMBER_OF_TILES) {
            mTiles[i]?.initialise()
        }
    }

    /**
     * Draws all the tiles
     */
    override fun draw(vpMatrix: FloatArray, lightPos: Triple<Float, Float, Float>, lightColour: Triple<Float, Float, Float>, cameraPos: Triple<Float, Float, Float>) {

        // draw all the tiles
        for (i in 0 until NUMBER_OF_TILES) {
            mTiles[i]?.draw(vpMatrix, lightPos, lightColour, cameraPos)
        }
    }
}