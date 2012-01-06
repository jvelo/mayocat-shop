package org.eschoppe



import org.junit.*
import grails.test.mixin.*

@TestFor(ImageSetController)
@Mock(ImageSet)
class ImageSetControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/imageSet/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.imageSetInstanceList.size() == 0
        assert model.imageSetInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.imageSetInstance != null
    }

    void testSave() {
        controller.save()

        assert model.imageSetInstance != null
        assert view == '/imageSet/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/imageSet/show/1'
        assert controller.flash.message != null
        assert ImageSet.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/imageSet/list'


        populateValidParams(params)
        def imageSet = new ImageSet(params)

        assert imageSet.save() != null

        params.id = imageSet.id

        def model = controller.show()

        assert model.imageSetInstance == imageSet
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/imageSet/list'


        populateValidParams(params)
        def imageSet = new ImageSet(params)

        assert imageSet.save() != null

        params.id = imageSet.id

        def model = controller.edit()

        assert model.imageSetInstance == imageSet
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/imageSet/list'

        response.reset()


        populateValidParams(params)
        def imageSet = new ImageSet(params)

        assert imageSet.save() != null

        // test invalid parameters in update
        params.id = imageSet.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/imageSet/edit"
        assert model.imageSetInstance != null

        imageSet.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/imageSet/show/$imageSet.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        imageSet.clearErrors()

        populateValidParams(params)
        params.id = imageSet.id
        params.version = -1
        controller.update()

        assert view == "/imageSet/edit"
        assert model.imageSetInstance != null
        assert model.imageSetInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/imageSet/list'

        response.reset()

        populateValidParams(params)
        def imageSet = new ImageSet(params)

        assert imageSet.save() != null
        assert ImageSet.count() == 1

        params.id = imageSet.id

        controller.delete()

        assert ImageSet.count() == 0
        assert ImageSet.get(imageSet.id) == null
        assert response.redirectedUrl == '/imageSet/list'
    }
}
