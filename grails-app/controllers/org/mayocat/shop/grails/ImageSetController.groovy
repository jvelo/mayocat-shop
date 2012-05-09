package org.mayocat.shop.grails

import org.springframework.dao.DataIntegrityViolationException

import org.mayocat.shop.grails.util.ImageUtils

import java.text.DecimalFormat
import javax.imageio.ImageIO

import grails.plugins.springsecurity.Secured
import grails.converters.JSON

@Secured(['ROLE_ADMIN'])
class ImageSetController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    static scaffold = true

    def save() {
      def product = Product.get(params.product?.id)
      def page = Page.get(params.page?.id)
      def product_or_page = product ?: page
      if (product_or_page) {
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
            def extension = (file.contentType =~ /image\/([a-z]+)/)[0][1]
            def filename = file.originalFilename
            def bufferedImage = ImageIO.read(file.inputStream)
            def image = new Image([data:file, width:bufferedImage.width, height:bufferedImage.height, extension:extension])
            imageSet.addToImages(image)
            imageSet.filename = filename
            product_or_page.addToImages(imageSet)
            product_or_page.save()
            flash.message = "Image set created"
            def sizes = grailsApplication.config.mayocat.shop.thumbnailSizes
            if (product) {
              render(view: 'show', model: [imageSetInstance: imageSet, productid:product.id, thumbnailSizes: sizes])
            }
            else {
              render(view: 'show', model: [imageSetInstance: imageSet, pageid:page.id, thumbnailSizes: sizes])
            }
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
        def sizes = grailsApplication.config.mayocat.shop.thumbnailSizes
        def imageSetInstance = ImageSet.get(params.id)
        if (!imageSetInstance) {
    		  flash.message = message(code: 'default.not.found.message', args: [message(code: 'imageSet.label', default: 'ImageSet'), params.id])
          redirect(action: "list")
          return
        }
        [imageSetInstance: imageSetInstance, thumbnailSizes: sizes]
    }

    def editThumbnail() {
      def sizes = grailsApplication.config.mayocat.shop.thumbnailSizes
      def imageSet = ImageSet.get(params.id)
      if (!imageSet) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'imageSet.label', default: 'ImageSet'), params.id])
      }
      def original = imageSet.images.find{ it.hint == null}
      def target = imageSet.images.find{ it.hint == params.size }
      [imageSet: imageSet, original:original, size: params.size, dimensions:sizes[params.size], target: target]
    }

    def saveThumbnail() {
      def imageSet = ImageSet.get(params.id)
      def sizes = grailsApplication.config.mayocat.shop.thumbnailSizes
      def hasError = false 
      if (!imageSet) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'imageSet.label', default: 'ImageSet'), params.id])
        hasError = true
      }
      if (!params.x || !params.y || !params.width || !params.height) {
        flash.message = message(code: 'imageSet.editThumbnail.invalidQuery', default: 'Invalid query')
        hasError = true
      }
      if (!hasError) {
        def thumbnail
        def exists = true
        thumbnail = imageSet.images.find { it.hint == params.size }
        if (!thumbnail) {
          exists = false
          thumbnail = new Image()
        }
        def output = new ByteArrayOutputStream()
        def original = imageSet.images.find { it.hint == null }
        ImageUtils.crop(original.data, original.extension, sizes[params.size], params, output)
        thumbnail.identity {
          data = output.toByteArray()
          width = sizes[params.size].width
          height = sizes[params.size].height
          extension = original.extension
          hint = params.size
          x1 = params.x as Integer
          y1 = params.y as Integer
          x2 = x1 + (params.width as Integer)
          y2 = y1 + (params.height as Integer)
        }
        def result = [ok: true]
        if (exists) {
          thumbnail.save()
          render result as JSON
        }
        else {
          imageSet.addToImages(thumbnail)
          imageSet.save()
          render result as JSON
        }
      } 
    }

    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def expose() {
      def imageSet = ImageSet.get(params.imageid)
      def image
      image = imageSet.images.find { it.hint == params.size }
      if (imageSet && image) {
          response.setContentType("image/" + image.extension)
          response.setContentLength(image.data.size())
          response.setHeader('filename', imageSet.filename)
          OutputStream out = response.outputStream
          out.write(image.data)
          out.close()
      }
      else {
          response.sendError(404)
      }
    }

    def list() {
      def product = Product.get(params.productid)
      def page = Page.get(params.pageid)
      if (product) {
        [imageSetInstanceList: product.images]
      }
      else if (page) {
        [imageSetInstanceList: page.images]
      }
      else {
        response.sendError(404)
      }
    }

    def view() {
      def imageSet = ImageSet.get(params.id)
      def image
      image = imageSet.images.find { it.hint == params.hint }
      if (!image) {
        // If no image is found, try without the hint
        image = imageSet.images.find { it.hint == null }
      }
      if (imageSet && image) {
          response.setContentType("image/" + image.extension)
          response.setContentLength(image.data.size())
          response.setHeader('filename', "image." + image.extension)
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
