package main.dialog

import main.DialogScheduler
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame

abstract class Dialog : ActionListener {
    private val frame = JFrame()

    protected fun getFrame() = frame

    abstract fun init(): Dialog
    abstract fun update(data: Any?)

    init {
        frame.defaultCloseOperation = JFrame.HIDE_ON_CLOSE
        frame.addWindowStateListener { event: WindowEvent ->
            if (event.newState == WindowEvent.WINDOW_DEACTIVATED) {
                DialogScheduler.windowClosed()
            }
        }
    }

    open fun show() {
        frame.isVisible = true
    }

    open fun hide() {
        frame.isVisible = false
    }

    class EventAdapter : WindowAdapter()
}