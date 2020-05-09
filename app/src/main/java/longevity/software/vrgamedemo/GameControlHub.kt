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
     * function to get whether the action button has been pressed
     */
    fun isActionButtonPressed(): Boolean {
        return mButtonControl.isActionButtonPressed()
    }
}