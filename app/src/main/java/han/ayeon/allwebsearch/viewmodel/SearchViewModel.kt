package han.ayeon.allwebsearch.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import han.ayeon.allwebsearch.model.SearchResult
import han.ayeon.allwebsearch.model.Site
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import java.io.IOException

class SearchViewModel : ViewModel() {

    private val _googleResult = MutableLiveData<List<SearchResult>>()
    val googleResult: LiveData<List<SearchResult>> = _googleResult

    private val _naverResult = MutableLiveData<List<SearchResult>>()
    val naverResult: LiveData<List<SearchResult>> = _naverResult

    fun search(site: Site, keyWord: String, count: Int) {
        GlobalScope.launch {
            getGoogleSearch(keyWord, count)
        }

        GlobalScope.launch {
            getNaverSearch("android")
        }
    }

    private suspend fun getGoogleSearch(keyWord: String, count: Int) {
        try {
            val titleList = HashMap<String, String>()
            val contentList = HashMap<String, String>()

            val response: Connection.Response =
                Jsoup.connect("http://google.com/search?q=$keyWord&start=$count")
                    .method(Connection.Method.GET)
                    .execute()

            val document = response.parse() as Document
            val divElement = document.select("div")

            for (div in divElement) {
                val itemElements = div.getElementsByClass("g")
                for (item in itemElements) {
                    val titleElements = item.getElementsByClass("r")
                    val contentElements = item.getElementsByClass("s")

                    for (title in titleElements) {
                        val itemUrl =
                            title.getElementsByTag("a")[0].attr("href")
                        val itemTitle = Jsoup.clean(
                            title.getElementsByTag("h3").toString(),
                            Whitelist.none()
                        )
                        if (itemUrl.isNotBlank()) {
                            for (content in contentElements) {
                                val itemContent =
                                    content.getElementsByClass("st")[0].removeClass("f")
                                        .toString()
                                val noneHtml = Jsoup.clean(itemContent, Whitelist.none())
                                val itemContentReal =
                                    noneHtml.replaceAfter("&nbsp;", "").replace("&nbsp;", "")
                                contentList[itemUrl] = itemContentReal
                            }
                            titleList[itemUrl] = itemTitle
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                _googleResult.value = setResult(titleList, contentList)
            }


        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }


    private fun setResult(
        title: HashMap<String, String>,
        content: HashMap<String, String>
    ): ArrayList<SearchResult> {
        val list = ArrayList<SearchResult>()

        for (titleK in title) {
            val result = SearchResult(titleK.value, titleK.key, content[titleK.key])
            list.add(result)
        }
        return list
    }

    private suspend fun getNaverSearch(keyWord: String) {
        try {
            val titleList = HashMap<String, String>()
            val contentList = HashMap<String, String>()

            val response: Connection.Response =
                Jsoup.connect("https://m.search.naver.com/search.naver?where=m_view&query=$keyWord")
                    .method(Connection.Method.GET)
                    .execute()
            val document = response.parse() as Document
            val divElement = document.select("div")

            for (div in divElement) {
                val titleElements = div.getElementsByClass("api_txt_lines total_tit")
                for (item in titleElements) {
                    val blogUrl = item.attr("href")
                    val blogTitle = Jsoup.clean(item.toString(), Whitelist.none())
                    Log.e("ayhan", "type01 : ${blogTitle}, $blogUrl")
                    titleList[blogUrl] = blogTitle
                }

                val contentElements = div.getElementsByClass("total_dsc")
                for (item in contentElements) {
                    val blogUrl = item.attr("href")
                    val blogContent =
                        Jsoup.clean(
                            item.getElementsByClass("api_txt_lines dsc_txt")[0].toString(),
                            Whitelist.none()
                        )
                    Log.e("ayhan", "type02 : ${blogContent}, $blogUrl")
                    contentList[blogUrl] = blogContent
                }
            }

            withContext(Dispatchers.Main) {
                _naverResult.value = setResult(titleList, contentList)
            }


        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

}
