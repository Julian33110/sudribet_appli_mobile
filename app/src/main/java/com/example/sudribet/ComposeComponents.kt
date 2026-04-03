package com.example.sudribet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Couleurs personnalisées (correspondant à colors.xml)
val NeonGreen = Color(0xFF39FF14)
val NeonRed = Color(0xFFFF3131)
val GlassWhite = Color(0x22FFFFFF)
val GlassBorder = Color(0x44FFFFFF)
val SurfaceDarker = Color(0xFF0A0A0A)
val PrimaryColor = Color(0xFFFF7941)

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(GlassWhite, RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column { content() }
    }
}

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(end = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) PrimaryColor else GlassWhite,
        border = if (isSelected) null else BorderStroke(1.dp, GlassBorder)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun MatchItemCompose(
    match: Match,
    onMatchClick: (Match) -> Unit,
    onBetClick: (Match, Int) -> Unit
) {
    GlassCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onMatchClick(match) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = match.categorie.uppercase(),
                    color = PrimaryColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                if (match.isLive) {
                    Text(
                        text = "● LIVE",
                        color = NeonGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = match.heure,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Equipe A
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = match.equipeA, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onBetClick(match, 1) },
                    colors = ButtonDefaults.buttonColors(containerColor = GlassWhite),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "1", color = Color.Gray, fontSize = 10.sp)
                        Text(text = String.format("%.2f", match.coteA), color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Score ou VS + bouton nul
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp)) {
                if (match.isLive) {
                    Text(
                        text = "${match.scoreA} - ${match.scoreB}",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                } else {
                    Text(text = "VS", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
                if (match.coteNul != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onBetClick(match, 3) },
                        colors = ButtonDefaults.buttonColors(containerColor = GlassWhite),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "X", color = Color.Gray, fontSize = 10.sp)
                            Text(text = String.format("%.2f", match.coteNul), color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Equipe B
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = match.equipeB, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onBetClick(match, 2) },
                    colors = ButtonDefaults.buttonColors(containerColor = GlassWhite),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "2", color = Color.Gray, fontSize = 10.sp)
                        Text(text = String.format("%.2f", match.coteB), color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
