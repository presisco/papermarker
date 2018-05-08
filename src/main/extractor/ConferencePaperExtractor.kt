package main.extractor

import main.model.PaperInfo
import org.jsoup.Jsoup
import java.util.regex.Pattern

class ConferencePaperExtractor : PaperExtractor() {
    val yearPattern = Pattern.compile(REGEX_YEAR)
    val publisherPattern = Pattern.compile(REGEX_PUBLISHER)
    val authorPattern = Pattern.compile(REGEX_AUTHOR)
    val institutePattern = Pattern.compile(REGEX_INSTITUTE)
    val abstractPattern = Pattern.compile(REGEX_ABSTRACT)

    override fun extractInfoFromUrl(url: String): PaperInfo {
        val htmlString = getHtmlString(url)!!
        val doc = Jsoup.parse(htmlString)
        val body = doc.body()

        return PaperInfo(
                title = getPaperTitle(body),
                authors = getMatchedTextList(htmlString, authorPattern),
                year = getMatchedTextOnce(htmlString, yearPattern),
                publisher = getMatchedTextOnce(htmlString, publisherPattern),
                institutes = getMatchedTextList(htmlString, institutePattern),
                keywords = getPaperKeywords(body),
                link = url,
                abstract = getMatchedTextOnce(htmlString, abstractPattern),
                downloadCount = getPaperDownloadCount(htmlString),
                citedCount = getPaperCitedCount(htmlString)
        )
    }

    companion object {
        //const val REF_LIST_URL = "http://www.cnki.net/kcms/detail/frame/list.aspx?dbcode=CJFQ&filename=njyd201504013&dbname=CJFQ2015&RefType=1&vl=MTU3NzJESDA2b0JRVDZ6ZDlUWC9xclJJMGZMS1dKaWZOZjl6bVJKaVlyWTlFWWVzTA=="
        const val REGEX_YEAR = "【会议时间】(.-)\\-"
        const val REGEX_AUTHOR = "【作者】(.-)</p>"
        const val REGEX_PUBLISHER = "【会议名称】(.-)</li>"
        const val REGEX_INSTITUTE = "【机构】(.-)</p>"
        const val REGEX_ABSTRACT = "【摘要】 <span>(.-)</span>"
    }
}