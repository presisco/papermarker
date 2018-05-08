package main

import java.awt.Component
import javax.swing.JFrame
import javax.swing.SpringLayout

object SwingLayoutHelper {

    fun addLayout2Frame(frame: JFrame, def: () -> Component) {
        frame.add(def())
    }

    fun addLayout2Frame(frame: JFrame, constraints: SpringLayout.Constraints, def: () -> Component) {
        frame.add(def(), constraints)
    }
}