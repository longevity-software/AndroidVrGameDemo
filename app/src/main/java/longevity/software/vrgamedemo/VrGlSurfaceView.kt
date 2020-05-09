package longevity.software.vrgamedemo

import android.content.Context
import android.opengl.GLSurfaceView

class VrGlSurfaceView(context: Context, vis: PlayerVision) : GLSurfaceView(context){

    private val mRenderer: VrRenderer

    /**
     * VrGlSurfaceView init block which sets up the OpenGl version and renderer.
     */
    init {

        // use openGL ES2.0
        setEGLContextClientVersion(2)

        // instantiate the renderer and set it to be used
        mRenderer = VrRenderer(vis)
        setRenderer(mRenderer)

        // set it to only render when we want it to.
        setRenderMode(RENDERMODE_WHEN_DIRTY)
    }

    /**
     * sets the camera position and then renders the scene
     */
    fun reRenderTheScene()
    {
        this.requestRender()
    }
}