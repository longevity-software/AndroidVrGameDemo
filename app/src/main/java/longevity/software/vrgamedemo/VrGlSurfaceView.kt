package longevity.software.vrgamedemo

import android.content.Context
import android.opengl.GLSurfaceView

class VrGlSurfaceView(context: Context, vis: PlayerVision, sky: SkyBox, sun: SunLight, scene: DrawableInterface, transparent: DrawableInterface) : GLSurfaceView(context){

    private val mRenderer: VrRenderer

    /**
     * VrGlSurfaceView init block which sets up the OpenGl version and renderer.
     */
    init {

        // use openGL ES2.0
        setEGLContextClientVersion(2)

        // instantiate the renderer and set it to be used
        mRenderer = VrRenderer(vis, sky, sun, scene, transparent)
        setRenderer(mRenderer)

        // set it to only render when we want it to.
        setRenderMode(RENDERMODE_WHEN_DIRTY)
    }

    fun AquireRenderLock() {

        mRenderer.AquireLock()
    }

    /**
     * sets the camera position and then renders the scene
     */
    fun ReleasLockAndRenderTheScene() {
        mRenderer.ReleaseLock()

        this.requestRender()
    }
}