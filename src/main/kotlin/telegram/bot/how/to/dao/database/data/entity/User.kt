package telegram.bot.how.to.dao.database.data.entity

import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.sql.Timestamp


@Entity
@Table(name = "USERS")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    @Column(name = "USER_CHAT_ID") var userChatId: String? = null,

    @Column(name = "USER_NAME") var userName: String? = null,

    @Column(name = "FIRST_NAME") var firstName: String? = null,

    @Column(name = "LAST_NAME") var lastName: String? = null,

    @Column(name = "CREATE_DATE") var createDate: Timestamp? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as User

        return id != null
                && id == other.id
                && userChatId == other.userChatId
                && userName == other.userName
                && firstName == other.firstName
                && lastName == other.lastName
                && createDate == other.createDate
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , userChatId = $userChatId , userName = $userName , firstName = $firstName , lastName = $lastName , createDate = $createDate )"
    }
}