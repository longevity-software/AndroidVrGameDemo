package longevity.software.vrgamedemo

class Vector3Float(x: Float, y: Float, z: Float) {

    private var mX = x
    private var mY = y
    private var mZ = z

    fun getX(): Float {
        return mX
    }

    fun getY(): Float {
        return mY
    }

    fun getZ(): Float {
        return mZ
    }
}