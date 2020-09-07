package longevity.software.vrgamedemo

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class SunLight(modelLoader: ModelLoader) {

    private val mModelLoader = modelLoader

    // constants.
    private val COORDS_PER_VERTEX = 3
    private val BYTES_PER_FLOAT = 4
    private val BYTES_PER_SHORT = 2
    private val mDefaultLightPosition = Triple(0.0f, -100.0f, 0.0f)

    private val vertexStride: Int = COORDS_PER_VERTEX * BYTES_PER_FLOAT

    private val midDayColour = Triple(1.0f, 1.0f, 1.0f)
    private val duskOrDawnColour = Triple(1.0f, 0.5f, 0.0f)
    private val earlyLateDayColour = Triple(1.0f, 1.0f, 0.0f)
    private val midNightColour = Triple(0.3f, 0.3f, 0.3f)

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
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indicesBuffer: ShortBuffer

    // private variables
    private var mLightColour = midNightColour
    private var mSunPosition = mDefaultLightPosition

    private var mProgram: Int = 0
    private var mIndexCount: Int = 0
    private var mParametersHaveBeenSet: Boolean = false

    /**
     * Function to initialise the sunlight, must be called within an opengl context
     */
    fun initialise() {

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        val sunModel = mModelLoader.getModelData(mModelLoader.SUN_MODEL)

        // set the buffers with the arrays passed in.
        vertexBuffer =
            ByteBuffer.allocateDirect(sunModel.mVertices.size * BYTES_PER_FLOAT).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(sunModel.mVertices)
                    position(0)
                }
            }

        indicesBuffer =
            ByteBuffer.allocateDirect(sunModel.mIndices.size * BYTES_PER_SHORT).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    put(sunModel.mIndices)
                    position(0)
                }
            }

        mLightColour = midNightColour

        // set the local copy of the number of indices and the flag
        // indicating that the parameters have been set
        mIndexCount = sunModel.mIndices.size
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
     * Function which updates the position and colour based on the time of day
     */
    fun UpdateSunLight(percentThroughDay: Float, playerPos: Triple<Float, Float, Float>) {

        val sunAngle = (360.0f * percentThroughDay)

        // work out the position
        val sunPos = FloatArray(16).also {
            Matrix.setIdentityM(it, 0)

            // make sure the sun is straight up
            Matrix.translateM(it, 0, mDefaultLightPosition.first, mDefaultLightPosition.second, mDefaultLightPosition.third)

            val sunRot = FloatArray(16).also {
                Matrix.setRotateM(it, 0, sunAngle, 1.0f, 0.0f, 0.0f)
            }

            // rotate it by the time of day
            Matrix.multiplyMM(it, 0, sunRot, 0, it, 0)
        }

        // set the position
        mSunPosition = Triple((playerPos.first + sunPos[12]), (playerPos.second + sunPos[13]), (playerPos.third + sunPos[14]))

        val sunAngleInt = sunAngle.toInt()

        // set the light colour based on the angle
        mLightColour = when (sunAngleInt) {
            in 90..99 -> {
                val interp = ((10 - (100 - sunAngleInt)).toFloat() / 10.0f)
                interpolateRGBColour(midNightColour, duskOrDawnColour, interp)
            }
            in 100..119 -> {
                val interp = ((20 - (119 - sunAngleInt)).toFloat() / 20.0f)
                interpolateRGBColour(duskOrDawnColour, earlyLateDayColour, interp)
            }
            in 120..179 -> {
                val interp = ((60 - (179 - sunAngleInt)).toFloat() / 60.0f)
                interpolateRGBColour(earlyLateDayColour, midDayColour, interp)
            }
            in 180..239 -> {
                val interp = ((60 - (239 - sunAngleInt)).toFloat() / 60.0f)
                interpolateRGBColour(midDayColour, earlyLateDayColour, interp)
            }
            in 240..259 -> {
                val interp = ((20 - (259 - sunAngleInt)).toFloat() / 20.0f)
                interpolateRGBColour(earlyLateDayColour, duskOrDawnColour, interp)
            }
            in 260..269 -> {
                val interp = ((10 - (269 - sunAngleInt)).toFloat() / 10.0f)
                interpolateRGBColour(duskOrDawnColour, midNightColour, interp)
            }
            else -> midNightColour
        }
    }

    /**
     * Function to interpolate an RGB Colour triple.
     * Note: This may not provide expected results if more than two of
     * the RGB components change between the from and to colours.
     */
    private fun interpolateRGBColour(from: Triple<Float, Float, Float>,
                                     to: Triple<Float, Float, Float>,
                                     interp: Float) : Triple<Float, Float, Float> {

        val diff = Triple((to.first - from.first),
                                                (to.second - from.second),
                                                (to.third - from.third))

        return Triple((from.first + (diff.first * interp)),
                        (from.second + (diff.second * interp)),
                        (from.third + (diff.third * interp)))
    }

    /**
     * Function which returns the current sun position
     */
    fun getSunPosition() : Triple<Float, Float, Float> {
        return mSunPosition
    }

    /**
     * Function which returns the current light colour
     */
    fun getLightColour() : Triple<Float, Float, Float> {
        return mLightColour
    }

    /**
     * Function which draws the model using the view projection matrix passed if
     * the parameters have been set.
     */
    fun draw(vpMatrix: FloatArray) {

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

                GLES20.glGetUniformLocation(mProgram, "vColor").also { colourHandle ->
                    GLES20.glUniform4fv(colourHandle, 1, floatArrayOf(mLightColour.first, mLightColour.second, mLightColour.third, 1.0f), 0)
                }

                // generate the model matrix Note Currently this is only a translation
                val translationMatrix = FloatArray(16).also {
                    Matrix.setIdentityM(it, 0)
                    Matrix.translateM(it, 0, mSunPosition.first, mSunPosition.second, mSunPosition.third)
                }

                val scaleMatrix = FloatArray(16).also {
                    Matrix.setIdentityM(it, 0)
                    Matrix.scaleM(it, 0, 8.0f, 8.0f, 8.0f)
                }

                // scale and then translate
                val modelMatrix = FloatArray(16).also {
                    Matrix.multiplyMM(it, 0, translationMatrix, 0, scaleMatrix, 0)
                }

                // add the model matrix to the view projection matrix to create the model view projection matrix.
                val mvpMatrix = FloatArray(16)
                Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

                GLES20.glGetUniformLocation(mProgram, "uMVPMatrix").also { matrixHandle ->
                    GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0)
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
}