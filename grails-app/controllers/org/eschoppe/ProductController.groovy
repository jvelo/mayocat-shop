package org.eschoppe

import org.springframework.dao.DataIntegrityViolationException

class ProductController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    static navigation = [
      action : "list",
      title : "Products",
      order : 10,
      path : "product"
    ]

    static scaffold = true

    def expose() {
      render "Hello public !"
    }
}
