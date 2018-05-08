package main.dialog

import main.PaperDatabaseHelper
import main.extractor.KeywordPaperListExtractor
import main.extractor.PaperInfoExtractor
import main.swingtoolbox.SwingLayoutHelper
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JTextField

class SearchDialog : Dialog() {
    private val searchButton = JButton("search!")
    private val searchKeyField = JTextField(48)
    private val paperInfoExtractor = PaperInfoExtractor()
    private val keywordPaperListExtractor = KeywordPaperListExtractor()
    private val paperDatabaseHelper = PaperDatabaseHelper()

    override fun init(): Dialog {
        getFrame().setSize(600, 100)

        searchButton.addActionListener(this)

        with(SwingLayoutHelper) {
            addLayout2Frame(getFrame(), {
                verticalBox(
                        horizontalBox(
                                JLabel("key words:"),
                                searchKeyField,
                                searchButton
                        )
                )
            })
        }
        return this
    }

    override fun update(data: Any?) {
        if (data !is String)
            return

        paperDatabaseHelper.setup(data)
    }

    override fun actionPerformed(e: ActionEvent) {
        when (e.source) {
            searchButton -> {
                val keyword = searchKeyField.text.replace(" ", "%20")
                val paperList = keywordPaperListExtractor.extractListFromKeyword(keyword)
                for (item in paperList) {
                    try {
                        val paperInfo = paperInfoExtractor.extractInfoFromUrl(item.link)
                        paperDatabaseHelper.addPaper(paperInfo)
                    } catch (e: Exception) {
                        println("error: ${e.message}, link: ${item.link}")
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}