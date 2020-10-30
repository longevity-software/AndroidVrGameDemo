package longevity.software.vrgamedemo

class Tile(baseModel: String,
           baseRotation: String,
           modelLoader: ModelLoader,
           tileName: String,
           upLeft: String,
           straightUp: String,
           upRight: String,
           left: String,
           right: String,
           downLeft: String,
           straightDown: String,
           downRight: String,
           fluidity: Float,
           gameObjectData: ArrayList<Triple<String, Position3Float, Float>>) : DrawableInterface {

    private val mEMPTY_TILE_STRING = "Empty_tile.vtf"
    private val mBASE_MODEL_INDEX = 0

    private var mBaseModel = baseModel
    private var mBaseRotation = baseRotation

    private var mStaticModels = ArrayList<AbstractGameObject>()

    private val mModelLoader = modelLoader

    private val mFluidity = if (null == fluidity) { 0.0f } else fluidity

    private var mModelsHaveNotBeenInitialised = true

    private var mXOffset = 0.0f
    private var mZOffset = 0.0f

    private val mTileName = tileName
    private val mTileUpAndLeft = upLeft
    private val mTileStraightUp = straightUp
    private val mTileUpAndRight = upRight
    private val mTileStraightLeft = left
    private val mTileStraightRight = right
    private val mTileDownAndLeft = downLeft
    private val mTileStraightDown = straightDown
    private val mTileDownAndRight = downRight

    private val mGameOjectData = gameObjectData

    /**
     * Function to initialise the static models
     */
    override fun initialise() {

        // confirm base model is sensible
        if (    ( null == mBaseModel )
                or ( "" == mBaseModel )
        ) {
            mBaseModel = mModelLoader.TILE_EMPTY
        }

        mStaticModels.clear()   // ensure there are no models
        mStaticModels.add(GenericGameObject(mModelLoader.getModelData(mBaseModel)))

        // set the rotation of the base model
        when (mBaseRotation) {
            "Left" -> {
                mStaticModels[mBASE_MODEL_INDEX].setYRotation(270.0f)
            }
            "Right" -> {
                mStaticModels[mBASE_MODEL_INDEX].setYRotation(90.0f)
            }
            "Flip" -> {
                mStaticModels[mBASE_MODEL_INDEX].setYRotation(180.0f)
            }
        }

        // add game objects
        for ( gameObject in mGameOjectData ) {

            val obj = GenericGameObject(mModelLoader.getModelData(gameObject.first))
            obj.setPosition(gameObject.second.X(), gameObject.second.Y(), gameObject.second.Z())
            obj.setYRotation(gameObject.third)

            mStaticModels.add(obj)
        }

        // apply the current offsets
        for (model in mStaticModels) {
            val localPosition = model.getPosition()
            model.setPosition( ( localPosition.X() + mXOffset ), ( localPosition.Y() + 0.0f ) , ( localPosition.Z() + mZOffset ) )
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

            model.setPosition((prevPos.X() - mXOffset + xOff),
                                prevPos.Y(),
                                (prevPos.Z() - mZOffset + zOff))
        }

        // then update the current offsets
        mXOffset = xOff
        mZOffset = zOff
    }

    /**
     *  returns a null checked string representing the tile Name
     */
    fun getTileName() : String {

        if (    ( null == mTileName )
            or ( "" == mTileName )
        ) {
            return mEMPTY_TILE_STRING
        }
        else
        {
            return mTileName
        }
    }

    /**
     *  returns a null checked string representing the tile straight up from this one
     */
    fun getTileStraightUp() : String {

        if (    ( null == mTileStraightUp )
            or ( "" == mTileStraightUp )
        ) {
            return mEMPTY_TILE_STRING
        }
        else
        {
            return mTileStraightUp
        }
    }

    /**
     *  returns a null checked string representing the tile straight down from this one
     */
    fun getTileStraightDown() : String {

        if (    ( null == mTileStraightDown )
            or ( "" == mTileStraightDown )
        ) {
            return mEMPTY_TILE_STRING
        }
        else
        {
            return mTileStraightDown
        }
    }

    /**
     *  returns a null checked string representing the tile straight left from this one
     */
    fun getTileStraightLeft() : String {

        if (    ( null == mTileStraightLeft )
            or ( "" == mTileStraightLeft )
        ) {
            return mEMPTY_TILE_STRING
        }
        else
        {
            return mTileStraightLeft
        }
    }

    /**
     *  returns a null checked string representing the tile straight right from this one
     */
    fun getTileStraightRight() : String {

        if (    ( null == mTileStraightRight )
            or ( "" == mTileStraightRight )
        ) {
            return mEMPTY_TILE_STRING
        }
        else
        {
            return mTileStraightRight
        }
    }

    /**
     *  returns a null checked string representing the tile up and left from this one
     */
    fun getTileUpLeft() : String {

        if (    ( null == mTileUpAndLeft )
            or ( "" == mTileUpAndLeft )
        ) {
            return mEMPTY_TILE_STRING
        }
        else
        {
            return mTileUpAndLeft
        }
    }

    /**
     *  returns a null checked string representing the tile up and right from this one
     */
    fun getTileUpRight() : String {

        if (    ( null == mTileUpAndRight )
            or ( "" == mTileUpAndRight )
        ) {
            return mEMPTY_TILE_STRING
        }
        else
        {
            return mTileUpAndRight
        }
    }

    /**
     *  returns a null checked string representing the tile down and left from this one
     */
    fun getTileDownLeft() : String {

        if (    ( null == mTileDownAndLeft )
            or ( "" == mTileDownAndLeft )
        ) {
            return mEMPTY_TILE_STRING
        }
        else
        {
            return mTileDownAndLeft
        }
    }

    /**
     *  returns a null checked string representing the tile down and right from this one
     */
    fun getTileDownRight() : String {

        if (    ( null == mTileDownAndRight )
            or ( "" == mTileDownAndRight )
        ) {
            return mEMPTY_TILE_STRING
        }
        else
        {
            return mTileDownAndRight
        }
    }

    /**
     *  Function to indicate how easily the player can move through the tile.
     *  Fluidity should be a value between 0.0 (player can't move)
     *  and 1.0 (player can move unimpeded)
     */
    fun getTileFluidity() : Float {
        return mFluidity
    }
}