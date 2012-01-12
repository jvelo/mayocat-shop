<div>
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>
  <g:else>
    <img class="thumbnail"
         data-original-width="${original.width}"
         data-original-height="${original.height}"
         style="max-width:500px;max-height:500px;"
         src="${createLink(url: [controller:'imageSet', action:'view', params: [productid: imageSet?.product?.id, id: imageSet?.id]])}" />
    <div style="margin-top:10px;"><strong><g:message code="imageSet.thumbnailEditor.preview" default="Preview:" /></strong></div>
    <div class="preview-container"
         style="height:${dimensions.height}px;width:${dimensions.width}px;overflow:hidden;margin-top:3px;display:inline-block">
      <img id="preview" 
           src="${createLink(url: [controller:'imageSet', action:'view', params: [productid: imageSet?.product?.id, id: imageSet?.id]])}"
           style="height:${dimensions.height}px;width:${dimensions.width}px;">
    </div>
  </g:else>
</div>
