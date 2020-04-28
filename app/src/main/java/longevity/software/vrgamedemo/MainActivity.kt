package longevity.software.vrgamedemo

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mDeviceRotationSensor: DeviceRotationSensor
    private lateinit var mXboxController: XboxController
    private lateinit var mPlayerControlHub: GameControlHub

    /**
     * function called when the Main activity is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set to full screen immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE)

        // initialise the device rotation sensor class
        // TODO - this currently does not work
        mDeviceRotationSensor = DeviceRotationSensor(this)

        // initialise the xbox controller class.
        mXboxController = XboxController()

        // the same xbox controller class implements the look, move and button interfaces.
        mPlayerControlHub = GameControlHub(mXboxController, mXboxController, mXboxController)

        // set the content to our VrGlSurfaceView
        setContentView(VrGlSurfaceView(this, mPlayerControlHub))
    }

    /**
     * overridded onResume function needed to register listeners for the device rotation sensor.
     */
    override fun onResume() {
        super.onResume()

        mDeviceRotationSensor.registerListeners()
    }

    /**
     * overriden onPause function needed to unregister the device rotation sensors listeners.
     */
    override fun onPause() {
        super.onPause()

        mDeviceRotationSensor.unregisterListeners()
    }

    /**
     * Overridden onKeyDown function which calls the XboxController class's onKeyDown function to process the event
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if ( true == mXboxController.onKeyDown(keyCode, event) ) {
            return true
        }
        else {
            return super.onKeyDown(keyCode, event)
        }
    }

    /**
     * Overridden onGenericMotionEvent function which calls the
     * XboxController class's onGenericMotionEvent function to process the event
     */
    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {

        if (true == mXboxController.onGenericMotionEvent(event)) {
            return true
        } else {
            return super.onGenericMotionEvent(event)
        }
    }
}
