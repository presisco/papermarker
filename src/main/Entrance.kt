package main

import main.dialog.MarkDialog
import main.dialog.SearchDialog
import main.dialog.WelcomeDialog

object Entrance {

    @JvmStatic
    fun main(args: Array<String>) {
        with(DialogScheduler) {
            registDialog("welcome", WelcomeDialog().init())
            registDialog("search", SearchDialog().init())
            registDialog("mark", MarkDialog().init())
            init("welcome")
        }
    }
}