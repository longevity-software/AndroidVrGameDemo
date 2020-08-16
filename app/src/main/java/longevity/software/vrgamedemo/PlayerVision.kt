package longevity.software.vrgamedemo

import android.opengl.Matrix

class PlayerVision {

    private val PLAYER_Y: Float = 3.0f

    private val mLeftEye = GameCamera(0.0f,3.0f,0.0f,
                             0.0f, 0.0f, 1.0f,
                                0.0f, 1.0f, 0.0f)

    private val mRightEye = GameCamera(0.0f,3.0f,0.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f)

    /**
     * get a copy of the left Eye
     */
    fun getLeftEyeCamera(): GameCamera {
        val left = mLeftEye

        return left
    }

    /**
     * get a copy of the right Eye
     */
    fun getRightEyeCamera(): GameCamera {
        val right = mRightEye

        return right
    }

    /**
     * sets the players vision
     */
    fun setVision(pitch: Float, yaw: Float, x: Float, z: Float) {

        // calculate the rotation matrix
        val lookAtRotationMatrix = FloatArray(16).also {

            val pitchMat = FloatArray(16).also {
                Matrix.setRotateM(it, 0, pitch, 1.0f, 0.0f, 0.0f)
            }

            val yawMat = FloatArray(16).also {
                Matrix.setRotateM(it, 0, yaw, 0.0f, 1.0f, 0.0f)
            }

            // apply the pitch rotation followed by the yaw rotation.
            Matrix.multiplyMM(it, 0, yawMat, 0, pitchMat, 0)
        }

        // this is where the player is looking from where they are.
        val lookAtDirectionMatrix = FloatArray(16).also {
            Matrix.setIdentityM(it, 0)

            // set it to look far into the distance
            Matrix.translateM(it, 0, 0.0f, 0.0f, 100.0f)

            // rotation to where the player is looking
            Matrix.multiplyMM(it, 0, lookAtRotationMatrix, 0, it, 0)
        }

        // this is the up direction (will need changing when roll is taken into account
        val lookUpDirectionMatrix = FloatArray(16).also {
            Matrix.setIdentityM(it, 0)

            // set it straight up initially
            Matrix.translateM(it, 0, 0.0f, 1.0f, 0.0f)

            // rotate it
            Matrix.multiplyMM(it, 0, lookAtRotationMatrix, 0, it, 0)
        }

        // calculate where the two cameras would be at.
        val leftCameraPositionMatrix = FloatArray(16).also {
            Matrix.setIdentityM(it, 0)

            // set the origin position
            Matrix.translateM(it, 0, -1.0f, 0.0f, 0.0f)

            // rotate to where the player is facing
            Matrix.multiplyMM(it, 0, lookAtRotationMatrix, 0, it, 0)
        }

        // calculate where the two cameras would be at.
        val rightCameraPositionMatrix = FloatArray(16).also {
            Matrix.setIdentityM(it, 0)

            // set the origin position
            Matrix.translateM(it, 0, 1.0f, 0.0f, 0.0f)

            // rotate to where the player is facing
            Matrix.multiplyMM(it, 0, lookAtRotationMatrix, 0, it, 0)
        }

        // update the cameras with their new positions
        mLeftEye.setPosition(
            Vector3Float((leftCameraPositionMatrix.get(12) + x),
                leftCameraPositionMatrix.get(13) + PLAYER_Y,
                (leftCameraPositionMatrix.get(14) + z))
        )
        mRightEye.setPosition(
            Vector3Float((rightCameraPositionMatrix.get(12) + x),
                rightCameraPositionMatrix.get(13) + PLAYER_Y,
                (rightCameraPositionMatrix.get(14) + z))
        )

        // set where the camera's are looking
        mLeftEye.setLookDirection(
            Vector3Float(lookAtDirectionMatrix.get(12),
                lookAtDirectionMatrix.get(13),
                lookAtDirectionMatrix.get(14))
        )
        mRightEye.setLookDirection(
            Vector3Float(lookAtDirectionMatrix.get(12),
                lookAtDirectionMatrix.get(13),
                lookAtDirectionMatrix.get(14))
        )

        // and set their up direction
        mLeftEye.setUpDirection(
            Vector3Float(lookUpDirectionMatrix.get(12),
                lookUpDirectionMatrix.get(13),
                lookUpDirectionMatrix.get(14))
        )
        mRightEye.setUpDirection(
            Vector3Float(lookUpDirectionMatrix.get(12),
                lookUpDirectionMatrix.get(13),
                lookUpDirectionMatrix.get(14))
        )
    }
}