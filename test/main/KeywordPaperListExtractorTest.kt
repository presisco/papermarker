package main

import main.extractor.KeywordPaperListExtractor
import org.junit.Test

class KeywordPaperListExtractorTest {
    @Test
    fun validate() {
        val extractor = KeywordPaperListExtractor()
        val paperList = extractor.extractListFromKeyword("交通监控")
        println(paperList.size)
    }
}