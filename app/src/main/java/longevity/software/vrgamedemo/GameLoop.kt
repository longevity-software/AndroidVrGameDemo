package longevity.software.vrgamedemo

class GameLoop(glSurfaceView: VrGlSurfaceView, gameControl: GameControlHub, player: Player, sun: SunLight, vision: PlayerVision): Runnable {

    private val MILLISECONDS_IN_A_DAY = 240000

    private var mRunning = false

    private var mScene = glSurfaceView
    private var mControlHub = gameControl
    private var mPlayer = player
    private var mSunLight = sun
    private var mPlayerVision = vision

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

            if (mControlHub.isR1ButtonPressed()) {
                mPlayerVision.increaseEyeDistance(0.1f)
            }

            if (mControlHub.isL1ButtonPressed()) {
                mPlayerVision.decreaseEyeDistance(0.1f)
            }

            if (mControlHub.isActionButtonPressed()) {
                mPlayerVision.toggleCameraLookAt()
            }

            val TIME = (System.currentTimeMillis() % MILLISECONDS_IN_A_DAY).toFloat()
            val TIME_OF_DAY = (TIME / MILLISECONDS_IN_A_DAY.toFloat())

            mSunLight.UpdateSunLight(TIME_OF_DAY, mPlayer.getPosition())

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