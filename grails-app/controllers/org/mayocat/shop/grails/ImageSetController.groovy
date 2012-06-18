package org.mayocat.shop.grails

import org.mayocat.shop.grails.ResourceController.CachedResource;
import org.mayocat.shop.grails.util.ImageUtils

import com.google.common.cache.CacheBuilderSpec
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.net.HttpHeaders;
import com.google.common.hash.Hashing

import javax.servlet.http.HttpServletResponse;

import java.text.DecimalFormat
import javax.imageio.ImageIO

import grails.plugins.springsecurity.Secured
import grails.converters.JSON

@Secured(['ROLE_ADMIN'])
class ImageSetController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    static scaffold = true

    private static LoadingCache<String, CachedResource> cache;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static class CachedImage {
        def byte[] data
        def String eTag
        def long lastModifiedTime
        def String filename
        def String extension

        private CachedImage(final String filename, final String extension, final byte[] data) {
            this.filename = filename
            this.extension = extension
            this.data = data
            this.eTag = Hashing.md5().hashBytes(data).toString()
            this.lastModifiedTime = roundedTimestamp()
        }

        private long roundedTimestamp() {
            return (System.currentTimeMillis() / 1000) * 1000
        }
    }

    private static class ImageLoader extends CacheLoader<String, CachedImage> {
        @Override
        public CachedImage load(String key) throws Exception {
            def id = key.split(":")[0]
            def hint = key.split(":")[1]
            if (hint == "_") {
              hint = null 
            } 
            def imageSet = ImageSet.get(id)
            def image = imageSet.images.find { it.hint == hint }
            if (image == null) {
                return null
            }
            return new CachedImage(imageSet.filename, image.extension, image.data)
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Lazy cache initialization
     *
     * @return the resource cache
     */
    def getCache() {
        if (this.cache == null) {
            this.cache = CacheBuilder.from(CacheBuilderSpec.parse("maximumSize=100"))
                    .build(new ImageLoader())
        }
        this.cache
    }

    /**
     * Checks whether the request for the passed resource is cached on the client (browser), checking both
     * last modification date and etag headers.
     *
     * @param request the request to check client caching for
     * @param resource the server-side resource to check client caching for
     * @return true if the client has not-expired cache resource, false otherwise
     */
    private boolean isCachedByClient(request, image) {
        return (image.eTag == request.getHeader(HttpHeaders.IF_NONE_MATCH)) ||
        (request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE) >= image.lastModifiedTime)
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
    @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
    def expose() {
        def image = this.getCache().getUnchecked(params.imageid + ":" + (params.size ?: "_"))
        
        if (image == null) {
            response.sendError(404)
            return
        }
        else if (this.isCachedByClient(request, image)) {
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        response.setContentType("image/" + image.extension)
        response.setContentLength(image.data.size())
        response.setHeader('filename', image.filename)
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, image.lastModifiedTime)
        response.setHeader(HttpHeaders.ETAG, image.eTag)
        
        OutputStream out = response.outputStream
        try {
            out.write(image.data)
        }
        finally {
            out.close()
        }
    }

    def save() {
      def product = null
      def page = null
      if (params.type == 'product') {
        product = Product.get(params.itemid)
      }
      else {
        page = Page.get(params.itemid)
      }
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

    def update() {
        def imageSetInstance = ImageSet.get(params.id)
        if (!imageSetInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'imageSet.label', default: 'ImageSet'), params.id])
            redirect(action: "list", params:params)
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (imageSetInstance.version > version) {
                imageSetInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'imageSet.label', default: 'ImageSet')] as Object[],
                          "Another user has updated this ImageSet while you were editing")
                render(view: "edit", model: [imageSetInstance: imageSetInstance])
                return
            }
        }

        imageSetInstance.properties = params

        if (!imageSetInstance.save(flush: true)) {
            render(view: "edit", model: [imageSetInstance: imageSetInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'imageSet.label', default: 'ImageSet'), imageSetInstance.id])
        redirect(action: "show", params:[id: imageSetInstance.id, type: params.type, itemid:params.itemid])
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
        ImageUtils.cropAndResize(original.data, original.extension, sizes[params.size], params, output)
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

    def list() {
      def product = null
      def page = null
      if (params.type == 'product') {
        product = Product.get(params.itemid)
      }
      else {
        page = Page.get(params.itemid)
      }
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
