package longevity.software.vrgamedemo

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class SkyBox() {

    // constants.
    private val COORDS_PER_VERTEX = 3
    private val BYTES_PER_FLOAT = 4
    private val BYTES_PER_SHORT = 2

    private val vertexStride: Int = COORDS_PER_VERTEX * BYTES_PER_FLOAT

    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "  gl_Position = uMVPMatrix * vPosition;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    // Parameters which are set in the setParameter function.
    private var vertexBuffer: FloatBuffer
    private var indicesBuffer: ShortBuffer

    // private variables
    private var mProgram: Int = 0
    private var mIndexCount: Int = 0
    private lateinit var mScaleMatrix: FloatArray

    private val vertexCoordsArray: FloatArray = floatArrayOf(    // in counterclockwise order:
        1.0f, 1.0f, 1.0f,       // top front left
        1.0f, 0.0f, 1.0f,       // bottom front left
        -1.0f, 0.0f, 1.0f,      // bottom front right
        -1.0f, 1.0f, 1.0f       // top front right
    )

    private val indicesArray: ShortArray = shortArrayOf(
        0, 1, 2,
        0, 2, 3
    )

    private val colourRGBA = floatArrayOf(0.22265625f, 0.63671875f, 0.76953125f, 1.0f)

    /**
     * AbstractGameObject init block which creates the program and loads shaders.
     */
    init {

        // set the buffers with the arrays passed in.
        vertexBuffer =
            ByteBuffer.allocateDirect(vertexCoordsArray.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(vertexCoordsArray)
                    position(0)
                }
            }

        indicesBuffer =
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

        // set the initial scale to 1
        mScaleMatrix = FloatArray(16).also {
            Matrix.setIdentityM(it, 0)
        }
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

    fun initialiseSkyBox(dist: Float) {

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        Matrix.setIdentityM(mScaleMatrix, 0)

        val SCALE = (dist * 0.99f)

        Matrix.scaleM(mScaleMatrix, 0,  SCALE, SCALE, SCALE)
    }

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
                vertexBuffer
            )

            GLES20.glGetUniformLocation(mProgram, "vColor").also { colourHandle ->
                GLES20.glUniform4fv(colourHandle, 1, colourRGBA, 0)
            }

            // scale the skybox.
            Matrix.multiplyMM(vpMatrix, 0, vpMatrix, 0, mScaleMatrix, 0)

            GLES20.glGetUniformLocation(mProgram, "uMVPMatrix").also { matrixHandle ->
                GLES20.glUniformMatrix4fv(matrixHandle, 1, false, vpMatrix, 0)
            }

            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                mIndexCount,
                GLES20.GL_UNSIGNED_SHORT,
                indicesBuffer
            )
            GLES20.glDisableVertexAttribArray(it)
        }
    }
}