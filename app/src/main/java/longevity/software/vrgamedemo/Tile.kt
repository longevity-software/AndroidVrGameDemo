package longevity.software.vrgamedemo

class Tile(baseModel: String, modelLoader: ModelLoader) : DrawableInterface {

    private val mBaseModel = baseModel
    private var mStaticModels = ArrayList<AbstractGameObject>()

    private val mModelLoader = modelLoader

    private var mModelsHaveNotBeenInitialised = true

    private var mXOffset = 0.0f
    private var mZOffset = 0.0f

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