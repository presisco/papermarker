package main

import main.extractor.JournalPaperExtractor
import org.junit.Test

class JournalPaperExtractorTest {
    private val url = "http://www.cnki.net/kcms/detail/detail.aspx?filename=NFLJ201709071&dbcode=CJFQ&dbname=CJFD2017&v=MDMzOTYyaHN4RnJDVVJMS2ZZdVpuRnl6aFY3ek9LeXZIWkxHNEg5Yk1wbzlDWllSK0MzODR6aDRYbkQwTFRnMlg="
    @Test
    fun validate() {
        val extractor = JournalPaperExtractor()
        val paperInfo = extractor.extractInfoFromUrl(url)
        print(paperInfo.title)
        //val year = "<a onclick=\"getKns55NaviLinkIssue('','CJFQ','CJFQyearinfo','NFLJ','2017','09')\">2017年09期\n</a>"
        //val str = extractor.getPaperYear(year)
        //println(str)
    }
}