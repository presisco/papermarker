package main

import main.extractor.JournalPaperExtractor
import org.junit.Test

class PaperExtractorTest {

    @Test
    fun validate() {
        val url = "http://www.cnki.net/kcms/detail/detail.aspx?filename=JSJX2017112900D&dbcode=CJFQ&dbname=CAPJ2015&v=AAA"
        val paperExtractor = JournalPaperExtractor()
        println(paperExtractor.getPaperIdFromUrl(url))
        println(paperExtractor.getPaperTypeFromUrl(url))
    }

}