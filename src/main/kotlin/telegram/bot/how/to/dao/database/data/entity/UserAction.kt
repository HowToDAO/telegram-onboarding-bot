package telegram.bot.how.to.dao.database.data.entity

import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.sql.Timestamp

@Entity
@Table(name = "USER_ACTIONS")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
data class UserAction(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    @Column(name = "ACTION_NAME") var actionName: String? = null,

    @Column(name = "ACTION_DATE_TIME") var actionDateTime: Timestamp? = null,

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "ID", insertable = false, updatable = false)
    var user: User? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as UserAction

        return id != null && id == other.id && actionName == other.actionName
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String = this::class.simpleName + "(id = $id, actionName = $actionName)"
}