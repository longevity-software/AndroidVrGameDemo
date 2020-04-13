package longevity.software.vrgamedemo

import android.opengl.Matrix

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
     * function to return the x position of the camera.
     */
    fun getPositionX() : Float {
        return mPositionX
    }

    /**
     * function to return the y position of the camera.
     */
    fun getPositionY() : Float {
        return mPositionY
    }

    /**
     * function to return the z position of the camera.
     */
    fun getPositionZ() : Float {
        return mPositionZ
    }

    /**
     * function to return the x portion of the point that the camera is looking at.
     */
    fun getLookPositionX() : Float {
        return (mPositionX + mLookDirectionX)
    }

    /**
     * function to return the y portion of the point that the camera is looking at.
     */
    fun getLookPositionY() : Float {
        return (mPositionY + mLookDirectionY)
    }

    /**
     * function to return the z portion of the point that the camera is looking at.
     */
    fun getLookPositionZ() : Float {
        return (mPositionZ + mLookDirectionZ)
    }

    /**
     * function to return the x portion of the up direction.
     */
    fun getUpDirectionX() : Float {
        return mUpDirectionX
    }

    /**
     * function to return the y portion of the up direction.
     */
    fun getUpDirectionY() : Float {
        return mUpDirectionY
    }

    /**
     * function to return the z portion of the up direction.
     */
    fun getUpDirectionZ() : Float {
        return mUpDirectionZ
    }

    /**
     * function to update the direction the camera is
     * looking based on the passed in rotation matrix.
     */
    fun setCameraRotation(rotMat: FloatArray) {

        var forward = FloatArray(16)
        var up = FloatArray(16)

        Matrix.setIdentityM(forward, 0)
        Matrix.setIdentityM(up, 0)

        // set the forward vector to default
        Matrix.translateM(forward, 0, 0.0f, 0.0f, 1.0f)

        // and the up vector to default
        Matrix.translateM(up, 0, 0.0f, 1.0f, 0.0f)

        // TODO - rotate the forward and up matrices using the passed rotation matrix
        // TODO - then update the local look direction
    }
}