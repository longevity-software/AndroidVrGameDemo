package longevity.software.vrgamedemo

class GameLoop(glSurfaceView: VrGlSurfaceView, gameControl: GameControlHub, player: Player): Runnable {

    private var mRunning = false

    private var mScene = glSurfaceView
    private var mControlHub = gameControl
    private var mPlayer = player

    /**
     * overriden run function from the Runnable interface
     */
    override fun run() {
        mRunning = true

        while ( mRunning ) {

            // process the controls
            mPlayer.adjustPlayer(mControlHub.getMoveForwardBackDelta(),
                                    mControlHub.getMoveLeftRightDelta(),
                                    mControlHub.getLookPitch(),
                                    mControlHub.getLookYaw())

            // render the scene
            mScene.reRenderTheScene()

            // sleep for 25mS
            Thread.sleep(25)
        }
    }

    /**
     * function to stop the game loop when the activity is paused
     */
    fun stopGameLoop() {
        mRunning = false
    }

}