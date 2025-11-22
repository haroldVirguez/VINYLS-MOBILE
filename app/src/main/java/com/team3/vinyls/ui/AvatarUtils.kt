package com.team3.vinyls.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.team3.vinyls.R

object AvatarUtils {

    private val PALETTE_RES = intArrayOf(
        R.color.accent_purple,
        R.color.accent_dark_red,
        R.color.accent_dark_purple,
        R.color.card_stroke
    )

    fun setInitialsAvatar(target: ImageView, name: String?, seed: Int?) {
        val ctx = target.context
        val initials = extractInitials(name)
        val color = pickColor(ctx, seed, name)
        val sizePx = dpToPx(ctx, 60) // tamaño por defecto del avatar
        val bmp = createInitialsBitmap(sizePx, initials, color)
        target.setImageBitmap(bmp)
    }

    private fun extractInitials(name: String?): String {
        if (name.isNullOrBlank()) return "?"
        val parts = name.trim().split(Regex("\\s+"))
        return when {
            parts.size == 1 -> parts[0].take(1).uppercase()
            else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
        }
    }

    private fun pickColor(ctx: Context, seed: Int?, name: String?): Int {
        val idx = when {
            seed != null -> Math.abs(seed) % PALETTE_RES.size
            !name.isNullOrBlank() -> Math.abs(name.hashCode()) % PALETTE_RES.size
            else -> 0
        }
        return ContextCompat.getColor(ctx, PALETTE_RES[idx])
    }

    private fun dpToPx(ctx: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            ctx.resources.displayMetrics
        ).toInt()
    }

    private fun createInitialsBitmap(sizePx: Int, initials: String, textColor: Int): Bitmap {
        val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = sizePx * 0.42f
            isFakeBoldText = true
        }


        val bounds = Rect()
        paintText.getTextBounds(initials, 0, initials.length, bounds)
        if (bounds.width() > sizePx * 0.75f) {
            paintText.textSize = paintText.textSize * (sizePx * 0.75f / bounds.width())
            paintText.getTextBounds(initials, 0, initials.length, bounds)
        }

        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val textY = cy - (paintText.descent() + paintText.ascent()) / 2


        canvas.drawText(initials, cx, textY, paintText)

        return bmp
    }
}
