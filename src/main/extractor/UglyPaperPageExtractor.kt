package main.extractor

class UglyPaperPageExtractor : HtmlInfoExtractor<List<String>>() {

    override fun extractInfoFromUrl(url: String): List<String> {
        return ArrayList()
    }

    companion object {
        const val URL_SEARCH = "http://cdmd.cnki.com.cn/Article/DB-NUM-ID.htm"
        const val REGEX_INFO = "【(.-)】："
    }
}