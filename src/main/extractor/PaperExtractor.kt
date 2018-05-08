package main.extractor

import main.model.PaperInfo
import org.jsoup.nodes.Element
import java.util.regex.Pattern

abstract class PaperExtractor : HtmlInfoExtractor<PaperInfo>() {
    protected val downloadCountPattern = Pattern.compile(REGEX_DOWNLOAD_COUNT)
    protected val citedCountPattern = Pattern.compile(REGEX_REF_COUNT)
    protected val xmlTextPattern = Pattern.compile(REGEX_XML_TEXT)
    protected val summaryInfoHeaderPattern = Pattern.compile(REGEX_SUMMARY_INFO_HEADER)

    fun getPaperTitle(body: Element) = body.getElementById("chTitle").text()

    fun getPaperKeywords(body: Element): List<String> {
        val keywordsListElement = body.getElementById("ChDivKeyWord")
        return if (keywordsListElement == null) {
            ArrayList()
        } else {
            element2StringList(keywordsListElement)
        }
    }

    fun getPaperDownloadCount(htmlString: String): Int {
        val text = getMatchedTextOnce(htmlString, downloadCountPattern)
        return if (text == "") {
            0
        } else {
            text.toInt()
        }
    }

    fun getPaperCitedCount(htmlString: String): Int {
        val text = getMatchedTextOnce(htmlString, citedCountPattern)
        return if (text == "") {
            0
        } else {
            text.toInt()
        }
    }

    companion object {
        //const val REF_LIST_URL = "http://www.cnki.net/kcms/detail/frame/list.aspx?dbcode=CJFQ&filename=njyd201504013&dbname=CJFQ2015&RefType=1&vl=MTU3NzJESDA2b0JRVDZ6ZDlUWC9xclJJMGZMS1dKaWZOZjl6bVJKaVlyWTlFWWVzTA=="
        const val REGEX_DOWNLOAD_COUNT = "【下载频次】(\\d+)</li>"
        const val REGEX_REF_COUNT = "【被引频次】(\\d+)</li>"
        const val REGEX_SUMMARY_INFO_HEADER = "【(.+)】"
        const val REGEX_XML_TEXT = ">(.-)</"
    }

}