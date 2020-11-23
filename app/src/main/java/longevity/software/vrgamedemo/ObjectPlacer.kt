package longevity.software.vrgamedemo

import java.util.concurrent.locks.ReentrantLock

class ObjectPlacer(modelLoader: ModelLoader) : DrawableInterface {

    private val mModelLoader = modelLoader

    private var mModelsHaveNotBeenInitialised = true
    private var mShowModel = false
    private var mModelName = ""
    private var mTransparentModel: GenericGameObject? = null

    private val mRenderLock = ReentrantLock()

    /**
     * function to adjust the place that the transparent object is shown
     */
    fun adjustObjectPlacer(position: Position3Float, show: Boolean = true) {

        mRenderLock.lock()

        mShowModel = show

        if ( ( mShowModel )
            &&  ( mTransparentModel != null ) ){
            mTransparentModel!!.setPosition(position.X(), position.Y(), position.Z())
        }

        mRenderLock.unlock()
    }

    /**
     * function to change the model being placed
     */
    fun setModelToBePlaced(model: String) {

        mRenderLock.lock()

        mModelName = model
        mModelsHaveNotBeenInitialised = true

        mRenderLock.unlock()
    }

    /**
     * function to stop placing a model
     */
    fun resetModelToBePlaced() {

        mRenderLock.lock()

        mModelName = ""
        mShowModel = false
        mTransparentModel = null
        mModelsHaveNotBeenInitialised = false // set to false so we don't try to initialise nothing

        mRenderLock.unlock()
    }

    /**
     * overriden draw function
     */
    override fun draw(
        vpMatrix: FloatArray,
        lightPos: Triple<Float, Float, Float>,
        lightColour: Triple<Float, Float, Float>,
        cameraPos: Triple<Float, Float, Float>
    ) {
        mRenderLock.lock()

        if ( mModelsHaveNotBeenInitialised ) {
            this.initialise()
        }

        if ( ( mShowModel )
            &&  ( mTransparentModel != null ) ){
            mTransparentModel!!.draw(vpMatrix, lightPos, lightColour, cameraPos)
        }

        mRenderLock.unlock()
    }

    /**
     * Overridden initialise function
     */
    override fun initialise() {

        mRenderLock.lock()

        val transparentModel = mModelName + "Transparent.obj"

        mTransparentModel = GenericGameObject(mModelLoader.getModelData(transparentModel))

        mModelsHaveNotBeenInitialised = false

        mRenderLock.unlock()
    }


}