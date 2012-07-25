<%@ page import="org.mayocat.shop.grails.Order"%>

<!doctype html>
<html>
<head>
<meta name="layout" content="checkout">
<title><g:message code="checkout.payment" default="Payment" /></title>
<r:require modules="checkout" />
</head>
<body>
	<div id="checkout" class="checkout-container content"
		role="main">
		<div class="page-header">
			<h2>
				<g:message code="checkout.checkout" default="Checkout" />
			</h2>
		</div>

     <g:if test="${!cart || cart.keySet().size() <= 0}">
		<g:message code="checkout.cartIsEmpty" default="Cart is empty..." />
		<g:link controller="home" method="expose">
		  <g:message code="checkout.cartIsEmpty.doShopping" default="do some shopping first" />
		</g:link>
     </g:if>

     <g:else>
        <form action="${createLink(method:'createOrder')}" method="POST" 
              enctype="multipart/form-data" accept-charset="UTF-8">
              <!-- Force encoding otherwise grails will fail at decoding unicode correctly-->

          <g:if test="${order?.errors?.allErrors?.size() > 0 || order?.billingAddress?.errors?.allErrors?.size() > 0}">
            <div class="alert alert-error">
              <g:message code="checkout.thereAreValidationErrors" default="There were some validation errors. Please fix error items below before you submit your checkout" />

            </div>
          </g:if>

          <div>          
            <label for="orderEmail" class="${hasErrors(bean:order,field:'customerEmail','error')}">Email <span class="required">*</span></label>
            <input type="email" name="customerEmail" id="orderEmail" class="input-xlarge" value="${order?.customerEmail}" blrequired=""/>
          </div>
          
          <fieldset id="billingAddress">
            <h3>
              <g:message code="checkout.yourPersonalInformation" default="Your personal information" />
            </h3>
          
            <label for="billingAddressFirstName" class="${hasErrors(bean:order?.billingAddress,field:'firstName','error')}">
              <g:message code="checkout.address.firstName" default="First name"/> 
              <span class="required">*</span>
            </label>
            <input type="text" name="billingAddress.firstName" id="billingAddressFirstName" value="${order?.billingAddress?.firstName}" class="input-xlarge" />

            <label for="billingAddressLastName" class="${hasErrors(bean:order?.billingAddress,field:'lastName','error')}">
              <g:message code="checkout.address.lastName" default="Last name"/>
              <span class="required">*</span>
            </label>
            <input type="text" name="billingAddress.lastName" id="billingAddressLastName" class="input-xlarge" value="${order?.billingAddress?.lastName}" blrequired=""/>

            <label for="billingAddressAddress" class="${hasErrors(bean:order?.billingAddress,field:'address','error')}">
              <g:message code="checkout.address.address" default="Address" />
              <span class="required">*</span>
            </label>
            <input type="text" name="billingAddress.address" id="billingAddressAddress" class="large input-xlarge" value="${order?.billingAddress?.address}" blrequired=""/>

            <label for="billingAddressAddress2">
              <g:message code="checkout.address.address2" default="Address (continuing)" />
            </label>
            <input type="text" name="billingAddress.address2" id="billingAddressAddress2" class="large input-xlarge" value="${order?.billingAddress?.address2}" />

            <label for="billingAddressZip" class="${hasErrors(bean:order?.billingAddress,field:'zip','error')}">
              <g:message code="checkout.address.zip" default="Zip" />
              <span class="required">*</span>
            </label>
            <input type="text" name="billingAddress.zip" id="billingAddressZip" class="small input-xlarge" value="${order?.billingAddress?.zip}" blrequired=""/>

            <label for="billingAddressCity" class="${hasErrors(bean:order?.billingAddress,field:'city','error')}">
              <g:message code="checkout.address.city" default="City" />
              <span class="required">*</span>
            </label>
            <input type="text" name="billingAddress.city" id="billingAddressCity" class="input-xlarge" value="${order?.billingAddress?.city}" blrequired=""/>

            <label for="billingAddressPhone">
              <g:message code="checkout.address.phone" default="Phone" />
            </label>
            <input type="text" name="billingAddress.phone" id="billingAddressPhone" class="input-xlarge" value="${order?.billingAddress?.phone}"/>

            <label for="deliveryExtraInformation">
              <g:message code="checkout.address.extraInformation" default="Extra information (floor number, access code, etc.)" />
            </label>
            <textarea name="billingAddress.extraInformation">${order?.billingAddress?.extraInformation}</textarea>

          </fieldset>

          <fieldset id="deliveryAddress" class="hidden">
            <h3>
              <g:message code="checkout.deliveryAddress" default="Delivery address" />
            </h3>
          
            <label for="deliveryAddressFirstName" class="${hasErrors(bean:order?.deliveryAddress,field:'firstName','error')}">
              <g:message code="checkout.address.firstName" default="First name"/> 
              <span class="required">*</span>
            </label>
            <input type="text" name="deliveryAddress.firstName" id="deliveryAddressFirstName" value="${order?.deliveryAddress?.firstName}" class="input-xlarge" />

            <label for="deliveryAddressLastName" class="${hasErrors(bean:order?.deliveryAddress,field:'lastName','error')}">
              <g:message code="checkout.address.lastName" default="Last name"/>
              <span class="required">*</span>
            </label>
            <input type="text" name="deliveryAddress.lastName" id="deliveryAddressLastName" class="input-xlarge" value="${order?.deliveryAddress?.lastName}" blrequired=""/>

            <label for="deliveryAddressAddress" class="${hasErrors(bean:order?.deliveryAddress,field:'address','error')}">
              <g:message code="checkout.address.address" default="Address" />
              <span class="required">*</span>
            </label>
            <input type="text" name="deliveryAddress.address" id="deliveryAddressAddress" class="large input-xlarge" value="${order?.deliveryAddress?.address}" blrequired=""/>

            <label for="deliveryAddressAddress2">
              <g:message code="checkout.address.address2" default="Address (continuing)" />
            </label>
            <input type="text" name="deliveryAddress.address2" id="deliveryAddressAddress2" class="large input-xlarge" value="${order?.deliveryAddress?.address2}" />

            <label for="deliveryAddressZip" class="${hasErrors(bean:order?.deliveryAddress,field:'zip','error')}">
              <g:message code="checkout.address.zip" default="Zip" />
              <span class="required">*</span>
            </label>
            <input type="text" name="deliveryAddress.zip" id="deliveryAddressZip" class="small input-xlarge" value="${order?.deliveryAddress?.zip}" blrequired=""/>

            <label for="deliveryAddressCity" class="${hasErrors(bean:order?.deliveryAddress,field:'city','error')}">
              <g:message code="checkout.address.city" default="City" />
              <span class="required">*</span>
            </label>
            <input type="text" name="deliveryAddress.city" id="deliveryAddressCity" class="input-xlarge" value="${order?.deliveryAddress?.city}" blrequired=""/>

            <label for="deliveryAddressPhone">
              <g:message code="checkout.address.phone" default="Phone" />
            </label>
            <input type="text" name="deliveryAddress.phone" id="deliveryAddressPhone" class="input-xlarge" value="${order?.deliveryAddress?.phone}"/>

            <label for="deliveryExtraInformation">
              <g:message code="checkout.address.extraInformation" default="Extra information (floor number, access code, etc.)" />
            </label>
            <textarea name="deliveryAddress.extraInformation">${order?.deliveryAddress?.extraInformation}</textarea>
          </fieldset>

          <div class="clearfix"></div>

          <label for="useBillingAddressForDelivery">
            <input type="checkbox" <g:if test="${!twoAddress}">checked</g:if> id="useBillingAddressForDelivery" name="useBillingAddressForDelivery" />
            <g:message code="checkout.useSameAddressForDelivery" default="Use same address for delivery" /> 
          </label>

          <script type="text/javascript">
          // <![CDATA[
          $(document).ready(function(){
            var showOrHideDeliveryAddress = function(checked) {
              if (checked) {
                $("#deliveryAddress").addClass("hidden");
              }
              else {
                $("#deliveryAddress").removeClass("hidden");
              }
            }
            showOrHideDeliveryAddress($("#useBillingAddressForDelivery")[0].checked);
        	$("#useBillingAddressForDelivery").on("click", function(event){
              showOrHideDeliveryAddress(event.target.checked);
            });
          });
          // ]]>
          </script>

          <div class="buttons">
            <input type="submit" value="<g:message code='checkout.continue' default='Continue' />" class="nice white small round button" />
          </div>
        </form>
        </g:else>
		
	</div>
</body>
</html>
