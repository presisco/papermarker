package main.dialog

import main.PaperDatabaseHelper
import main.extractor.PaperInfoExtractor
import main.swingtoolbox.SwingLayoutHelper
import java.awt.event.ActionEvent
import javax.swing.Box
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JTextField

class MarkDialog : Dialog() {
    private val addPaperButton = JButton("add paper")
    private val getInfoFromUrlButton = JButton("get info from url")
    private val saveButton = JButton("save to database")
    private val nextButton = JButton("next paper")
    private val indexField = JTextField(5)
    private val gotoButton = JButton("go to")
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
    private val paperInfoExtractor = PaperInfoExtractor()
    private val paperDatabaseHelper = PaperDatabaseHelper()

    override fun init(): Dialog {
        getFrame().setSize(600, 100)

        val vbox = Box.createVerticalBox()
        for ((title, field) in paperFieldMap) {
            val row = Box.createHorizontalBox()
            row.add(JLabel(title))
            row.add(field)
            vbox.add(row)
        }

        with(SwingLayoutHelper) {
            addLayout2Frame(getFrame(), {
                verticalBox(
                        vbox,
                        horizontalBox(
                                saveButton,
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
    }

    override fun actionPerformed(e: ActionEvent?) {
    }
}