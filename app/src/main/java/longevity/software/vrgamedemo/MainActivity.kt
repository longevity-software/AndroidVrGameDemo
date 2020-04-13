package longevity.software.vrgamedemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mDeviceRotationSensor: DeviceRotationSensor
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

        mPlayerControlHub = GameControlHub(mDeviceRotationSensor)

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
}
