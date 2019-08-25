package com.s16.poetry.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.s16.poetry.R

abstract class SwipeToDeleteCallback(private val context: Context): ItemTouchHelper.Callback() {

    private val mClearPaint: Paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val iconTintColor: Int
        get() {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.colorForeground, typedValue, true)
            return typedValue.data
        }

    private val backgroundColor: Int
        get() {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorAccent, typedValue, true)
            return ColorUtils.setAlphaComponent(typedValue.data, 60)
        }

    private val mBackground = ColorDrawable().apply {
        color = backgroundColor
    }
    private val deleteDrawable = ContextCompat.getDrawable(context, R.drawable.ic_delete_gray)
    private val intrinsicWidth = deleteDrawable?.intrinsicWidth
    private val intrinsicHeight = deleteDrawable?.intrinsicHeight

    init {
        if (deleteDrawable != null) {
            DrawableCompat.setTint(deleteDrawable, iconTintColor)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val itemHeight = itemView.height

        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        mBackground.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
        mBackground.draw(c)

        if (deleteDrawable != null && intrinsicHeight != null && intrinsicWidth != null) {
            val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2;
            val deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
            val deleteIconLeft = itemView.left + deleteIconMargin;
            val deleteIconRight = itemView.left + deleteIconMargin + intrinsicWidth;
            val deleteIconBottom = deleteIconTop + intrinsicHeight;

            deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            deleteDrawable.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.6f
}