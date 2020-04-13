package longevity.software.vrgamedemo

class GameControlHub(lookControl: LookControlInterface) {

    private var mLookControl = lookControl

    /**
     * function to change the current look control interface for this hub
     */
    fun updateLookControlInterface(newLookControlInterface: LookControlInterface) {
        mLookControl = newLookControlInterface
    }

    /**
     * function to get the current look rotation
     * of the currently set look control interface
     */
    fun getLookRotationMatrix(): FloatArray {
        return mLookControl.getLatestRotationMatrix()
    }
}