package com.notesapp.data.model

import androidx.compose.ui.graphics.Color
import com.notesapp.ui.theme.HighPriorityColor
import com.notesapp.ui.theme.LowPriorityColor
import com.notesapp.ui.theme.MediumPriorityColor
import com.notesapp.ui.theme.NonePriorityColor


enum class Priority(val color: Color) {
    HIGH(HighPriorityColor),
    MEDIUM(MediumPriorityColor),
    LOW(LowPriorityColor),
    NONE(NonePriorityColor)
}