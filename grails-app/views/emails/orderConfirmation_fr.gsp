<%@ page contentType="text/html"%>

Hello,<br />
<br />
Your order #${order.getId()} has been validated, and will be prepared for shipment as soon as possible.<br />
<br />
Your order details:<br />
<br />
<g:render template="/emails/orderTable"/>
<br />
Thank you.<br />
