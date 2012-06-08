<%@ page import="org.mayocat.shop.grails.Order"%>

<!doctype html>
<html>
<head>
<meta name="layout" content="checkout">
<title><g:message code="checkout.selectPaymentMethod"
		default="Select payment method" /></title>
</head>
<body>
	<div id="selectPaymentMethod" class="checkout-container content"
		role="main">
		<div class="page-header">
			<h2>
				<g:message code="checkout.selectPaymentMethod"
					default="Select payment method" />
			</h2>
		</div>
		<g:if test="${flash.message}">
			<div class="alert alert-info" role="status">
				${flash.message}
			</div>
		</g:if>
		<g:form action="doBeforePayment" method="post"
			mapping="checkoutPaymentBefore">
			<div>
				<g:each var="method" in="${methods}" status="count">
					<div class="method clearfix">
						<div class="left">
							<input type="radio" name="method" value="${method.technicalName}"
							  <g:if test="${count == 0}">checked</g:if>
							 />
						</div>
						<div class="middle">
							<g:if test="${method.imageVersion && method.imageVersion > 0}">
								<img
									src="${createLink(controller: 'shop', action:'serveImage', params:[filename:image, method:method.technicalName])}" />
							</g:if>

						</div>
						<div class="right">
							<h3>
								${method.displayName}
							</h3>
							${method.description}
						</div>
					</div>
				</g:each>
				<div class="buttons">
					<input type="submit" class="btn"
						value="<g:message code='checkout.selectPaymentMethod.submit' default='Continue' />" />
				</div>
			</div>
		</g:form>
	</div>
</body>
</html>
