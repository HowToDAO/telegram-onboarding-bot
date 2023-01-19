package telegram.bot.how.to.dao.chats

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import telegram.bot.how.to.dao.database.data.entity.Messenger
import telegram.bot.how.to.dao.database.dto.FrequentlyAskedQuestionsDTO
import telegram.bot.how.to.dao.database.service.UserService
import telegram.bot.how.to.dao.database.data.entity.User
import telegram.bot.how.to.dao.database.data.entity.UserAction
import java.io.File
import java.sql.Timestamp


class TelegramBot(
    private val botUsername: String,
    private val botToken: String,
    fileName: String,
    override val userService: UserService
) : Communicator, TelegramLongPollingBot() {
    private val fileAnswers = File(fileName)

    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private var answers = read(fileAnswers)
    private var updateFileTime = 0L

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

        val userAction: UserAction?

        if (update.hasCallbackQuery() && updateFileTime == fileAnswers.lastModified()) {
            // Set variables
            val callData = update.callbackQuery.data
            val messageId = update.callbackQuery.message.messageId
            val chatId = update.callbackQuery.message.chatId

            val answer = findAnswer(callData, answers)

            sendMessageWithButtons(
                messageText = answer?.text ?: answers.text ?: " -- text not found -- ",
                chatId = chatId,
                messageId = messageId,
                answer = answer ?: answers
            )

            userService.getUserByUserChatIdAndMessenger(
                update.callbackQuery?.from?.id.toString(),
                Messenger.TELEGRAM
            )?.let {
                userAction = UserAction(
                    id = null,
                    actionName = "callBack:$callData",
                    actionDateTime = Timestamp(System.currentTimeMillis()),
                    user = it
                )
                userService.saveUserAction(userAction)
            } ?: run {

                val user = userService.saveUser(
                    User(
                        id = null,
                        messenger = Messenger.TELEGRAM,
                        userChatId = update.callbackQuery?.from?.id.toString(),
                        firstName = update.callbackQuery?.from?.firstName,
                        lastName = update.callbackQuery?.from?.lastName,
                        userName = update.callbackQuery?.from?.userName,
                        createDate = Timestamp(System.currentTimeMillis())
                    )
                )
                userService.saveUserAction(
                    UserAction(
                        id = null,
                        actionName = "callBack:$callData",
                        actionDateTime = Timestamp(System.currentTimeMillis()),
                        user = user
                    )
                )
            }
        } else if (updateFileTime == fileAnswers.lastModified()) {
            sendMessageWithButtons(
                messageText = answers.text ?: " -- text not found -- ",
                chatId = update.message.chatId,
                answer = answers,
                deleteMessage = DeleteMessage(update.message.chatId.toString(), update.message.messageId)
            )

            userService.getUserByUserChatIdAndMessenger(
                update.message.from?.id.toString(),
                Messenger.TELEGRAM
            )?.let {
                userAction = UserAction(
                    id = null,
                    actionName = "msg:${update.message.text}",
                    actionDateTime = Timestamp(System.currentTimeMillis()),
                    user = it
                )
                userService.saveUserAction(userAction)
            } ?: run {
                val user = userService.saveUser(
                    User(
                        id = null,
                        userChatId = update.message.from?.id.toString(),
                        messenger = Messenger.TELEGRAM,
                        firstName = update.message.from?.firstName,
                        lastName = update.message.from?.lastName,
                        userName = update.message.from?.userName,
                        createDate = Timestamp(System.currentTimeMillis())
                    )
                )
                userService.saveUserAction(
                    UserAction(
                        id = null,
                        actionName = "msg:${update.message.text}",
                        actionDateTime = Timestamp(System.currentTimeMillis()),
                        user = user
                    )
                )
            }
        } else {
            answers = read(fileAnswers)
            updateFileTime = fileAnswers.lastModified()

            val messageId = update.callbackQuery.message.messageId ?: update.message.messageId!!
            val chatId = update.callbackQuery.message.chatId ?: update.message.chatId!!

            sendMessageWithButtons(
                messageText = answers.text ?: " -- text not found -- ",
                chatId = chatId,
                answer = answers,
                deleteMessage = DeleteMessage(chatId.toString(), messageId)
            )
        }
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
        messageId: Int? = null,
        deleteMessage: DeleteMessage? = null,
    ): Unit = try {
        deleteMessage?.let { execute(it) }
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

    private fun read(file: File): FrequentlyAskedQuestionsDTO {
        val jsonString = file.inputStream().readBytes().toString(Charsets.UTF_8)
        val answers = Gson().fromJson(jsonString, FrequentlyAskedQuestionsDTO::class.java)

        updateFileTime = file.lastModified()

        validate(answers, file)

        return answers
    }

    private fun findAnswer(code: String, answers: FrequentlyAskedQuestionsDTO): FrequentlyAskedQuestionsDTO? {
        if (answers.code == code && answers.text != null) return answers
        answers.list?.forEach { answer ->
            findAnswer(code, answer)?.let { return it }
        }
        return null
    }

    private fun validate(answers: FrequentlyAskedQuestionsDTO, file: File) {
        val codes = mutableSetOf<String>()
        answers.list?.forEach {
            it.code?.let { code ->
                if (codes.contains(code) && it.text != null) {
                    throw RuntimeException("Code '$code' is duplicated in '${file.name}'")
                } else if (it.text != null) {
                    codes.add(code)
                }
            }
            validate(it, file)
        }
    }

    override fun getBotUsername(): String = botUsername

    override fun getBotToken(): String = botToken
}