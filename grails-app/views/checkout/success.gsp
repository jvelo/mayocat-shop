<%@ page import="org.mayocat.shop.grails.Order"%>

<!doctype html>
<html>
<head>
<meta name="layout" content="checkout">
<title><g:message code="checkout.payment" default="Payment" /></title>
<r:require modules="checkout" />
</head>
<body>
	<div id="selectPaymentMethod" class="checkout-container content"
		role="main">
		<div class="page-header">
			<h2>
				<g:message code="checkout.payment" default="Payment" />
			</h2>
		</div>
		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>

        <div class="well">
        <g:message code="checkout.payment.success" default="Thank you for your order !" />

		  <div>
			${successContent}
		  </div>
		</div>
		<div class="submit-step center">
          <a class="btn" href="${createLink(controller:'home', action:'expose')}">
          Back to the shop
          </a>
		</div>
	</div>
</body>
</html>
