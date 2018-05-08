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
        val (_, _, result) = Fuel.get(url)
                .timeout(5000)
                .timeoutRead(5000)
                .responseString()
        return result.component1()
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