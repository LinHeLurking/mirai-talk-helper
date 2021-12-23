package online.ruin_of_future

import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.disable
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.utils.info

object TalkerHelper : KotlinPlugin(
    JvmPluginDescription(
        id = "online.ruin_of_future.talker_helper",
        name = "talker_helper",
        version = "1.0-SNAPSHOT",
    ) {
        author("LinHeLurking")
        info("""Make QGJC grate again!""")
    }
) {
    override fun onEnable() {
        CommandManager.registerCommand(TalkerCommand)

        this.globalEventChannel().filter {
            it is GroupMessageEvent || it is FriendMessageEvent || it is GroupTempMessageEvent
        }.subscribeAlways<MessageEvent> {
            if (this.message.isNotEmpty() && !Regex("(\\/(qgjc|talk)( )?(start|end)?)").matches(this.message.contentToString())) {
                val mp = TalkerData.talkerMessage
                if (mp.containsKey(this.sender.id)) {
                    val contentList = mp[this.sender.id]!!
                    for (msg in this.message) {
                        contentList.add(msg.contentToString())
                    }
                }
            }
        }
        logger.info { "情感剧场小帮手加载成功" }
    }

    override fun onDisable() {
        logger.info { "情感剧场小帮手卸载" }
        CommandManager.unregisterCommand(TalkerCommand)
    }
}