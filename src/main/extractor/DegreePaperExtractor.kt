package main.extractor

import main.model.PaperInfo
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.regex.Pattern

class DegreePaperExtractor : PaperExtractor() {
    val yearPattern = Pattern.compile(REGEX_YEAR)
    val publisherPattern = Pattern.compile(REGEX_PUBLISHER)
    val authorPattern = Pattern.compile(REGEX_AUTHOR)

    fun getPaperYear(htmlString: String) = getMatchedTextOnce(htmlString, yearPattern)

    fun getPublisher(htmlString: String) = getMatchedTextOnce(htmlString, publisherPattern)

    fun getAuthor(htmlString: String) = getMatchedTextOnce(htmlString, authorPattern)

    fun getAbstract(body: Element): String {
        val abstractElement = body.getElementById("ChDivSummary")
        return abstractElement.text()
    }

    override fun extractInfoFromUrl(url: String): PaperInfo {
        val htmlString = getHtmlString(url)!!
        val doc = Jsoup.parse(htmlString)
        val body = doc.body()

        val publisher = getPublisher(htmlString)

        return PaperInfo(
                title = getPaperTitle(body),
                authors = arrayListOf(getAuthor(htmlString)),
                year = getPaperYear(htmlString),
                publisher = publisher,
                institutes = arrayListOf(publisher),
                keywords = getPaperKeywords(body),
                link = url,
                abstract = getAbstract(body),
                downloadCount = getPaperDownloadCount(htmlString),
                citedCount = getPaperCitedCount(htmlString)
        )
    }

    companion object {
        //const val REF_LIST_URL = "http://www.cnki.net/kcms/detail/frame/list.aspx?dbcode=CJFQ&filename=njyd201504013&dbname=CJFQ2015&RefType=1&vl=MTU3NzJESDA2b0JRVDZ6ZDlUWC9xclJJMGZMS1dKaWZOZjl6bVJKaVlyWTlFWWVzTA=="
        const val REGEX_YEAR = "【作者基本信息】.->(.-)</a>"
        const val REGEX_AUTHOR = "【作者】./a>.-，(.-)，.-</p>"
        const val REGEX_PUBLISHER = "【网络出版投稿人】.->(.-)</a>"
    }
}