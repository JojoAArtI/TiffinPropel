package com.propel.tiffin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.propel.tiffin.ui.theme.SurfaceMuted
import com.propel.tiffin.ui.theme.TextPrimary
import com.propel.tiffin.ui.theme.TextTertiary

@Composable
fun TiffinSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "Search for dishes & kitchens",
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
        cursorBrush = SolidColor(TextPrimary),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(SurfaceMuted)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_search),
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextTertiary
                        )
                    }
                    innerTextField()
                }
                if (value.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✕",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextTertiary,
                        modifier = Modifier.clickable { onValueChange("") }
                    )
                }
            }
        }
    )
}
