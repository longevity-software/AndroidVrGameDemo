package longevity.software.vrgamedemo

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20
import java.nio.*

class SkyBox(front: Bitmap, back: Bitmap, left: Bitmap, right: Bitmap, top: Bitmap, bottom: Bitmap) {

    // constants.
    private val COORDS_PER_VERTEX = 3
    private val BYTES_PER_FLOAT = 4
    private val BYTES_PER_PIXEL = 3
    private val BYTES_PER_SHORT = 2

    private val vertexStride: Int = COORDS_PER_VERTEX * BYTES_PER_FLOAT

    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec3 vPosition;" +
                "varying vec3 vTextureCoords;" +
                "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                " vec4 pos = uMVPMatrix * vec4(vPosition.xyz, 1.0);" +      // calculate the position
                // set the z component to the w value so the skybox is always at the furthest possible distance
                "  gl_Position = vec4(pos.x, pos.y, pos.w, pos.w);" +
                " vTextureCoords = vPosition.xyz;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "varying vec3 vTextureCoords;" +
                "uniform samplerCube skybox;" +
                "uniform vec3 vLightColour;" +
                "void main() {" +
                " vec4 texture = textureCube(skybox, vTextureCoords);" +
                "  gl_FragColor = texture * vec4(vLightColour, 1.0);" +
                "}"

    // buffers used to render the skybox.
    private var mVertexBuffer: FloatBuffer
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
        0, 3, 7,    //
        0, 7, 4,    // top
        1, 5, 6,    //
        1, 6, 2     // bottom
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
            TextureData(right.width, right.height, getPixelData(right)),
            TextureData(left.width, left.height, getPixelData(left)),
            TextureData(top.width, top.height, getPixelData(top)),
            TextureData(bottom.width, bottom.height, getPixelData(bottom)),
            TextureData(back.width, back.height, getPixelData(back)),
            TextureData(front.width, front.height, getPixelData(front))
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

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
    }

    /**
     * function to get pixel data from a bitmap
     */
    private fun getPixelData(bmp: Bitmap): ByteBuffer {
        val bb = ByteBuffer.allocateDirect(bmp.height * bmp.width * BYTES_PER_PIXEL).also {

            // loop through all pixels
            for ( y in 0 .. ( bmp.height - 1 ) ) {
                for ( x in 0 .. ( bmp.width - 1 ) ) {
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
    fun draw(vpMatrix: FloatArray, lightColour: Triple<Float, Float, Float>) {
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

            GLES20.glGetUniformLocation(mProgram, "uMVPMatrix").also { matrixHandle ->
                GLES20.glUniformMatrix4fv(matrixHandle, 1, false, vpMatrix, 0)
            }

            GLES20.glGetUniformLocation(mProgram, "vLightColour").also { lightColourHandle ->
                GLES20.glUniform3fv(lightColourHandle, 1, floatArrayOf(lightColour.first, lightColour.second, lightColour.third), 0)
            }

            GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, mTextureBuffer[0])

            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                mIndexCount,
                GLES20.GL_UNSIGNED_SHORT,
                mIndicesBuffer
            )

            GLES20.glDisableVertexAttribArray(it)
        }
    }
}