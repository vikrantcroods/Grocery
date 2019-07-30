package com.croodstech.grocery.model

class ProductVo(
    val productId: Long,
    val haveVariation: Int,
    val name: String,
    val description: String,
    val productVarientsVos: List<ProductVarientsVo>,
    var imageSrc: String
)