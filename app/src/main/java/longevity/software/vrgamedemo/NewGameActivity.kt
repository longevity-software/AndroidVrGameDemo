package longevity.software.vrgamedemo

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NewGameActivity : AppCompatActivity() {

    private val mFIRST_LETTER_SELECTED = 0
    private val mSECOND_LETTER_SELECTED = 1
    private val mTHIRD_LETTER_SELECTED = 2

    private val mNEXT_LETTER_MAP = mapOf<String, String>("A" to "B",
        "B" to "C",
        "C" to "D",
        "D" to "E",
        "E" to "F",
        "F" to "G",
        "G" to "H",
        "H" to "I",
        "I" to "J",
        "J" to "K",
        "K" to "L",
        "L" to "M",
        "M" to "N",
        "N" to "O",
        "O" to "P",
        "P" to "Q",
        "Q" to "R",
        "R" to "S",
        "S" to "T",
        "T" to "U",
        "U" to "V",
        "V" to "W",
        "W" to "X",
        "X" to "Y",
        "Y" to "Z",
        "Z" to "A")

    private val mPREVIOUS_LETTER_MAP = mapOf<String, String>("A" to "Z",
        "B" to "A",
        "C" to "B",
        "D" to "C",
        "E" to "D",
        "F" to "E",
        "G" to "F",
        "H" to "G",
        "I" to "H",
        "J" to "I",
        "K" to "J",
        "L" to "K",
        "M" to "L",
        "N" to "M",
        "O" to "N",
        "P" to "O",
        "Q" to "P",
        "R" to "Q",
        "S" to "R",
        "T" to "S",
        "U" to "T",
        "V" to "U",
        "W" to "V",
        "X" to "W",
        "Y" to "X",
        "Z" to "Y")

    private lateinit var mLeftFirstLetter: TextView
    private lateinit var mRightFirstLetter: TextView
    private lateinit var mLeftSecondLetter: TextView
    private lateinit var mRightSecondLetter: TextView
    private lateinit var mLeftThirdLetter: TextView
    private lateinit var mRightThirdLetter: TextView

    private lateinit var mXboxController: XboxController
    private lateinit var mNewGameDatabase: DatabaseHelper

    private var mLetterSelection = mFIRST_LETTER_SELECTED

    private var mRunThread = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set to full screen immersive mode.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE)

        setContentView(R.layout.activity_new_game)

        mXboxController = XboxController()

        mRightFirstLetter = findViewById(R.id.rightFirstCharacter)
        mRightSecondLetter = findViewById(R.id.rightSecondCharacter)
        mRightThirdLetter = findViewById(R.id.rightThirdCharacter)

        mLeftFirstLetter = findViewById(R.id.leftFirstCharacter)
        mLeftSecondLetter = findViewById(R.id.leftSecondCharacter)
        mLeftThirdLetter = findViewById(R.id.leftThirdCharacter)

        mRightFirstLetter.text = "A"
        mLeftFirstLetter.text = "A"

        mRightSecondLetter.text = "A"
        mLeftSecondLetter.text = "A"

        mRightThirdLetter.text = "A"
        mLeftThirdLetter.text = "A"

        updateLetterSelection()

        mNewGameDatabase = DatabaseHelper(this)
    }

    private fun updateLetterSelection() {

        when(mLetterSelection) {
            mFIRST_LETTER_SELECTED -> {
                mRightFirstLetter.setBackgroundColor(resources.getColor(R.color.active_button_colour))
                mLeftFirstLetter.setBackgroundColor(resources.getColor(R.color.active_button_colour))

                mRightSecondLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftSecondLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mRightThirdLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftThirdLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
            }
            mSECOND_LETTER_SELECTED -> {
                mRightSecondLetter.setBackgroundColor(resources.getColor(R.color.active_button_colour))
                mLeftSecondLetter.setBackgroundColor(resources.getColor(R.color.active_button_colour))

                mRightFirstLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftFirstLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mRightThirdLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftThirdLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
            }
            mTHIRD_LETTER_SELECTED -> {
                mRightThirdLetter.setBackgroundColor(resources.getColor(R.color.active_button_colour))
                mLeftThirdLetter.setBackgroundColor(resources.getColor(R.color.active_button_colour))

                mRightFirstLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftFirstLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mRightSecondLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftSecondLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
            }
            else -> {
                mLetterSelection = mFIRST_LETTER_SELECTED

                mRightFirstLetter.setBackgroundColor(resources.getColor(R.color.active_button_colour))
                mLeftFirstLetter.setBackgroundColor(resources.getColor(R.color.active_button_colour))

                mRightSecondLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftSecondLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mRightThirdLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
                mLeftThirdLetter.setBackgroundColor(resources.getColor(R.color.inactive_button_colour))
            }
        }
    }

    /**
     * overridded onResume function needed to register listeners for the device rotation sensor.
     */
    override fun onResume() {
        super.onResume()

        mRunThread = true

        val menuThread = Thread(Runnable {

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

                            setPreviousLetter()
                        }

                    } else {

                        dpadRepeatCounter = 0

                        setPreviousLetter()
                    }
                } else if (DPadControlInterface.DpadState.DOWN_DIRECTION == dpadState) {

                    if (DPadControlInterface.DpadState.DOWN_DIRECTION == lastDpadState) {

                        dpadRepeatCounter++

                        if (DPAD_UP_DOWN_REPEAT_TIME == dpadRepeatCounter) {
                            dpadRepeatCounter = 0

                            setNextLetter()
                        }

                    } else {

                        dpadRepeatCounter = 0

                        setNextLetter()
                    }
                } else if (DPadControlInterface.DpadState.RIGHT_DIRECTION == dpadState) {
                    if (DPadControlInterface.DpadState.RIGHT_DIRECTION == lastDpadState) {

                        dpadRepeatCounter++

                        if (DPAD_LEFT_RIGHT_REPEAT_TIME == dpadRepeatCounter) {
                            dpadRepeatCounter = 0

                            mLetterSelection++

                            if (mTHIRD_LETTER_SELECTED < mLetterSelection)
                            {
                                mLetterSelection = mFIRST_LETTER_SELECTED
                            }

                            updateLetterSelection()
                        }

                    } else {

                        dpadRepeatCounter = 0

                        mLetterSelection++

                        if (mTHIRD_LETTER_SELECTED < mLetterSelection)
                        {
                            mLetterSelection = mFIRST_LETTER_SELECTED
                        }

                        updateLetterSelection()
                    }
                } else if (DPadControlInterface.DpadState.LEFT_DIRECTION == dpadState) {
                    if (DPadControlInterface.DpadState.LEFT_DIRECTION == lastDpadState) {

                        dpadRepeatCounter++

                        if (DPAD_LEFT_RIGHT_REPEAT_TIME == dpadRepeatCounter) {
                            dpadRepeatCounter = 0

                            mLetterSelection--

                            if (mFIRST_LETTER_SELECTED > mLetterSelection)
                            {
                                mLetterSelection = mTHIRD_LETTER_SELECTED
                            }

                            updateLetterSelection()
                        }

                    } else {

                        dpadRepeatCounter = 0

                        mLetterSelection--

                        if (mFIRST_LETTER_SELECTED > mLetterSelection)
                        {
                            mLetterSelection = mTHIRD_LETTER_SELECTED
                        }

                        updateLetterSelection()
                    }
                } else if (mXboxController.getActionButtonState() == ButtonControlInterface.ButtonState.PRESSED) {

                    val newSaveName = mLeftFirstLetter.text.toString() +
                                                mLeftSecondLetter.text.toString() +
                                                mLeftThirdLetter.text.toString()

                    if (mNewGameDatabase.CreateSaveGame(newSaveName)) {

                        val modelLoader = ModelLoader(this)

                        val startTile = Tile(modelLoader.GRASS_MODEL,
                            "None",
                            modelLoader,
                            newSaveName + "_0_0.vtf",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            1.0f,
                            ArrayList<Triple<String, Position3Float, Float>>()).also {

                            it.saveTileToFile(this)
                        }


                        val switchActivityIntent =
                            Intent(this, MainActivity::class.java).also {
                                it.putExtra("save_name", newSaveName)
                            }
                        startActivity(switchActivityIntent)
                    }
                }

                lastDpadState = dpadState

                Thread.sleep(10)
            }
        })

        menuThread.start()
    }

    fun setNextLetter() {

        when (mLetterSelection) {
            mFIRST_LETTER_SELECTED -> {
                val next = mNEXT_LETTER_MAP.get(mLeftFirstLetter.text)

                if (next != null) {
                    mLeftFirstLetter.text = next
                    mRightFirstLetter.text = next
                } else {
                    mLeftFirstLetter.text = "A"
                    mRightFirstLetter.text = "A"
                }
            }
            mSECOND_LETTER_SELECTED -> {
                val next = mNEXT_LETTER_MAP.get(mLeftSecondLetter.text)

                if (next != null) {
                    mLeftSecondLetter.text = next
                    mRightSecondLetter.text = next
                } else {
                    mLeftSecondLetter.text = "A"
                    mRightSecondLetter.text = "A"
                }
            }
            mTHIRD_LETTER_SELECTED -> {
                val next = mNEXT_LETTER_MAP.get(mLeftThirdLetter.text)

                if (next != null) {
                    mLeftThirdLetter.text = next
                    mRightThirdLetter.text = next
                } else {
                    mLeftThirdLetter.text = "A"
                    mRightThirdLetter.text = "A"
                }
            }
        }
    }

    fun setPreviousLetter() {
        when (mLetterSelection) {
            mFIRST_LETTER_SELECTED -> {
                val next = mPREVIOUS_LETTER_MAP.get(mLeftFirstLetter.text)

                if (next != null) {
                    mLeftFirstLetter.text = next
                    mRightFirstLetter.text = next
                } else {
                    mLeftFirstLetter.text = "A"
                    mRightFirstLetter.text = "A"
                }
            }
            mSECOND_LETTER_SELECTED -> {
                val next = mPREVIOUS_LETTER_MAP.get(mLeftSecondLetter.text)

                if (next != null) {
                    mLeftSecondLetter.text = next
                    mRightSecondLetter.text = next
                } else {
                    mLeftSecondLetter.text = "A"
                    mRightSecondLetter.text = "A"
                }
            }
            mTHIRD_LETTER_SELECTED -> {
                val next = mPREVIOUS_LETTER_MAP.get(mLeftThirdLetter.text)

                if (next != null) {
                    mLeftThirdLetter.text = next
                    mRightThirdLetter.text = next
                } else {
                    mLeftThirdLetter.text = "A"
                    mRightThirdLetter.text = "A"
                }
            }
        }
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