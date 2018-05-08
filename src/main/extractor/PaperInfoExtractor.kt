package main.extractor

import main.model.PaperInfo
import main.model.RefInfo
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.regex.Pattern

class PaperInfoExtractor : HtmlInfoExtractor<PaperInfo>() {
    private val downloadCountPattern = Pattern.compile(REGEX_DOWNLOAD_COUNT)
    private val citedCountPattern = Pattern.compile(REGEX_REF_COUNT)
    private val yearPattern = Pattern.compile(REGEX_YEAR)
    private val xmlTextPattern = Pattern.compile(REGEX_XML_TEXT)
    private val authorPattern = Pattern.compile(REGEX_AUTHOR_BLOCK)
    private val institutePattern = Pattern.compile(REGEX_INSTITUTE_BLOCK)

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

    fun getPaperAuthors(htmlString: String): List<String> {
        val list = ArrayList<String>()
        val blockMatcher = authorPattern.matcher(htmlString)
        if (blockMatcher.find()) {
            val authorString = blockMatcher.group(1)
            val authorMatcher = xmlTextPattern.matcher(authorString)
            while (authorMatcher.find()) {
                list.add(authorMatcher.group(1))
            }
        }
        return list
    }

    fun getInstitutes(htmlString: String): List<String> {
        val list = ArrayList<String>()
        val blockMatcher = institutePattern.matcher(htmlString)
        if (blockMatcher.find()) {
            val authorString = blockMatcher.group(1)
            val authorMatcher = xmlTextPattern.matcher(authorString)
            while (authorMatcher.find()) {
                list.add(authorMatcher.group(1))
            }
        }
        return list
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
        val countMatcher = downloadCountPattern.matcher(htmlString)
        return if (countMatcher.find()) {
            countMatcher.group(1).toInt()
        } else {
            0
        }
    }

    fun getPaperCitedCount(htmlString: String): Int {
        val countMatcher = citedCountPattern.matcher(htmlString)
        return if (countMatcher.find()) {
            countMatcher.group(1).toInt()
        } else {
            0
        }
    }

    fun getPublisher(body: Element) = body.getElementsByClass("detailLink")[0].child(0).text()

    fun getPaperYear(htmlString: String) = getMatchedTextOnce(htmlString, yearPattern)

    override fun extractInfoFromUrl(url: String): PaperInfo {
        val htmlString = getHtmlString(url)!!
        val doc = Jsoup.parse(htmlString)
        val body = doc.body()

        return PaperInfo(
                title = getPaperTitle(body),
                authors = getPaperAuthors(htmlString),
                year = getPaperYear(htmlString),
                publisher = getPublisher(body),
                institutes = getInstitutes(htmlString),
                keywords = getPaperKeywords(body),
                link = url,
                abstract = getPaperAbstract(body),
                downloadCount = getPaperDownloadCount(htmlString),
                citedCount = getPaperCitedCount(htmlString),
                refList = getPaperRefInfo("")
        )
    }

    companion object {
        //const val REF_LIST_URL = "http://www.cnki.net/kcms/detail/frame/list.aspx?dbcode=CJFQ&filename=njyd201504013&dbname=CJFQ2015&RefType=1&vl=MTU3NzJESDA2b0JRVDZ6ZDlUWC9xclJJMGZMS1dKaWZOZjl6bVJKaVlyWTlFWWVzTA=="
        const val REGEX_DOWNLOAD_COUNT = "【下载频次】(\\d+)</li>"
        const val REGEX_REF_COUNT = "【被引频次】(\\d+)</li>"
        const val REGEX_YEAR = ">(\\d+)年"
        const val REGEX_AUTHOR_BLOCK = "【作者】(.-)【"
        const val REGEX_INSTITUTE_BLOCK = "【机构】(.-)【"
        const val REGEX_XML_TEXT = ">(.-)</"
    }

}