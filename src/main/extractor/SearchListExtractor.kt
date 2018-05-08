package main.extractor

import org.jsoup.Jsoup
import java.util.regex.Pattern

class SearchListExtractor : HtmlInfoExtractor<List<String>>() {
    private val totalCountPattern = Pattern.compile(REGEX_TOTAL_COUNT)

    override fun extractInfoFromUrl(url: String): List<String> {
        val paperList = ArrayList<String>()
        var isFinished = false
        var counter = 0
        while (!isFinished) {
            val finalUrl = "$url&p=$counter"
            var total = 0
            try {
                val htmlString = getHtmlString(finalUrl)
                val doc = Jsoup.parse(htmlString)
                val body = doc.body()

                total = getMatchedTextOnce(body.getElementById("page").child(0).text(), totalCountPattern).toInt()

                val elementList = body.getElementsByClass("wz_tab")

                counter += elementList.size

                for (element in elementList) {
                    val contentElement = element.child(0)
                    val titleElement = contentElement.child(0).child(0)
                    paperList.add(titleElement.attr("href"))
                }

            } catch (e: Exception) {
                println("link failed: $finalUrl")
                e.printStackTrace()
            } finally {
                if (counter >= total) {
                    isFinished = true
                }
            }
        }
        return paperList
    }

    companion object {
        const val URL_SEARCH = "http://search.cnki.net/Search.aspx?q=TYPEKEY&rank=RANK&cluster=CLUSTER&val=VAL&p=FLOOR"
        const val REGEX_TOTAL_COUNT = "共找到相关记录(\\d+)条"
    }
}