package com.example.a2048.ui.theme

import androidx.compose.ui.graphics.Color

object TileColors {
    // Цвета фона для каждого числа (расширенная разнообразная палитра)
    val backgroundColors = mapOf(
        2 to Color(0xFFE8F5E8),      // Светло-зелёный (пастельный)
        4 to Color(0xFFE3F2FD),      // Светло-голубой (пастельный)
        8 to Color(0xFFFFF3E0),      // Светло-оранжевый
        16 to Color(0xFFFFEBEE),     // Светло-розовый
        32 to Color(0xFFFCE4EC),     // Светло-красный
        64 to Color(0xFFF3E5F5),     // Светло-фиолетовый
        128 to Color(0xFFE8F5E8),    // Зелёный
        256 to Color(0xFFE3F2FD),    // Голубой
        512 to Color(0xFFFFF3E0),    // Оранжевый
        1024 to Color(0xFFFFEBEE),   // Розовый
        2048 to Color(0xFFFCE4EC),   // Красный
        4096 to Color(0xFFF3E5F5),   // Фиолетовый
        8192 to Color(0xFFE0F2F1),   // Бирюзовый
        16384 to Color(0xFFFBE9E7),  // Коралловый
        32768 to Color(0xFFECEFF1),  // Светло-серый
        65536 to Color(0xFF212121),  // Тёмно-серый
    )

    // Цвета текста для каждого числа (сочетание с фоном, гармоничные оттенки)
    val textColors = mapOf(
        2 to Color(0xFF388E3C),      // Тёмно-зелёный (гармонирует с пастельным зелёным)
        4 to Color(0xFF1976D2),      // Тёмно-синий (гармонирует с голубым)
        8 to Color(0xFFFF9800),      // Оранжевый (контрастный, но сочетающийся)
        16 to Color(0xFFE91E63),     // Розовый (комплиментарный к светло-розовому)
        32 to Color(0xFFF44336),     // Красный (яркий, сочетающийся с фоном)
        64 to Color(0xFF9C27B0),     // Фиолетовый (гармоничный оттенок)
        128 to Color(0xFF4CAF50),    // Зелёный (аналогичный фону)
        256 to Color(0xFF2196F3),    // Синий (комплиментарный)
        512 to Color(0xFFFF5722),    // Тёмно-оранжевый (контрастный)
        1024 to Color(0xFFE91E63),   // Розовый (повтор для гармонии)
        2048 to Color(0xFFFFC107),   // Жёлтый (яркий акцент на красном)
        4096 to Color(0xFF673AB7),   // Тёмно-фиолетовый (сочетающийся)
        8192 to Color(0xFF009688),   // Бирюзовый (аналогичный)
        16384 to Color(0xFFFF5722),  // Оранжевый (для кораллового)
    )

    // Функции для получения цветов (с fallback для неизвестных чисел)
    fun getBackgroundColor(number: Int): Color {
        return backgroundColors[number] ?: Color(0xFFAAAAAA)  // Тёмно-серый по умолчанию
    }

    fun getTextColor(number: Int): Color {
        return textColors[number] ?: Color(0xFFFFFFFF)  // Белый по умолчанию
    }
}

