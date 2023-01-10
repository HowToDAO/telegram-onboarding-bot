package telegram.bot.how.to.dao.database.repositories

import org.springframework.data.repository.CrudRepository
import telegram.bot.how.to.dao.database.data.entity.UserAction

interface UserActionRepository : CrudRepository<UserAction, Long> {
    fun findUserActionById(id: Int): UserAction?
    fun deleteById(userAction: UserAction): UserAction?
}
