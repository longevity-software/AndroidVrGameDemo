package longevity.software.vrgamedemo

class Position3Float (x: Float, y: Float, z: Float) {

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
     * Defines how this class and a Vector3Float are added
     */
    operator fun plus(increment: Vector3Float) : Position3Float {
        return Position3Float((mX + increment.X()),
            (mY + increment.Y()),
            (mZ + increment.Z()))
    }

    /**
     * Defines how two Position3Float values are subtracted
     */
    operator fun minus(decrement: Position3Float) : Position3Float {
        return Position3Float((mX - decrement.X()),
            (mY - decrement.Y()),
            (mZ - decrement.Z()))
    }

    /**
     * returns the distance from this position to another position
     */
    fun distanceTo(to: Position3Float) : Float {

        val xDiff = mX - to.X()
        val yDiff = mY - to.Y()
        val zDiff = mZ - to.Z()

        return Math.sqrt((
                (xDiff * xDiff) + (yDiff * yDiff) + (zDiff * zDiff)
                ).toDouble()).toFloat()
    }
}