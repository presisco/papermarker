package main.swingtoolbox

import java.awt.Component
import javax.swing.Box
import javax.swing.JFrame
import javax.swing.SpringLayout

object SwingLayoutHelper {

    fun addLayout2Frame(frame: JFrame, def: () -> Component) {
        frame.add(def())
    }

    fun addLayout2Frame(frame: JFrame, constraints: SpringLayout.Constraints, def: () -> Component) {
        frame.add(def(), constraints)
    }

    fun horizontalBox(vararg components: Component): Box {
        val box = Box.createHorizontalBox()
        for (component in components) {
            box.add(component)
        }
        return box
    }

    fun verticalBox(vararg components: Component): Box {
        val box = Box.createVerticalBox()
        for (component in components) {
            box.add(component)
        }
        return box
    }
}