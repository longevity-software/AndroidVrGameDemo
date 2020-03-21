package longevity.software.vrgamedemo

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class VrRenderer : GLSurfaceView.Renderer {

    private val modelViewProjectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    private lateinit var mTriangle: TriangleGameObject

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {

        mTriangle = TriangleGameObject()

        // set the background frame colour to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {
        // reset the viewport
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, 10.0f)
    }

    override fun onDrawFrame(unused: GL10?) {

        val veiwProjectionMatrix = FloatArray(16)

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0,
            0.0f, 0.0f, -3.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f)

        Matrix.multiplyMM(veiwProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        mTriangle.draw(veiwProjectionMatrix)
    }
}