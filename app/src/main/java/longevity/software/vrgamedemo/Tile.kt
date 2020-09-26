package longevity.software.vrgamedemo

class Tile(baseModel: String,
           modelLoader: ModelLoader,
           upLeft: String,
           straightUp: String,
           upRight: String,
           left: String,
           right: String,
           downLeft: String,
           straightDown: String,
           downRight: String) : DrawableInterface {

    // set the base model to a blank tile if it is not set
    private val mBaseModel = if ("" == baseModel) { modelLoader.TILE_EMPTY } else { baseModel }

    private var mStaticModels = ArrayList<AbstractGameObject>()

    private val mModelLoader = modelLoader

    private var mModelsHaveNotBeenInitialised = true

    private var mXOffset = 0.0f
    private var mZOffset = 0.0f

    val TileUpAndLeft = if (null == upLeft) { "" } else { upLeft }
    val TileStraightUp = if (null == straightUp) { "" } else { straightUp }
    val TileUpAndRight = if (null == upRight) { "" } else { upRight }
    val TileLeft = if (null == left) { "" } else { left }
    val TileRight = if (null == right) { "" } else { right }
    val TileDownAndLeft = if (null == downLeft) { "" } else { downLeft }
    val TileStraightDown = if (null == straightDown) { "" } else { straightDown }
    val TileDownAndRight = if (null == downRight) { "" } else { downRight }

    /**
     * Function to initialise the static models
     */
    override fun initialise() {

        mStaticModels.add(GenericGameObject(mModelLoader.getModelData(mBaseModel)))

        // apply the current offsets
        for (model in mStaticModels) {
            model.setPosition(mXOffset, 0.0f, mZOffset)
        }

        // models have now been initialised.
        mModelsHaveNotBeenInitialised = false
    }

    /**
     * Function to render all the static models in the Tile
     */
    override fun draw(vpMatrix: FloatArray, lightPos: Triple<Float, Float, Float>, lightColour: Triple<Float, Float, Float>, cameraPos: Triple<Float, Float, Float>) {

        // before we draw, check that the models have been initialised as the tile may be a new one
        if ( mModelsHaveNotBeenInitialised ) {
            this.initialise()
        }

        for (model in mStaticModels) {
            model.draw(vpMatrix, lightPos, lightColour, cameraPos)
        }
    }

    /**
     * Function to set the offset of the Tile
     */
    fun setTileOffset(xOff: Float, zOff: Float) {

        // for all the models
        // subtract the current offsets and then apply the new ones
        for (model in mStaticModels) {

            val prevPos = model.getPosition()

            model.setPosition((prevPos.first - mXOffset + xOff),
                                prevPos.second,
                                (prevPos.third - mZOffset + zOff))
        }

        // then update the current offsets
        mXOffset = xOff
        mZOffset = zOff
    }
}