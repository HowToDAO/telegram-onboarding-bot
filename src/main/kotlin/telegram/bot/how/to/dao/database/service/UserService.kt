package telegram.bot.how.to.dao.database.service

import telegram.bot.how.to.dao.database.data.entity.User

interface UserService {
    fun getUserById(userId: Int): User?
    fun getUserByUserChatId(userChatId: String): User?
    fun saveUser(user: User): User?
    fun getAllUsers(): Iterable<User>?
    fun deleteById(userId: Int): User?
}
