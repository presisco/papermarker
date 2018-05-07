package main

import java.awt.BorderLayout
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

object MainDialog: ActionListener{
    private val selectFileButton = JButton("select file")
    private val addPaperButton = JButton("add paper")
    private val srcFileField = JTextField(48)

    private var filepath = ""

    private val paperDatabaseHelper = PaperDatabaseHelper()

    private val paperFieldMap = mapOf(
            "title" to JTextField(60),
            "author" to JTextField(48),
            "year" to JTextField(4),
            "link" to JTextField(48),
            "users" to JTextField(48),
            "algorithms" to JTextField(48),
            "data" to JTextField(48)
    )

    fun GUI(){
        JFrame.setDefaultLookAndFeelDecorated(true)
        val frame = JFrame("Paper Marker")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(800, 800)

        val panel = JPanel()
        panel.layout = GridLayout(9, 1)

        selectFileButton.addActionListener(this)

        val srcFileRow = Box.createHorizontalBox()
        srcFileRow.add(JLabel("File:"))
        srcFileRow.add(srcFileField)
        srcFileRow.add(selectFileButton)
        panel.add(srcFileRow)

        for( (title, field) in paperFieldMap){
            val row = Box.createHorizontalBox()
            row.add(JLabel(title))
            row.add(field)
            panel.add(row)
        }

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

                paperDatabaseHelper.addPaper(propMap)
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>){
        GUI()
    }
}