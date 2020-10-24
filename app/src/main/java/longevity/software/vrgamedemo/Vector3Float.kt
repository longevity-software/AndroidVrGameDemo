package longevity.software.vrgamedemo

class Vector3Float(x: Float, y: Float, z: Float) {

    private var mX = x
    private var mY = y
    private var mZ = z

    /**
     * returns the local X value
     */
    fun X(): Float {
        return mX
    }

    /**
     * returns the local Y value
     */
    fun Y(): Float {
        return mY
    }

    /**
     * returns the local Z value
     */
    fun Z(): Float {
        return mZ
    }

    /**
     * Normalises the vector
     */
    fun normalise() {
        val divisor = getLength()

        mX /= divisor
        mY /= divisor
        mZ /= divisor
    }

    /**
     * returns a copy of the vector which has been normalised
     */
    fun getNormalised() : Vector3Float {
        val divisor = getLength()

        return Vector3Float(
            (mX / divisor),
            (mY / divisor),
            (mZ / divisor)
        )
    }

    /**
     * returns the length of the vector
     */
    fun getLength() : Float {
        return Math.sqrt(
                    ((mX * mX) +
                    (mY * mY) +
                    (mZ * mZ)).toDouble()
                ).toFloat()
    }

    /**
     * Defines how a Vector3Float is multiplied by a scalar float
     */
    operator fun times(multiplier: Float) : Vector3Float {
        return Vector3Float((mX * multiplier),
            (mY * multiplier),
            (mZ * multiplier))
    }
}