package telegram.bot.how.to.dao.chats

import telegram.bot.how.to.dao.database.service.UserService

interface Communicator {
    val userService: UserService

    fun onUpdate(message: String): String {
        return if (message.matches("\\d+".toRegex())) {
            userService.getUserById(message.toLong())?.toString() ?: "Не найден пользователь ID = $message"
        }
        else
            "Не корректный ID"
    }
}