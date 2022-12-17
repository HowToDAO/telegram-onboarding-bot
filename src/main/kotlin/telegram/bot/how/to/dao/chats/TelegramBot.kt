package telegram.bot.how.to.dao.chats

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import telegram.bot.how.to.dao.database.dto.FrequentlyAskedQuestionsDTO
import telegram.bot.how.to.dao.database.service.UserService
import java.io.File


class TelegramBot(
    private val botUsername: String,
    private val botToken: String,
    fileName: String,
    override val userService: UserService
) : Communicator, TelegramLongPollingBot() {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private val answers = read(fileName)

    override fun onUpdateReceived(update: Update) {
        log.debug(
            "\nMessage: " + update.message?.text +
                    "\nFromMsg: " + update.message?.from +
                    "\nChat: " + update.message?.chat +
                    "\nCallbackQuery: " + update.callbackQuery?.data +
                    "\nFromCallBck: " + update.callbackQuery?.from +
                    "\nChatId: " + update.message?.chatId +
                    "\nSticker: " + update.message?.sticker
        )

        if (update.hasCallbackQuery()) {
            // Set variables
            val callData = update.callbackQuery.data;
            val messageId = update.callbackQuery.message.messageId
            val chatId = update.callbackQuery.message.chatId

            val answer = findAnswer(callData, answers)

            sendMessageWithButtons(
                messageText = answer?.text ?: " -- text not found -- ",
                chatId = chatId,
                messageId = messageId,
                answer = answer ?: answers
            )
        } else {
            sendMessageWithButtons(
                messageText = answers.text,
                chatId = update.message.chatId,
                answer = answers
            )
        }


//        val user = User(
//            id = null,
//            userChatId = update.message.chatId.toString(),
//            firstName = update.message?.from?.firstName,
//            lastName = update.message?.from?.lastName,
//            userName = update.message?.from?.userName,
//            createDate = Timestamp(System.currentTimeMillis())
//        )
//
//        update.message?.text
//            ?.let { onUpdate(it, user) }
//            ?.run { sendMessage(this, update.message.chatId) }
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

    private fun sendMessageWithButtons(
        messageText: String,
        chatId: Long,
        answer: FrequentlyAskedQuestionsDTO,
        messageId: Int? = null
    ): Unit = try {
        messageId?.let {
            EditMessageText().also {
                it.chatId = chatId.toString()
                it.messageId = messageId
                it.text = messageText

                val rowsInline: MutableList<List<InlineKeyboardButton>> = if (answer.list.isNullOrEmpty().not())
                    answer.list!!.map {
                        ArrayList<InlineKeyboardButton>().apply {
                            addAll(listOf(
                                InlineKeyboardButton().also { btn ->
                                    btn.text = it.button ?: " -- text for button not found -- "
                                    btn.callbackData = it.code
                                }
                            ))
                        }
                    }.toMutableList()
                else ArrayList()

                rowsInline.let { rows -> it.replyMarkup = InlineKeyboardMarkup().apply { keyboard = rows } }

                execute(it)
            }
        } ?: SendMessage().also {
            it.chatId = chatId.toString()
            it.text = messageText

            val rowsInline: MutableList<List<InlineKeyboardButton>> = if (answer.list.isNullOrEmpty().not())
                answer.list!!.map {
                    ArrayList<InlineKeyboardButton>().apply {
                        addAll(listOf(
                            InlineKeyboardButton().also { btn ->
                                btn.text = it.button ?: " -- text for button not found -- "
                                btn.callbackData = it.code
                            }
                        ))
                    }
                }.toMutableList()
            else ArrayList()

            rowsInline.let { rows -> it.replyMarkup = InlineKeyboardMarkup().apply { keyboard = rows } }

            execute(it)
        }
        Unit
    } catch (e: Exception) {
        log.error(e.message, e)
    }

    private fun read(fileName: String): FrequentlyAskedQuestionsDTO {
        val jsonString = File(fileName).inputStream().readBytes().toString(Charsets.UTF_8)
        val answers = Gson().fromJson(jsonString, FrequentlyAskedQuestionsDTO::class.java)

        val codes = mutableSetOf<String>()

        fun validate(answers: FrequentlyAskedQuestionsDTO) {
            answers.list?.forEach {
                it.code?.let { code ->
                    if (codes.contains(code)) {
                        throw RuntimeException("Code '$code' is duplicated in '$fileName'")
                    } else {
                        codes.add(code)
                    }
                }
                validate(it)
            }
        }

        validate(answers)

        return answers
    }

    private fun findAnswer(code: String, answers: FrequentlyAskedQuestionsDTO): FrequentlyAskedQuestionsDTO? {
        answers.list?.forEach { answer ->
            if (answer.code == code) return answer
            findAnswer(code, answer)?.let { return it }
        }
        return null
    }

    override fun getBotUsername(): String = botUsername

    override fun getBotToken(): String = botToken
}