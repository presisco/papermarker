package main.extractor

import com.github.kittinunf.fuel.Fuel
import java.util.regex.Pattern

abstract class HtmlInfoExtractor<out T> {

    fun getMatchedTextOnce(text: String, pattern: Pattern): String {
        val countMatcher = pattern.matcher(text)
        countMatcher.find()
        return countMatcher.group(1)
    }

    fun getHtmlString(url: String): String? {
        val (_, _, result) = Fuel.get(url)
                .timeout(5000)
                .timeoutRead(5000)
                .responseString()
        return result.component1()
    }

    abstract fun extractInfoFromUrl(url: String): T
}