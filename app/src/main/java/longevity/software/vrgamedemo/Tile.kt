package longevity.software.vrgamedemo

import android.content.Context
import java.io.File

class Tile(baseModel: String,
           baseRotation: String,
           modelLoader: ModelLoader,
           tileName: String,
           fluidity: Float,
           gameObjectData: ArrayList<Triple<String, Position3Float, Float>>) : DrawableInterface {

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

    private val mGameOjectData = gameObjectData

    companion object Factory {
        private val mEMPTY_TILE_STRING = "Empty_tile.vtf"

        fun GetEmptyTile(modelLoader: ModelLoader) : Tile = Tile(modelLoader.TILE_EMPTY,
            "None",
            modelLoader,
            mEMPTY_TILE_STRING,
            0.0f,
            ArrayList<Triple<String, Position3Float, Float>>()
        )
    }

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