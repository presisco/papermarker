package main.dialog

import com.presisco.toolbox.StringToolbox
import main.PaperDatabaseHelper
import main.extractor.JournalPaperExtractor
import main.model.PaperInfo
import main.swingtoolbox.SwingLayoutHelper
import java.awt.event.ActionEvent
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JTextField

class MarkDialog : Dialog() {
    private val cancelButton = JButton("cancel changes")
    private val nextButton = JButton("next paper")
    private val indexField = JTextField(5)
    private val keywordField = JTextField(48)
    private val readByKeywordButton = JButton("read by keyword")
    private val gotoButton = JButton("go to")
    private val paperFieldMap = mapOf(
            "link" to JTextField(48),
            "title" to JTextField(60),
            "authors" to JTextField(48),
            "institutes" to JTextField(60),
            "keywords" to JTextField(60),
            "abstract" to JTextField(60),
            "publisher" to JTextField(60),
            "downloads" to JTextField(5),
            "cited" to JTextField(5),
            "year" to JTextField(4),
            "users" to JTextField(48),
            "algorithms" to JTextField(48),
            "data" to JTextField(48)
    )
    private val paperInfoExtractor = JournalPaperExtractor()
    private val paperDatabaseHelper = PaperDatabaseHelper()

    private lateinit var paperTitleList: List<String>
    private var index = 1
    private lateinit var originalPaperInfo: PaperInfo
    private lateinit var originalUsers: String
    private lateinit var originalAlgorithms: String
    private lateinit var originalData: String

    override fun init(): Dialog {
        getFrame().setSize(600, 100)

        val vbox = Box.createVerticalBox()
        for ((title, field) in paperFieldMap) {
            val row = Box.createHorizontalBox()
            row.add(JLabel(title))
            row.add(field)
            vbox.add(row)
        }

        paperFieldMap["title"]!!.isEditable = false


        readByKeywordButton.addActionListener(this)
        cancelButton.addActionListener(this)
        nextButton.addActionListener(this)
        gotoButton.addActionListener(this)

        with(SwingLayoutHelper) {
            addLayout2Frame(getFrame(), {
                verticalBox(
                        horizontalBox(
                                JLabel("read keyword"),
                                keywordField,
                                readByKeywordButton
                        ),
                        vbox,
                        horizontalBox(
                                cancelButton,
                                nextButton,
                                indexField,
                                gotoButton
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
        paperTitleList = paperDatabaseHelper.readPaperTitles()
    }

    fun fromField() = PaperInfo(
            title = paperFieldMap["title"]!!.text,
            abstract = paperFieldMap["abstract"]!!.text,
            year = paperFieldMap["year"]!!.text,
            authors = paperFieldMap["authors"]!!.text.split(","),
            keywords = paperFieldMap["keywords"]!!.text.split(","),
            institutes = paperFieldMap["keywords"]!!.text.split(","),
            refList = arrayListOf(),
            publisher = paperFieldMap["publisher"]!!.text,
            downloadCount = paperFieldMap["downloads"]!!.text.toInt(),
            citedCount = paperFieldMap["cited"]!!.text.toInt(),
            link = paperFieldMap["link"]!!.text
    )

    fun toField(paperInfo: PaperInfo) {
        with(paperInfo) {
            paperFieldMap["title"]!!.text = title
            paperFieldMap["abstract"]!!.text = abstract
            paperFieldMap["year"]!!.text = year
            paperFieldMap["authors"]!!.text = StringToolbox.concat(authors, ",")
            paperFieldMap["keywords"]!!.text = StringToolbox.concat(keywords, ",")
            paperFieldMap["institutes"]!!.text = StringToolbox.concat(institutes, ",")
            paperFieldMap["publisher"]!!.text = publisher
            paperFieldMap["downloads"]!!.text = downloadCount.toString()
            paperFieldMap["cited"]!!.text = citedCount.toString()
            paperFieldMap["link"]!!.text = link
        }
    }

    fun save() {
        val paper = fromField()
        paperDatabaseHelper.editPaper(paper)
        paperDatabaseHelper.editUsersInfo(paper.title, paperFieldMap["users"]!!.text)
        paperDatabaseHelper.editAlgorithmsInfo(paper.title, paperFieldMap["algorithms"]!!.text)
        paperDatabaseHelper.editDataInfo(paper.title, paperFieldMap["data"]!!.text)
    }

    fun loadPaperInfo(newIndex: Int) {
        index = newIndex
        originalPaperInfo = paperDatabaseHelper.readPaperInfo(paperTitleList[newIndex])
        originalUsers = StringToolbox.concat(paperDatabaseHelper.readUsersForPaper(originalPaperInfo.title), ",")
        originalAlgorithms = StringToolbox.concat(paperDatabaseHelper.readAlgorithmsForPaper(originalPaperInfo.title), ",")
        originalData = StringToolbox.concat(paperDatabaseHelper.readDataForPaper(originalPaperInfo.title), ",")
    }

    fun updateFields() {
        toField(originalPaperInfo)
        paperFieldMap["users"]!!.text = originalUsers
        paperFieldMap["algorithms"]!!.text = originalAlgorithms
        paperFieldMap["data"]!!.text = originalData
    }

    override fun actionPerformed(e: ActionEvent) {
        when (e.source) {
            readByKeywordButton -> {
                paperTitleList = paperDatabaseHelper.readPaperTitlesByKeyword(keywordField.text)
                loadPaperInfo(1)
                updateFields()
            }
            cancelButton -> {
                toField(originalPaperInfo)
                paperFieldMap["users"]!!.text = StringToolbox.concat(paperDatabaseHelper.readUsersForPaper(originalPaperInfo.title), ",")
                paperFieldMap["algorithms"]!!.text = StringToolbox.concat(paperDatabaseHelper.readAlgorithmsForPaper(originalPaperInfo.title), ",")
                paperFieldMap["data"]!!.text = StringToolbox.concat(paperDatabaseHelper.readDataForPaper(originalPaperInfo.title), ",")
            }
            nextButton -> {
                save()
                loadPaperInfo(index + 1)
                updateFields()
                indexField.text = index.toString()
            }
            gotoButton -> {
                save()
                loadPaperInfo(indexField.text.toInt())
                updateFields()
            }
        }
    }
}