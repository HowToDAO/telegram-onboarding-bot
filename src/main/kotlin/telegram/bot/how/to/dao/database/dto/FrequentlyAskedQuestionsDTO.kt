package telegram.bot.how.to.dao.database.dto

data class FrequentlyAskedQuestionsDTO(
    val text: String,
    val code: String?,
    val button: String?,
    val list: List<FrequentlyAskedQuestionsDTO>?
)