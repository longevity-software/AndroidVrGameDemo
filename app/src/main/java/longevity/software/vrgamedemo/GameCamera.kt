package longevity.software.vrgamedemo

class GameCamera (position: Position3Float,
                  lookDirection: Vector3Float,
                  upDirection: Vector3Float) {

    private var mPosition = position

    private var mLookDirection = lookDirection

    private var mUpDirection = upDirection

    /**
     * function to return the position of the camera.
     */
    fun getPosition() : Position3Float {
        return mPosition
    }

    /**
     * function to return the the point that the camera is looking at.
     */
    fun getLookPosition() : Position3Float {
        return (mPosition + mLookDirection)
    }

    /**
     * function to return the look direction.
     */
    fun getLookDirection() : Vector3Float {
        return mLookDirection
    }

    /**
     * function to return the up direction.
     */
    fun getUpDirection() : Vector3Float {
        return mUpDirection
    }

    /**
     * function to set the camera position
     */
    fun setPosition(pos: Position3Float) {
        mPosition = pos
    }

    /**
     * function to set the direction to look
     */
    fun setLookDirection(vec: Vector3Float) {
        mLookDirection = vec
    }

    /**
     * function to set the up direction
     */
    fun setUpDirection(vec: Vector3Float) {
        mUpDirection = vec
    }
}