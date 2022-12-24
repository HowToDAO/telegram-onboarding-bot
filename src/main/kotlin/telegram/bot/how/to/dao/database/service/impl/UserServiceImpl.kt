package telegram.bot.how.to.dao.database.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import telegram.bot.how.to.dao.database.repositories.UserRepository
import telegram.bot.how.to.dao.database.data.entity.User
import telegram.bot.how.to.dao.database.data.entity.UserAction
import telegram.bot.how.to.dao.database.repositories.UserActionRepository
import telegram.bot.how.to.dao.database.service.UserService

@Service
open class UserServiceImpl(
    @Autowired open val userRepository: UserRepository,
    @Autowired open val userActionRepository: UserActionRepository
) : UserService {

    @Transactional
    override fun saveUser(user: User): User = userRepository.save(user)

    @Transactional
    override fun saveUserAction(userAction: UserAction): UserAction = userActionRepository.save(userAction)

    @Transactional
    override fun getUserById(userId: Int): User? = userRepository.findUserById(userId)

    @Transactional
    override fun getUserByUserChatId(userChatId: String): User? = userRepository.findUserByUserChatId(userChatId)

    @Transactional
    override fun getAllUsers(): Iterable<User>? = userRepository.findAll()

    @Transactional
    override fun deleteById(userId: Int): User? = userRepository.deleteById(User(userId))
}
