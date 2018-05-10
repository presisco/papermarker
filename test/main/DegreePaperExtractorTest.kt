package main

import main.extractor.DegreePaperExtractor
import org.junit.Test

class DegreePaperExtractorTest {
    private val url = "http://www.cnki.net/kcms/detail/detail.aspx?filename=1017278958.nh&dbcode=CDFD&dbname=CDFD2018&v=MTUwNzViUElSK0MzODR6aDRYbkQwTFRnMlgyaHN4RnJDVVJMS2ZZdWR2RnluZ1dyM09WRjI2R2JHL0Z0akpwNUU="

    @Test
    fun validate() {
        val extractor = DegreePaperExtractor()
        println(extractor.extractInfoFromUrl(url))
    }
}