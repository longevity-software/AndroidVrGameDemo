package longevity.software.vrgamedemo

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MenuActivity : AppCompatActivity() {

    private val mNEW_GAME_SELECTED = 0
    private val mLOAD_GAME_SELECTED = 1
    private val mSETTINGS_SELECTED = 2

    private lateinit var mLeftNewButton: TextView
    private lateinit var mRightNewButton: TextView
    private lateinit var mLeftLoadButton: TextView
    private lateinit var mRightLoadButton: TextView
    private lateinit var mLeftSettingsButton: TextView
    private lateinit var mRightSettingsButton: TextView

    private lateinit var mXboxController: XboxController
    private lateinit var mLoadGameDatabase: DatabaseHelper

    private var mMenuSelection = mNEW_GAME_SELECTED
    private var mLoadGameSelectable = false

    private var mRunThread = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set to full screen immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE)

        setContentView(R.layout.menu_activity_fullscreen)

        mLeftNewButton = findViewById<TextView>(R.id.leftNewButton)
        mRightNewButton = findViewById<TextView>(R.id.rightNewButton)
        mLeftLoadButton = findViewById<TextView>(R.id.leftLoadButton)
        mRightLoadButton = findViewById<TextView>(R.id.rightLoadButton)
        mLeftSettingsButton = findViewById<TextView>(R.id.leftSettingsButton)
        mRightSettingsButton = findViewById<TextView>(R.id.rightSettingsButton)

        // initialise the xbox controller class.
        mXboxController = XboxController()

        // get the load game database
        mLoadGameDatabase = DatabaseHelper(this)

        if ( mLoadGameDatabase.getSaves().size > 0 ) {
            mLoadGameSelectable = true
        }

        updateSelectedMenuItem()

        val modelLoader = ModelLoader(this)

        // make sure empty tile exists
        val tile = Tile.GetEmptyTile(modelLoader).also {
                it.saveTileToFile(this)
        }
    }

    private fun updateSelectedMenuItem() {

        when(mMenuSelection) {
            mNEW_GAME_SELECTED -> {
                mRightNewButton.setBackgroundColor(resources.getColor(R.color.active_button_colour))
                mLeftNewButton.setBackgroundColor(resources.getColor(R.color.active_button_colour))

                mRightLoadButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftLoadButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mRightSettingsButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftSettingsButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
            }
            mLOAD_GAME_SELECTED -> {

                val loadButtonColour = if (mLoadGameSelectable) { R.color.active_button_colour } else { R.color.active_unslectable_button_colour }

                mRightLoadButton.setBackgroundColor(resources.getColor(loadButtonColour))
                mLeftLoadButton.setBackgroundColor(resources.getColor(loadButtonColour))

                mRightNewButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftNewButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mRightSettingsButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftSettingsButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
            }
            mSETTINGS_SELECTED -> {
                mRightSettingsButton.setBackgroundColor(resources.getColor(R.color.active_button_colour))
                mLeftSettingsButton.setBackgroundColor(resources.getColor(R.color.active_button_colour))

                mRightLoadButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftLoadButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mRightNewButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftNewButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
            }
            else -> {
                mMenuSelection = mNEW_GAME_SELECTED

                mRightNewButton.setBackgroundColor(resources.getColor(R.color.active_button_colour))
                mLeftNewButton.setBackgroundColor(resources.getColor(R.color.active_button_colour))

                mRightLoadButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftLoadButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mRightSettingsButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftSettingsButton.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
            }
        }
    }

    /**
     * overridded onResume function needed to register listeners for the device rotation sensor.
     */
    override fun onResume() {
        super.onResume()

        mRunThread = true

        if ( mLoadGameDatabase.getSaves().size > 0 ) {
            mLoadGameSelectable = true
        }

        val menuThread = Thread(Runnable {

            var lastDpadState = DPadControlInterface.DpadState.NO_DIRECTION
            val DPAD_REPEAT_TIME = 50
            var dpadRepeatCounter = 0

            while(mRunThread) {

                val dpadState = mXboxController.getDpadState()

                if (DPadControlInterface.DpadState.UP_DIRECTION == dpadState) {

                    if (DPadControlInterface.DpadState.UP_DIRECTION == lastDpadState) {

                        dpadRepeatCounter++

                        if (DPAD_REPEAT_TIME == dpadRepeatCounter) {
                            dpadRepeatCounter = 0

                            mMenuSelection--

                            if (mMenuSelection < mNEW_GAME_SELECTED) {
                                mMenuSelection = mSETTINGS_SELECTED
                            }
                        }

                    } else {

                        dpadRepeatCounter = 0

                        mMenuSelection--

                        if (mMenuSelection < mNEW_GAME_SELECTED) {
                            mMenuSelection = mSETTINGS_SELECTED
                        }
                    }

                    updateSelectedMenuItem()
                }
                else if (DPadControlInterface.DpadState.DOWN_DIRECTION == dpadState) {

                    if (DPadControlInterface.DpadState.DOWN_DIRECTION == lastDpadState) {

                        dpadRepeatCounter++

                        if (DPAD_REPEAT_TIME == dpadRepeatCounter) {
                            dpadRepeatCounter = 0

                            mMenuSelection++

                            if (mMenuSelection > mSETTINGS_SELECTED) {
                                mMenuSelection = mNEW_GAME_SELECTED
                            }
                        }

                    } else {

                        dpadRepeatCounter = 0

                        mMenuSelection++

                        if (mMenuSelection > mSETTINGS_SELECTED) {
                            mMenuSelection = mNEW_GAME_SELECTED
                        }
                    }

                    updateSelectedMenuItem()
                } else if (mXboxController.getActionButtonState() == ButtonControlInterface.ButtonState.PRESSED) {
                    when (mMenuSelection) {
                        mNEW_GAME_SELECTED -> {

                            val switchActivityIntent =
                                Intent(this, NewGameActivity::class.java)
                            startActivity(switchActivityIntent)
                        }
                        mLOAD_GAME_SELECTED -> {
                            if (mLoadGameSelectable) {
                                val switchActivityIntent =
                                    Intent(this, LoadActivity::class.java)
                                startActivity(switchActivityIntent)
                            }
                        }
                        mSETTINGS_SELECTED -> {

                        }
                    }
                }

                lastDpadState = dpadState

                Thread.sleep(10)
            }
        })

        menuThread.start()
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
