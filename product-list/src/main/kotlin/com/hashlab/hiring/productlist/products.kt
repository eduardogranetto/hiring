package com.hashlab.hiring.productlist

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/v1/products/")
class ProductController(
  private val productService: ProductService
) {
  
  @GetMapping
  fun all(@RequestHeader userId: String) = ResponseEntity.ok(
    productService.list(userId)
  )
  
}

@Service
class ProductService(
  private val productRepository: ProductsIntegration,
  private val discountCalculatorIntegration: DiscountCalculatorIntegration
) {
  
  fun list(userId: String) = productRepository.ids().map {
    discountCalculatorIntegration.calculate(it, userId)
  }
  
}

interface ProductsIntegration {
  
  fun ids(): List<String>
  
}

interface DiscountCalculatorIntegration {
  
  fun calculate(productId: String, userId: String): Product
  
}

data class Product(
  val id: String,
  val price: Int,
  val title: String,
  val description: String,
  val discount: ProductDiscount
)

data class ProductDiscount(
  val pct: BigDecimal,
  val value: Int
)

@Component
class ProductsInMemoryIntegration : ProductsIntegration {
  
  private val productsIds = listOf(
    "c8276ce4-fc4e-11ea-a47c-23fcf1c286e9", "d9730c1a-fc4e-11ea-b1ec-9b7e662ad50a"
  )
  
  override fun ids() = productsIds
  
}

@Component
class DiscountCalculatorInMemoryIntegration : DiscountCalculatorIntegration {
  
  val products = listOf(
    Product(
      id = "c8276ce4-fc4e-11ea-a47c-23fcf1c286e9",
      price = 1499,
      title = "Broca Gedore Madeira 6mm",
      description = "Broca para madeira 6mm",
      discount = ProductDiscount(
        pct = BigDecimal.ZERO,
        value = 0
      )
    ),
    Product(
      id = "c8276ce4-fc4e-11ea-a47c-23fcf1c286e9",
      price = 1499,
      title = "Broca Gedore Madeira 6mm",
      description = "Broca para madeira 6mm",
      discount = ProductDiscount(
        pct = BigDecimal.ZERO,
        value = 0
      )
    ),
  )
  
  override fun calculate(productId: String, userId: String): Product {
  
  }
  
}