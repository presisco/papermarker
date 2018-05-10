package main

import main.dialog.Dialog
import java.util.*

object DialogScheduler {
    private val dialogMap = mutableMapOf<String, Dialog>()
    private val callStack = Stack<Dialog>()

    fun registDialog(name: String, dialog: Dialog) {
        dialogMap[name] = dialog
    }

    fun return2previous() {
        callStack.pop()
        callStack.peek().show()
    }

    fun windowClosed() {
        callStack.pop()
        callStack.peek().show()
    }

    fun jumpTo(next: String) {
        callStack.peek().hide()
        callStack.pop()
        dialogMap[next]!!.show()
        callStack.push(dialogMap[next])
    }

    fun jumpTo(next: String, data: Any?) {
        dialogMap[next]!!.update(data)
        jumpTo(next)
    }

    fun add(next: String) {
        //callStack.peek().hide()
        dialogMap[next]!!.show()
        callStack.push(dialogMap[next])
    }

    fun add(next: String, data: Any?) {
        dialogMap[next]!!.update(data)
        add(next)
    }

    fun init(name: String) {
        dialogMap[name]!!.show()
        callStack.push(dialogMap[name])
    }
}