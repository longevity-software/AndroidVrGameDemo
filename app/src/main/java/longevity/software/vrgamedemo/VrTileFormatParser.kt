package longevity.software.vrgamedemo

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class VrTileFormatParser(context: Context, file: String, modelLoader: ModelLoader) {

    private var mTile: Tile

    init {
        val inStream = context.assets.open(file)
        val reader = BufferedReader(InputStreamReader(inStream))

        // all the variables for creating a tile
        var baseModel = ""
        var baseRotation = ""
        var upLeft = ""
        var straightUp = ""
        var upRight = ""
        var left = ""
        var right = ""
        var downLeft = ""
        var straightDown = ""
        var downRight = ""
        var fluidity = 0.0f

        val gameObjects = ArrayList<Triple<String, Position3Float, Float>>()

        val GAME_OBJECT_MODEL_INDEX = 0
        val GAME_OBJECT_X_POSITION_INDEX = 1
        val GAME_OBJECT_Y_POSITION_INDEX = 2
        val GAME_OBJECT_Z_POSITION_INDEX = 3
        val GAME_OBJECT_ROTATION_INDEX = 4

        reader.forEachLine {

            // if this the base model definition
            if ( it.startsWith("<BM>" ) ) {

                baseModel = it.substringAfter("<BM>").substringBefore("</BM>")
            }
            else if ( it.startsWith("<BR>" ) ) {

                baseRotation = it.substringAfter("<BR>").substringBefore("</BR>")
            }
            else if ( it.startsWith( "<UL>" ) ) {
                upLeft = it.substringAfter("<UL>").substringBefore("</UL>")
            }
            else if ( it.startsWith("<SU>" ) ) {
                straightUp = it.substringAfter("<SU>").substringBefore("</SU>")
            }
            else if ( it.startsWith("<UR>" ) ) {
                upRight = it.substringAfter("<UR>").substringBefore("</UR>")
            }
            else if ( it.startsWith("<SL>" ) ) {
                left = it.substringAfter("<SL>").substringBefore("</SL>")
            }
            else if ( it.startsWith("<SR>" ) ) {
                right = it.substringAfter("<SR>").substringBefore("</SR>")
            }
            else if ( it.startsWith("<DL>" ) ) {
                downLeft = it.substringAfter("<DL>").substringBefore("</DL>")
            }
            else if ( it.startsWith("<SD>" ) ) {
                straightDown = it.substringAfter("<SD>").substringBefore("</SD>")
            }
            else if ( it.startsWith("<DR>" ) ) {
                downRight = it.substringAfter("<DR>").substringBefore("</DR>")
            }
            else if ( it.startsWith( "<TF>" ) ) {
                val fluidityString = it.substringAfter("<TF>").substringBefore("</TF>")

                // set viscosity if it is a valid float value
                fluidityString.toFloatOrNull().also {
                    if ( null != it ) {
                        fluidity = it
                    }
                }
            }
            else if ( it.startsWith( "<GO>" ) ) {
                val settingsString = it.substringAfter("<GO>").substringBefore("</GO>")
                val settings = settingsString.split( "," )

                gameObjects.add(Triple(settings[GAME_OBJECT_MODEL_INDEX],
                    Position3Float(settings[GAME_OBJECT_X_POSITION_INDEX].toFloat(),
                        settings[GAME_OBJECT_Y_POSITION_INDEX].toFloat(),
                        settings[GAME_OBJECT_Z_POSITION_INDEX].toFloat()),
                    settings[GAME_OBJECT_ROTATION_INDEX].toFloat()))
            }
        }

        mTile = Tile(baseModel, baseRotation, modelLoader, file, upLeft, straightUp, upRight, left, right, downLeft, straightDown, downRight, fluidity, gameObjects)
    }

    /**
     * Function to get the parsed tile.
     */
    fun getParsedTile() : Tile {
        return mTile
    }
}