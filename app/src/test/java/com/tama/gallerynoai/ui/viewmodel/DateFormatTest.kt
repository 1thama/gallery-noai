package com.tama.gallerynoai.ui.viewmodel

import org.junit.Test
import org.junit.Assert.assertNotNull
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.ZoneId
import java.time.Instant

class DateFormatTest {
    @Test
    fun testFormats() {
        val formats = listOf("dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "dd MMM yyyy")
        val timestamp = System.currentTimeMillis() / 1000
        val locales = listOf(Locale.US, Locale.GERMANY, Locale.FRANCE, Locale.JAPAN, Locale.CHINA)

        for (format in formats) {
            for (locale in locales) {
                // Test DateTimeFormatter
                val dtf = DateTimeFormatter.ofPattern(format, locale)
                    .withZone(ZoneId.systemDefault())
                val dtfResult = dtf.format(Instant.ofEpochSecond(timestamp))
                assertNotNull(dtfResult)
            }
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidFormat() {
        DateTimeFormatter.ofPattern("invalid", Locale.getDefault())
    }
}
