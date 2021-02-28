package longevity.software.vrgamedemo

interface SaveProgressInterface {

    fun CreateSaveGame(name: String) : Boolean
    fun updateSaveGame(tile: String, pos: Position3Float)
}