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
    private val COLOURS_PER_VERTEX = 3

    private val mVertexStride: Int = COORDS_PER_VERTEX * BYTES_PER_FLOAT
    private val mNormalStride: Int = NORMALS_PER_VERTEX * BYTES_PER_FLOAT
    private val mColourStride: Int = COLOURS_PER_VERTEX * BYTES_PER_FLOAT

    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uVPMatrix;" +
                "uniform mat4 uMMatrix;" +
                "attribute vec3 vPosition;" +
                "attribute vec3 vNormal;" +
                "attribute vec3 vAmbient;" +
                "attribute vec3 vDiffuse;" +
                "attribute vec3 vSpecular;" +
                "attribute float fSpecularExponent;" +
                "attribute float fDissolve;" +
                "varying vec3 vWorldPos;" +
                "varying vec3 vNorm;" +
                "varying vec3 vAmb;" +
                "varying vec3 vDiff;" +
                "varying vec3 vSpec;" +
                "varying float fSpecExp;" +
                "varying float fDiss;" +
                "void main() {" +
                // the matrix must be included as a modifier of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                " gl_Position = uVPMatrix * uMMatrix * vec4(vPosition, 1.0);" +
                // passed to fragment shader for lighting
                " vWorldPos = vec3(uMMatrix * vec4(vPosition, 1.0));" +
                //
                // Pass attributes to the fragment shader
                " vNorm = vNormal;" +
                " vAmb = vAmbient;" +
                " vDiff = vDiffuse;" +
                " vSpec = vSpecular;" +
                " fSpecExp = fSpecularExponent;" +
                " fDiss = fDissolve;" +
                "}"

    private val fragmentShaderCode =
                "precision highp float;" +
                "uniform vec3 uCameraPos;" +
                "uniform vec3 uLightPos;" +
                "uniform vec3 uLightCol;" +
                "varying vec3 vWorldPos;" +
                "varying vec3 vNorm;" +
                "varying vec3 vAmb;" +
                "varying vec3 vDiff;" +
                "varying vec3 vSpec;" +
                "varying float fSpecExp;" +
                "varying float fDiss;" +
                "const float cfToonShaderLevels = 4.0;" +
                //
                "float toonify(float intensity, float levels) {" +
                " float level = floor(intensity * levels);" +
                " return (level / levels);" +
                "}" +
                //
                "void main() {" +
                // variables used for multiple lighting calculations
                " vec3 vNormalisedNorm = normalize(vNorm);" +
                " vec3 vLightDirection = normalize(uLightPos - vWorldPos);" +
                // Ambient lighting
                " float fAmbientStrength = 0.3;" +
                " vec3 vAmbient = (uLightCol * vAmb * fAmbientStrength);" +
                // Diffuse lighting
                " float fDiffuse = max(dot(vNormalisedNorm, vLightDirection), 0.0);" +
                " vec3 vDiffuse = (uLightCol * vDiff * toonify(fDiffuse, cfToonShaderLevels));" +
                //" vec3 vDiffuse = (uLightCol * vDiff * fDiffuse);" +
                // Specular lighting
                " float fSpecularStrength = 0.5;" +
                " vec3 vViewDirection = normalize(uCameraPos - vWorldPos);" +
                " vec3 vReflectionDirection = reflect(-vLightDirection, vNormalisedNorm);" +
                " float fSpecDot = min(max(dot(vReflectionDirection, vViewDirection), 0.0), 1.0);" +
                " float fSpecular = pow(fSpecDot, fSpecExp);" +
                " vec3 vSpecular = (uLightCol * vSpec * fSpecularStrength * toonify(fSpecular, cfToonShaderLevels));" +
                //" vec3 vSpecular = (uLightCol * vSpec * fSpecularStrength * fSpecular);" +
                // Final colour
                " vec3 vFinalColour = (vAmbient + vDiffuse + vSpecular);" +
                " gl_FragColor = vec4(vFinalColour, fDiss);" +
                "}"

    // Parameters which are set in the setParameter function.
    private lateinit var mAmbientColourBuffer: FloatBuffer
    private lateinit var mDiffuseColourBuffer: FloatBuffer
    private lateinit var mSpecularColourBuffer: FloatBuffer
    private lateinit var mSpecularExpBuffer: FloatBuffer
    private lateinit var mDissolveBuffer: FloatBuffer
    private lateinit var mVerticesBuffer: FloatBuffer
    private lateinit var mNormalsBuffer: FloatBuffer
    private lateinit var mIndicesBuffer: ShortBuffer

    // private variables
    private var mProgram: Int
    private var mIndexCount: Int = 0
    private var mParametersHaveBeenSet: Boolean

    // variables used to generate the model matrix
    private var mPosition: Position3Float
    private var mScale: Vector3Float
    private var mRotation: Float

    /**
     * AbstractGameObject init block which creates the program and loads shaders.
     */
    init {

        mParametersHaveBeenSet = false

        // set the initial position, rotation and scale.
        mPosition = Position3Float(0.0f, 0.0f, 0.0f)
        mScale = Vector3Float(1.0f, 1.0f, 1.0f)
        mRotation = 0.0f

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
    fun SetParameters(modelData: ModelData) {

        // set the buffers with the arrays passed in.
        mVerticesBuffer =
            ByteBuffer.allocateDirect(modelData.mVertices.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(modelData.mVertices)
                    position(0)
                }
            }

        mNormalsBuffer =
            ByteBuffer.allocateDirect(modelData.mNormals.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(modelData.mNormals)
                    position(0)
                }
            }

        mIndicesBuffer =
            ByteBuffer.allocateDirect(modelData.mIndices.size * BYTES_PER_SHORT).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    put(modelData.mIndices)
                    position(0)
                }
            }

        mAmbientColourBuffer =
            ByteBuffer.allocateDirect(modelData.mAmbientColour.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(modelData.mAmbientColour)
                    position(0)
                }
            }

        mDiffuseColourBuffer =
            ByteBuffer.allocateDirect(modelData.mDiffuseColour.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(modelData.mDiffuseColour)
                    position(0)
                }
            }

        mSpecularColourBuffer =
            ByteBuffer.allocateDirect(modelData.mSpecularColour.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(modelData.mSpecularColour)
                    position(0)
                }
            }

        mSpecularExpBuffer =
                ByteBuffer.allocateDirect(modelData.mSpecularExponent.size * BYTES_PER_FLOAT).run {
                    order(ByteOrder.nativeOrder())
                    asFloatBuffer().apply {
                        put(modelData.mSpecularExponent)
                        position(0)
                    }
                }

        mDissolveBuffer =
                ByteBuffer.allocateDirect(modelData.mDissolve.size * BYTES_PER_FLOAT).run {
                    order(ByteOrder.nativeOrder())
                    asFloatBuffer().apply {
                        put(modelData.mDissolve)
                        position(0)
                    }
                }

        // set the local copy of the number of indices and the flag
        // indicating that the parameters have been set
        mIndexCount = modelData.mIndices.size
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
    fun draw(vpMatrix: FloatArray, lightPos: Triple<Float, Float, Float>, lightColour: Triple<Float, Float, Float>, cameraPos: Triple<Float, Float, Float>) {

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

                    GLES20.glGetAttribLocation(mProgram, "vAmbient").also {
                        GLES20.glEnableVertexAttribArray(it)
                        GLES20.glVertexAttribPointer(
                            it,
                            COLOURS_PER_VERTEX,
                            GLES20.GL_FLOAT,
                            false,
                            mColourStride,
                            mAmbientColourBuffer
                        )

                        GLES20.glGetAttribLocation(mProgram, "vDiffuse").also {
                            GLES20.glEnableVertexAttribArray(it)
                            GLES20.glVertexAttribPointer(
                                it,
                                COLOURS_PER_VERTEX,
                                GLES20.GL_FLOAT,
                                false,
                                mColourStride,
                                mDiffuseColourBuffer
                            )

                            GLES20.glGetAttribLocation(mProgram, "vSpecular").also {
                                GLES20.glEnableVertexAttribArray(it)
                                GLES20.glVertexAttribPointer(
                                    it,
                                    COLOURS_PER_VERTEX,
                                    GLES20.GL_FLOAT,
                                    false,
                                    mColourStride,
                                    mSpecularColourBuffer
                                )

                                GLES20.glGetAttribLocation(mProgram, "fSpecularExponent").also {
                                    GLES20.glEnableVertexAttribArray(it)
                                    GLES20.glVertexAttribPointer(
                                        it,
                                        1,
                                        GLES20.GL_FLOAT,
                                        false,
                                        BYTES_PER_FLOAT,
                                        mSpecularExpBuffer
                                    )

                                    GLES20.glGetAttribLocation(mProgram, "fDissolve").also {
                                        GLES20.glEnableVertexAttribArray(it)
                                        GLES20.glVertexAttribPointer(
                                            it,
                                            1,
                                            GLES20.GL_FLOAT,
                                            false,
                                            BYTES_PER_FLOAT,
                                            mDissolveBuffer
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
                                            mPosition.X(),
                                            mPosition.Y(),
                                            mPosition.Z()
                                        )

                                        val scaleMatrix = FloatArray(16).also {
                                            Matrix.setIdentityM(it, 0)
                                            Matrix.scaleM(
                                                it,
                                                0,
                                                mScale.X(),
                                                mScale.Y(),
                                                mScale.Z()
                                            )
                                        }

                                        val rotationMatrix = FloatArray(16).also {
                                            Matrix.setRotateM(it, 0, mRotation, 0.0f, 1.0f, 0.0f)
                                        }

                                        val modelMatrix = FloatArray(16).also {
                                            Matrix.multiplyMM(
                                                it,
                                                0,
                                                rotationMatrix,
                                                0,
                                                scaleMatrix,
                                                0
                                            )
                                            Matrix.multiplyMM(it, 0, translationMatrix, 0, it, 0)
                                        }

                                        GLES20.glGetUniformLocation(mProgram, "uVPMatrix")
                                            .also { matrixHandle ->
                                                GLES20.glUniformMatrix4fv(
                                                    matrixHandle,
                                                    1,
                                                    false,
                                                    vpMatrix,
                                                    0
                                                )
                                            }

                                        GLES20.glGetUniformLocation(mProgram, "uMMatrix")
                                            .also { matrixHandle ->
                                                GLES20.glUniformMatrix4fv(
                                                    matrixHandle,
                                                    1,
                                                    false,
                                                    modelMatrix,
                                                    0
                                                )
                                            }

                                        GLES20.glGetUniformLocation(mProgram, "uLightPos")
                                            .also { lightPosHandle ->
                                                GLES20.glUniform3fv(
                                                    lightPosHandle,
                                                    1,
                                                    floatArrayOf(
                                                        lightPos.first,
                                                        lightPos.second,
                                                        lightPos.third
                                                    ),
                                                    0
                                                )
                                            }

                                        GLES20.glGetUniformLocation(mProgram, "uLightCol")
                                            .also { lightColHandle ->
                                                GLES20.glUniform3fv(
                                                    lightColHandle,
                                                    1,
                                                    floatArrayOf(
                                                        lightColour.first,
                                                        lightColour.second,
                                                        lightColour.third
                                                    ),
                                                    0
                                                )
                                            }

                                        GLES20.glGetUniformLocation(mProgram, "uCameraPos")
                                            .also { cameraPosHandle ->
                                                GLES20.glUniform3fv(
                                                    cameraPosHandle,
                                                    1,
                                                    floatArrayOf(
                                                        cameraPos.first,
                                                        cameraPos.second,
                                                        cameraPos.third
                                                    ),
                                                    0
                                                )
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

                                GLES20.glDisableVertexAttribArray(it)
                            }

                            GLES20.glDisableVertexAttribArray(it)
                        }

                        GLES20.glDisableVertexAttribArray(it)
                    }

                    GLES20.glDisableVertexAttribArray(it)
                }

                GLES20.glDisableVertexAttribArray(it)
            }
        }
    }

    /**
     * function to change the position of the GameObject
     */
    fun setPosition(x: Float, y: Float, z: Float) {
        mPosition = Position3Float(x, y, z)
    }

    /**
     * function to get the position
     */
    fun getPosition() : Position3Float {
        return mPosition
    }

    /**
     * function to set the models scale
     */
    fun setScale(scale: Vector3Float) {
        mScale = scale
    }

    /**
     * function to set the Y axis Rotation
     */
    fun setYRotation(rot: Float) {
        mRotation = rot
    }

    /**
     * function to adjust the Y rotation
     */
    fun adjustYRotation(delta: Float) {
        mRotation += delta
    }

    /**
     * function to get the Y rotation
     */
    fun getYRotation() : Float {
        return mRotation
    }
}