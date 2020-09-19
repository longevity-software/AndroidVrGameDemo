package longevity.software.vrgamedemo

class TileMap(modelLoader: ModelLoader) : DrawableInterface, PlayerPositionTileMapInterface {

    private val NUMBER_OF_TILES = 9

    private val TILE_SIZE : Float = 20.0f
    private val HALF_TILE_SIZE = TILE_SIZE / 2.0f

    private var mTiles = Array<Tile?>(9) {null}

    private val mModelLoader = modelLoader

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
    override fun getPlayerPositionOnTileMap(current : Triple<Float, Float, Float>, delta : Triple<Float, Float, Float>) : Triple<Float, Float, Float> {

        val DONT_MOVE = 0x00
        val MOVE_UP = 0x01
        val MOVE_DOWN = 0x02
        val MOVE_LEFT = 0x04
        val MOVE_RIGHT = 0x08

        // diaganols
        val MOVE_UP_AND_RIGHT = MOVE_UP or MOVE_RIGHT
        val MOVE_UP_AND_LEFT = MOVE_UP or MOVE_LEFT
        val MOVE_DOWN_AND_RIGHT = MOVE_DOWN or MOVE_RIGHT
        val MOVE_DOWN_AND_LEFT = MOVE_DOWN or MOVE_LEFT

        var tempX = (current.first + delta.first)
        var tempY = (current.second + delta.second)
        var tempZ = (current.third + delta.third)

        var moveDirection = DONT_MOVE

        if ( tempX > HALF_TILE_SIZE ) {
            tempX -= TILE_SIZE
            moveDirection = moveDirection or MOVE_RIGHT
        } else if ( tempX < -HALF_TILE_SIZE ) {
            tempX += TILE_SIZE
            moveDirection = moveDirection or MOVE_LEFT
        }

        if ( tempZ > HALF_TILE_SIZE ) {
            tempZ -= TILE_SIZE
            moveDirection = moveDirection or MOVE_DOWN
        } else if ( tempZ < -HALF_TILE_SIZE ) {
            tempZ += TILE_SIZE
            moveDirection = moveDirection or MOVE_UP
        }

        when (moveDirection) {
            MOVE_UP -> {
                // move the first column up
                mTiles[6] = mTiles[3]
                mTiles[3] = mTiles[0]
                mTiles[0] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and the middle column
                mTiles[7] = mTiles[4]
                mTiles[4] = mTiles[1]
                mTiles[1] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and finally the last column
                mTiles[8] = mTiles[5]
                mTiles[5] = mTiles[2]
                mTiles[2] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)
            }
            MOVE_DOWN -> {
                // move the first column down
                mTiles[0] = mTiles[3]
                mTiles[3] = mTiles[6]
                mTiles[6] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and the middle column
                mTiles[1] = mTiles[4]
                mTiles[4] = mTiles[7]
                mTiles[7] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and finally the last column
                mTiles[2] = mTiles[5]
                mTiles[5] = mTiles[8]
                mTiles[8] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)
            }
            MOVE_LEFT -> {

                // move the first row right
                mTiles[2] = mTiles[1]
                mTiles[1] = mTiles[0]
                mTiles[0] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and the middle column
                mTiles[5] = mTiles[4]
                mTiles[4] = mTiles[3]
                mTiles[3] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and finally the last column
                mTiles[8] = mTiles[7]
                mTiles[7] = mTiles[6]
                mTiles[6] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)
            }
            MOVE_RIGHT -> {

                // move the first row left
                mTiles[0] = mTiles[1]
                mTiles[1] = mTiles[2]
                mTiles[2] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and the middle row left
                mTiles[3] = mTiles[4]
                mTiles[4] = mTiles[5]
                mTiles[5] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and finally the last row
                mTiles[6] = mTiles[7]
                mTiles[7] = mTiles[8]
                mTiles[8] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)
            }
            MOVE_UP_AND_RIGHT -> {
                // move the diagonal
                mTiles[3] = mTiles[1]
                mTiles[1] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and the middle diaganol
                mTiles[6] = mTiles[4]
                mTiles[4] = mTiles[2]
                mTiles[2] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and finally the last diagonal
                mTiles[7] = mTiles[5]
                mTiles[5] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)
            }
            MOVE_UP_AND_LEFT -> {
                // move the diagonal
                mTiles[3] = mTiles[7]
                mTiles[7] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and the middle diaganol
                mTiles[8] = mTiles[4]
                mTiles[4] = mTiles[0]
                mTiles[0] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and finally the last diagonal
                mTiles[5] = mTiles[1]
                mTiles[1] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)
            }
            MOVE_DOWN_AND_RIGHT -> {
                // move the diagonal
                mTiles[7] = mTiles[3]
                mTiles[3] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and the middle diaganol
                mTiles[0] = mTiles[4]
                mTiles[4] = mTiles[8]
                mTiles[8] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and finally the last diagonal
                mTiles[1] = mTiles[5]
                mTiles[5] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)
            }
            MOVE_DOWN_AND_LEFT -> {
                // move the diagonal
                mTiles[1] = mTiles[3]
                mTiles[3] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and the middle diaganol
                mTiles[2] = mTiles[4]
                mTiles[4] = mTiles[6]
                mTiles[6] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)

                // and finally the last diagonal
                mTiles[5] = mTiles[7]
                mTiles[7] = Tile(mModelLoader.GRASS_MODEL, mModelLoader)
            }
            else -> {
                // do nothing if the direction is not valid
            }
        }

        // if the tiles have moved then reset the offsets
        if ( DONT_MOVE != moveDirection ) {
            val offsets = arrayOf(
                Pair(-TILE_SIZE, -TILE_SIZE),
                Pair(0.0f, -TILE_SIZE),
                Pair(TILE_SIZE, -TILE_SIZE),
                Pair(-TILE_SIZE, 0.0f),
                Pair(0.0f, 0.0f),
                Pair(TILE_SIZE, 0.0f),
                Pair(-TILE_SIZE, TILE_SIZE),
                Pair(0.0f, TILE_SIZE),
                Pair(TILE_SIZE, TILE_SIZE)
            )

            // add all the models to the arraylist of model data
            for (i in 0 until NUMBER_OF_TILES) {
                mTiles[i]?.setTileOffset(offsets[i].first, offsets[i].second)
            }
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