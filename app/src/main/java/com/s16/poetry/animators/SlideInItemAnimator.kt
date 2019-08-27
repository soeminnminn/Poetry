package com.s16.poetry.animators

import androidx.recyclerview.widget.RecyclerView
import android.view.animation.AccelerateDecelerateInterpolator
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.animation.Animator
import androidx.recyclerview.widget.SimpleItemAnimator

class SlideInItemAnimator : SimpleItemAnimator() {

    private var mPendingAdd = mutableListOf<RecyclerView.ViewHolder>()
    private var mPendingRemove = mutableListOf<RecyclerView.ViewHolder>()

    override fun runPendingAnimations() {
        val animationDuration = 300
        if (mPendingAdd.isNotEmpty()) {
            for (viewHolder in mPendingAdd) {
                val target = viewHolder.itemView
                target.pivotX = target.measuredWidth / 2F
                target.pivotY = target.measuredHeight / 2F

                val animator = AnimatorSet()

                animator.playTogether(
                    ObjectAnimator.ofFloat(target, "translationX", -target.measuredWidth.toFloat(), 0.0f),
                    ObjectAnimator.ofFloat(target, "alpha", target.alpha, 1.0f)
                )

                animator.setTarget(target)
                animator.duration = animationDuration.toLong()
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.startDelay = (animationDuration * viewHolder.position / 10).toLong()
                animator.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        mPendingAdd.remove(viewHolder)
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
                animator.start()
            }
        }
        if (mPendingRemove.isNotEmpty()) {
            for (viewHolder in mPendingRemove) {
                val target = viewHolder.itemView
                target.pivotX = target.measuredWidth / 2F
                target.pivotY = target.measuredHeight / 2F

                val animator = AnimatorSet()

                animator.playTogether(
                    ObjectAnimator.ofFloat(target, "translationX", 0.0f, target.measuredWidth.toFloat()),
                    ObjectAnimator.ofFloat(target, "alpha", target.alpha, 0.0f)
                )

                animator.setTarget(target)
                animator.duration = animationDuration.toLong()
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.startDelay = (animationDuration * viewHolder.position / 10).toLong()
                animator.start()
            }
        }
    }

    override fun animateRemove(viewHolder: RecyclerView.ViewHolder): Boolean {
        mPendingRemove.add(viewHolder)
        return false
    }

    override fun animateAdd(viewHolder: RecyclerView.ViewHolder): Boolean {
        viewHolder.itemView.alpha = 0.0f
        return mPendingAdd.add(viewHolder)
    }

    override fun animateMove(
        holder: RecyclerView.ViewHolder?,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean = false

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder?,
        newHolder: RecyclerView.ViewHolder?,
        fromLeft: Int,
        fromTop: Int,
        toLeft: Int,
        toTop: Int
    ): Boolean = false

    override fun endAnimation(viewHolder: RecyclerView.ViewHolder) {}

    override fun endAnimations() {}

    override fun isRunning(): Boolean {
        return mPendingAdd.isNotEmpty() || mPendingRemove.isNotEmpty()
    }

}