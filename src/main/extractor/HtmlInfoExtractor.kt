package main.extractor

import com.github.kittinunf.fuel.Fuel
import org.jsoup.nodes.Element
import java.util.regex.Pattern

abstract class HtmlInfoExtractor<out T> {

    fun getMatchedTextOnce(text: String, pattern: Pattern): String {
        val countMatcher = pattern.matcher(text)
        return if (countMatcher.find()) {
            countMatcher.group(1)
        } else {
            ""
        }
    }

    fun getMatchedTextList(text: String, pattern: Pattern): List<String> {
        val textList = ArrayList<String>()
        val countMatcher = pattern.matcher(text)
        while (countMatcher.find()) {
            textList.add(countMatcher.group(1))
        }
        return textList
    }

    fun getHtmlString(url: String): String? {
        var retryCounter = 5

        var (_, _, result) = Fuel.get(url)
                .timeout(5000)
                .timeoutRead(5000)
                .responseString()

        while (retryCounter > 0 && result == null) {
            println("link: $url received null, retry in 0.5 second")
            Thread.sleep(500)
            val triple = Fuel.get(url)
                    .timeout(5000)
                    .timeoutRead(5000)
                    .responseString()
            result = triple.third
            retryCounter--
        }

        val stringResult = result.component1()
        stringResult ?: throw IllegalStateException("link failed! tried 5 times")

        return stringResult
    }

    fun element2StringList(rootElement: Element): List<String> {
        val stringList = ArrayList<String>()

        for (element in rootElement.children()) {
            stringList.add(element.text())
        }

        return stringList
    }

    abstract fun extractInfoFromUrl(url: String): T
}