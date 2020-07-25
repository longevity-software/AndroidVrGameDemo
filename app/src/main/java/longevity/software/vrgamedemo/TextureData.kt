package longevity.software.vrgamedemo

import java.nio.ByteBuffer

class TextureData(width: Int, height: Int, data: ByteBuffer) {

    private val mWidth = width
    private val mHeight = height

    private val mData = data

    /**
     * returns local constant width
     */
    fun width(): Int {
        return mWidth
    }

    /**
     * returns local constant height
     */
    fun height(): Int {
        return mHeight
    }

    /**
     * returns local constant data
     */
    fun data(): ByteBuffer {
        return mData
    }
}