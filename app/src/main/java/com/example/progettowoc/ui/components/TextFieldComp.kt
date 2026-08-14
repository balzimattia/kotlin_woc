package com.example.progettowoc.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextFieldComp(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    showErrorText: Boolean = true,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        singleLine = true,
        modifier = Modifier.semantics { testTag = label },
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
            cursorColor = MaterialTheme.colorScheme.onBackground,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
        isError = error != null,
        supportingText = { if (showErrorText) error?.let { Text(it) } },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        trailingIcon =
            if (isPassword) {
                {
                    val image = if (passwordVisible) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    }

                    val description = if (passwordVisible) "Nascondi password" else "Mostra password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = image,
                            contentDescription = description,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            } else {
                null
            }
    )
}