package longevity.software.vrgamedemo

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

abstract class AbstractGameObject() {

    // constants.
    private val COORDS_PER_VERTEX = 3
    private val BYTES_PER_FLOAT = 4
    private val BYTES_PER_SHORT = 2
    private val NORMALS_PER_VERTEX = 3

    private val mVertexStride: Int = COORDS_PER_VERTEX * BYTES_PER_FLOAT
    private val mNormalStride: Int = NORMALS_PER_VERTEX * BYTES_PER_FLOAT

    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uVPMatrix;" +
                "uniform mat4 uMMatrix;" +
                "attribute vec3 vPosition;" +
                "attribute vec3 vNormal;" +
                "varying vec3 vWorldPos;" +
                "varying vec3 vNorm;" +
                "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                " gl_Position = uVPMatrix * uMMatrix * vec4(vPosition, 1.0);" +
                // passed to fragment shader for lighting
                " vWorldPos = vec3(uMMatrix * vec4(vPosition, 1.0));" +
                " vNorm = vNormal;" +
                "}"

    private val fragmentShaderCode =
                "precision highp float;" +
                "uniform vec4 uColour;" +
                "uniform vec3 uCameraPos;" +
                "uniform vec3 uLightPos;" +
                "varying vec3 vWorldPos;" +
                "varying vec3 vNorm;" +
                "const float cfToonShaderLevels = 4.0;" +
                //
                "float toonify(float intensity, float levels) {" +
                " float level = floor(intensity * levels);" +
                " return (level / levels);" +
                "}" +
                //
                "void main() {" +
                // variables used for multiple lighting calculations
                " vec3 vLightColour = vec3(1.0, 1.0, 1.0);" +
                " vec3 vNormalisedNorm = normalize(vNorm);" +
                " vec3 vLightDirection = normalize(uLightPos - vWorldPos);" +
                // Ambient lighting
                " float fAmbientStrength = 0.3;" +
                " vec3 vAmbient = (vLightColour * fAmbientStrength);" +
                // Diffuse lighting
                " float fDiffuse = max(dot(vNormalisedNorm, vLightDirection), 0.0);" +
                " vec3 vDiffuse = (vLightColour * toonify(fDiffuse, cfToonShaderLevels));" +
                //" vec3 vDiffuse = (vLightColour * fDiff);" +
                // Specular lighting
                " float fSpecularStrength = 0.5;" +
                " vec3 vViewDirection = normalize(uCameraPos - vWorldPos);" +
                " vec3 vReflectionDirection = reflect(-vLightDirection, vNormalisedNorm);" +
                " float fSpecDot = min(max(dot(vReflectionDirection, vViewDirection), 0.0), 1.0);" +
                " float fSpecular = pow(fSpecDot, 32.0);" +
                " vec3 vSpecular = (vLightColour * fSpecularStrength * toonify(fSpecular, cfToonShaderLevels));" +
                //" vec3 vSpecular = (vLightColour * fSpecularStrength * fSpecPower);" +
                // Final colour
                " vec3 vFinalColour = (vAmbient + vDiffuse + vSpecular) * uColour.xyz;" +
                " gl_FragColor = vec4(vFinalColour, uColour.w);" +
                "}"

    // Parameters which are set in the setParameter function.
    private lateinit var mColourRGBA: FloatArray
    private lateinit var mVerticesBuffer: FloatBuffer
    private lateinit var mNormalsBuffer: FloatBuffer
    private lateinit var mIndicesBuffer: ShortBuffer

    // private variables
    private var mProgram: Int
    private var mIndexCount: Int = 0
    private var mParametersHaveBeenSet: Boolean

    // variables used to generate the model matrix
    private var mPosition: Vector3Float
    private var mScale: Vector3Float
    private var mRotation: Float

    /**
     * AbstractGameObject init block which creates the program and loads shaders.
     */
    init {

        mParametersHaveBeenSet = false

        // set the initial position.
        mPosition = Vector3Float(0.0f, 0.0f, 0.0f)
        mScale = Vector3Float(1.0f, 1.0f, 1.0f)
        mRotation = 90.0f

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
    fun SetParameters(vertices: FloatArray, indices: ShortArray, normals: FloatArray, colours: FloatArray) {

        // set the buffers with the arrays passed in.
        mVerticesBuffer =
            ByteBuffer.allocateDirect(vertices.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(vertices)
                    position(0)
                }
            }

        mNormalsBuffer =
            ByteBuffer.allocateDirect(normals.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(normals)
                    position(0)
                }
            }

        mIndicesBuffer =
            ByteBuffer.allocateDirect(indices.size * BYTES_PER_SHORT).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    put(indices)
                    position(0)
                }
            }

        mColourRGBA = colours

        // set the local copy of the number of indices and the flag
        // indicating that the parameters have been set
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
     * Function which draws the model using the view projection matrix passed if
     * the parameters have been set.
     */
    fun draw(vpMatrix: FloatArray, lightPos: Vector3Float, cameraPos: Vector3Float) {

        if (mParametersHaveBeenSet) {

            GLES20.glUseProgram(mProgram)

            GLES20.glGetAttribLocation(mProgram, "vPosition").also {
                GLES20.glEnableVertexAttribArray(it)
                GLES20.glVertexAttribPointer(
                    it,
                    COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT,
                    false,
                    mVertexStride,
                    mVerticesBuffer
                )

                GLES20.glGetAttribLocation(mProgram, "vNormal").also {
                    GLES20.glEnableVertexAttribArray(it)
                    GLES20.glVertexAttribPointer(
                        it,
                        NORMALS_PER_VERTEX,
                        GLES20.GL_FLOAT,
                        false,
                        mNormalStride,
                        mNormalsBuffer
                    )

                    // generate the model matrix Note Currently this is only a translation
                    val translationMatrix = FloatArray(16)
                    Matrix.setIdentityM(
                        translationMatrix,
                        0
                    )   // ensure we are starting from identity
                    Matrix.translateM(
                        translationMatrix,
                        0,
                        mPosition.getX(),
                        mPosition.getY(),
                        mPosition.getZ()
                    )

                    val scaleMatrix = FloatArray(16).also {
                        Matrix.setIdentityM(it, 0)
                        Matrix.scaleM(it, 0, mScale.getX(), mScale.getY(), mScale.getZ())
                    }

                    val rotationMatrix = FloatArray(16).also {
                        Matrix.setRotateM(it, 0, mRotation, 0.0f, 1.0f, 0.0f);
                    }

                    mRotation = (mRotation + 1.0f) % 360.0f;

                    val modelMatrix = FloatArray(16).also {
                        Matrix.multiplyMM(it, 0, rotationMatrix, 0, scaleMatrix, 0)
                        Matrix.multiplyMM(it, 0, translationMatrix, 0, it, 0)
                    }

                    GLES20.glGetUniformLocation(mProgram, "uVPMatrix").also { matrixHandle ->
                        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, vpMatrix, 0)
                    }

                    GLES20.glGetUniformLocation(mProgram, "uMMatrix").also { matrixHandle ->
                        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, modelMatrix, 0)
                    }

                    GLES20.glGetUniformLocation(mProgram, "uColour").also { colourHandle ->
                        GLES20.glUniform4fv(colourHandle, 1, mColourRGBA, 0)
                    }

                    GLES20.glGetUniformLocation(mProgram, "uLightPos").also { lightPosHandle ->
                        GLES20.glUniform3fv(lightPosHandle, 1, floatArrayOf(lightPos.getX(), lightPos.getY(), lightPos.getZ()), 0)
                    }

                    GLES20.glGetUniformLocation(mProgram, "uCameraPos").also { cameraPosHandle ->
                        GLES20.glUniform3fv(cameraPosHandle, 1, floatArrayOf(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ()), 0)
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

    /**
     * function to change the position of the GameObject
     */
    fun setPosition(pos: Vector3Float) {
        mPosition = pos
    }

    fun setScale(scale: Vector3Float) {
        mScale = scale
    }
}