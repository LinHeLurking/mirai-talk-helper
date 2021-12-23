package online.ruin_of_future

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object TalkerData {
    val talkerMessage: MutableMap<Long, ArrayList<String>> = mutableMapOf()
    val talkerStart: MutableMap<Long, Long> = mutableMapOf()
}