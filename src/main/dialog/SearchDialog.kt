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
        progressBar.isStringPainted = true
        progressBar.string = "idle"

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
                    progressBar.minimum = 0
                    progressBar.string = "loading paper list"

                    val keyword = searchKeyField.text.replace(" ", "%20")
                    val paperList = keywordPaperListExtractor.extractAllListFromKeyword(keyword)
                    val failList = ArrayList<Pair<String, String>>()
                    println("paper count for keyword: $keyword is ${paperList.size}")

                    progressBar.value = 0
                    progressBar.maximum = paperList.size

                    var failCounter = 0
                    var totalCounter = 0
                    for (item in paperList) {
                        totalCounter++
                        progressBar.value = totalCounter
                        progressBar.string = "loading paper $totalCounter / ${paperList.size} "
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
                        }
                    }
                    println("total papers: $totalCounter, saved: ${totalCounter - failCounter}")
                    JOptionPane.showMessageDialog(null, "total papers: $totalCounter, saved: ${totalCounter - failCounter}")
                }, {
                    searchButton.isEnabled = true
                    progressBar.string = "idle"
                }).start()
            }
        }
    }
}