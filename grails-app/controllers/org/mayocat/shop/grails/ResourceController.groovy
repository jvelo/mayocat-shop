package org.mayocat.shop.grails

import java.net.URI
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils

class ResourceController {

  static def SLASH = "/"

  static def STOREFRONTS_DIR = "storefronts"

  def serve = {
    def request = unwrapRequest(request)
    def requestURI = request.fowardURI ?: request.requestURI
    def contextPath = request.contextPath
    def resourcePath = requestURI[requestURI.indexOf("/resources/")..-1]
    def storefront = grailsApplication.config.mayocat.shop.storefront
    if (!storefront || storefront == "") {
      storefront = "default"
    }
    def filePath = this.getFilePath(resourcePath, storefront)
    def is = servletContext.getResourceAsStream(filePath)
    if (is == null) {
      throw new java.io.FileNotFoundException("Could not find file with path ${filePath}")
    }
    def filename = filePath[filePath.lastIndexOf("/") + 1..-1];
    def mime = servletContext.getMimeType(filename.toLowerCase())
    def data = IOUtils.toByteArray(is);
    this.prepareHeaders(mime, data.length)
    def out = response.outputStream
    out.write(data);
    out.close()
  }

  def getFilePath(String filepath, String storefront) {
    def path = URI.create(SLASH + STOREFRONTS_DIR + SLASH + storefront + SLASH + filepath).normalize().toString()
    if (!path.startsWith(SLASH + STOREFRONTS_DIR + SLASH + storefront + SLASH)) {
      log.warn("Illegal access for file [{}]. Possible break-in attempt!", filepath)
    }
    path
  }

  def prepareHeaders(mimetype, length) {
    if (!StringUtils.isBlank(mimetype)) {
      response.setContentType(mimetype);
    } else {
      response.setContentType("application/octet-stream");
    }    
  }

  def unwrapRequest(request) {
    request.request ?: request
  }

}
