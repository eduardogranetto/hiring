package com.hashlab.hiring.discountcalculator

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

@RestController
@RequestMapping("/v1/products/{productId}/discount")
class DiscountController(
  private val discountService: DiscountService
){

  @GetMapping
  fun get(@PathVariable productId: String, @RequestParam userId: String) = ResponseEntity.ok(
      discountService.get(productId, userId)
  )

}

@Service
class DiscountService(
  private val userIntegration: UserIntegration,
  private val productIntegration: ProductIntegration,
  private val discountPolicies: List<DiscountPolicy>
){

  fun get(productId: String, userId: String) : ProductDiscount {
    val user = userIntegration.get(userId)
    val product = productIntegration.get(productId)
    return discountPolicies.fold(ProductDiscount.EMPTY){
      discount, discountPolicy -> discountPolicy.apply(product, discount, user)
    }
  }
  
}

data class ProductDiscount(
  val pct: BigDecimal = BigDecimal.ZERO,
  val value: Int = 0
){
  companion object{
    val EMPTY = ProductDiscount()
  }
}

data class User(
  val id: String,
  val firstName: String,
  val lastName: String,
  val dateOfBirth: LocalDate
)

data class Product(
  val priceInCents: Int
)

interface DiscountPolicy{
  
  fun apply(user: User, pct: BigDecimal) : BigDecimal
  
}

@Configuration
class DiscountPolicyConfiguration(
  private val birthdayPolicy: BirthdayPolicy,
  private val blackFridayPolicy: BlackFridayPolicy,
  private val maxDiscountPolicy: MaxDiscountPolicy
){

  @Bean
  fun discountPolicies() : List<DiscountPolicy> = arrayListOf(birthdayPolicy, blackFridayPolicy, maxDiscountPolicy)

}

@Component
class BirthdayPolicy : DiscountPolicy{
  
  override fun apply(user: User, pct: BigDecimal)  = if(user.dateOfBirth.isBirthDay()){
  
  }else{
    productDiscount
  }
  
}

fun LocalDate.isBirthDay() : Boolean {
  val today = LocalDate.now()
  return (today.month == month && today.dayOfMonth == dayOfMonth)
}

@Component
class BlackFridayPolicy : DiscountPolicy{
  
  override fun apply(user: User, pct: BigDecimal) : BigDecimal {
    TODO("Not yet implemented")
  }
  
}

@Component
class MaxDiscountPolicy : DiscountPolicy{
  
  override fun apply(user: User, pct: BigDecimal) : BigDecimal {
    TODO("Not yet implemented")
  }
  
}


interface ProductIntegration{
  
  fun get(id: String) : Product
  
}

interface UserIntegration{
  
  fun get(id: String) : User
  
}
