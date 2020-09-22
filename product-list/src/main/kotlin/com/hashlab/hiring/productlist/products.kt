package com.hashlab.hiring.productlist

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.math.BigDecimal.TEN
import java.math.BigDecimal.ZERO

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
  private val productRepository: ProductRepository,
  private val discountCalculatorIntegration: DiscountCalculatorIntegration
) {
  
  fun list(userId: String) = productRepository.findAll().map {
      it.withDiscount(discountCalculatorIntegration.discount(it.id, userId))
  }
  
}

interface ProductRepository {
  
  fun findAll(): List<Product>
  
}

interface DiscountCalculatorIntegration {
  
  fun discount(productId: String, userId: String): ProductDiscount
  
}

data class Product(
  val id: String,
  val price: Int,
  val title: String,
  val description: String
)

data class ProductDiscount(
  val pct: BigDecimal = ZERO,
  val value: Int = 0
)

data class ProductWithDiscount(
  val product: Product,
  val discount: ProductDiscount
)

fun Product.withDiscount(discount: ProductDiscount) = ProductWithDiscount(
  product = this,
  discount = discount
)

@Component
class ProductInMemoryRepository : ProductRepository {
  
  val products = listOf(
    Product(
      id = "c8276ce4-fc4e-11ea-a47c-23fcf1c286e9",
      price = 1499,
      title = "Broca Gedore Madeira 6mm",
      description = "Broca para madeira 6mm"
    ),
    Product(
      id = "7cb8eee8-fcb4-11ea-af34-9f307f7fa81a",
      price = 9999,
      title = "Parafuso + Bucha 6mm - PCT 30",
      description = "Pacote com 30 parafusos e 30 buchas 6mm"
    )
  )
  
  override fun findAll() = products
  
}

@Component
class DiscountCalculatorInMemoryIntegration : DiscountCalculatorIntegration {
  
  val discounts = mapOf(
    "c8276ce4-fc4e-11ea-a47c-23fcf1c286e9" to ProductDiscount(TEN, 149),
    "7cb8eee8-fcb4-11ea-af34-9f307f7fa81a" to ProductDiscount(BigDecimal.valueOf(5), 99)
  )
  
  val empty = ProductDiscount()
  
  override fun discount(productId: String, userId: String) = discounts[productId] ?: empty
  
}