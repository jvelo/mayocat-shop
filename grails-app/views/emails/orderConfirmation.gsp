<%@ page contentType="text/html"%>

Bonjour,<br />
<br />
Votre commande #${order.getId()} à été validée, et sera préparée dans les plus bref délais.<br />
<br />
Détails de votre commande:<br />
<br />
<g:render template="/emails/orderTable"/>
<br />
Merci.<br />
