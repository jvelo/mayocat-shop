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

/*
    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [categoryInstanceList: Category.list(params), categoryInstanceTotal: Category.count()]
    }

    def create() {
        [categoryInstance: new Category(params)]
    }

    def save() {
        def categoryInstance = new Category(params)
        if (!categoryInstance.save(flush: true)) {
            render(view: "create", model: [categoryInstance: categoryInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'category.label', default: 'Category'), categoryInstance.id])
        redirect(action: "show", id: categoryInstance.id)
    }

    def show() {
        def categoryInstance = Category.get(params.id)
        if (!categoryInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'category.label', default: 'Category'), params.id])
            redirect(action: "list")
            return
        }

        [categoryInstance: categoryInstance]
    }

    def edit() {
        def categoryInstance = Category.get(params.id)
        if (!categoryInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'category.label', default: 'Category'), params.id])
            redirect(action: "list")
            return
        }

        [categoryInstance: categoryInstance]
    }

    def update() {
        def categoryInstance = Category.get(params.id)
        if (!categoryInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'category.label', default: 'Category'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (categoryInstance.version > version) {
                categoryInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'category.label', default: 'Category')] as Object[],
                          "Another user has updated this Category while you were editing")
                render(view: "edit", model: [categoryInstance: categoryInstance])
                return
            }
        }

        categoryInstance.properties = params

        if (!categoryInstance.save(flush: true)) {
            render(view: "edit", model: [categoryInstance: categoryInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'category.label', default: 'Category'), categoryInstance.id])
        redirect(action: "show", id: categoryInstance.id)
    }

    def delete() {
        def categoryInstance = Category.get(params.id)
        if (!categoryInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'category.label', default: 'Category'), params.id])
            redirect(action: "list")
            return
        }

        try {
            categoryInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'category.label', default: 'Category'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'category.label', default: 'Category'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
    */
}
