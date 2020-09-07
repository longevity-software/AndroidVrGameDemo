package longevity.software.vrgamedemo

class Tile(models: Array<String>, modelLoader: ModelLoader) : DrawableInterface {

    private var mStaticModels = ArrayList<AbstractGameObject>()

    private val mModels = models
    private val mModelLoader = modelLoader

    /**
     * Function to initialise the static models
     */
    override fun initialise() {

        // add all the models to the arraylist of model data
        for (model in mModels) {
            mStaticModels.add(GenericGameObject(mModelLoader.getModelData(model)))
        }
    }

    /**
     * Function to render all the static models in the Tile
     */
    override fun draw(vpMatrix: FloatArray, lightPos: Triple<Float, Float, Float>, lightColour: Triple<Float, Float, Float>, cameraPos: Triple<Float, Float, Float>) {

        for (model in mStaticModels) {
            model.draw(vpMatrix, lightPos, lightColour, cameraPos)
        }
    }
}