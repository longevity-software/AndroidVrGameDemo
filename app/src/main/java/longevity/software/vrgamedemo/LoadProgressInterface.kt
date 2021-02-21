package longevity.software.vrgamedemo

interface LoadProgressInterface {

    fun getSaves() : List<String>
    fun getTile(saveName: String) : String
    fun getPlayerOffset(saveName: String): Position3Float
}