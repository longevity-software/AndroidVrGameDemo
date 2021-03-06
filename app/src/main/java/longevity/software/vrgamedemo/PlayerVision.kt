package longevity.software.vrgamedemo

import android.opengl.Matrix

class PlayerVision {

    private val PLAYER_Y: Float = 3.0f

    private val mLeftEye = GameCamera(Position3Float(0.0f,PLAYER_Y,0.0f),
        Vector3Float(0.0f, 0.0f, 1.0f),
        Vector3Float(0.0f, 1.0f, 0.0f))

    private val mRightEye = GameCamera(Position3Float(0.0f,PLAYER_Y,0.0f),
        Vector3Float(0.0f, 0.0f, 1.0f),
        Vector3Float(0.0f, 1.0f, 0.0f))

    private var mHalfEyeDistance = 0.1f
    private var mLookDistance = 100.0f

    private var mLookStraightAhead = true

    private var mLookDirection = Vector3Float(0.0f, 0.0f, -1.0f)
    private var mPlayerPosition = Position3Float(0.0f, PLAYER_Y, 0.0f)

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
            Matrix.translateM(it, 0, 0.0f, 0.0f, -mLookDistance)

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
            Matrix.translateM(it, 0, -mHalfEyeDistance, 0.0f, 0.0f)

            // rotate to where the player is facing
            Matrix.multiplyMM(it, 0, lookAtRotationMatrix, 0, it, 0)
        }

        // calculate where the two cameras would be at.
        val rightCameraPositionMatrix = FloatArray(16).also {
            Matrix.setIdentityM(it, 0)

            // set the origin position
            Matrix.translateM(it, 0, mHalfEyeDistance, 0.0f, 0.0f)

            // rotate to where the player is facing
            Matrix.multiplyMM(it, 0, lookAtRotationMatrix, 0, it, 0)
        }

        // update the cameras with their new positions
        mLeftEye.setPosition(
            Position3Float((leftCameraPositionMatrix.get(12) + x),
                leftCameraPositionMatrix.get(13) + PLAYER_Y,
                (leftCameraPositionMatrix.get(14) + z))
        )
        mRightEye.setPosition(
            Position3Float((rightCameraPositionMatrix.get(12) + x),
                rightCameraPositionMatrix.get(13) + PLAYER_Y,
                (rightCameraPositionMatrix.get(14) + z))
        )

        // set where the camera's are looking
        if (mLookStraightAhead) {
            val lookPos = Vector3Float(
                x + lookAtDirectionMatrix.get(12),
                PLAYER_Y + lookAtDirectionMatrix.get(13),
                z + lookAtDirectionMatrix.get(14)
            )

            mLeftEye.setLookDirection(
                Vector3Float(
                    lookPos.X() - mLeftEye.getPosition().X(),
                    lookPos.Y() - mLeftEye.getPosition().Y(),
                    lookPos.Z() - mLeftEye.getPosition().Z()
                )
            )
            mRightEye.setLookDirection(
                Vector3Float(
                    lookPos.X() - mRightEye.getPosition().X(),
                    lookPos.Y() - mRightEye.getPosition().Y(),
                    lookPos.Z() - mRightEye.getPosition().Z()
                )
            )
        }
        else {
            mLeftEye.setLookDirection(
                Vector3Float(
                    lookAtDirectionMatrix.get(12),
                    lookAtDirectionMatrix.get(13),
                    lookAtDirectionMatrix.get(14)
                )
            )
            mRightEye.setLookDirection(
                Vector3Float(
                    lookAtDirectionMatrix.get(12),
                    lookAtDirectionMatrix.get(13),
                    lookAtDirectionMatrix.get(14)
                )
            )
        }

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

        // set the variables used to determine where the player is looking on the y axis
        mPlayerPosition = Position3Float(x, PLAYER_Y, z)
        mLookDirection = Vector3Float(lookAtDirectionMatrix.get(12),
            lookAtDirectionMatrix.get(13),
            lookAtDirectionMatrix.get(14))
    }

    /**
     * Function to decrease the eye distance
     */
    fun decreaseEyeDistance(sub: Float) {
        mHalfEyeDistance -= sub

        // cap it at 0
        if (mHalfEyeDistance < 0.0f) {
            mHalfEyeDistance = 0.0f
        }
    }

    /**
     * Function to increase the eye distance
     */
    fun increaseEyeDistance(add: Float) {
        mHalfEyeDistance += add
    }

    /**
     * Function to change where the two cameras are looking
     */
    fun toggleCameraLookAt() {
        mLookStraightAhead = if (mLookStraightAhead) false else true
    }

    /**
     * Gets the position on the 0 Y axis that the player is looking
     */
    fun GetPositionPlayerIsLookingOnTheYAxisPlane() : Pair<Boolean, Position3Float> {

        // check the player is looking down
        if ( mLookDirection.Y() < 0.0f ) {

            val vectorToGround = Vector3Float(0.0f, -mPlayerPosition.Y(), 0.0f)

            // get the angle between the two vectors
            val angle = vectorToGround.AngleToVector(mLookDirection)

            // now calculate the length of the look Direction required to contact the ground using pythagoras
            val length = vectorToGround.getLength() / Math.cos(angle.toDouble())

            val finalPos = mPlayerPosition + (mLookDirection.getNormalised() * length.toFloat())

            return Pair(true, finalPos)
        }

        return Pair(false, Position3Float(0.0f, 0.0f, 0.0f))
    }
}