package online.ruin_of_future

import com.hankcs.hanlp.HanLP
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.utils.ExternalResource.*
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule


object TalkerCommand : CompositeCommand(
    TalkerHelper,
    "qgjc",
    description = "情感剧场小帮手"
) {
    @SubCommand
    suspend fun UserCommandSender.start() {
        this.sendMessage("开启演讲侦听模式")
        val contentMp = TalkerData.talkerMessage
        val contentStamp = TalkerData.talkerStart
        if (contentMp[this.user.id] != null) {
            this.sendMessage("已经在侦听该演讲者")
            return
        }
        contentMp[this.user.id] = arrayListOf()
        contentStamp[this.user.id] = System.nanoTime()

        val durationInMilli: Long = 3 * 60 * 60 * 100
        Timer().schedule(durationInMilli) {
            val now = System.nanoTime()
            if (contentStamp.containsKey(this@start.user.id) &&
                now - contentStamp[this@start.user.id]!! <= durationInMilli
            ) {
                contentStamp.remove(this@start.user.id)
                runBlocking {
                    this@start.sendMessage("超时未回复，已清除记录")
                }
            }
        }
    }

    @SubCommand
    suspend fun UserCommandSender.end() {
        val contentList = TalkerData.talkerMessage[this.user.id]
        if (contentList == null) {
            this.sendMessage("侦听结果异常，请检查日志")
        } else {
            if (contentList.isEmpty()) {
                this.sendMessage("没有要处理的内容")
                return
            }
            this.sendMessage("${this.user.nameCardOrNick} 的演讲结束，正在生成摘要")

            val sb = StringBuilder()
            for (str in contentList) {
                sb.append(str)
            }
            val content = sb.toString()
            val keywordList = HanLP.extractKeyword(content, 5)
            val keywordInfo = StringBuilder()
            for (word in keywordList) {
                keywordInfo.append("$word ")
            }
            this.sendMessage("关键词：${keywordInfo}")
            val summaryList = HanLP.extractSummary(content, 3)
            val summary = StringBuilder()
            for (sentence in summaryList) {
                summary.append(sentence)
                val ending = listOf(".", "。", ",", "，", "!", "！", "?", "？")
                var flag = false
                for (ch in ending) {
                    flag = flag || sentence.endsWith(ch)
                }
                if (!flag) {
                    summary.append("。")
                }
            }
            this.sendMessage("摘要：${summary}")
            if (this is MemberCommandSender) {
                val fileName: String = "${this.user.nameCardOrNick}_" +
                        "${SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(Date())}.txt"
                this.group.files.uploadNewFile("./${fileName}", content.toByteArray().toExternalResource())
            }

            TalkerData.talkerMessage.remove(this.user.id)
            TalkerData.talkerStart.remove(this.user.id)
        }
    }
}