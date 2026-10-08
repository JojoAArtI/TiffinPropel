package com.propel.tiffin.ui.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.propel.tiffin.data.model.Kitchen
import com.propel.tiffin.ui.components.DietBadge
import com.propel.tiffin.ui.components.PricePill
import com.propel.tiffin.ui.components.RatingCoin
import com.propel.tiffin.ui.theme.Carbon
import com.propel.tiffin.ui.theme.PaperWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KitchenCard(kitchen: Kitchen, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.outlinedCardColors(containerColor = PaperWhite),
        border = BorderStroke(1.dp, Carbon),
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = kitchen.name,
                style = MaterialTheme.typography.headlineSmall,
                color = Carbon
            )

            Text(
                text = kitchen.cuisine,
                style = MaterialTheme.typography.bodyMedium,
                color = Carbon,
                modifier = Modifier.padding(top = 4.dp)
            )

            FlowRow(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DietBadge(veg = kitchen.veg)
                RatingCoin(rating = kitchen.rating)
                PricePill(price = kitchen.pricePerTiffin)
            }
        }
    }
}
