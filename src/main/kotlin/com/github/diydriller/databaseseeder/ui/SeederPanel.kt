package com.github.diydriller.databaseseeder.ui

import com.github.diydriller.databaseseeder.domain.DatabaseType
import com.github.diydriller.databaseseeder.seeder.SeederService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableColumn

class SeederPanel(private val project: Project) {

    val mainPanel: JPanel
    private val service = SeederService()

    private val dbTypeCombo = ComboBox(DatabaseType.entries.toTypedArray())
    private val dbUrlField = JBTextField("jdbc:mysql://localhost:3306/testdb")
    private val userField = JBTextField("root")
    private val passwordField = JBPasswordField()
    private val tableField = JBTextField("")
    private val countSpinner = JSpinner(SpinnerNumberModel(100, 1, 100000, 100))

    private val loadButton = JButton("🔍 Load Columns")
    private val generateButton = JButton("🚀 Generate Data")

    private val columnTableModel = DefaultTableModel(arrayOf("Column", "Database Type", "Faker Type"), 0)

    private val columnTable = JBTable(columnTableModel).apply {
        autoResizeMode = JTable.AUTO_RESIZE_OFF
        preferredScrollableViewportSize = Dimension(600, 250)

        columnModel.getColumn(0).preferredWidth = 200
        columnModel.getColumn(1).preferredWidth = 150
        columnModel.getColumn(2).preferredWidth = 150

        val comboColumn: TableColumn = columnModel.getColumn(2)
        comboColumn.cellEditor = FakerTypeCellEditor(columnTableModel)
    }

    private val logArea = JTextArea().apply {
        lineWrap = true
        isEditable = false
    }

    init {
        mainPanel = JPanel(BorderLayout()).apply {
            val topPanel = panel {
                row("DataSource:") { cell(dbTypeCombo) }
                row("URL:") { cell(dbUrlField).align(AlignX.FILL) }
                row("User:") { cell(userField).align(AlignX.FILL) }
                row("Password:") { cell(passwordField).align(AlignX.FILL) }
                row("Table:") { cell(tableField).align(AlignX.FILL) }
                row("Insert Count:") { cell(countSpinner) }
                row {
                    cell(loadButton)
                    cell(generateButton)
                }
            }.apply {
                border = JBUI.Borders.empty(12)
            }

            val tableScroll = JBScrollPane(
                columnTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
            )

            val logScroll = JBScrollPane(logArea).apply { preferredSize = Dimension(0, 200) }

            val centerPanel = JPanel(BorderLayout()).apply {
                add(tableScroll, BorderLayout.CENTER)
                add(logScroll, BorderLayout.SOUTH)
            }

            add(topPanel, BorderLayout.NORTH)
            add(centerPanel, BorderLayout.CENTER)
        }

        loadButton.addActionListener { onLoadColumns() }
        generateButton.addActionListener { onGenerateData() }
    }

    private fun onLoadColumns() {
        val url = dbUrlField.text.trim()
        val user = userField.text.trim()
        val password = String(passwordField.password)
        val table = tableField.text.trim()
        val type = dbTypeCombo.item

        try {
            appendLog("🔍 Loading columns from '$table'...")
            val columns = service.loadTableColumns(type, url, user, password, table)
            columnTableModel.rowCount = 0
            columns.forEach { col ->
                columnTableModel.addRow(arrayOf(col.name, col.type, "none"))
            }
            appendLog("✅ ${columns.size} columns loaded.")
        } catch (e: Exception) {
            Messages.showErrorDialog(project, e.message ?: "Unknown error", "Failed to Load Columns")
            appendLog("❌ Error: ${e.message}")
        }
    }

    private fun onGenerateData() {
        val url = dbUrlField.text.trim()
        val user = userField.text.trim()
        val password = String(passwordField.password)
        val table = tableField.text.trim()
        val type = dbTypeCombo.item
        val count = (countSpinner.value as Int)

        val fakerMapping = mutableMapOf<String, String>()
        for (i in 0 until columnTableModel.rowCount) {
            val column = columnTableModel.getValueAt(i, 0) as String
            val fakerType = columnTableModel.getValueAt(i, 2) as String
            if (fakerType != "none") fakerMapping[column] = fakerType
        }

        if (fakerMapping.isEmpty()) {
            Messages.showInfoMessage(project, "Please select at least one Faker type.", "No Mapping Selected")
            return
        }

        appendLog("🚀 Generating fake data for '$table'...")
        try {
            service.generateWithMapping(type, url, user, password, table, count, fakerMapping) { msg ->
                appendLog(msg)
            }
            appendLog("✅ Done.")
        } catch (e: Exception) {
            appendLog("❌ Generation failed: ${e.message}")
            Messages.showErrorDialog(project, e.message, "Error")
        }
    }

    private fun appendLog(text: String) {
        SwingUtilities.invokeLater {
            logArea.append(text + "\n")
            logArea.caretPosition = logArea.document.length
        }
    }
}