package org.mayocat.shop.grails

import java.net.URI
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils

import com.google.common.cache.CacheBuilderSpec
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.io.Resources
import com.google.common.net.HttpHeaders;
import com.google.common.hash.Hashing

class ResourceController {

    static scope = "singleton"
    static def SLASH = "/"
    static def STOREFRONTS_DIR = "storefronts"
    
    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static class CachedResource {
        def byte[] resource;
        def String eTag;
        def long lastModifiedTime;

        private CachedResource(final byte[] resource) {
            this.resource = resource;
            this.eTag = Hashing.md5().hashBytes(resource).toString();
            this.lastModifiedTime = roundedTimestamp();
        }

        private long roundedTimestamp() {
            return (System.currentTimeMillis() / 1000) * 1000;
        }
    }

    private static class ResourceLoader extends CacheLoader<String, CachedResource> {
        private final String resourcePath;
        private final String uriPath;

        private ResourceLoader(String resourcePath, String uriPath) {
            this.resourcePath = resourcePath
            this.uriPath = uriPath
        }
        
        @Override
        public CachedResource load(String key) throws Exception {
            final String resource = key.substring(uriPath.length());
            String fullResourcePath = this.resourcePath + resource;
            final URL resourceURL = servletContext.getResource(fullResourcePath);
            return new CachedResource(Resources.toByteArray(resourceURL));
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
    private LoadingCache<String, CachedResource> cache;

    // ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    

    /**
     * Lazy cache initialization
     * 
     * @return the resource cache
     */
    def getCache(contextPath) {
        if (this.cache == null) {
            def shop = Shop.list()[0]
            def storefront = shop?.storefront
            if (!storefront || storefront == "") {
                storefront = "default"
            }
            
            this.cache = CacheBuilder.from(CacheBuilderSpec.parse("maximumSize=100"))
                .build(new ResourceLoader(SLASH + STOREFRONTS_DIR + SLASH + storefront, contextPath))
        }
        this.cache
    }
    
    /**
     * Try to serve to requested resource.
     * 
     * First, check if the client has a non-expired cached version of the resource
     */
    def serve = {
        def request = unwrapRequest(request)
        def requestURI = request.forwardURI ?: request.requestURI
        def contextPath = request.contextPath ?: ""

        def resource = this.getCache(contextPath).getUnchecked(requestURI)        
        
        if (this.isCachedByClient(request, resource)) {
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        def filename = requestURI[requestURI.lastIndexOf("/")..-1]
        def mime = servletContext.getMimeType(filename.toLowerCase())

        def data = resource.resource

        response.setDateHeader(HttpHeaders.LAST_MODIFIED, resource.lastModifiedTime)
        response.setHeader(HttpHeaders.ETAG, resource.eTag)
        response.setContentType(mime ?: "application/octet-stream")

        def out = response.outputStream
        try {
          out.write(data);
        }
        finally {
          out.close()
        }
    }

    /**
     * Checks whether the request for the passed resource is cached on the client (browser), checking both 
     * last modification date and etag headers.
     *  
     * @param request the request to check client caching for
     * @param resource the server-side resource to check client caching for
     * @return true if the client has not-expired cache resource, false otherwise
     */
    private boolean isCachedByClient(request, resource) {
        return (resource.eTag == request.getHeader(HttpHeaders.IF_NONE_MATCH)) ||
        (request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE) >= resource.lastModifiedTime)
    }

    def unwrapRequest(request) {
        request.request ?: request
    }

}
