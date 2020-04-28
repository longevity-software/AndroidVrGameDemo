package longevity.software.vrgamedemo

class GameControlHub(lookControl: LookControlInterface,
                     moveControl: MoveControlInterface,
                     buttonControl: ButtonControlInterface) {

    private var mLookControl = lookControl
    private var mMoveControl = moveControl
    private var mButtonControl = buttonControl

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

    /**
     * function to get the current translation matrix
     * of the currently set move control interface
     */
    fun getMoveTranslationMatrix(): FloatArray {
        return mMoveControl.getLatestTranslationMatrix()
    }

    /**
     * function to get whether the action button has been pressed
     */
    fun isActionButtonPressed(): Boolean {
        return mButtonControl.isActionButtonPressed()
    }
}