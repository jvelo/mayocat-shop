<%@ page import="org.mayocat.shop.grails.Shop" %>

%{-- General preferences --}%

<fieldset>
  <legend><g:message code="admin.preferences.general" default="General" /></legend>

  <div class="control-group  ${hasErrors(bean: shopInstance, field: 'name', 'error')} ">
    <label for="name" class="control-label">
      <g:message code="shop.name.label" default="Name" />
    </label>
    <div class="controls">
    <g:textField name="name" value="${shopInstance?.name}"/>
    </div>
  </div>

  <div class="control-group  ${hasErrors(bean: shopInstance, field: 'storefront', 'error')} ">
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

%{-- Product preferences --}%

<fieldset>
  <legend><g:message code="admin.preferences.product" default="Products" /></legend>

  <div class="control-group  ${hasErrors(bean: shopInstance, field: 'singleUnitProducts', 'error')} ">
    <label for="singleUnitProducts" class="control-label">
      <g:message code="shop.products.singleUnitProducts" default="Single unit products" />
    </label>
    <div class="controls">
    <label for="singleUnitProducts">
      <g:checkBox name="singleUnitProducts" value="${shopInstance.singleUnitProducts}" />
      <g:message code="shop.products.singleUnitProducts.hint" default="If the products sold are one offs, single units, checking this option will automatically keep track of a single stock unique and ensure only one piece is sold" />
    </label>
    </div>
  </div>

  <div class="control-group  ${hasErrors(bean: shopInstance, field: 'sentBySnailMail', 'error')} ">
    <label for="sentBySnailMail" class="control-label">
      <g:message code="shop.products.sentBySnailMail" default="Sent by snail mail" />
    </label>
    <div class="controls">
    <label for="sentBySnailMail">
      <g:checkBox name="sentBySnailMail" value="${shopInstance.sentBySnailMail}" data-bind="checked: sentBySnailMail" />
      <g:message code="shop.products.sentBySnailMail.hint" default="Select if some or all of the product sold are sent by snail mail" />
    </label>
    </div>
  </div>

  %{-- Snail mail options --}%
   <div class="snailMailOptions" data-bind="slideVisible: sentBySnailMail" style="padding-left:80px">

    <h4><g:message code="shop.products.package.dimensionsToManage" default="Dimensions to manage" /></h4>

    %{-- Weight --}%
    <div class="control-group ${hasErrors(bean: shopInstance.packageManagement, field: 'weight', 'error')} "
         data-bind="css: { disabled: !sentBySnailMail() }">
      <label for="packageManagement.weight" class="control-label">
        <g:message code="shop.products.package.weight" default="Manage weight" />
      </label>
      <div class="controls">
      <label for="packageManagement.weight">
        %{-- We can't use g:checkBox here, it places the '_' in the hidden input wrongly ; see http://jira.grails.org/browse/GRAILS-3299 --}%
        <input type="checkbox" name="packageManagement.weight" <g:if test="${shopInstance.packageManagement.weight}">checked</g:if>
               data-bind="enable: sentBySnailMail" />
        <input type="hidden" name="packageManagement._weight" />
        <g:message code="shop.products.packageManagement.weight" default="Should the weight of package be managed ?" />
      </label>
      </div>
    </div>

    %{-- Length --}%
    <div class="control-group ${hasErrors(bean: shopInstance.packageManagement, field: 'length', 'error')} "
         data-bind="css: { disabled: !sentBySnailMail() }">
      <label for="packageManagement.length" class="control-label">
        <g:message code="shop.products.package.length" default="Manage length" />
      </label>
      <div class="controls">
      <label for="packageManagement.length">
        %{-- We can't use g:checkBox here, it places the '_' in the hidden input wrongly ; see http://jira.grails.org/browse/GRAILS-3299 --}%
        <input type="checkbox" name="packageManagement.length" <g:if test="${shopInstance.packageManagement.length}">checked</g:if>
               data-bind="enable: sentBySnailMail" />
        <input type="hidden" name="packageManagement._length" />
        <g:message code="shop.products.packageManagement.length" default="Should the length of package be managed ?" />
      </label>
      </div>
    </div>

    %{-- Width --}%
    <div class="control-group ${hasErrors(bean: shopInstance.packageManagement, field: 'width', 'error')} "
         data-bind="css: { disabled: !sentBySnailMail() }">
      <label for="packageManagement.width" class="control-label">
        <g:message code="shop.products.package.width" default="Manage width" />
      </label>
      <div class="controls">
      <label for="packageManagement.width">
        %{-- We can't use g:checkBox here, it places the '_' in the hidden input wrongly ; see http://jira.grails.org/browse/GRAILS-3299 --}%
        <input type="checkbox" name="packageManagement.width" <g:if test="${shopInstance.packageManagement.width}">checked</g:if>
               data-bind="enable: sentBySnailMail" />
        <input type="hidden" name="packageManagement._width" />
        <g:message code="shop.products.packageManagement.width" default="Should the width of package be managed ?" />
      </label>
      </div>
    </div>

    %{-- Height --}%
    <div class="control-group ${hasErrors(bean: shopInstance.packageManagement, field: 'height', 'error')} "
         data-bind="css: { disabled: !sentBySnailMail() }">
      <label for="packageManagement.height" class="control-label">
        <g:message code="shop.products.package.height" default="Manage height" />
      </label>
      <div class="controls">
      <label for="packageManagement.height">
        %{-- We can't use g:checkBox here, it places the '_' in the hidden input wrongly ; see http://jira.grails.org/browse/GRAILS-3299 --}%
        <input type="checkbox" name="packageManagement.height" <g:if test="${shopInstance.packageManagement.height}">checked</g:if>
               data-bind="enable: sentBySnailMail" />
        <input type="hidden" name="packageManagement._height" />
        <g:message code="shop.products.packageManagement.height" default="Should the height of package be managed ?" />
      </label>
      </div>
    </div>

    %{-- Shipping price rules --}%
    <h4><g:message code="shop.products.package.shippingPriceRules" default="Shipping price rules" /></h4>
    %{-- existing rules --}%
    <g:set var="counter" value="${0}" />
    <g:each in="${shopInstance.packageManagement.priceRules}" var="rule">
    <div class="control-group"
         data-bind="css: { disabled: !sentBySnailMail() }">
      <label for="rule" class="control-label">
        <g:message code="shop.products.package.priceRule" default="Rule" />
      </label>
      <div class="controls">
      <label for="packageManagement.height">
        <select name="packageManagement.priceRules[${counter}].dimension" 
                 placeholder="dimension" class="span1">
          <option value="">---</option>
          <option value="weight" <g:if test="${rule.dimension == 'weight'}">selected</g:if>><g:message code="shop.products.package.priceRule.weight" default="Weight" /></option>
          <option value="price" <g:if test="${rule.dimension == 'price'}">selected</g:if>><g:message code="shop.products.package.priceRule.price" default="Price" /></option>
        </select>
        <input type="number" name="packageManagement.priceRules[${counter}].threshold"
                             step="1" value="${rule.threshold}" class="span1" />
        <input type="number" name="packageManagement.priceRules[${counter}].price"
                             step="0.01" value="${rule.price}" class="span1" />
      </label>
      </div>
    </div>
    <g:set var="counter" value="${counter + 1}" />
    </g:each>
    %{-- new rule --}%
    <div class="control-group"
         data-bind="css: { disabled: !sentBySnailMail() }">
      <label for="rule" class="control-label">
        <g:message code="shop.products.package.priceRule" default="Rule" />
      </label>
      <div class="controls">
      <label for="packageManagement.height">
        <select name="packageManagement.priceRules[${counter}].dimension" 
                 placeholder="dimension" class="span1">
          <option value="">---</option>
          <option value="weight"><g:message code="shop.products.package.priceRule.weight" default="Weight" /></option>
          <option value="price"><g:message code="shop.products.package.priceRule.price" default="Price" /></option>
        </select>
        <input type="number" name="packageManagement.priceRules[${counter}].threshold"
                             step="1" value="" placeholder="threshold" class="span1" />
        <input type="number" name="packageManagement.priceRules[${counter}].price"
                             step="0.01" value="" placeholder="Price" class="span1" />
      </label>
      </div>
    </div>

  </div>

</fieldset>

%{-- Categories --}%

<fieldset>
  <legend><g:message code="admin.preferences.categories" default="Categories" /></legend>

  <div class="control-group  ${hasErrors(bean: shopInstance, field: 'productsPerPage', 'error')} ">
    <label for="productsPerPage" class="control-label">
      <g:message code="shop.categories.productsPerPage" default="Products per page" />
      
    </label>
    <div class="controls">
    <label for="productsPerPage">
	    <input type="number" name="categoryProductsPerPage" value="${shopInstance.categoryProductsPerPage}" min="0" step="1" />
      <g:message code="shop.categories.productsPerPage" default="How many products to display per category page." />
    </label>
    </div>
  </div>
</fieldset>

<script type="text/javascript">
// <![CDATA[
$(document).ready(function(){
  (function(){
    var viewModel = {
      sentBySnailMail : ko.observable( $('#sentBySnailMail')[0].checked == true)
    };

    // Custom slide up/down binding
    ko.bindingHandlers.slideVisible = {
        init: function(element, valueAccessor) {
            // Initially set the element to be instantly visible/hidden depending on the value
            var value = valueAccessor();
            $(element).toggle(ko.utils.unwrapObservable(value)); // Use "unwrapObservable" so we can handle values that may or may not be observable
        },
        update: function(element, valueAccessor) {
            // Whenever the value subsequently changes, slowly slide the element up or down
            var value = valueAccessor();
            ko.utils.unwrapObservable(value) ? $(element).slideDown() : $(element).slideUp();
        }
    };

    ko.applyBindings(viewModel);
  })();

});
// ]]>
</script>
