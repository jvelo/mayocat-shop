<%@ page import="org.mayocat.shop.grails.Shop" %>

%{-- General preferences --}%

<fieldset>
  <legend><g:message code="admin.preferences.general" default="General" /></legend>

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
</fieldset>

%{-- General preferences --}%

<fieldset>
  <legend><g:message code="admin.preferences.product" default="Products" /></legend>

  <div class="control-group fieldcontain ${hasErrors(bean: shopInstance, field: 'singleUnitProducts', 'error')} ">
    <label for="singleUnitProducts" class="control-label">
      <g:message code="shop.name.singleUnitProducts" default="Single unit products" />
      
    </label>
    <div class="controls">
    <label for="singleUnitProducts">
      <g:checkBox name="singleUnitProducts" value="${shopInstance.singleUnitProducts}" />
      <g:message code="shop.name.singleUnitProducts.hint" default="If the products sold are one offs, single units, checking this option will automatically keep track of a single stock unique and ensure only one piece is sold" />
    </label>
    </div>
  </div>

</fieldset>
