package main

import main.extractor.ConferencePaperExtractor
import org.junit.Test

class ConferencePaperExtractorTest {
    private val url = "http://www.cnki.net/kcms/detail/detail.aspx?filename=KZJC201705004043&dbcode=IPFD&dbname=IPFD2017&v=MTk5NDlyaWZadTV2RXlqa1U3N05LVndTTGpmQmJiRzRIOWJNcW85RllPc0xEeE5JeUJVUzZ6ZDVQbjJVcW1CQUQ3dVFL"

    @Test
    fun validate() {
        val extractor = ConferencePaperExtractor()
        println(extractor.extractInfoFromUrl(url))
    }
}