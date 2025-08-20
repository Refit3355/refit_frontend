package com.refit.app.ui.fake

import com.refit.app.data.product.model.Product

object ProductDummy {
    val products = listOf(
        Product(
            id = 101,
            image = "https://img.hankyung.com/photo/202005/9d763ebf57052c6fc0b2a75d7ad43a81.jpg", // 더미 이미지
            brand = "ABC",
            name = "비타민C 1000mg",
            discountRate = 15,
            price = 12900,
            discountedPrice = 10965
        ),
        Product(
            id = 100,
            image = "https://img.hankyung.com/photo/202005/9d763ebf57052c6fc0b2a75d7ad43a81.jpg",
            brand = "XYZ",
            name = "멀티비타민",
            discountRate = 10,
            price = 15000,
            discountedPrice = 13500
        ),
        Product(
            id = 99,
            image = "https://img.hankyung.com/photo/202005/9d763ebf57052c6fc0b2a75d7ad43a81.jpg",
            brand = "QWER",
            name = "프로바이오틱스",
            discountRate = 20,
            price = 20000,
            discountedPrice = 16000
        ),
        Product(
            id = 98,
            image = "https://img.hankyung.com/photo/202005/9d763ebf57052c6fc0b2a75d7ad43a81.jpg", // 더미 이미지
            brand = "ABC",
            name = "비타민C 1000mg",
            discountRate = 15,
            price = 12900,
            discountedPrice = 10965
        ),
        Product(
            id = 97,
            image = "https://img.hankyung.com/photo/202005/9d763ebf57052c6fc0b2a75d7ad43a81.jpg",
            brand = "XYZ",
            name = "멀티비타민",
            discountRate = 10,
            price = 15000,
            discountedPrice = 13500
        ),
        Product(
            id = 96,
            image = "https://img.hankyung.com/photo/202005/9d763ebf57052c6fc0b2a75d7ad43a81.jpg",
            brand = "QWER",
            name = "프로바이오틱스",
            discountRate = 20,
            price = 20000,
            discountedPrice = 16000
        )
    )
}
