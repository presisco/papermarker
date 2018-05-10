package main.extractor

import main.model.PaperInfo
import org.jsoup.Jsoup
import java.util.regex.Pattern

class ConferencePaperExtractor : PaperExtractor() {
    val yearPattern = Pattern.compile(REGEX_YEAR)
    val publisherPattern = Pattern.compile(REGEX_PUBLISHER)
    val publisherAltPattern = Pattern.compile(REGEX_PUBLISHER_ALT)
    val authorPattern = Pattern.compile(REGEX_AUTHOR)
    val institutePattern = Pattern.compile(REGEX_INSTITUTE)
    val abstractPattern = Pattern.compile(REGEX_ABSTRACT)

    override fun extractInfoFromUrl(url: String): PaperInfo {
        val htmlString = getHtmlString(url)!!
        val doc = Jsoup.parse(htmlString)
        val body = doc.body()

        var publisher = getMatchedTextOnce(htmlString, publisherPattern)
        if (publisher == "")
            publisher = getMatchedTextOnce(htmlString, publisherAltPattern)

        return PaperInfo(
                id = getPaperIdFromUrl(url),
                title = getPaperTitle(body),
                authors = getMatchedTextList(getMatchedTextOnce(htmlString, authorPattern), xmlTextPattern),
                year = getMatchedTextOnce(htmlString, yearPattern),
                publisher = publisher,
                type = getPaperTypeFromUrl(url),
                institutes = getMatchedTextList(getMatchedTextOnce(htmlString, institutePattern), xmlTextPattern),
                keywords = getPaperKeywords(body),
                link = url,
                abstract = getMatchedTextOnce(htmlString, abstractPattern),
                downloadCount = getPaperDownloadCount(htmlString),
                citedCount = getPaperCitedCount(htmlString)
        )
    }

    companion object {
        //const val REF_LIST_URL = "http://www.cnki.net/kcms/detail/frame/list.aspx?dbcode=CJFQ&filename=njyd201504013&dbname=CJFQ2015&RefType=1&vl=MTU3NzJESDA2b0JRVDZ6ZDlUWC9xclJJMGZMS1dKaWZOZjl6bVJKaVlyWTlFWWVzTA=="
        const val REGEX_YEAR = "【会议时间】(.+?)\\-"
        const val REGEX_AUTHOR = "【作者】[\\s+?]([\\s\\S]+?)</p>"
        const val REGEX_PUBLISHER = "【会议名称】(.+?)</li>"
        const val REGEX_PUBLISHER_ALT = "【会议录名称】[\\s\\S]+?>(.+?)</a>"
        const val REGEX_INSTITUTE = "【机构】[\\s+?]([\\s\\S]+?)</p>"
        const val REGEX_ABSTRACT = "【摘要】 <span>(.+?)</span>"
    }
}