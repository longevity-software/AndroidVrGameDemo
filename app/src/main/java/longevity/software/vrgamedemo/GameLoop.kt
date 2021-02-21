package longevity.software.vrgamedemo

import android.util.Log

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

            val start_time = System.currentTimeMillis()

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

            mControlHub.getR1ButtonState().also {
                if ( ( ButtonControlInterface.ButtonState.PRESSED == it )
                    || ( ButtonControlInterface.ButtonState.HELD == it ) ) {
                    //mPlayerVision.increaseEyeDistance(0.1f)
                    mObjectPlacer.rotateObjectPlacer(true, true)
                }
            }

            mControlHub.getL1ButtonState().also {
                if ((ButtonControlInterface.ButtonState.PRESSED == it)
                    || (ButtonControlInterface.ButtonState.HELD == it)
                ) {
                    //mPlayerVision.decreaseEyeDistance(0.1f)
                    mObjectPlacer.rotateObjectPlacer(true, false)
                }
            }

            if ( ButtonControlInterface.ButtonState.PRESSED == mControlHub.getActionButtonState() ) {
                mTileMap.placeObjectInMap(mObjectPlacer.getModelName(),
                                            mObjectPlacer.getModelPosition(),
                                            mObjectPlacer.getModelRotation())
            }

            val TIME = (System.currentTimeMillis() % MILLISECONDS_IN_A_DAY).toFloat()
            val TIME_OF_DAY = (TIME / MILLISECONDS_IN_A_DAY.toFloat())

            mSunLight.UpdateSunLight(0.5f, mPlayer.getPosition())

            // render the scene
            mScene.ReleasLockAndRenderTheScene()

            val end_time = System.currentTimeMillis()

            // if the elapsed time is less than the desired fps then wait the remainder
            // else just continue as we are too slow
            if ((end_time - start_time) < 25) {
                try {
                    Thread.sleep(25 - (end_time - start_time))
                } catch (e: Exception) {
                    Log.d("[Thread.Sleep]", "GA $e")
                }
            }
            else {
                Log.d("[Frame Time]", (end_time - start_time).toString())
            }
        }
    }

    /**
     * function to stop the game loop when the activity is paused
     */
    fun stopGameLoop() {
        mRunning = false
    }

}