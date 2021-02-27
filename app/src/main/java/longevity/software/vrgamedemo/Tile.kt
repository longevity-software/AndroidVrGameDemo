package longevity.software.vrgamedemo

import android.content.Context
import java.io.File

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

    private var mFluidity = if (null == fluidity) { 0.0f } else fluidity

    private var mModelsHaveNotBeenInitialised = true

    private var mXOffset = 0.0f
    private var mZOffset = 0.0f

    private val mTileName = tileName
    private var mTileUpAndLeft = upLeft
    private var mTileStraightUp = straightUp
    private var mTileUpAndRight = upRight
    private var mTileStraightLeft = left
    private var mTileStraightRight = right
    private var mTileDownAndLeft = downLeft
    private var mTileStraightDown = straightDown
    private var mTileDownAndRight = downRight

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
     * adds a model to the tile
     */
    fun addModel(modelName: String, pos: Position3Float, rot: Float) {

        val gameObjectData = Triple(modelName, pos, rot)

        mGameOjectData.add(gameObjectData)

        mModelsHaveNotBeenInitialised = true
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
     * returns true if the tile is empty as detected by having the empty tile name
     */
    fun tileIsEmpty() : Boolean {
        return mEMPTY_TILE_STRING == getTileName()
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

    /**
     * Update the base mode
     */
    fun setTileBaseModel(model: String) {

        mBaseModel = model

        mModelsHaveNotBeenInitialised = true
    }

    /**
     * Updates the tile straight Up
     */
    fun setTileStraightUp(name: String) {
        mTileStraightUp = name
    }

    /**
     * Updates the tile straight Down
     */
    fun setTileStraightDown(name: String) {
        mTileStraightDown = name
    }

    /**
     * Updates the tile straight Left
     */
    fun setTileStraightLeft(name: String) {
        mTileStraightLeft = name
    }

    /**
     * Updates the tile straight Right
     */
    fun setTileStraightRight(name: String) {
        mTileStraightRight = name
    }

    /**
     * Updates the tile up and left
     */
    fun setTileUpLeft(name: String) {
        mTileUpAndLeft = name
    }

    /**
     * Updates the tile up and right
     */
    fun setTileUpRight(name: String) {
        mTileUpAndRight = name
    }

    /**
     * Updates the tile down and left
     */
    fun setTileDownLeft(name: String) {
        mTileDownAndLeft = name
    }

    /**
     * Updates the tile down and right
     */
    fun setTileDownRight(name: String) {
        mTileDownAndRight = name
    }

    /**
     * Updates the tile fluidity
     */
    fun setTileFluidity(fluidity: Float) {
        mFluidity = fluidity
    }

    /**
     * Function used to save the tile to a file
     */
    fun saveTileToFile(context: Context) {

        val file = File(context.filesDir, mTileName).also {

            if ( it.exists() ) {
                it.delete()
            }
        }

        context.openFileOutput(mTileName, Context.MODE_PRIVATE).use {
            it.write( ( "<BM>" + mBaseModel + "</BM>" + System.lineSeparator() ).toByteArray() )
            it.write( ( "<BR>" + mBaseRotation + "</BR>" + System.lineSeparator() ).toByteArray() )
            it.write( ( "<SU>" + getTileStraightUp() + "</SU>" + System.lineSeparator() ).toByteArray() )
            it.write( ( "<SD>" + getTileStraightDown() + "</SD>" + System.lineSeparator() ).toByteArray() )
            it.write( ( "<SL>" + getTileStraightLeft() + "</SL>" + System.lineSeparator() ).toByteArray() )
            it.write( ( "<SR>" + getTileStraightRight() + "</SR>" + System.lineSeparator() ).toByteArray() )
            it.write( ( "<UL>" + getTileUpLeft() + "</UL>" + System.lineSeparator() ).toByteArray() )
            it.write( ( "<UR>" + getTileUpRight() + "</UR>" + System.lineSeparator() ).toByteArray() )
            it.write( ( "<DL>" + getTileDownLeft() + "</DL>" + System.lineSeparator() ).toByteArray() )
            it.write( ( "<DR>" + getTileDownRight() + "</DR>" + System.lineSeparator() ).toByteArray() )
            it.write( ( "<TF>" + mFluidity.toString() + "</TF>" + System.lineSeparator() ).toByteArray() )

            // save any game objects
            mGameOjectData.forEach { gameObject ->
                it.write( ("<GO>" +
                        gameObject.first + "," +
                        gameObject.second.X().toString() + "," +
                        gameObject.second.Y().toString() + "," +
                        gameObject.second.Z().toString() + "," +
                        gameObject.third.toString() + "</GO>" +
                        System.lineSeparator() ).toByteArray() )
            }
        }
    }
}