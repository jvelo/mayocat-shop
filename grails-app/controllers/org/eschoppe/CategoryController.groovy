package org.eschoppe

import org.springframework.dao.DataIntegrityViolationException

class CategoryController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    static navigation = [
      order : 100,
      action : "list",
      title : "Categories",
      path : "category"
    ]
    
    static scaffold = true

}
