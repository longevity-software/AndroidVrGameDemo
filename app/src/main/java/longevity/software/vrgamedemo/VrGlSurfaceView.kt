package longevity.software.vrgamedemo

import android.content.Context
import android.opengl.GLSurfaceView

class VrGlSurfaceView(context: Context, controlHub: GameControlHub) : GLSurfaceView(context){

    private val mRenderer: VrRenderer

    /**
     * VrGlSurfaceView init block which sets up the OpenGl version and renderer.
     */
    init {

        // use openGL ES2.0
        setEGLContextClientVersion(2)

        // instantiate the renderer, pass it the device rotation sensor and set it to be used
        mRenderer = VrRenderer(controlHub)
        setRenderer(mRenderer)
    }
}