package main.dialog

import main.PaperDatabaseHelper
import main.extractor.ConferencePaperExtractor
import main.extractor.DegreePaperExtractor
import main.extractor.JournalPaperExtractor
import main.extractor.KeywordPaperListExtractor
import main.swingtoolbox.SwingLayoutHelper
import main.task.SimpleTask
import java.awt.event.ActionEvent
import javax.swing.*

class SearchDialog : Dialog() {
    private val searchButton = JButton("search!")
    private val searchKeyField = JTextField(48)
    private val extractors = mapOf(
            "journal" to JournalPaperExtractor(),
            "degree" to DegreePaperExtractor(),
            "conference" to ConferencePaperExtractor()
    )
    private val keywordPaperListExtractor = KeywordPaperListExtractor()
    private val paperDatabaseHelper = PaperDatabaseHelper()
    private val progressBar = JProgressBar()

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
                        ),
                        progressBar
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
                SimpleTask({
                    searchButton.isEnabled = false

                    val keyword = searchKeyField.text.replace(" ", "%20")
                    val paperList = keywordPaperListExtractor.extractListFromKeyword(keyword)
                    val failList = ArrayList<Pair<String, String>>()
                    println("paper count for keyword: $keyword is ${paperList.size}")

                    progressBar.value = 0

                    var failCounter = 0
                    var totalCounter = 0
                    for (item in paperList) {
                        try {
                            val paperInfo = extractors[item.first]!!.extractInfoFromUrl(item.second)
                            paperDatabaseHelper.addPaper(paperInfo)
                        } catch (e: Exception) {
                            failList.add(item)
                            println("error: ${e.message}, link: ${item.second}")
                            if (e.message != null && !e.message!!.contains("SQLITE_CONSTRAINT_PRIMARYKEY")) {
                                e.printStackTrace()
                            } else if (e.message == null) {
                                e.printStackTrace()
                            }
                            failCounter++
                        } finally {
                            totalCounter++
                            progressBar.value = totalCounter * 100 / paperList.size
                        }
                    }
                    println("total papers: $totalCounter, saved: ${totalCounter - failCounter}")
                    JOptionPane.showMessageDialog(null, "total papers: $totalCounter, saved: ${totalCounter - failCounter}")
                }, {
                    searchButton.isEnabled = true
                }).start()
            }
        }
    }
}