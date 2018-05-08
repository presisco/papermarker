package main

import main.extractor.KeywordPaperListExtractor
import main.extractor.PaperInfoExtractor
import main.model.PaperInfo
import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

object MainDialog: ActionListener{
    private val selectFileButton = JButton("select file")
    private val searchKeywordButton = JButton("search keyword")
    private val searchKeywordField = JTextField(48)
    private val addPaperButton = JButton("add paper")
    private val getInfoFromUrlButton = JButton("get info from url")
    private val srcFileField = JTextField(48)

    private val paperInfoExtractor = PaperInfoExtractor()
    private val keywordPaperListExtractor = KeywordPaperListExtractor()

    private var filepath = ""

    private val paperDatabaseHelper = PaperDatabaseHelper()

    private lateinit var currentPaper: PaperInfo

    private val paperFieldMap = mapOf(
            "link" to JTextField(48),
            "title" to JTextField(60),
            "authors" to JTextField(48),
            "keywords" to JTextField(60),
            "year" to JTextField(4),
            "users" to JTextField(48),
            "algorithms" to JTextField(48),
            "data" to JTextField(48)
    )

    fun GUI(){
        JFrame.setDefaultLookAndFeelDecorated(true)
        val frame = JFrame("Paper Marker")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        //frame.setSize(800, 800)

        val panel = JPanel()
        panel.layout = GridLayout(12, 1)

        selectFileButton.addActionListener(this)

        val srcFileRow = Box.createHorizontalBox()
        srcFileRow.add(JLabel("File:"))
        srcFileRow.add(srcFileField)
        srcFileRow.add(selectFileButton)
        panel.add(srcFileRow)

        searchKeywordButton.addActionListener(this)

        val keywordSearchRow = Box.createHorizontalBox()
        keywordSearchRow.add(JLabel("Keyword:"))
        keywordSearchRow.add(searchKeywordField)
        keywordSearchRow.add(searchKeywordButton)
        panel.add(keywordSearchRow)

        for( (title, field) in paperFieldMap){
            val row = Box.createHorizontalBox()
            row.add(JLabel(title))
            row.add(field)
            panel.add(row)
        }

        getInfoFromUrlButton.addActionListener(this)
        panel.add(getInfoFromUrlButton)

        addPaperButton.addActionListener(this)
        panel.add(addPaperButton)

        frame.add(panel, BorderLayout.CENTER)
        frame.pack()
        frame.isVisible = true
    }

    override fun actionPerformed(e: ActionEvent) {
        when(e.source){
            selectFileButton -> {
                val chooser = JFileChooser()
                chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                val result = chooser.showDialog(JLabel(), "Select")
                if(result == JFileChooser.APPROVE_OPTION) {
                    filepath = chooser.selectedFile.absolutePath
                    srcFileField.text = filepath
                    paperDatabaseHelper.setup(filepath)
                    paperDatabaseHelper.createTables()
                }
            }
            addPaperButton -> {
                val propMap = mutableMapOf<String,String>()

                paperFieldMap.map { (prop, field) -> propMap[prop] = field.text }

                if(propMap["title"]!!.isEmpty()){
                    JOptionPane.showMessageDialog(null,"empty title!")
                    return
                }

                val userString = propMap["users"]!!
                val algorithmsString = propMap["algorithms"]!!
                val dataString = propMap["data"]!!

                paperDatabaseHelper.addPaper(currentPaper, userString, algorithmsString, dataString)
            }
            getInfoFromUrlButton -> {
                val url = paperFieldMap["link"]!!.text
                currentPaper = paperInfoExtractor.extractInfoFromUrl(url)
                paperFieldMap["title"]!!.text = currentPaper.title
                paperFieldMap["authors"]!!.text = currentPaper.authors.toString()
                paperFieldMap["keywords"]!!.text = currentPaper.keywords.toString()
                paperFieldMap["year"]!!.text = currentPaper.year
            }
            searchKeywordButton -> {
                val keyword = searchKeywordField.text.replace(" ", "%20")
                val paperList = keywordPaperListExtractor.extractListFromKeyword(keyword)
                for (item in paperList) {
                    try {
                        val paperInfo = paperInfoExtractor.extractInfoFromUrl(item.link)
                        paperDatabaseHelper.addPaper(paperInfo, "", "", "")
                    } catch (e: Exception) {
                        println("error: ${e.message}, link: ${item.link}")
                    }
                }
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>){
        GUI()
    }
}