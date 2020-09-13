package longevity.software.vrgamedemo

interface PlayerPositionTileMapInterface {
    fun getPlayerPositionOnTileMap(current : Triple<Float, Float, Float>, delta : Triple<Float, Float, Float>) : Triple<Float, Float, Float>
}