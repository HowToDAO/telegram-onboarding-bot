package telegram.bot.how.to.dao.database.data.entity

import java.sql.Timestamp

data class User(
    var userId: Long,

    var userName: String? = null,

    var firstName: String? = null,

    var lastName: String? = null,

    var createDate: Timestamp
)