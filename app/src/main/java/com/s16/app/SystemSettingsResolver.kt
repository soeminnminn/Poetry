package com.s16.app

import android.annotation.TargetApi
import android.app.WallpaperManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.ColorUtils.colorToHSL


class SystemSettingsResolver(private val context: Context) {

    fun getString(setting: String): String {
        val resolver = context.contentResolver
        return Settings.System.getString(resolver, setting)
    }

    fun getInt(setting: String): Int {
        val resolver = context.contentResolver
        return Settings.System.getInt(resolver, setting, -1)
    }

    fun getLong(setting: String): Long {
        val resolver = context.contentResolver
        return Settings.System.getLong(resolver, setting, -1)
    }

    fun getFloat(setting: String): Float {
        val resolver = context.contentResolver
        return Settings.System.getFloat(resolver, setting, -1f)
    }

    companion object {
        const val OEM_BLACK_MODE = "oem_black_mode"
        const val OEM_BLACK_MODE_ACCENT_COLOR = "oem_black_mode_accent_color"
        const val OEM_BLACK_MODE_ACCENT_COLOR_INDEX = "oem_black_mode_accent_color_index"
    }
}

/*
// https://stackoverflow.com/questions/55787035/is-there-an-api-to-detect-which-theme-the-os-is-using-dark-or-light-or-other
enum class DarkThemeCheckResult {
    DEFAULT_BEFORE_THEMES, LIGHT, DARK, PROBABLY_DARK, PROBABLY_LIGHT, USER_CHOSEN
}

/**
 * Checks if image is bright and clean enough to support light text.
 *
 * @param source What to read.
 * @return Whether image supports dark text or not.
 * /
private fun calculateDarkHints(source: Bitmap?): Int {
    if (source == null) {
        return 0
    }

    val pixels = IntArray(source.width * source.height)
    var totalLuminance = 0.0
    val maxDarkPixels = (pixels.size * MAX_DARK_AREA) as Int
    var darkPixels = 0
    source.getPixels(
        pixels, 0 /* offset */, source.width, 0 /* x */, 0 /* y */,
        source.width, source.height
    )

    // This bitmap was already resized to fit the maximum allowed area.
    // Let's just loop through the pixels, no sweat!
    val tmpHsl = FloatArray(3)
    for (i in pixels.indices) {
        ColorUtils.colorToHSL(pixels[i], tmpHsl)
        val luminance = tmpHsl[2]
        val alpha = Color.alpha(pixels[i])
        // Make sure we don't have a dark pixel mass that will
        // make text illegible.
        if (luminance < DARK_PIXEL_LUMINANCE && alpha != 0) {
            darkPixels++
        }
        totalLuminance += luminance.toDouble()
    }

    var hints = 0
    val meanLuminance = totalLuminance / pixels.size
    if (meanLuminance > BRIGHT_IMAGE_MEAN_LUMINANCE && darkPixels < maxDarkPixels) {
        hints = hints or HINT_SUPPORTS_DARK_TEXT
    }
    if (meanLuminance < DARK_THEME_MEAN_LUMINANCE) {
        hints = hints or HINT_SUPPORTS_DARK_THEME
    }

    return hints
}

@JvmStatic
fun getIsOsDarkTheme(context: Context): DarkThemeCheckResult {
    when {
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.O -> return DarkThemeCheckResult.DEFAULT_BEFORE_THEMES
        Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val wallpaperColors = wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
                ?: return DarkThemeCheckResult.UNKNOWN
            val primaryColor = wallpaperColors.primaryColor.toArgb()
            val secondaryColor = wallpaperColors.secondaryColor?.toArgb() ?: primaryColor
            val tertiaryColor = wallpaperColors.tertiaryColor?.toArgb() ?: secondaryColor
            val bitmap = generateBitmapFromColors(primaryColor, secondaryColor, tertiaryColor)
            val darkHints = calculateDarkHints(bitmap)
            //taken from StatusBar.java , in updateTheme :
            val HINT_SUPPORTS_DARK_THEME = 1 shl 1
            val useDarkTheme = darkHints and HINT_SUPPORTS_DARK_THEME != 0
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1)
                return if (useDarkTheme)
                    DarkThemeCheckResult.UNKNOWN_MAYBE_DARK
                else DarkThemeCheckResult.UNKNOWN_MAYBE_LIGHT
            return if (useDarkTheme)
                DarkThemeCheckResult.MOST_PROBABLY_DARK
            else DarkThemeCheckResult.MOST_PROBABLY_LIGHT
        }
        else -> {
            return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> DarkThemeCheckResult.DARK
                Configuration.UI_MODE_NIGHT_NO -> DarkThemeCheckResult.LIGHT
                else -> DarkThemeCheckResult.MOST_PROBABLY_LIGHT
            }
        }
    }
}

fun generateBitmapFromColors(@ColorInt primaryColor: Int, @ColorInt secondaryColor: Int, @ColorInt tertiaryColor: Int): Bitmap {
    val colors = intArrayOf(primaryColor, secondaryColor, tertiaryColor)
    val imageSize = 6
    val bitmap = Bitmap.createBitmap(imageSize, 1, Bitmap.Config.ARGB_8888)
    for (i in 0 until imageSize / 2)
        bitmap.setPixel(i, 0, colors[0])
    for (i in imageSize / 2 until imageSize / 2 + imageSize / 3)
        bitmap.setPixel(i, 0, colors[1])
    for (i in imageSize / 2 + imageSize / 3 until imageSize)
        bitmap.setPixel(i, 0, colors[2])
    return bitmap
}
*/