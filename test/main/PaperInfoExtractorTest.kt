package main

import main.extractor.PaperInfoExtractor
import org.junit.Test

class PaperInfoExtractorTest {
    private val url = "http://www.cnki.net/kcms/detail/detail.aspx?dbcode=CJFQ&dbName=CJFQ2015&FileName=NJYD201504013&v=MjE1MzdiN01LeWZTYXJHNEg5VE1xNDlFWjRSOGVYMUx1eFlTN0RoMVQzcVRyV00xRnJDVVJMS2ZZdVpuRnlubVY"

    @Test
    fun validate() {
        val extractor = PaperInfoExtractor()
        val paperInfo = extractor.extractInfoFromUrl(url)
        print(paperInfo.title)
    }
}