package longevity.software.vrgamedemo

interface ButtonControlInterface {

    enum class ButtonState {
        IDLE,
        PRESSED,
        HELD,
        RELEASED
    }

    fun getActionButtonState() : ButtonState
    fun getActionButton2State() : ButtonState
    fun getL1ButtonState() : ButtonState
    fun getR1ButtonState() : ButtonState
    fun getOptionsButtonState() : ButtonState
}