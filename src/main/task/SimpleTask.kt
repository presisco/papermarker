package main.task

class SimpleTask(private val exec: () -> Unit, private val onFinished: () -> Unit) : Thread() {
    override fun run() {
        exec()
        onFinished()
    }
}