package org.mayocat.shop.grails

enum OrderStatus {

  NONE('none'),

  WAITING_FOR_PAYMENT('waiting_for_payment'),

  PAYMENT_FAILED('payment_failed'),

  PAID('paid'),

  PREPARED('prepared'),

  SHIPPED('shipped'),

  CANCELLED('cancelled')

  String code

  OrderStatus(String code) {
    this.code = code
  }
}
