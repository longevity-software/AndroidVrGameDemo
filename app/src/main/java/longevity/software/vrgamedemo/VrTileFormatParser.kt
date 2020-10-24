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
        var upLeft = ""
        var straightUp = ""
        var upRight = ""
        var left = ""
        var right = ""
        var downLeft = ""
        var straightDown = ""
        var downRight = ""
        var fluidity = 0.0f

        reader.forEachLine {

            // if this the base model definition
            if ( it.startsWith("<BM>" ) ) {

                baseModel = it.substringAfter("<BM>").substringBefore("</BM>")
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
        }

        mTile = Tile(baseModel, modelLoader, file, upLeft, straightUp, upRight, left, right, downLeft, straightDown, downRight, fluidity)
    }

    /**
     * Function to get the parsed tile.
     */
    fun getParsedTile() : Tile {
        return mTile
    }
}