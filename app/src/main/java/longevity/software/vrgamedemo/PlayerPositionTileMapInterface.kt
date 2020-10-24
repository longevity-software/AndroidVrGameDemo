package longevity.software.vrgamedemo

interface PlayerPositionTileMapInterface {
    fun getPlayerPositionOnTileMap(current : Position3Float, directionVector : Vector3Float, distance: Float) : Position3Float
}