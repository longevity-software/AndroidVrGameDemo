package longevity.software.vrgamedemo

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

abstract class AbstractTexturedGameObject {

    // constants
    private val COORDS_PER_VERTEX = 3
    private val UVS_PER_VERTEX = 2
    private val BYTES_PER_FLOAT = 4
    private val BYTES_PER_SHORT = 2

    private val vertexStride: Int = COORDS_PER_VERTEX * BYTES_PER_FLOAT
    private val uvStride: Int = UVS_PER_VERTEX * BYTES_PER_FLOAT

    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "attribute vec2 vTexture;" +
                "varying vec2 vTex;" +
                "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                "gl_Position = uMVPMatrix * vPosition;" +
                "vTex = vTexture;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform sampler2D uTexture;" +
                "varying vec2 vTex;" +
                "void main() {" +
                "  gl_FragColor = texture2D(uTexture, vTex);" +
                "}"

    // parameters set by the setParameters function
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indicesBuffer: ShortBuffer
    private lateinit var uvBuffer: FloatBuffer

    // private variables
    private var mProgram: Int
    private var mIndexCount: Int = 0
    private var mParametersHaveBeenSet: Boolean

    // variables used to generate the model matrix
    private var mPosition: Vector3Float

    /**
     * AbstractTexturedGameObject init block which loads shaders and creates the program.
     */
    init {

        mParametersHaveBeenSet = false

        // set the initial position.
        mPosition = Vector3Float(0.0f, 0.0f, 0.0f)

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    /**
     * Function to set the parameters which are specific to each class which
     * inherits from this abstract class
     */
    fun SetParameters(vertices: FloatArray, indices: ShortArray, uvs: FloatArray) {
        vertexBuffer =
            ByteBuffer.allocateDirect(vertices.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(vertices)
                    position(0)
                }
            }

        indicesBuffer =
            ByteBuffer.allocateDirect(indices.size * BYTES_PER_SHORT).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    put(indices)
                    position(0)
                }
            }

        uvBuffer = ByteBuffer.allocateDirect(uvs.size * BYTES_PER_FLOAT).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(uvs)
                position(0)
            }
        }

        mIndexCount = indices.size
        mParametersHaveBeenSet = true
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
     * Function which draws the model using the model view projection matrix passed
     * and textures it with the passed texture reference if the parameters have been set.
     */
    fun draw(vpMatrix: FloatArray, texture: Int) {

        if (mParametersHaveBeenSet) {

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

                GLES20.glGetAttribLocation(mProgram, "vTexture").also {
                    GLES20.glEnableVertexAttribArray(it)
                    GLES20.glVertexAttribPointer(
                        it,
                        UVS_PER_VERTEX,
                        GLES20.GL_FLOAT,
                        false,
                        uvStride,
                        uvBuffer
                    )

                    GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)

                    GLES20.glGetUniformLocation(mProgram, "uTexture").also {
                        uvHandle -> GLES20.glUniform1i(uvHandle, 0)
                    }

                    // generate the model matrix Note Currently this is only a translation
                    val translationMatrix = FloatArray(16)
                    Matrix.setIdentityM(translationMatrix, 0)   // ensure we are starting from identity
                    Matrix.translateM(translationMatrix, 0, mPosition.X(), mPosition.Y(), mPosition.Z())

                    // add the model matrix to the view projection matrix to create the model view projection matrix.
                    val mvpMatrix = FloatArray(16)
                    Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, translationMatrix, 0)

                    GLES20.glGetUniformLocation(mProgram, "uMVPMatrix").also {
                            matrixHandle -> GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0)
                    }

                    GLES20.glDrawElements(
                        GLES20.GL_TRIANGLES,
                        mIndexCount,
                        GLES20.GL_UNSIGNED_SHORT,
                        indicesBuffer
                    )

                    GLES20.glDisableVertexAttribArray(it)
                }

                GLES20.glDisableVertexAttribArray(it)
            }
        }
    }
}