package longevity.software.vrgamedemo

class GenericGameObject(modelData: ModelData) :AbstractGameObject() {

    /**
     * sets the parameters based on the passed colour value
     */
    init {
        super.SetParameters(modelData)
    }
}