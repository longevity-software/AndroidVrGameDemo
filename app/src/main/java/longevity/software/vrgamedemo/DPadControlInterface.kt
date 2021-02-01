package longevity.software.vrgamedemo

interface DPadControlInterface {

    enum class DpadState {
        NO_DIRECTION,
        UP_DIRECTION,
        UP_RIGHT_DIRECTION,
        RIGHT_DIRECTION,
        DOWN_RIGHT_DIRECTION,
        DOWN_DIRECTION,
        DOWN_LEFT_DIRECTION,
        LEFT_DIRECTION,
        UP_LEFT_DIRECTION
    }

    fun getDpadState() : DpadState
}