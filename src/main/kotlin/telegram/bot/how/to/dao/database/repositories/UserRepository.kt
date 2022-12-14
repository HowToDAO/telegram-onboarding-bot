package telegram.bot.how.to.dao.database.repositories

import org.springframework.data.repository.CrudRepository
import telegram.bot.how.to.dao.database.data.entity.Messenger
import telegram.bot.how.to.dao.database.data.entity.User

interface UserRepository : CrudRepository<User, Long> {
    fun findUserById(id: Int): User?
    fun findUserByUserChatIdAndMessenger(userChatId: String, messenger: Messenger): User?
    fun findUserByUserChatId(userChatId: String): User?
    fun deleteById(user: User): User?
}
