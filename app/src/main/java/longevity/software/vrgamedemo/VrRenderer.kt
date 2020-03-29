package longevity.software.vrgamedemo

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class VrRenderer : GLSurfaceView.Renderer {

    // matrices for future use.
    private val modelViewProjectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    // Quads which fill the left and right side of the screen
    private lateinit var mLeftQuad: TexturedQuad
    private lateinit var mRightQuad: TexturedQuad

    // Camera definitions.
    private lateinit var mRightCamera: GameCamera
    private lateinit var mLeftCamera: GameCamera

    // framebuffer constants
    private val TEXTURE_WIDTH: Int = 768
    private val TEXTURE_HEIGHT: Int = 432

    private val NUMBER_OF_BUFFERS: Int = 2
    private val RIGHT_FRAMEBUFFER_INDEX: Int = 0
    private val LEFT_FRAMEBUFFER_INDEX: Int = 1
    private val BYTES_PER_FLOAT: Int = 4

    // framebuffer variables
    private lateinit var mFrameBuffer: IntArray
    private lateinit var mDepthRenderBuffer: IntArray
    private lateinit var mRenderTexture: IntArray
    private lateinit var mTextureBufferLeft: IntBuffer
    private lateinit var mTextureBufferRight: IntBuffer

    // screen width and height variables
    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0

    /**
     * Function called when the surface is created.
     * This function sets up the Quad's, camera's and
     * framebuffers for rendering the scene.
     */
    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {

        // initialise the two Quads
        mLeftQuad = TexturedQuad(true)
        mRightQuad = TexturedQuad(false)

        // initialise the two cameras to the same values for now
        mRightCamera = GameCamera(0.0f, 1.0f, 0.0f,
                                0.0f, 0.0f, 1.0f,
                                0.0f, 1.0f, 0.0f)

        mLeftCamera = GameCamera(0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f)

        // intialise the framebuffers
        mFrameBuffer = IntArray(NUMBER_OF_BUFFERS)
        mDepthRenderBuffer = IntArray(NUMBER_OF_BUFFERS)
        mRenderTexture = IntArray(NUMBER_OF_BUFFERS)

        // generate framebuffers, render buffers and textures
        GLES20.glGenFramebuffers(NUMBER_OF_BUFFERS, mFrameBuffer, 0)
        GLES20.glGenRenderbuffers(NUMBER_OF_BUFFERS, mDepthRenderBuffer, 0)
        GLES20.glGenTextures(NUMBER_OF_BUFFERS, mRenderTexture, 0)

        // start with the right buffer
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mDepthRenderBuffer[RIGHT_FRAMEBUFFER_INDEX])

        // Clamp the texture
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)

        // create it
        // create empty int buffer first
        var bufRight: IntArray = IntArray(TEXTURE_WIDTH * TEXTURE_HEIGHT)

        mTextureBufferRight = ByteBuffer.allocateDirect(bufRight.size * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asIntBuffer()

        // generate texture
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGB,
            TEXTURE_WIDTH, TEXTURE_HEIGHT,
            0,
            GLES20.GL_RGB,
            GLES20.GL_UNSIGNED_SHORT_5_6_5,
            mTextureBufferRight)

        // now the render depth buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthRenderBuffer[RIGHT_FRAMEBUFFER_INDEX])
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, TEXTURE_WIDTH, TEXTURE_HEIGHT)

        // and now the left one.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mDepthRenderBuffer[LEFT_FRAMEBUFFER_INDEX])

        // Clamp the texture
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)

        // create it
        // create empty int buffer first
        var bufLeft: IntArray = IntArray(TEXTURE_WIDTH * TEXTURE_HEIGHT)

        mTextureBufferLeft = ByteBuffer.allocateDirect(bufLeft.size * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asIntBuffer()

        // generate texture
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGB,
            TEXTURE_WIDTH, TEXTURE_HEIGHT,
            0,
            GLES20.GL_RGB,
            GLES20.GL_UNSIGNED_SHORT_5_6_5,
            mTextureBufferLeft)

        // and now the depth render buffer
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mDepthRenderBuffer[LEFT_FRAMEBUFFER_INDEX])
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, TEXTURE_WIDTH, TEXTURE_HEIGHT)

        // set the background frame colour to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    /**
     * Function called when the surface has changed to reset the viewport
     */
    override fun onSurfaceChanged(unused: GL10?, width: Int, height: Int) {

        mScreenWidth = width
        mScreenHeight = height
    }

    /**
     * Function called when the frame is drawn.
     * it calls local functions to draw the scene the the framebuffers and then
     * draw the textured quads to the screen
     */
    override fun onDrawFrame(unused: GL10?) {

        // Draw the left side
        drawSceneToFrameBuffer(mLeftCamera,
            mFrameBuffer[LEFT_FRAMEBUFFER_INDEX],
            mRenderTexture[LEFT_FRAMEBUFFER_INDEX],
            mDepthRenderBuffer[LEFT_FRAMEBUFFER_INDEX])

        // and the right side
        drawSceneToFrameBuffer(mRightCamera,
            mFrameBuffer[RIGHT_FRAMEBUFFER_INDEX],
            mRenderTexture[RIGHT_FRAMEBUFFER_INDEX],
            mDepthRenderBuffer[RIGHT_FRAMEBUFFER_INDEX])

        // set an identity matrix as the quads are already declared in clip space coordinates
        val identityMatrix: FloatArray = FloatArray(16).also {
            Matrix.setIdentityM(it, 0)
        }

        // set the viewport back to the screen size
        GLES20.glViewport(0, 0, mScreenWidth, mScreenHeight)

        // set the clear colour for rendering the quads and clear the colour buffer bit.
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // draw the quad with the generated textures.
        mLeftQuad.draw(identityMatrix, mRenderTexture[LEFT_FRAMEBUFFER_INDEX])
        mRightQuad.draw(identityMatrix, mRenderTexture[RIGHT_FRAMEBUFFER_INDEX])
    }

    /**
     * Function that draws the scene to the passed framebuffer references from the
     * passed camera position
     */
    private fun drawSceneToFrameBuffer(camera: GameCamera, frameBuffer: Int, renderTexture: Int, renderBuffer: Int) {

        // bind the framebuffer we want to use
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer)

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0,
            GLES20.GL_TEXTURE_2D,
            renderTexture,
            0)

        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
            GLES20.GL_DEPTH_ATTACHMENT,
            GLES20.GL_RENDERBUFFER,
            renderBuffer)

        // check if framebuffer is complete
        if (GLES20.GL_FRAMEBUFFER_COMPLETE == GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER))
        {
            // set the viewport to render to the texture
            GLES20.glViewport(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT)

            // TODO - temporary check to make each side a different colour
            if (frameBuffer == mFrameBuffer[LEFT_FRAMEBUFFER_INDEX]) {
                GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f)
            } else {
                GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f)
            }

            // clear the colour buffer bit before we start drawing
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

            // do the actual drawing of the scene
            drawScene(camera)

            // unbind the frame buffer
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        }
    }

    /**
     * Function used to draw the scene from the passed camera position
     */
    private fun drawScene(camera: GameCamera) {

        // TODO - populate this with draw calls.
    }
}