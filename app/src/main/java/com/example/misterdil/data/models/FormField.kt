package com.example.misterdil.data.models

enum class FieldType {
    TEXT,
    TEXT_AREA,
    NUMBER,
    DATE,
    DROPDOWN,
    CHECKBOX,
    RADIO,
    MULTI_SELECT,
    FILE_UPLOAD,
    SECTION_HEADER,
    READ_ONLY
}

data class ConditionalRule(
    val fieldId: String,
    val operator: String, // "equals", "not_equals", "contains", "greater_than", "less_than"
    val value: String
)

data class FormField(
    val id: String,
    val label: String,
    val type: FieldType,
    val options: List<String>? = null,
    val required: Boolean = true,
    var value: String = "",
    val placeholder: String? = null,
    val helperText: String? = null,
    val visibleIf: ConditionalRule? = null,
    val maxLength: Int? = null,
    val min: Int? = null,
    val max: Int? = null
)

data class FormSection(
    val id: String,
    val title: String,
    val description: String? = null,
    val fields: List<FormField>,
    val order: Int = 0
)

data class FormSchema(
    val id: String,
    val title: String,
    val description: String,
    val sections: List<FormSection>,
    val dossierType: String // "ENTREE_EXPRESS" or "PERMIS_ETUDES"
)
