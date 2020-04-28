package longevity.software.vrgamedemo

import android.opengl.Matrix
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import java.util.concurrent.locks.ReentrantLock

class XboxController(): MoveControlInterface, LookControlInterface, ButtonControlInterface {

    // locks to prevent the rotation or translation matrices from being read and written to at the same time
    private val mRotationLock = ReentrantLock()
    private val mTranslationLock = ReentrantLock()
    private val mButtonLock = ReentrantLock()

    // constants for translation scaling
    private val X_TRANSLATION_SCALE = -50.0f
    private val Z_TRANSLATION_SCALE = -50.0f

    // constants for the look rotation
    private val MAX_PITCH_ANGLE = 90.0f
    private val PITCH_STEP = 1.0f
    private val MAX_YAW_ANGLE = 360.0f
    private val YAW_STEP = -1.0f

    // translation, look and button tracking variables
    private var mTranslationMatrix = FloatArray(16)
    private var mRotationMatrix = FloatArray(16)
    private var mPitchAngle: Float
    private var mPitchDelta: Float
    private var mYawAngle: Float
    private var mYawDelta: Float
    private var mActionButtonPressed: Boolean

    /**
     * init function initialises the translation and rotation matrices to an identity matrix.
     */
    init {
        // ensure matrices are set to identity values.
        Matrix.setIdentityM(mTranslationMatrix, 0)
        Matrix.setIdentityM(mRotationMatrix, 0)

        // set the initial rotation angles and deltas
        mPitchAngle = 0.0f
        mPitchDelta = 0.0f
        mYawAngle = 0.0f
        mYawDelta = 0.0f

        // action button is initially not pressed
        mActionButtonPressed = false
    }

    /**
     * overriden function from the MoveControlInterface
     * returns a copy of the calculated translation matrix.
     */
    override fun getLatestTranslationMatrix(): FloatArray {

        // lock while we cope the translation matrix
        mTranslationLock.lock()

        // take a copy
        val tranMat = mTranslationMatrix

        // unlock
        mTranslationLock.unlock()

        return tranMat
    }

    /**
     * overriden function from the LookControlInterface
     * returns a copy of the calculated rotation matrix.
     */
    override fun getLatestRotationMatrix(): FloatArray {

        // lock while we use the delta values and update the rotation matrix
        mRotationLock.lock()

        // update the pitch angle with the current delta
        mPitchAngle += mPitchDelta

        // cap the pitch at the maximum range
        if ( Math.abs(mPitchAngle) > MAX_PITCH_ANGLE )        {
            if ( mPitchAngle > 0 )            {
                mPitchAngle = MAX_PITCH_ANGLE
            } else {
                mPitchAngle = -MAX_PITCH_ANGLE
            }
        }

        // update the yaw angle with the current delta
        mYawAngle += mYawDelta

        // ensure the angle wraps around from 0 -> 360°
        if ( mYawAngle < 0.0f ) {
            mYawAngle += MAX_YAW_ANGLE
        } else if ( mYawAngle > MAX_YAW_ANGLE ) {
            mYawAngle -= MAX_YAW_ANGLE
        }

        // generate the rotation matrix
        // pitch is first, followed by yaw.
        Matrix.setRotateM(mRotationMatrix, 0, mPitchAngle, 1.0f, 0.0f, 0.0f)
        Matrix.rotateM(mRotationMatrix, 0, mYawAngle, 0.0f, 1.0f, 0.0f)

        // take a copy
        val rotMat = mRotationMatrix

        // unlock
        mRotationLock.unlock()

        return rotMat
    }

    /**
     *  Overridden function from the ButtonControlInterface
     *  returns the current state of the action button.
     */
    override fun isActionButtonPressed() : Boolean {

        // lock while we use the action button pressed variable
        mButtonLock.lock()

        // take a copy of the action button state and then reset it.
        val isPressed = mActionButtonPressed
        mActionButtonPressed = false

        // unlock
        mButtonLock.unlock()

        return isPressed
    }

    /**
     * Function to be called from the main activity/view's onKeyDown function.
     */
    fun onKeyDown(keyCode: Int, event: KeyEvent?) : Boolean {

        var keyDownHasBeenHandled = false

        // confirm the event is not null and the input device is a gamepad
        if ( null != event ) {
            if ( InputDevice.SOURCE_GAMEPAD == ( event.source and InputDevice.SOURCE_GAMEPAD ) ) {
                if ( 0 == event.repeatCount ) {

                    // we have got here so majority of situations should handle the event
                    keyDownHasBeenHandled = true

                    // process the key pressed
                    when ( keyCode ) {
                        KeyEvent.KEYCODE_BUTTON_A -> {
                            // lock while the Action button state is updated
                            mButtonLock.lock()

                            mActionButtonPressed = true

                            // unlock
                            mButtonLock.unlock()
                        }
                        else -> {
                            // no other keycodes are currently processed
                            keyDownHasBeenHandled = false
                        }
                    }
                }
            }
        }

        return keyDownHasBeenHandled
    }

    /**
     * Function to be called from the main activity/view's onGenericMotionEvent function.
     */
    fun onGenericMotionEvent(event: MotionEvent?) : Boolean {

        var motionEventHasBeenHandled = false

        // confirm the event is not null and the source is a joystick with a movement action.
        if ( null != event ) {
            if ( ( InputDevice.SOURCE_JOYSTICK == ( event.source and InputDevice.SOURCE_JOYSTICK ) )
                && ( event.action == MotionEvent.ACTION_MOVE ) ){

                // process and history
                ( 0 until event.historySize ).forEach{ i ->
                    processJoystickInput(event, i)
                }

                // process the current input
                processJoystickInput(event, -1)

                motionEventHasBeenHandled = true
            }
        }

        return motionEventHasBeenHandled
    }

    /**
     * Function to process a joystick input and generate movement, pitch and yaw deltas
     */
    private fun processJoystickInput(event: MotionEvent, historyPos: Int) {
        val inputDevice = event.device

        // get the left stick data
        val leftX: Float = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_X, historyPos)
        var leftZ: Float = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y, historyPos)

        // get the right stick data
        val rightX: Float = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Z, historyPos)
        val rightZ: Float = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_RZ, historyPos)

        // lock while the translation matrix is being updated
        mTranslationLock.lock()

        // get the move delta and use to generate translation matrix
        Matrix.setIdentityM(mTranslationMatrix, 0)
        Matrix.translateM(mTranslationMatrix, 0, (leftX / X_TRANSLATION_SCALE), 0.0f, (leftZ / Z_TRANSLATION_SCALE))

        mTranslationLock.unlock()

        // lock while the rotation deltas are being generated
        mRotationLock.lock()

        mPitchDelta = ( rightZ * PITCH_STEP )
        mYawDelta = ( rightX * YAW_STEP )

        mRotationLock.unlock()
    }

    /**
     * Function to get the axis value accounting for the flat centre area.
     */
    private fun getCenteredAxis(
                    event: MotionEvent,
                    device: InputDevice,
                    axis: Int,
                    historyPos: Int
    ) : Float {

        val range: InputDevice.MotionRange? = device.getMotionRange(axis, event.source)

        // use the getFlat method to get the range of the values
        // bounding the joystick axis center
        range?.apply {
            val value: Float = if ( 0 > historyPos ) {
                event.getAxisValue(axis)
            } else {
                event.getHistoricalAxisValue(axis, historyPos)
            }

            // check if the value is outside the flat spot.
            if ( flat < Math.abs(value) ) {
                return value
            }
        }

        return 0.0f
    }
}