package com.duoc.materiald.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val AppTypography = Typography(
   titleLarge = TextStyle(
       fontSize = 22.sp,
       fontWeight = FontWeight.SemiBold
   ),
    bodyMedium = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal, //400
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium //500-600
    )

)