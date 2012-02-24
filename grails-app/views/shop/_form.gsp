<%@ page import="org.mayocat.shop.grails.Shop" %>



<div class="control-group fieldcontain ${hasErrors(bean: shopInstance, field: 'name', 'error')} ">
	<label for="name" class="control-label">
		<g:message code="shop.name.label" default="Name" />
		
	</label>
  <div class="controls">
	<g:textField name="name" value="${shopInstance?.name}"/>
  </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: shopInstance, field: 'storefront', 'error')} ">
	<label for="storefront" class="control-label">
		<g:message code="shop.storefront.label" default="Storefront" />
		
	</label>
  <div class="controls">
	  <g:textField name="storefront" value="${shopInstance?.storefront}"/>
    <p class="help-block">
      <g:message code="shop.storefront.help" default="The name of the storefront to use for the shop. If empty, the default storefront will be used" />
    </p>
  </div>
</div>

