package org.eschoppe

import org.springframework.dao.DataIntegrityViolationException
import java.text.DecimalFormat

class ImageSetController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    static scaffold = true

    def save() {
      def product = Product.get(params.product.id)
      if (product) {
        def imageSet = new ImageSet(params)
        def file = request.getFile('file')
        if (file && !file.isEmpty()) {
          if (!file.contentType.startsWith("image/")) {
            imageSet.errors.rejectValue('file', 'image.file.notAnImage', "Not an image.")
          }
          else if (file.size > Image.MAX_SIZE) {
            imageSet.errors.rejectValue('file', 'image.file.tooBig', [readableSize(Image.MAX_SIZE)] as Object[], "Image too big. Max size is {0}")
          }
          else {
            def image = new Image([data:file, hint: ''])
            imageSet.original = image
            imageSet.save()
            product.addToImages(imageSet)
            product.save()
            flash.message = "Image set created"
            render(view: 'show', model: [imageSetInstance: imageSet, productid:product.id, thumbnailSizes: ImageSet.THUMBNAIL_SIZES])
          }
        }
        else {
          imageSet.errors.rejectValue('file', 'image.file.missing', 'Image is mandatory')
        }
        if (imageSet.hasErrors()) {
          render(view: 'create', model: [imageSetInstance: imageSet])
        }
      }
      else {
        flash.message = "Image set could not be created : product not found"
        redirect(controller: "product", action: "list")
      }
    }

    def show() {
        def imageSetInstance = ImageSet.get(params.id)
        if (!imageSetInstance) {
    		  flash.message = message(code: 'default.not.found.message', args: [message(code: 'imageSet.label', default: 'ImageSet'), params.id])
          redirect(action: "list")
          return
        }

        [imageSetInstance: imageSetInstance, thumbnailSizes: ImageSet.THUMBNAIL_SIZES]
    }

    def view() {
      def imageSet = ImageSet.get(params.id)
      def image = imageSet.original
      if (imageSet && image) {
          response.setContentType("image/jpg")
          response.setContentLength(image.data.size())
          response.setHeader('filename', "image.jpg")
          OutputStream out = response.outputStream
          out.write(image.data)
          out.close()
      }
      else {
          response.sendError(404)
      }
    }

    def readableSize(size) {
      if (size <= 0) "0";
      def units = ["B", "KB", "MB", "GB", "TB" ];
      int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
      new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}
