package com.github.diydriller.databaseseeder.ui

import com.github.diydriller.databaseseeder.faker.FakerTypeRegistry.getFakerTypes
import java.awt.Component
import javax.swing.DefaultCellEditor
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

class FakerTypeCellEditor(
    private val model: DefaultTableModel
) : DefaultCellEditor(JComboBox<String>()) {

    private val combo = editorComponent as JComboBox<String>

    override fun getTableCellEditorComponent(
        table: JTable,
        value: Any?,
        isSelected: Boolean,
        row: Int,
        column: Int
    ): Component {
        val dbType = model.getValueAt(row, 1)?.toString()?.lowercase() ?: ""
        val options = getFakerTypes(dbType)
        combo.model = DefaultComboBoxModel(options)
        combo.selectedItem = value ?: "none"
        return combo
    }
}