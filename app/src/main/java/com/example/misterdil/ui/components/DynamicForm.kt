package com.example.misterdil.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.misterdil.data.models.*

@Composable
fun DynamicForm(
    schema: FormSchema,
    onFieldValueChange: (fieldId: String, value: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Form header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    schema.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    schema.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        // Render sections
        schema.sections.forEach { section ->
            FormSectionCard(
                section = section,
                onFieldValueChange = onFieldValueChange
            )
        }
    }
}

@Composable
fun FormSectionCard(
    section: FormSection,
    onFieldValueChange: (fieldId: String, value: String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section header
            Text(
                section.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            if (section.description != null) {
                Text(
                    section.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Divider()
            
            // Render fields
            section.fields.forEach { field ->
                FormFieldRenderer(
                    field = field,
                    allFields = section.fields,
                    onValueChange = onFieldValueChange
                )
            }
        }
    }
}

@Composable
fun FormFieldRenderer(
    field: FormField,
    allFields: List<FormField>,
    onValueChange: (fieldId: String, value: String) -> Unit
) {
    // Check conditional visibility
    val isVisible = evaluateVisibility(field, allFields)
    
    if (!isVisible) return
    
    when (field.type) {
        FieldType.TEXT -> TextField(
            field = field,
            onValueChange = onValueChange,
            singleLine = true
        )
        FieldType.TEXT_AREA -> TextField(
            field = field,
            onValueChange = onValueChange,
            singleLine = false,
            minLines = 3
        )
        FieldType.NUMBER -> NumberField(
            field = field,
            onValueChange = onValueChange
        )
        FieldType.DATE -> DateField(
            field = field,
            onValueChange = onValueChange
        )
        FieldType.DROPDOWN -> DropdownField(
            field = field,
            onValueChange = onValueChange
        )
        FieldType.CHECKBOX -> CheckboxField(
            field = field,
            onValueChange = onValueChange
        )
        FieldType.RADIO -> RadioField(
            field = field,
            onValueChange = onValueChange
        )
        FieldType.MULTI_SELECT -> MultiSelectField(
            field = field,
            onValueChange = onValueChange
        )
        FieldType.FILE_UPLOAD -> FileUploadField(
            field = field,
            onValueChange = onValueChange
        )
        FieldType.SECTION_HEADER -> SectionHeader(field)
        FieldType.READ_ONLY -> ReadOnlyField(field)
    }
}

@Composable
fun TextField(
    field: FormField,
    onValueChange: (fieldId: String, value: String) -> Unit,
    singleLine: Boolean,
    minLines: Int = 1
) {
    var text by remember { mutableStateOf(field.value) }
    
    OutlinedTextField(
        value = text,
        onValueChange = { 
            text = it
            onValueChange(field.id, it)
        },
        label = { Text(if (field.required) "${field.label} *" else field.label) },
        placeholder = field.placeholder?.let { { Text(it) } },
        supportingText = field.helperText?.let { { Text(it) } },
        singleLine = singleLine,
        minLines = minLines,
        modifier = Modifier.fillMaxWidth(),
        isError = field.required && text.isBlank()
    )
}

@Composable
fun NumberField(
    field: FormField,
    onValueChange: (fieldId: String, value: String) -> Unit
) {
    var text by remember { mutableStateOf(field.value) }
    
    OutlinedTextField(
        value = text,
        onValueChange = { 
            if (it.all { char -> char.isDigit() || char == '-' }) {
                text = it
                onValueChange(field.id, it)
            }
        },
        label = { Text(if (field.required) "${field.label} *" else field.label) },
        placeholder = field.placeholder?.let { { Text(it) } },
        supportingText = field.helperText?.let { { Text(it) } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        isError = field.required && text.isBlank()
    )
}

@Composable
fun DateField(
    field: FormField,
    onValueChange: (fieldId: String, value: String) -> Unit
) {
    var text by remember { mutableStateOf(field.value) }
    
    OutlinedTextField(
        value = text,
        onValueChange = { 
            text = it
            onValueChange(field.id, it)
        },
        label = { Text(if (field.required) "${field.label} *" else field.label) },
        placeholder = { Text("JJ/MM/AAAA") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        isError = field.required && text.isBlank()
    )
}

@Composable
fun DropdownField(
    field: FormField,
    onValueChange: (fieldId: String, value: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(field.value) }
    
    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            label = { Text(if (field.required) "${field.label} *" else field.label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dérouler")
                }
            }
        )
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            field.options?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption = option
                        onValueChange(field.id, option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CheckboxField(
    field: FormField,
    onValueChange: (fieldId: String, value: String) -> Unit
) {
    var checked by remember { mutableStateOf(field.value == "true") }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { 
                checked = it
                onValueChange(field.id, if (it) "true" else "false")
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            field.label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun RadioField(
    field: FormField,
    onValueChange: (fieldId: String, value: String) -> Unit
) {
    var selectedOption by remember { mutableStateOf(field.value) }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            if (field.required) "${field.label} *" else field.label,
            style = MaterialTheme.typography.bodyMedium
        )
        
        field.options?.forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = {
                        selectedOption = option
                        onValueChange(field.id, option)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    option,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun MultiSelectField(
    field: FormField,
    onValueChange: (fieldId: String, value: String) -> Unit
) {
    var selectedOptions by remember { 
        mutableStateOf(
            if (field.value.isNotEmpty()) field.value.split(",") else emptyList()
        ) 
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            if (field.required) "${field.label} *" else field.label,
            style = MaterialTheme.typography.bodyMedium
        )
        
        field.options?.forEach { option ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = selectedOptions.contains(option),
                    onCheckedChange = { 
                        val newOptions = if (it) {
                            selectedOptions + option
                        } else {
                            selectedOptions - option
                        }
                        selectedOptions = newOptions
                        onValueChange(field.id, newOptions.joinToString(","))
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    option,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun FileUploadField(
    field: FormField,
    onValueChange: (fieldId: String, value: String) -> Unit
) {
    var fileName by remember { mutableStateOf(field.value) }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            if (field.required) "${field.label} *" else field.label,
            style = MaterialTheme.typography.bodyMedium
        )
        
        OutlinedButton(
            onClick = { /* TODO: Implement file picker */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AttachFile, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (fileName.isEmpty()) "Choisir un fichier" else fileName)
        }
    }
}

@Composable
fun SectionHeader(field: FormField) {
    Text(
        field.label,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun ReadOnlyField(field: FormField) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                field.label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                field.value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Evaluate conditional visibility based on visible_if rule
fun evaluateVisibility(field: FormField, allFields: List<FormField> = emptyList()): Boolean {
    val rule = field.visibleIf ?: return true
    
    // Find the referenced field
    val referencedField = allFields.find { it.id == rule.fieldId } ?: return true
    
    val referencedValue = referencedField.value
    
    return when (rule.operator) {
        "equals" -> referencedValue == rule.value
        "not_equals" -> referencedValue != rule.value
        "contains" -> referencedValue.contains(rule.value, ignoreCase = true)
        "greater_than" -> {
            try {
                referencedValue.toInt() > rule.value.toInt()
            } catch (e: NumberFormatException) {
                false
            }
        }
        "less_than" -> {
            try {
                referencedValue.toInt() < rule.value.toInt()
            } catch (e: NumberFormatException) {
                false
            }
        }
        else -> true
    }
}
