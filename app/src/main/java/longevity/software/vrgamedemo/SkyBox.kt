package longevity.software.vrgamedemo

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20
import java.nio.*

class SkyBox(front: Bitmap, back: Bitmap, left: Bitmap, right: Bitmap, top: Bitmap, bottom: Bitmap) {

    // constants.
    private val COORDS_PER_VERTEX = 3
    private val COLOURS_PER_VERTEX = 4
    private val BYTES_PER_FLOAT = 4
    private val BYTES_PER_PIXEL = 3
    private val BYTES_PER_SHORT = 2

    private val vertexStride: Int = COORDS_PER_VERTEX * BYTES_PER_FLOAT
    private val colourStride: Int = COLOURS_PER_VERTEX * BYTES_PER_FLOAT

    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec3 vPosition;" +
                "attribute vec4 vColour;" +
                "varying vec4 vCol;" +
                "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                " vec4 pos = uMVPMatrix * vec4(vPosition.xyz, 1.0);" +      // calculate the position
                // set the z component to the w value so the skybox is always at the furthest possible distance
                "  gl_Position = vec4(pos.x, pos.y, pos.w, pos.w);" +
                " vCol = vColour;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "varying vec4 vCol;" +
                "void main() {" +
                "  gl_FragColor = vCol;" +
                "}"

    // buffers used to render the skybox.
    private var mVertexBuffer: FloatBuffer
    private var mColourBuffer: FloatBuffer
    private var mIndicesBuffer: ShortBuffer
    private var mTextureBuffer: IntBuffer
    private var mTextureData: Array<TextureData>

    // private variables
    private var mProgram: Int = 0
    private var mIndexCount: Int = 0

    private val vertexCoordsArray: FloatArray = floatArrayOf(    // in counterclockwise order:
        1.0f, 1.0f, 1.0f,       // top front left       0
        1.0f, -1.0f, 1.0f,       // bottom front left    1
        -1.0f, -1.0f, 1.0f,      // bottom front right   2
        -1.0f, 1.0f, 1.0f,      // top front right      3
        1.0f, 1.0f, -1.0f,       // top rear left       4
        1.0f, -1.0f, -1.0f,       // bottom rear left    5
        -1.0f, -1.0f, -1.0f,      // bottom rear right   6
        -1.0f, 1.0f, -1.0f       // top rear right      7
    )

    private val indicesArray: ShortArray = shortArrayOf(
        0, 1, 2,    //
        0, 2, 3,    // front
        0, 5, 1,    //
        0, 4, 5,    // left
        4, 6, 5,    //
        4, 7, 6,    // rear
        7, 2, 6,    //
        7, 3, 2,    // right
        0, 7, 3,    //
        0, 4, 7,    // top
        1, 6, 5,    //
        1, 2, 6     // bottom
    )

    private val colourRGBA: FloatArray =  floatArrayOf(
        0.76953125f, 0.22265625f, 0.63671875f, 1.0f,
        0.22265625f, 0.63671875f, 0.76953125f, 1.0f,
        0.22265625f, 0.63671875f, 0.76953125f, 1.0f,
        0.76953125f, 0.22265625f, 0.63671875f, 1.0f,
        0.76953125f, 0.22265625f, 0.63671875f, 1.0f,
        0.22265625f, 0.63671875f, 0.76953125f, 1.0f,
        0.22265625f, 0.63671875f, 0.76953125f, 1.0f,
        0.76953125f, 0.22265625f, 0.63671875f, 1.0f
        )

    /**
     * AbstractGameObject init block which creates the program and loads shaders.
     */
    init {

        // set the buffers with the arrays passed in.
        mVertexBuffer =
            ByteBuffer.allocateDirect(vertexCoordsArray.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(vertexCoordsArray)
                    position(0)
                }
            }

        mColourBuffer =
            ByteBuffer.allocateDirect(colourRGBA.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(colourRGBA)
                    position(0)
                }
            }

        mIndicesBuffer =
            ByteBuffer.allocateDirect(indicesArray.size * BYTES_PER_SHORT).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    put(indicesArray)
                    position(0)
                }
            }

        // set the local copy of the number of indices and the flag
        // indicating that the parameters have been set
        mIndexCount = indicesArray.size

        // allocate the texture buffer
        mTextureBuffer = IntBuffer.allocate(1)

        // get the texture data
        mTextureData = arrayOf(
            TextureData(front.width, front.height, getPixelData(front)),
            TextureData(back.width, back.height, getPixelData(back)),
            TextureData(left.width, left.height, getPixelData(left)),
            TextureData(right.width, right.height, getPixelData(right)),
            TextureData(top.width, top.height, getPixelData(top)),
            TextureData(bottom.width, bottom.height, getPixelData(bottom))
        )
    }

    /**
     * Function used to load a shader
     */
    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also{
                shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    /**
     * Function to initialise the rendering program
     * Note: Must be called in the renderers onSurfaceCreated method.
     */
    fun initialiseSkyBox() {

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        // set up the texturing
        GLES20.glGenTextures(1, mTextureBuffer)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, mTextureBuffer[0])

        // integer texture targets
        val textureTargets: IntArray = intArrayOf(
            GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
            GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
            GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
            GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y,
            GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
            GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
        )

        // loop for all images, use integer from 0 to 5 as multiple arrays are being indexed.
        for ( i in 0 .. ( textureTargets.size - 1 ) ) {

            GLES20.glTexImage2D(
                textureTargets[i],
                0,
                GLES20.GL_RGB,
                mTextureData[i].width(),
                mTextureData[i].height(),
                0,
                GLES20.GL_RGB,
                GLES20.GL_UNSIGNED_BYTE,
                mTextureData[i].data()
            )
        }
    }

    /**
     * function to get pixel data from a bitmap
     */
    private fun getPixelData(bmp: Bitmap): ByteBuffer {
        val bb = ByteBuffer.allocateDirect(bmp.height * bmp.width * BYTES_PER_PIXEL).also {

            // loop through all pixels
            for ( x in 0 .. ( bmp.width - 1 ) ) {
                for ( y in 0 .. ( bmp.height - 1 ) ) {

                    // get this pixel
                    val pixel = bmp.getPixel(x, y)

                    // and put its RGB values in the ByteBuffer
                    it.put(Color.red(pixel).toByte())   // Red
                    it.put(Color.green(pixel).toByte()) // Green
                    it.put(Color.blue(pixel).toByte())  // Blue
                }
            }

            // reset the position back to 0
            it.position(0)
        }

        return bb
    }

    /**
     * draw function
     */
    fun draw(vpMatrix: FloatArray) {
        GLES20.glUseProgram(mProgram)

        GLES20.glGetAttribLocation(mProgram, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                mVertexBuffer
            )

            GLES20.glGetAttribLocation(mProgram, "vColour").also {
                GLES20.glEnableVertexAttribArray(it)
                GLES20.glVertexAttribPointer(
                    it,
                    COLOURS_PER_VERTEX,
                    GLES20.GL_FLOAT,
                    false,
                    colourStride,
                    mColourBuffer
                )

                GLES20.glGetUniformLocation(mProgram, "uMVPMatrix").also { matrixHandle ->
                    GLES20.glUniformMatrix4fv(matrixHandle, 1, false, vpMatrix, 0)
                }

                GLES20.glDrawElements(
                    GLES20.GL_TRIANGLES,
                    mIndexCount,
                    GLES20.GL_UNSIGNED_SHORT,
                    mIndicesBuffer
                )

                GLES20.glDisableVertexAttribArray(it)
            }

            GLES20.glDisableVertexAttribArray(it)
        }
    }
}