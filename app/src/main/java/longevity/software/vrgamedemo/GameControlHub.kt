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
     * function to get the current look pitch
     * of the currently set look control interface
     */
    fun getLookPitch(): Float {
        return mLookControl.getLatestPitch()
    }

    /**
     * function to get the current look yaw
     * of the currently set look control interface
     */
    fun getLookYaw(): Float {
        return mLookControl.getLatestYaw()
    }

    /**
     * function to get the current forward/backwards delta
     * of the currently set move control interface
     */
    fun getMoveForwardBackDelta(): Float {
        return mMoveControl.getLatestForwardBackwardsDelta()
    }

    /**
     * function to get the current left/right delta
     * of the currently set move control interface
     */
    fun getMoveLeftRightDelta(): Float {
        return mMoveControl.getLatestLeftRightDelta()
    }

    /**
     * function to get whether the action button state
     */
    fun getActionButtonState(): ButtonControlInterface.ButtonState {
        return mButtonControl.getActionButtonState()
    }

    /**
     * function to get whether the 2nd action button state
     */
    fun getActionButton2State(): ButtonControlInterface.ButtonState {
        return mButtonControl.getActionButton2State()
    }

    /**
     * function to get whether the R1 button state
     */
    fun getR1ButtonState(): ButtonControlInterface.ButtonState {
        return mButtonControl.getR1ButtonState()
    }

    /**
     * function to get whether the L1 button state
     */
    fun getL1ButtonState(): ButtonControlInterface.ButtonState {
        return mButtonControl.getL1ButtonState()
    }

    /**
     * function to get the state of the options button
     */
    fun getOptionsButtonState(): ButtonControlInterface.ButtonState {
        return mButtonControl.getOptionsButtonState()
    }
}