package com.propel.tiffin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.propel.tiffin.ui.theme.Divider
import com.propel.tiffin.ui.theme.Surface
import com.propel.tiffin.ui.theme.SwiggyOrange
import com.propel.tiffin.ui.theme.TextPrimary

@Composable
fun StickerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Button(
        onClick = onClick,
        modifier = modifier.stickerPress(interactionSource),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SwiggyOrange,
            contentColor = Surface
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        interactionSource = interactionSource
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.stickerPress(interactionSource),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Surface,
            contentColor = TextPrimary
        ),
        border = BorderStroke(1.dp, Divider),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        interactionSource = interactionSource
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
