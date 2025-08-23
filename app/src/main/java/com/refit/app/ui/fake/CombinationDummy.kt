package com.refit.app.ui.fake

import com.refit.app.data.local.combination.model.CombinationResponse
import com.refit.app.data.local.combination.model.CombinationItemImageDto

val fakeCombinations = listOf(
    CombinationResponse(
        combinationId = 1,
        memberId = 1,
        nickname = "aaaaaaaaaaaaaaaaaaaaaaaaaa",
        profileUrl = "https://refit-s3.s3.ap-northeast-2.amazonaws.com/default_profile/default.png",
        combinationName = "aaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        discountPrice = 175000,
        originalPrice = 236000,
        products = listOf(
            CombinationItemImageDto(1,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80"),
            CombinationItemImageDto(2,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80"),
            CombinationItemImageDto(3,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80"),
            CombinationItemImageDto(4,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80"),
            CombinationItemImageDto(5,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80"),
            CombinationItemImageDto(6,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80")
        ),
        likes = 11
    ),
    CombinationResponse(
        combinationId = 2,
        memberId = 2,
        nickname = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
        profileUrl = "https://refit-s3.s3.ap-northeast-2.amazonaws.com/default_profile/default.png",
        combinationName = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
        discountPrice = 120000,
        originalPrice = 180000,
        products = listOf(
            CombinationItemImageDto(6,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80"),
            CombinationItemImageDto(7,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80")
        ),
        likes = 8
    ),
    CombinationResponse(
        combinationId = 3,
        memberId = 3,
        nickname = "cccccccccccccccccccccccc",
        profileUrl = "https://refit-s3.s3.ap-northeast-2.amazonaws.com/default_profile/default.png",
        combinationName = "ccccccccccccccccccccccccc",
        discountPrice = 99000,
        originalPrice = 150000,
        products = listOf(
            CombinationItemImageDto(8,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80"),
            CombinationItemImageDto(9,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80"),
            CombinationItemImageDto(10,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80")
        ),
        likes = 258
    ),
    CombinationResponse(
        combinationId = 3,
        memberId = 3,
        nickname = "dddddddddddddddddddddddddddddddddd",
        profileUrl = "https://refit-s3.s3.ap-northeast-2.amazonaws.com/default_profile/default.png",
        combinationName = "ddddddddddddddddddddddddddddddddd",
        discountPrice = 99000,
        originalPrice = 150000,
        products = listOf(
            CombinationItemImageDto(8,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80"),
            CombinationItemImageDto(9,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80"),
            CombinationItemImageDto(10,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80"),
            CombinationItemImageDto(10,"https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/10/0000/0018/A00000018371404ko.jpg?qt=80")
        ),
        likes = 258
    )
)
