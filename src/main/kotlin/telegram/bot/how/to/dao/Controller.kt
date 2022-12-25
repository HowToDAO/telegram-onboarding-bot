package telegram.bot.how.to.dao

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import telegram.bot.how.to.dao.database.service.UserService
import telegram.bot.how.to.dao.chats.TelegramBot
import java.io.File


@Controller
open class Controller(userService: UserService) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    init {
        val conf = readConf("telegram.conf")!!

        val bot = TelegramBot(
            botUsername = conf.getString("bot_username"),
            botToken = conf.getString("bot_token"),
            fileName = conf.getString("file_name"),
            userService = userService
        )
        TelegramBotsApi(DefaultBotSession::class.java).registerBot(bot)
    }

    private fun readConf(path: String?): Config? = try {
        path?.run {
            val file = File(this)
            if (file.exists()) ConfigFactory.parseFile(file)
            else null
        }
    } catch (t: Throwable) {
        log.error("Can't read config file: '$path'", t)
        null
    }
}
