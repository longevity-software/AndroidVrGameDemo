package longevity.software.vrgamedemo

import android.opengl.Matrix

class Player(origin: Position3Float, vis: PlayerVision) {

    private var mPlayerPosition = origin

    private val mPlayerVision = vis

    /**
     * adjusts the players position and vision
     */
    fun adjustPlayer(forwardBackDelta: Float,
                     leftRightDelta: Float,
                     headPitch: Float,
                     headYaw: Float,
                     tileMap: PlayerPositionTileMapInterface) {

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

        val targetVector = Vector3Float(
            translationMatrix.get(12),
            translationMatrix.get(13),
            translationMatrix.get(14)
        )

        mPlayerPosition = tileMap.getPlayerPositionOnTileMap(
            mPlayerPosition,
            targetVector,
            targetVector.getLength()
        )

        // update what the player can see.
        mPlayerVision.setVision(headPitch, headYaw, mPlayerPosition.X(), mPlayerPosition.Z())
    }

    /**
     * Gets the players current position
     */
    fun getPosition() : Position3Float {
        return mPlayerPosition
    }
}