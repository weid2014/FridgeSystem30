package com.jhteck.icebox.adapter

/**
 * 列表中项的操作
 */
interface ItemEditCellNumberAdapter<T> {

    /**
     * 编辑
     */
    fun onEdit(t: T,position:Int)
}
