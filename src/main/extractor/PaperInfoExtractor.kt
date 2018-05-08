package main.extractor

import main.model.PaperInfo
import main.model.RefInfo
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.regex.Pattern

class PaperInfoExtractor : HtmlInfoExtractor<PaperInfo>() {
    private val downloadCountMatcher = Pattern.compile(REGEX_DOWNLOAD_COUNT)
    private val cittedCountMatcher = Pattern.compile(REGEX_REF_COUNT)

    fun getPaperRefInfo(url: String): List<RefInfo> {
        return ArrayList()
    }

    fun element2StringList(rootElement: Element): List<String> {
        val stringList = ArrayList<String>()

        for (element in rootElement.children()) {
            stringList.add(element.text())
        }

        return stringList
    }

    fun getPaperTitle(body: Element) = body.getElementById("chTitle").text()

    fun getPaperAbstract(body: Element) = body.getElementById("ChDivSummary").text()

    fun getPaperAuthors(body: Element): List<String> {
        val authorListElement = body.getElementsByClass("author summaryRight")[0].getElementsByTag("p")[0]
        return element2StringList(authorListElement)
    }

    fun getPaperKeywords(body: Element): List<String> {
        val keywordsListElement = body.getElementById("ChDivKeyWord")
        return if (keywordsListElement == null) {
            ArrayList()
        } else {
            element2StringList(keywordsListElement)
        }
    }

    fun getPaperDownloadCount(htmlString: String): Int {
        val countMatcher = downloadCountMatcher.matcher(htmlString)
        return if (countMatcher.find()) {
            countMatcher.group(1).toInt()
        } else {
            0
        }
    }

    fun getPaperCittedCount(htmlString: String): Int {
        val countMatcher = cittedCountMatcher.matcher(htmlString)
        return if (countMatcher.find()) {
            countMatcher.group(1).toInt()
        } else {
            0
        }
    }

    override fun extractInfoFromUrl(url: String): PaperInfo {
        val htmlString = getHtmlString(url)
        val doc = Jsoup.parse(htmlString)
        val body = doc.body()

        return PaperInfo(
                title = getPaperTitle(body),
                authors = getPaperAuthors(body),
                year = "",
                keywords = getPaperKeywords(body),
                link = url,
                abstract = getPaperAbstract(body),
                downloadCount = getPaperDownloadCount(htmlString!!),
                citedCount = getPaperCittedCount(htmlString),
                refList = getPaperRefInfo("")
        )
    }

    companion object {
        //const val REF_LIST_URL = "http://www.cnki.net/kcms/detail/frame/list.aspx?dbcode=CJFQ&filename=njyd201504013&dbname=CJFQ2015&RefType=1&vl=MTU3NzJESDA2b0JRVDZ6ZDlUWC9xclJJMGZMS1dKaWZOZjl6bVJKaVlyWTlFWWVzTA=="
        const val REGEX_DOWNLOAD_COUNT = "【下载频次】(\\d+)</li>"
        const val REGEX_REF_COUNT = "【被引频次】(\\d+)</li>"
    }

}