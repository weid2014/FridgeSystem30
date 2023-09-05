package com.jhteck.icebox.adapter

/**
 * 列表中项的操作
 */
interface ItemOperatorAdapter<T> {
    /**
     * 删除
     */
    fun onDelete(t: T)

    /**
     * 编辑
     */
    fun onEdit(t: T)
}
