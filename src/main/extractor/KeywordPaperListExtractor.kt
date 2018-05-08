package main.extractor

import main.model.PaperListItem
import org.jsoup.Jsoup
import java.util.regex.Pattern

class KeywordPaperListExtractor : HtmlInfoExtractor<List<PaperListItem>>() {
    private val authorPattern = Pattern.compile(REGEX_AUTHOR)

    fun extractListFromKeyword(keyword: String) = extractInfoFromUrl(KEYWORD_SEARCH.replace("KEYWORD", keyword))

    override fun extractInfoFromUrl(url: String): List<PaperListItem> {
        val paperList = ArrayList<PaperListItem>()
        var isFinished = false
        var page = 1
        while (!isFinished) {
            val htmlString = getHtmlString(url.replace("PAGE", page.toString()))
            val doc = Jsoup.parse(htmlString)
            val total = doc.getElementById("pcount").text().toInt()
            val listElement = doc.getElementsByTag("li")
            for (element in listElement) {
                val propElements = element.getElementsByTag("a")

                paperList.add(
                        PaperListItem(
                                author = getMatchedTextOnce(htmlString!!, authorPattern),
                                title = propElements[0].text(),
                                link = "http://www.cnki.net" + propElements[0].attr("href"),
                                publisher = propElements[1].text(),
                                year = propElements[2].text().substring(0..3)
                        )
                )
            }

            if (paperList.size >= total) {
                isFinished = true
            } else {
                page++
            }
        }
        return paperList
    }

    companion object {
        const val KEYWORD_SEARCH = "http://www.cnki.net/kcms/detail/frame/asynlist.aspx?dbcode=CJFQ&CurDBCode=CJFQ&search=KEYWORD&code=&ds=frame/list.aspx&reftype=16&page=PAGE"
        const val REGEX_AUTHOR = "\\[\\d+\\] (.+).&nbsp&nbsp"
    }
}