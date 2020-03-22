package longevity.software.vrgamedemo

class GameCamera (positionX: Float,
                  positionY: Float,
                  positionZ: Float,
                  lookDirectionX: Float,
                  lookDirectionY: Float,
                  lookDirectionZ: Float,
                  upDirectionX: Float,
                  upDirectionY: Float,
                  upDirectionZ: Float) {

    private var mPositionX: Float = positionX
    private var mPositionY: Float = positionY
    private var mPositionZ: Float = positionZ

    private var mLookDirectionX: Float = lookDirectionX
    private var mLookDirectionY: Float = lookDirectionY
    private var mLookDirectionZ: Float = lookDirectionZ

    private var mUpDirectionX: Float = upDirectionX
    private var mUpDirectionY: Float = upDirectionY
    private var mUpDirectionZ: Float = upDirectionZ

    /**
     * Public getter functions.
     */

    fun getPositionX() : Float {
        return mPositionX
    }

    fun getPositionY() : Float {
        return mPositionY
    }

    fun getPositionZ() : Float {
        return mPositionZ
    }

    fun getLookPositionX() : Float {
        return (mPositionX + mLookDirectionX)
    }

    fun getLookPositionY() : Float {
        return (mPositionY + mLookDirectionY)
    }

    fun getLookPositionZ() : Float {
        return (mPositionZ + mLookDirectionZ)
    }

    fun getUpDirectionX() : Float {
        return mUpDirectionX
    }

    fun getUpDirectionY() : Float {
        return mUpDirectionY
    }

    fun getUpDirectionZ() : Float {
        return mUpDirectionZ
    }
}