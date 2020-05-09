package longevity.software.vrgamedemo

interface MoveControlInterface {

    fun getLatestForwardBackwardsDelta(): Float
    fun getLatestLeftRightDelta(): Float
}