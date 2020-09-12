package longevity.software.vrgamedemo

class TileMap(modelLoader: ModelLoader) : DrawableInterface {

    private val NUMBER_OF_TILES = 9

    private val TILE_SIZE : Float = 20.0f
    private val HALF_TILE_SIZE = TILE_SIZE / 2.0f

    private var mTiles = Array<Tile?>(9) {null}

    /**
     * Initialise the tiles to default ones for now
     */
    init {

        val models = arrayOf<String>(modelLoader.TILE_ONE_MODEL,
            modelLoader.TILE_TWO_MODEL,
            modelLoader.TILE_THREE_MODEL,
            modelLoader.TILE_FOUR_MODEL,
            modelLoader.TILE_FIVE_MODEL,
            modelLoader.TILE_SIX_MODEL,
            modelLoader.TILE_SEVEN_MODEL,
            modelLoader.TILE_EIGHT_MODEL,
            modelLoader.TILE_NINE_MODEL)

        // Create all the tiles
        for (i in 0 until NUMBER_OF_TILES) {
            mTiles[i] = Tile(models[i], modelLoader)
        }
    }

    /**
     * Function to initialise the Tiles models
     */
    override fun initialise() {

        val offsets = arrayOf(Pair(-TILE_SIZE, -TILE_SIZE),
            Pair(0.0f, -TILE_SIZE),
            Pair(TILE_SIZE, -TILE_SIZE),
            Pair(-TILE_SIZE, 0.0f),
            Pair(0.0f, 0.0f),
            Pair(TILE_SIZE, 0.0f),
            Pair(-TILE_SIZE, TILE_SIZE),
            Pair(0.0f, TILE_SIZE),
            Pair(TILE_SIZE, TILE_SIZE))

        // add all the models to the arraylist of model data
        for (i in 0 until NUMBER_OF_TILES) {
            mTiles[i]?.initialise()
            mTiles[i]?.setTileOffset(offsets[i].first, offsets[i].second)
        }
    }

    /**
     * Function to get the players position on the tile map
     */
    fun getPlayerPositionOnTileMap(current : Triple<Float, Float, Float>, delta : Triple<Float, Float, Float>) : Triple<Float, Float, Float> {

        var tempX = (current.first + delta.first)
        var tempY = (current.second + delta.second)
        var tempZ = (current.third + delta.third)

        if ( tempX > HALF_TILE_SIZE ) {
            tempX -= TILE_SIZE
        } else if ( tempX < -HALF_TILE_SIZE ) {
            tempX += TILE_SIZE
        }

        if ( tempZ > HALF_TILE_SIZE ) {
            tempZ -= TILE_SIZE
        } else if ( tempZ < -HALF_TILE_SIZE ) {
            tempZ += TILE_SIZE
        }

        return Triple(tempX, tempY, tempZ)
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