package longevity.software.vrgamedemo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mDeviceRotationSensor: DeviceRotationSensor
    private lateinit var mXboxController: XboxController
    private lateinit var mPlayerControlHub: GameControlHub
    private lateinit var mVrGlSurfaceView: VrGlSurfaceView
    private lateinit var mGameLoop: GameLoop
    private lateinit var mPlayer: Player
    private lateinit var mPlayerVision: PlayerVision
    private lateinit var mSkyBox: SkyBox
    private lateinit var mModelLoader: ModelLoader
    private lateinit var mSunLight: SunLight

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

        // instantiate the player vision class which is used to render the scene
        mPlayerVision = PlayerVision()

        // instantiate the sky box and set default bitmaps
        mSkyBox = SkyBox(
                    BitmapFactory.decodeResource(getResources(), R.drawable.front),
                    BitmapFactory.decodeResource(getResources(), R.drawable.back),
                    BitmapFactory.decodeResource(getResources(), R.drawable.left),
                    BitmapFactory.decodeResource(getResources(), R.drawable.right),
                    BitmapFactory.decodeResource(getResources(), R.drawable.top),
                    BitmapFactory.decodeResource(getResources(), R.drawable.bottom))

        // initialise the player instance
        mPlayer = Player(0.0f, 1.0f, 0.0f, mPlayerVision)

        // load all the models
        mModelLoader = ModelLoader(this)

        // initialise the sunlight
        mSunLight = SunLight(mModelLoader)

        // create local insance of the VrGlSurfaceView so we can set it
        // as the content view and we can pass it to the GameLoop
        mVrGlSurfaceView = VrGlSurfaceView(this, mPlayerVision, mSkyBox, mModelLoader, mSunLight)

        // set the content to our VrGlSurfaceView
        setContentView(mVrGlSurfaceView)

        // Initialise the game loop
        mGameLoop = GameLoop(mVrGlSurfaceView, mPlayerControlHub, mPlayer, mSunLight)
    }

    /**
     * overridded onResume function needed to register listeners for the device rotation sensor.
     */
    override fun onResume() {
        super.onResume()

        mDeviceRotationSensor.registerListeners()

        // start the game loop as a thread
        Thread(mGameLoop).start()
    }

    /**
     * overriden onPause function needed to unregister the device rotation sensors listeners.
     */
    override fun onPause() {
        super.onPause()

        // stop the game loop from processing any input and rendering the scene
        mGameLoop.stopGameLoop()

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
