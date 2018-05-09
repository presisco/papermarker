package main.extractor

import main.model.PaperInfo
import main.model.RefInfo
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.regex.Pattern

class JournalPaperExtractor : PaperExtractor() {
    protected val yearPattern = Pattern.compile(REGEX_YEAR)

    fun getPaperRefInfo(url: String): List<RefInfo> {
        return ArrayList()
    }

    fun getPublisher(body: Element) = body.getElementsByClass("detailLink")[0].child(0).text()

    fun getPaperYear(htmlString: String) = getMatchedTextOnce(htmlString, yearPattern)

    fun getSummaryInfo(body: Element): Map<String, Any> {
        val elements = body.getElementsByClass("author summaryRight")[0].getElementsByTag("p")
        val result = mutableMapOf(
                "authors" to ArrayList<String>(),
                "institutes" to ArrayList<String>(),
                "abstract" to ""
        )

        for (element in elements) {
            when (getMatchedTextOnce(element.text(), summaryInfoHeaderPattern)) {
                "作者" -> (result["authors"] as ArrayList<String>).addAll(element2StringList(element))
                "机构" -> (result["institutes"] as ArrayList<String>).addAll(element2StringList(element))
                "摘要" -> result["abstract"] = element.child(0).text()
            }
        }

        return result
    }

    override fun extractInfoFromUrl(url: String): PaperInfo {
        val htmlString = getHtmlString(url)!!

        val doc = Jsoup.parse(htmlString)
        val body = doc.body()

        val sumInfoMap = getSummaryInfo(body)

        return PaperInfo(
                id = getPaperIdFromUrl(url),
                title = getPaperTitle(body),
                authors = sumInfoMap["authors"] as ArrayList<String>,
                year = getPaperYear(htmlString),
                publisher = getPublisher(body),
                type = getPaperTypeFromUrl(url),
                institutes = sumInfoMap["institutes"] as ArrayList<String>,
                keywords = getPaperKeywords(body),
                link = url,
                abstract = sumInfoMap["abstract"] as String,
                downloadCount = getPaperDownloadCount(htmlString),
                citedCount = getPaperCitedCount(htmlString),
                refList = getPaperRefInfo("")
        )
    }

    companion object {
        const val REGEX_YEAR = ">(\\d+?)年"
    }

}