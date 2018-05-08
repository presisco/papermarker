package main.dialog

import main.DialogScheduler
import main.swingtoolbox.SwingLayoutHelper
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JTextField

class WelcomeDialog : Dialog() {
    private val selectFileButton = JButton("select file")
    private val selectedFileField = JTextField(80)
    private val searchButton = JButton("search")
    private val markButton = JButton("mark")
    private var filePath = ""

    override fun init(): Dialog {
        getFrame().setSize(600, 100)

        selectFileButton.addActionListener(this)
        searchButton.addActionListener(this)
        markButton.addActionListener(this)

        with(SwingLayoutHelper) {
            addLayout2Frame(getFrame(), {
                verticalBox(
                        horizontalBox(
                                JLabel("File:"),
                                selectedFileField,
                                selectFileButton
                        ),
                        horizontalBox(
                                searchButton,
                                markButton
                        )
                )
            })
        }

        return this
    }

    override fun update(data: Any?) {

    }

    override fun actionPerformed(e: ActionEvent) {
        when (e.source) {
            selectFileButton -> {
                val chooser = JFileChooser()
                chooser.fileSelectionMode = JFileChooser.FILES_ONLY
                val result = chooser.showDialog(JLabel(), "Select/Create")
                if (result == JFileChooser.APPROVE_OPTION) {
                    filePath = chooser.selectedFile.absolutePath
                    selectedFileField.text = filePath
                }
            }
            searchButton -> {
                DialogScheduler.jumpTo("search", filePath)
            }
            markButton -> {
                DialogScheduler.jumpTo("mark", filePath)
            }
        }
    }
}