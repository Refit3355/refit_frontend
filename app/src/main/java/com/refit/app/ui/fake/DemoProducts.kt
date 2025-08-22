package com.refit.app.ui.fake

import com.refit.app.data.product.model.Product

object DemoProducts {
    operator fun invoke(n: Int): List<Product> = List(n) { i ->
        Product(
            id = (i + 1).toLong(),
            brand = "라블",
            name = "다이브인 저분자 히알루론산",
            image = "",
            price = 36500,
            discountedPrice = 19500,
            discountRate = 47
        )
    }
}
