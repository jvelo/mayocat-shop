package org.mayocat.shop.viewmodel.builder

import org.mayocat.shop.viewmodel.OrderViewModel

class OrderViewModelBuilder {

  def build(order) {
    
    return new OrderViewModel(
      id: order.id,
      total: order.grandTotal, 
      displayTotal: String.format("%10.2f", order.grandTotal),
    )
  }

}
