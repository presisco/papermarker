package main

import main.extractor.SearchListExtractor
import org.junit.Test

class SearchListExtractorTest {
    private val url = "http://search.cnki.com.cn/Search.aspx?q=%E7%99%BD%E8%A1%80%E7%97%85&rank=&cluster=&val="

    @Test
    fun validate() {
        val extractor = SearchListExtractor()
        val linkList = extractor.extractInfoFromUrl(url)
        print(linkList.toString())
    }
}