package org.eschoppe

import java.net.URI
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils

class ResourceController {

  static def SLASH = "/"

  static def STOREFRONTS_DIR = "storefronts"

  def serve = {
    def requestURI = request.request.requestURI
    def contextPath = request.request.contextPath
    def resourcePath = requestURI[(contextPath).size()..-1]
    def storefront = grailsApplication.config.eschoppe.storefront
    log.error( "storefront : " + storefront )
    if (!storefront || storefront == "") {
      storefront = "default"
    }
    def filePath = this.getFilePath(resourcePath, storefront)
    def is = servletContext.context.getResourceAsStream(filePath)
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

}
