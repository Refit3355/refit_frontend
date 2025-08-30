package com.refit.app.util.common

object PriceUtil {
    fun formatPrice(value: Long): String {
        return "%,d".format(value) + "원"
    }
}
