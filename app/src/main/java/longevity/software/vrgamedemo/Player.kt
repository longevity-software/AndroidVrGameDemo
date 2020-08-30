package longevity.software.vrgamedemo

import android.opengl.Matrix

class Player(originX: Float, originY: Float, originZ: Float, vis: PlayerVision) {

    private var mPlayerX = originX
    private var mPlayerY = originY
    private var mPlayerZ = originZ

    private val mPlayerVision = vis

    /**
     * adjusts the players position and vision
     */
    fun adjustPlayer(forwardBackDelta: Float,
                     leftRightDelta: Float,
                     headPitch: Float,
                     headYaw: Float) {

        // we need to take into account which direction we are facing so
        // generate a rotation matrix using the yaw angle
        val moveRotationMatrix = FloatArray(16).also {
            // apply the yaw rotation
            Matrix.setRotateM(it, 0, headYaw, 0.0f, 1.0f, 0.0f)
        }

        // generate the translation matrix and apply the rotation to it
        val translationMatrix =  FloatArray(16).also {
            // set the translation matrix initially to an identity matrix
            Matrix.setIdentityM(it, 0)

            // translate it by the forward/backwards and left/right values
            Matrix.translateM(it, 0, leftRightDelta, 0.0f, forwardBackDelta)

            Matrix.multiplyMM(it, 0, moveRotationMatrix, 0, it, 0)
        }

        // extract and adjust the player position by the x, y and z components
        mPlayerX += translationMatrix.get(12)
        mPlayerY += translationMatrix.get(13)
        mPlayerZ += translationMatrix.get(14)

        // update what the player can see.
        mPlayerVision.setVision(headPitch, headYaw, mPlayerX, mPlayerZ)
    }

    /**
     * Gets the players current position as a Triple
     */
    fun getPosition() : Triple<Float, Float, Float> {
        return Triple(mPlayerX, mPlayerY, mPlayerZ)
    }
}