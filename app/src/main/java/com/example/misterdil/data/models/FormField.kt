package com.example.misterdil.data.models

enum class FieldType {
    TEXT, NUMBER, DATE, DROPDOWN
}

data class FormField(
    val id: String,
    val label: String,
    val type: FieldType,
    val options: List<String>? = null,
    val required: Boolean = true,
    var value: String = ""
)
