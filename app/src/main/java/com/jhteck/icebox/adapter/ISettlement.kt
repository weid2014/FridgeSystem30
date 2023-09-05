package com.jhteck.icebox.adapter

import com.jhteck.icebox.api.AvailRfid

/**
 * 结算
 */
interface ISettlement {
    fun settlement(availRfid: AvailRfid);
}