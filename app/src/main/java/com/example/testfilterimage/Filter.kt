package com.example.testfilterimage

import androidx.annotation.ColorInt

enum class Filter(
    val label: String,
    @param:ColorInt val colorInt: Int
) {
    RED("Red", 0xFFE53935.toInt()),
    ORANGE("Orange", 0xFFFB8C00.toInt()),
    AMBER("Amber", 0xFFFFB300.toInt()),
    YELLOW("Yellow", 0xFFFDD835.toInt()),
    GREEN("Green", 0xFF43A047.toInt()),
    TEAL("Teal", 0xFF00897B.toInt()),
    CYAN("Cyan", 0xFF00ACC1.toInt()),
    BLUE("Blue", 0xFF1E88E5.toInt()),
    PURPLE("Purple", 0xFF8E24AA.toInt()),
    PINK("Pink", 0xFFD81B60.toInt()),
}
