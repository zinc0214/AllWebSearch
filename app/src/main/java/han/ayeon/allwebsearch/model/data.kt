package han.ayeon.allwebsearch.model

data class SearchResult(
    val title: String,
    val url: String,
    val content: String?
)

enum class Site{
    GOOGLE,
    NAVER,
    DAUM,
    ALL
}