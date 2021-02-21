package longevity.software.vrgamedemo

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoadActivity : AppCompatActivity() {

    private lateinit var mLeftLoadTop: TextView
    private lateinit var mLeftLoadMid: TextView
    private lateinit var mLeftLoadBot: TextView

    private lateinit var mRightLoadTop: TextView
    private lateinit var mRightLoadMid: TextView
    private lateinit var mRightLoadBot: TextView

    private lateinit var mLoads: List<String>
    private var mLoadIndex: Int = -1

    private lateinit var mXboxController: XboxController

    private var mRunThread = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set to full screen immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE)

        setContentView(R.layout.activity_load)

        mLeftLoadTop = findViewById(R.id.leftTopLoad)
        mLeftLoadMid = findViewById(R.id.leftMidLoad)
        mLeftLoadBot = findViewById(R.id.leftBotLoad)

        mRightLoadTop = findViewById(R.id.rightTopLoad)
        mRightLoadMid = findViewById(R.id.rightMidLoad)
        mRightLoadBot = findViewById(R.id.rightBotLoad)

        mXboxController = XboxController()

        val db = DatabaseHelper(this)

        mLoads = db.getSaves()

        if (0 == mLoads.size) {
            mLoadIndex = -1
        }
        else {
            mLoadIndex = 0
        }

        populateLoadList()
    }

    private fun populateLoadList() {

        if ( -1 == mLoadIndex ) {
            mLeftLoadMid.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
            mRightLoadMid.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
        }
        else {
            mLeftLoadMid.setBackgroundColor(resources.getColor(R.color.active_button_colour))
            mRightLoadMid.setBackgroundColor(resources.getColor(R.color.active_button_colour))

            mLeftLoadMid.text = mLoads[mLoadIndex]
            mRightLoadMid.text = mLoads[mLoadIndex]

            if ( mLoadIndex > 0 ) {
                mLeftLoadTop.text = mLoads[mLoadIndex - 1]
                mRightLoadTop.text = mLoads[mLoadIndex - 1]
            }
            else {
                mLeftLoadTop.text = ""
                mRightLoadTop.text = ""
            }

            if ( mLoadIndex < ( mLoads.size - 1 ) ) {
                mLeftLoadBot.text = mLoads[mLoadIndex + 1]
                mRightLoadBot.text = mLoads[mLoadIndex + 1]
            }
            else {
                mLeftLoadBot.text = ""
                mRightLoadBot.text = ""
            }
        }
    }


    /**
     * overridded onResume function needed to register listeners for the device rotation sensor.
     */
    override fun onResume() {
        super.onResume()

        mRunThread = true

        val loadThread = Thread(Runnable {

            var lastDpadState = DPadControlInterface.DpadState.NO_DIRECTION
            val DPAD_UP_DOWN_REPEAT_TIME = 20
            val DPAD_LEFT_RIGHT_REPEAT_TIME = 50
            var dpadRepeatCounter = 0

            while(mRunThread) {

                val dpadState = mXboxController.getDpadState()

                if (DPadControlInterface.DpadState.UP_DIRECTION == dpadState) {

                    if (DPadControlInterface.DpadState.UP_DIRECTION == lastDpadState) {

                        dpadRepeatCounter++

                        if (DPAD_UP_DOWN_REPEAT_TIME == dpadRepeatCounter) {
                            dpadRepeatCounter = 0

                            if ( mLoadIndex > 0 ) {
                                mLoadIndex--
                                populateLoadList()
                            }
                        }

                    } else {

                        dpadRepeatCounter = 0

                        if ( mLoadIndex > 0 ) {
                            mLoadIndex--
                            populateLoadList()
                        }
                    }
                } else if (DPadControlInterface.DpadState.DOWN_DIRECTION == dpadState) {

                    if (DPadControlInterface.DpadState.DOWN_DIRECTION == lastDpadState) {

                        dpadRepeatCounter++

                        if (DPAD_UP_DOWN_REPEAT_TIME == dpadRepeatCounter) {
                            dpadRepeatCounter = 0

                            if ( mLoadIndex < ( mLoads.size - 1 ) ) {
                                mLoadIndex++
                                populateLoadList()
                            }
                        }

                    } else {

                        dpadRepeatCounter = 0

                        if ( mLoadIndex < ( mLoads.size - 1 ) ) {
                            mLoadIndex++
                            populateLoadList()
                        }
                    }
                }
                else if (mXboxController.getActionButtonState() == ButtonControlInterface.ButtonState.PRESSED) {

                    val switchActivityIntent =
                        Intent(this, MainActivity::class.java).also {
                            it.putExtra("save_name", mLoads[mLoadIndex])
                        }
                    startActivity(switchActivityIntent)
                }

                lastDpadState = dpadState

                Thread.sleep(10)
            }
        })

        loadThread.start()
    }

    /**
     * overriden onPause function needed to unregister the device rotation sensors listeners.
     */
    override fun onPause() {
        super.onPause()

        mRunThread = false
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
     * Overridden onKeyUp function which calls the XboxController class's onKeyDown function to process the event
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {

        if ( true == mXboxController.onKeyUp(keyCode, event) ) {
            return true
        }
        else {
            return super.onKeyUp(keyCode, event)
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