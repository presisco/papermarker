package main.extractor

import org.jsoup.Jsoup
import java.util.regex.Pattern

class KeywordPaperListExtractor : HtmlInfoExtractor<List<Pair<String, String>>>() {
    private val authorPattern = Pattern.compile(REGEX_AUTHOR)

    fun extractAllListFromKeyword(keyword: String): List<Pair<String, String>> {
        val paperList = ArrayList<Pair<String, String>>()
        paperList.addAll(extractJournalList(keyword))
        paperList.addAll(extractDoctorList(keyword))
        paperList.addAll(extractMasterList(keyword))
        paperList.addAll(extractLocalConferenceList(keyword))
        paperList.addAll(extractGlobalConferenceList(keyword))
        return paperList
    }

    fun extractJournalList(keyword: String) = extractInfoFromUrl(KEYWORD_JOURNAL_SEARCH.replace("KEYWORD", keyword), "journal")

    fun extractDoctorList(keyword: String) = extractInfoFromUrl(KEYWORD_DOCTOR_DEGREE_SEARCH.replace("KEYWORD", keyword), "degree")

    fun extractMasterList(keyword: String) = extractInfoFromUrl(KEYWORD_MASTER_DEGREE_SEARCH.replace("KEYWORD", keyword), "degree")

    fun extractLocalConferenceList(keyword: String) = extractInfoFromUrl(KEYWORD_LOCAL_CONFERENCE_SEARCH.replace("KEYWORD", keyword), "conference")

    fun extractGlobalConferenceList(keyword: String) = extractInfoFromUrl(KEYWORD_GLOBAL_CONFERENCE_SEARCH.replace("KEYWORD", keyword), "conference")

    override fun extractInfoFromUrl(url: String): List<Pair<String, String>> = extractInfoFromUrl(url, "journal")

    fun extractInfoFromUrl(url: String, type: String): List<Pair<String, String>> {
        val paperList = ArrayList<Pair<String, String>>()
        var isFinished = false
        var page = 1
        while (!isFinished) {
            val finalUrl = url.replace("PAGE", page.toString())
            var total = 0
            try {
                val htmlString = getHtmlString(finalUrl)

                val doc = Jsoup.parse(htmlString)
                total = doc.getElementById("pcount").text().toInt()
                val listElement = doc.getElementsByTag("li")
                for (element in listElement) {
                    val propElements = element.getElementsByTag("a")
                    paperList.add(Pair(type, "http://www.cnki.net" + propElements[0].attr("href")))
                }
            } catch (e: Exception) {
                println("link failed: $finalUrl")
                e.printStackTrace()
            } finally {
                if (paperList.size >= total) {
                    isFinished = true
                } else {
                    page++
                }
            }
        }
        return paperList
    }

    companion object {
        const val KEYWORD_JOURNAL_SEARCH = "http://www.cnki.net/kcms/detail/frame/asynlist.aspx?dbcode=CJFQ&CurDBCode=CJFQ&search=KEYWORD&code=&ds=frame/list.aspx&reftype=16&page=PAGE"
        const val KEYWORD_DOCTOR_DEGREE_SEARCH = "http://www.cnki.net/kcms/detail/frame/asynlist.aspx?dbcode=CDFD&CurDBCode=CDFD&search=KEYWORD&code=&ds=frame/list.aspx&reftype=16&page=PAGE"
        const val KEYWORD_MASTER_DEGREE_SEARCH = "http://www.cnki.net/kcms/detail/frame/asynlist.aspx?dbcode=CDFD&CurDBCode=CMFD&search=KEYWORD&code=&ds=frame/list.aspx&reftype=16&page=PAGE"
        const val KEYWORD_LOCAL_CONFERENCE_SEARCH = "http://www.cnki.net/kcms/detail/frame/asynlist.aspx?dbcode=CDFD&CurDBCode=CPFD&search=KEYWORD&code=&ds=frame/list.aspx&reftype=16&page=PAGE"
        const val KEYWORD_GLOBAL_CONFERENCE_SEARCH = "http://www.cnki.net/kcms/detail/frame/asynlist.aspx?dbcode=CJFQ&CurDBCode=IPFD&search=KEYWORD&code=&ds=frame/list.aspx&reftype=16&page=PAGE"
        const val REGEX_AUTHOR = "\\[\\d+\\] (.+).&nbsp&nbsp"
    }
}