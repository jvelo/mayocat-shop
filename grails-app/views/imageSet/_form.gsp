<%@ page import="org.mayocat.shop.grails.ImageSet" %>


<div class="control-group fieldcontain ${hasErrors(bean: imageSetInstance, field: 'description', 'error')} ">
	<label for="description">
		<g:message code="imageSet.description.label" default="Description" />
		
	</label>
  <div class="controls">
	<g:textField name="description" value="${imageSetInstance?.description}"/>
  </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: imageSetInstance, field: 'caption', 'error')} ">
	<label for="caption">
		<g:message code="imageSet.caption.label" default="Caption" />
		
	</label>
  <div class="controls">
	<g:textField name="caption" value="${imageSetInstance?.caption}"/>
  </div>
</div>

