<div>
  <g:if test="${flash.message}">
    <div class="message" role="status">${flash.message}</div>
  </g:if>
  <g:else>
    <img class="thumbnail"
         style="max-width:500px;max-height:500px;"
         src="${createLink(url: [controller:'imageSet', action:'view', params: [productid: imageSet?.product?.id, id: imageSet?.id]])}" />
  </g:else>
</div>
