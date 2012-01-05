package org.eschoppe



import org.junit.*
import grails.test.mixin.*

@TestFor(CategoryController)
@Mock(Category)
class CategoryControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/category/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.categoryInstanceList.size() == 0
        assert model.categoryInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.categoryInstance != null
    }

    void testSave() {
        controller.save()

        assert model.categoryInstance != null
        assert view == '/category/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/category/show/1'
        assert controller.flash.message != null
        assert Category.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/category/list'


        populateValidParams(params)
        def category = new Category(params)

        assert category.save() != null

        params.id = category.id

        def model = controller.show()

        assert model.categoryInstance == category
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/category/list'


        populateValidParams(params)
        def category = new Category(params)

        assert category.save() != null

        params.id = category.id

        def model = controller.edit()

        assert model.categoryInstance == category
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/category/list'

        response.reset()


        populateValidParams(params)
        def category = new Category(params)

        assert category.save() != null

        // test invalid parameters in update
        params.id = category.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/category/edit"
        assert model.categoryInstance != null

        category.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/category/show/$category.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        category.clearErrors()

        populateValidParams(params)
        params.id = category.id
        params.version = -1
        controller.update()

        assert view == "/category/edit"
        assert model.categoryInstance != null
        assert model.categoryInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/category/list'

        response.reset()

        populateValidParams(params)
        def category = new Category(params)

        assert category.save() != null
        assert Category.count() == 1

        params.id = category.id

        controller.delete()

        assert Category.count() == 0
        assert Category.get(category.id) == null
        assert response.redirectedUrl == '/category/list'
    }
}
