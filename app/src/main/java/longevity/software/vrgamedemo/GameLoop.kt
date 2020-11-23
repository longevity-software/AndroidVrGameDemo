package longevity.software.vrgamedemo

class GameLoop(glSurfaceView: VrGlSurfaceView, gameControl: GameControlHub, player: Player, sun: SunLight, vision: PlayerVision, tileMap: TileMap, objectPlacer: ObjectPlacer): Runnable {

    private val MILLISECONDS_IN_A_DAY = 240000

    private var mRunning = false

    private var mScene = glSurfaceView
    private var mControlHub = gameControl
    private var mPlayer = player
    private var mSunLight = sun
    private var mPlayerVision = vision
    private var mTileMap = tileMap
    private var mObjectPlacer = objectPlacer

    private var mTemp = 1

    /**
     * overriden run function from the Runnable interface
     */
    override fun run() {
        mRunning = true

        while ( mRunning ) {

            // gain render lock while we are adjusting the scene
            mScene.AquireRenderLock()

            // process the controls
            mPlayer.adjustPlayer(mControlHub.getMoveForwardBackDelta(),
                                    mControlHub.getMoveLeftRightDelta(),
                                    mControlHub.getLookPitch(),
                                    mControlHub.getLookYaw(),
                                    mTileMap)

            val playerLookingAtGround = mPlayerVision.GetPositionPlayerIsLookingOnTheYAxisPlane()

            val showObjectPlacer = playerLookingAtGround.first && mTileMap.isPositionValidOnTileMap(playerLookingAtGround.second)

            mObjectPlacer.adjustObjectPlacer(playerLookingAtGround.second, showObjectPlacer)

            if (mControlHub.isR1ButtonPressed()) {
                mPlayerVision.increaseEyeDistance(0.1f)
            }

            if (mControlHub.isL1ButtonPressed()) {
                mPlayerVision.decreaseEyeDistance(0.1f)
            }

            if (mControlHub.isActionButtonPressed()) {
                //mPlayerVision.toggleCameraLookAt()
                when (mTemp) {
                    0 -> {
                        mObjectPlacer.setModelToBePlaced("Tree")
                        mTemp = 1
                    }
                    1 -> {
                        mObjectPlacer.setModelToBePlaced("Rocks")
                        mTemp = 2
                    }
                    else -> {
                        mObjectPlacer.resetModelToBePlaced()
                        mTemp = 0
                    }
                }

            }

            val TIME = (System.currentTimeMillis() % MILLISECONDS_IN_A_DAY).toFloat()
            val TIME_OF_DAY = (TIME / MILLISECONDS_IN_A_DAY.toFloat())

            mSunLight.UpdateSunLight(0.5f, mPlayer.getPosition())

            // render the scene
            mScene.ReleasLockAndRenderTheScene()

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