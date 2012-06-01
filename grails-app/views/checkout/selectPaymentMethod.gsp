<%@ page import="org.mayocat.shop.grails.Order" %>

<!doctype html>
<html>
	<head>
		<meta name="layout" content="checkout">
		<title><g:message code="checkout.selectPaymentMethod" default="Select payment method" /></title>
	</head>
	<body>
    <div id="selectPaymentMethod" class="checkout-container content" role="main">
      <div class="page-header">
        <h2><g:message code="checkout.selectPaymentMethod" default="Select payment method" /></h2>
      </div>
      <g:if test="${flash.message}">
      <div class="alert alert-info" role="status">${flash.message}</div>
      </g:if>
      <form>
        <div>
          <g:each var="method" in="${methods}">
            <div class="method">
              <input type="checkbox" name="method" value="${method.name}" />
            ${method.displayName} - ${method.description}
            </div>
          </g:each>
          <div class="buttons">
            <input type="submit" value="<g:message code='checkout.selectPaymentMethod.submit' default='Continue' />" />
          </div>
        </div>
      </form>
		</div>
	</body>
</html>
