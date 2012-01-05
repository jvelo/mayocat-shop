package org.eschoppe



import org.junit.*
import grails.test.mixin.*

@TestFor(ProductController)
@Mock(Product)
class ProductControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/product/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.productInstanceList.size() == 0
        assert model.productInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.productInstance != null
    }

    void testSave() {
        controller.save()

        assert model.productInstance != null
        assert view == '/product/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/product/show/1'
        assert controller.flash.message != null
        assert Product.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/product/list'


        populateValidParams(params)
        def product = new Product(params)

        assert product.save() != null

        params.id = product.id

        def model = controller.show()

        assert model.productInstance == product
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/product/list'


        populateValidParams(params)
        def product = new Product(params)

        assert product.save() != null

        params.id = product.id

        def model = controller.edit()

        assert model.productInstance == product
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/product/list'

        response.reset()


        populateValidParams(params)
        def product = new Product(params)

        assert product.save() != null

        // test invalid parameters in update
        params.id = product.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/product/edit"
        assert model.productInstance != null

        product.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/product/show/$product.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        product.clearErrors()

        populateValidParams(params)
        params.id = product.id
        params.version = -1
        controller.update()

        assert view == "/product/edit"
        assert model.productInstance != null
        assert model.productInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/product/list'

        response.reset()

        populateValidParams(params)
        def product = new Product(params)

        assert product.save() != null
        assert Product.count() == 1

        params.id = product.id

        controller.delete()

        assert Product.count() == 0
        assert Product.get(product.id) == null
        assert response.redirectedUrl == '/product/list'
    }
}
