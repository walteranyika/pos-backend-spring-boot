package com.walter.pos.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DateUtils {
    companion object {
        fun convertToStandard(localDateTime: LocalDateTime?): String? {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm a")
            return localDateTime?.format(dateTimeFormatter)
        }
    }
}