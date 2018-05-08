package main.dialog

import java.awt.event.ActionListener
import javax.swing.JFrame

abstract class Dialog : ActionListener {
    private val frame = JFrame()

    protected fun getFrame() = frame

    abstract fun init(): Dialog
    abstract fun update(data: Any?)

    init {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    }

    open fun show() {
        frame.isVisible = true
    }

    open fun hide() {
        frame.isVisible = false
    }
}