package telegram.bot.how.to.dao.chats

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import telegram.bot.how.to.dao.database.service.UserService

class TelegramBot(
    private val botUsername: String,
    private val botToken: String,
    override val userService: UserService
) : Communicator, TelegramLongPollingBot() {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun onUpdateReceived(update: Update) {
        log.info(
            "\nMessage: " + update.message?.text +
                    "\nFromMsg: " + update.message?.from +
                    "\nChat: " + update.message?.chat +
                    "\nCallbackQuery: " + update.callbackQuery?.data +
                    "\nFromCallBck: " + update.callbackQuery?.from +
                    "\nChatId: " + update.message?.chatId +
                    "\nSticker: " + update.message?.sticker
        )

        update.message?.text
            ?.let { onUpdate(it) }
            ?.run { sendMessage(this, update.message.chatId) }
    }

    private fun sendMessage(messageText: String, chatId: Long): Unit = try {
        execute(SendMessage().also {
            log.debug("Send to chatId = $chatId\nMessage: \"$messageText\"")
            it.chatId = chatId.toString()
            it.text = messageText
        })
        Unit
    } catch (e: Exception) {
        log.error(e.message, e)
    }

    override fun getBotUsername(): String = botUsername

    override fun getBotToken(): String = botToken
}
