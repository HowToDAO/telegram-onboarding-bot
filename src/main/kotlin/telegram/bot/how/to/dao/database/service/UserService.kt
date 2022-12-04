package telegram.bot.how.to.dao.database.service

import telegram.bot.how.to.dao.database.data.entity.User

interface UserService {
    fun getUserById(userId: Long): User?
//    fun saveUser(admin: User): User?
}
