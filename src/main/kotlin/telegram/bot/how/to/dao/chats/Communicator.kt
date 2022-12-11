package telegram.bot.how.to.dao.chats

import telegram.bot.how.to.dao.database.data.entity.User
import telegram.bot.how.to.dao.database.service.UserService

interface Communicator {
    val userService: UserService

    fun onUpdate(message: String, user: User): String {

        return if (message.equals("Зарегистрироваться", true)) {
            if (userService.getUserByUserChatId(user.userChatId!!) == null) {
                userService.saveUser(user)
                "Вы зарегистрированы"
            } else
                "Вы были зарегистрированы ранее"
        } else
            "Не корректная комманда"
    }
}