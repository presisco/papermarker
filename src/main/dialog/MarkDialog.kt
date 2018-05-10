package main.dialog

import com.presisco.toolbox.StringToolbox
import main.PaperDatabaseHelper
import main.model.PaperInfo
import main.swingtoolbox.SwingLayoutHelper
import java.awt.event.ActionEvent
import javax.swing.*

class MarkDialog : Dialog() {
    private val cancelButton = JButton("cancel changes")
    private val nextButton = JButton("next paper")
    private val saveButton = JButton("save/add")
    private val indexField = JTextField(5)
    private val indexLabel = JLabel("/0")
    private val sqlField = JTextField(60)
    private val readBySqlButton = JButton("read by sql")
    private val gotoButton = JButton("go to")
    private val paperFieldMap = mapOf(
            "link" to JTextField(48),
            "id" to JTextField(48),
            "title" to JTextField(60),
            "authors" to JTextField(48),
            "institutes" to JTextField(60),
            "keywords" to JTextField(60),
            "abstract" to JTextArea(60, 60),
            "publisher" to JTextField(60),
            "downloads" to JTextField(5),
            "cited" to JTextField(5),
            "year" to JTextField(4),
            "users" to JTextField(48),
            "algorithms" to JTextField(48),
            "data" to JTextField(48)
    )
    private val paperDatabaseHelper = PaperDatabaseHelper()

    private lateinit var paperIdList: List<String>
    private var index = 1
    private lateinit var originalPaperInfo: PaperInfo
    private lateinit var originalUsers: String
    private lateinit var originalAlgorithms: String
    private lateinit var originalData: String

    override fun init(): Dialog {
        getFrame().setSize(600, 800)

        val vbox = Box.createVerticalBox()
        for ((title, field) in paperFieldMap) {
            val row = Box.createHorizontalBox()
            row.add(JLabel(title))
            row.add(when (field) {
                is JTextArea -> JScrollPane(field)
                else -> field
            })
            vbox.add(row)
        }

        (paperFieldMap["abstract"] as JTextArea).lineWrap = true

        readBySqlButton.addActionListener(this)
        cancelButton.addActionListener(this)
        nextButton.addActionListener(this)
        gotoButton.addActionListener(this)
        saveButton.addActionListener(this)

        with(SwingLayoutHelper) {
            addLayout2Frame(getFrame(), {
                verticalBox(
                        horizontalBox(
                                JLabel("sql"),
                                sqlField,
                                readBySqlButton
                        ),
                        vbox,
                        horizontalBox(
                                cancelButton,
                                saveButton,
                                nextButton,
                                indexField,
                                indexLabel,
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
        paperIdList = paperDatabaseHelper.readPaperIds()
    }

    fun fromField() = PaperInfo(
            id = paperFieldMap["id"]!!.text,
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
            paperFieldMap["id"]!!.text = id
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
        indexField.text = (index + 1).toString()
        originalPaperInfo = paperDatabaseHelper.readPaperInfo(paperIdList[newIndex])
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
            readBySqlButton -> {
                paperIdList = paperDatabaseHelper.readPaperIdsWithSql(sqlField.text)
                loadPaperInfo(0)
                updateFields()
            }
            cancelButton -> {
                toField(originalPaperInfo)
                paperFieldMap["users"]!!.text = StringToolbox.concat(paperDatabaseHelper.readUsersForPaper(originalPaperInfo.title), ",")
                paperFieldMap["algorithms"]!!.text = StringToolbox.concat(paperDatabaseHelper.readAlgorithmsForPaper(originalPaperInfo.title), ",")
                paperFieldMap["data"]!!.text = StringToolbox.concat(paperDatabaseHelper.readDataForPaper(originalPaperInfo.title), ",")
            }
            saveButton -> {
                save()
            }
            nextButton -> {
                save()
                loadPaperInfo(index + 1)
                updateFields()
            }
            gotoButton -> {
                save()
                loadPaperInfo(indexField.text.toInt())
                updateFields()
            }
        }
    }
}