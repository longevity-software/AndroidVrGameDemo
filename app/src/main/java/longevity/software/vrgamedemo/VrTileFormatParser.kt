package longevity.software.vrgamedemo

import android.content.Context

class VrTileFormatParser(context: Context, file: String, modelLoader: ModelLoader) {

    private var mTile: Tile

    init {
        val reader = context.openFileInput(file).bufferedReader()

        // all the variables for creating a tile
        var baseModel = ""
        var baseRotation = ""
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

        mTile = Tile(baseModel, baseRotation, modelLoader, file, fluidity, gameObjects)
    }

    /**
     * Function to get the parsed tile.
     */
    fun getParsedTile() : Tile {
        return mTile
    }
}