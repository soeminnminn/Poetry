package com.s16.poetry.sync

abstract class Differentiator<T>(
    private val left: List<T>,
    private val right: List<T>) : Comparator<T?> {

    private val diffResult: MutableList<DiffHolder<T>> = mutableListOf()

    fun different() : List<DiffHolder<T>> {
        val rTemp = right.toMutableList()
        for (l in left) {
            val r = similarOf(rTemp, l)
            if (r != null) {
                rTemp.remove(r);
            }
            when (compare(l, r)) {
                -1 -> {
                    // l.found, r.null -> (r.isDeleted) ? DiffStatus.LeftDelete : DiffStatus.RightInsert
                    if (r == null) {
                        val status = if (isLeftDeleted(l)) DiffStatus.LeftDelete else DiffStatus.RightInsert
                        diffResult.add(DiffHolder(status, l))
                    } else {
                        // l.found, r.found -> DiffStatus.RightUpdate
                        diffResult.add(DiffHolder(DiffStatus.RightUpdate, l))
                    }
                }
                1 -> {
                    if (r != null) {
                        // l.found, r.found -> DiffStatus.LeftUpdate
                        diffResult.add(DiffHolder(DiffStatus.LeftUpdate, r))
                    } else {
                        // l.found, r.null -> DiffStatus.UnChange
                        diffResult.add(DiffHolder(DiffStatus.UnChange, l))
                    }
                }
                else -> {
                    diffResult.add(DiffHolder(DiffStatus.UnChange, l))
                }
            }
        }

        for (r in rTemp) {
            when (compare(null, r)) {
                -1 -> {
                    // l.null, r.found -> (l.isDeleted) ? DiffStatus.RightDelete : DiffStatus.LeftInsert
                    val status = if (isRightDeleted(r)) DiffStatus.RightDelete else DiffStatus.LeftInsert
                    diffResult.add(DiffHolder(status, r))
                }
                else -> {
                    diffResult.add(DiffHolder(DiffStatus.UnChange, r))
                }
            }
        }

        return diffResult
    }

    abstract fun similarOf(list: List<T>, o: T) : T?

    abstract fun isLeftDeleted(o: T) : Boolean

    abstract fun isRightDeleted(o: T) : Boolean
}

enum class DiffStatus {
    UnChange,
    LeftUpdate,
    LeftInsert,
    LeftDelete,
    RightUpdate,
    RightInsert,
    RightDelete
}

data class DiffHolder<T>(
    val status: DiffStatus = DiffStatus.UnChange,
    val value: T
)