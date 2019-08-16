package com.s16.poetry.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.s16.poetry.R
import com.s16.poetry.data.Record
import com.s16.utils.formatToViewDateDefaults
import com.s16.widget.RecyclerViewHolder
import java.util.*
import android.animation.Animator
import android.view.View
import android.view.animation.LinearInterpolator
import android.animation.ObjectAnimator
import androidx.core.content.ContextCompat
import com.s16.widget.CheckableCardView
import kotlin.math.max

class RecordsPagedAdapter: PagedListAdapter<Record, RecyclerViewHolder>(DIFF_CALLBACK) {

    private val mInterpolator: LinearInterpolator = LinearInterpolator()
    private val mDuration: Long = 300
    private var mLastPosition: Int = -1
    private var mItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: ViewGroup = LayoutInflater.from(parent.context).inflate(R.layout.list_item_note, parent, false) as ViewGroup

//        val cardView: CheckableCardView = view.findViewById(R.id.cardView)
//        cardView.isLongClickable = true

        return RecyclerViewHolder(view, R.id.noteTitle, R.id.noteContent, R.id.noteLastModify)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        getItem(position)?.let { record ->
            val label: TextView = holder[R.id.noteTitle]
            label.text = record.title

            val linesCount = record.text?.split(Regex("\\n"))?.size ?: 0
            val content: TextView = holder[R.id.noteContent]
            if (linesCount > 4) {
                content.maxLines = max(linesCount / 2, 4)
            } else {
                content.maxLines = 3
            }

            content.text = record.text

            val lastEdit: TextView = holder[R.id.noteLastModify]
            lastEdit.text = if (record.date != null) {
                Date(record.date!!).formatToViewDateDefaults()
            } else {
                ""
            }

            holder.itemView.setOnClickListener {
                if (mItemClickListener != null) {
                    mItemClickListener!!.onItemClick(holder.itemView, record.id, position)
                }
            }
        }

        animate(holder, position);
    }

    private fun animate(holder: RecyclerView.ViewHolder, position: Int) {
        if (position > mLastPosition) {
            for (anim in getAnimators(holder.itemView)) {
                anim.setDuration(mDuration).start()
                anim.interpolator = mInterpolator

            }
            mLastPosition = position
        } else {
            clearAnimation(holder.itemView)
        }
    }

    private fun getAnimators(view: View): Array<Animator> {
        if (view.measuredHeight <= 0) {
            val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1.0f)
            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.05f, 1.0f)
            return arrayOf<Animator>(scaleX, scaleY)
        }
        return arrayOf(
            ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1.0f),
            ObjectAnimator.ofFloat(view, "scaleY", 1.05f, 1.0f)
        )
    }

    private fun clearAnimation(view: View) {
        view.apply {
            alpha = 1f
            scaleY = 1f
            scaleX = 1f
            translationY = 0f
            translationX = 0f
            rotation = 0f
            rotationY = 0f
            rotationX = 0f
            pivotY = measuredHeight / 2f
            pivotX = measuredWidth / 2f
        }.animate().interpolator = null
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        mItemClickListener = listener;
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, id: Long, position: Int)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Record>() {
            override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean = oldItem == newItem
        }
    }
}

// MARK: Extensions

inline fun RecordsPagedAdapter.setItemClickListener(
    crossinline listener: (view: View, id: Long, position: Int) -> Unit) {

    val wrapper = object: RecordsPagedAdapter.OnItemClickListener {
        override fun onItemClick(view: View, id: Long, position: Int) {
            listener.invoke(view, id, position)
        }
    }
    setItemClickListener(wrapper)
}