package com.example.iobuild_kt.devices.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.iobuild_kt.core.i18n.lang

data class DeviceFormData(
    val name: String = "",
    val type: String = "Temperature",
    val location: String = "",
    val macAddress: String = "",
    val status: String = "Online"
)

private val deviceTypes = listOf(
    "Temperature", "Humidity", "Energy", "Water",
    "Lighting", "HVAC", "Access Control", "Construction", "Security"
)
private val statusOptions = listOf("Online", "Offline")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceFormDialog(
    title: String,
    initial: DeviceFormData,
    isSaving: Boolean = false,
    error: String? = null,
    onDismiss: () -> Unit,
    onSave: (DeviceFormData) -> Unit
) {
    var name by remember { mutableStateOf(initial.name) }
    var location by remember { mutableStateOf(initial.location) }
    var mac by remember { mutableStateOf(initial.macAddress) }
    var selectedType by remember { mutableStateOf(initial.type) }
    var selectedStatus by remember { mutableStateOf(initial.status) }
    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(lang("devices.name")) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))

                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                    OutlinedTextField(value = selectedType, onValueChange = {}, readOnly = true, label = { Text(lang("devices.type")) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) }, modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        deviceTypes.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = { selectedType = option; typeExpanded = false })
                        }
                    }
                }

                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text(lang("devices.location")) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                OutlinedTextField(value = mac, onValueChange = { mac = it }, label = { Text(lang("devices.mac")) }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = it }) {
                    OutlinedTextField(value = selectedStatus, onValueChange = {}, readOnly = true, label = { Text(lang("devices.status")) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) }, modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                    ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = { selectedStatus = option; statusExpanded = false })
                        }
                    }
                }

                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(DeviceFormData(name, selectedType, location, mac, selectedStatus)) },
                enabled = !isSaving && name.isNotBlank()
            ) {
                if (isSaving) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                else Text(lang("devices.save"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) { Text(lang("devices.cancel")) }
        }
    )
}
