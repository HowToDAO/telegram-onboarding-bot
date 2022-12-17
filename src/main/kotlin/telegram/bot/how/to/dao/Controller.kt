package telegram.bot.how.to.dao

import org.springframework.stereotype.Controller
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import telegram.bot.how.to.dao.database.service.UserService
import telegram.bot.how.to.dao.chats.TelegramBot


@Controller
open class Controller(userService: UserService) {

    init {
        val bot = TelegramBot(
            botUsername = "", // todo:: add your telegram bot name here
            botToken = "", // todo:: add your telegram bot token here
            fileName = "frequently_asked_questions.json",
            userService = userService
        )
        TelegramBotsApi(DefaultBotSession::class.java).registerBot(bot)
    }
}
