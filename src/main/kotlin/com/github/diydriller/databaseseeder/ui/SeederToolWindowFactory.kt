package com.github.diydriller.databaseseeder.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class SeederToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val seederPanel = SeederPanel(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(seederPanel.mainPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}