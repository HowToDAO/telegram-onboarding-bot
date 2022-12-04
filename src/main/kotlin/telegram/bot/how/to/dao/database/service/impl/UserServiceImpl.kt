package telegram.bot.how.to.dao.database.service.impl

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import telegram.bot.how.to.dao.database.data.entity.User
import telegram.bot.how.to.dao.database.service.UserService
import java.sql.ResultSet

@Service
open class UserServiceImpl(private val jtm: JdbcTemplate) : UserService {

    private val rowMapper: RowMapper<User> = RowMapper<User> { resultSet: ResultSet, _: Int ->
        User(
            userId = resultSet.getLong("user_id"),
            userName = resultSet.getString("user_Name"),
            firstName = resultSet.getString("first_Name"),
            lastName = resultSet.getString("last_Name"),
            createDate = resultSet.getTimestamp("create_Date")
        )
    }

    override fun getUserById(userId: Long): User? =
        jtm.query("SELECT * FROM users WHERE USER_ID = $userId", rowMapper).firstOrNull()
}
