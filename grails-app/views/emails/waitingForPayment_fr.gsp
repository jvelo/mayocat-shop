<%@ page contentType="text/html"%>

Bonjour,<br />
<br />
Votre commande #${order.getId()} est en attente de paiement. Dès que votre paiement sera reçu, celle-ci sera traitée.<br />
<br />
Détail de votre commande:<br />
<br />
<g:render template="/emails/orderTable"/>
<br />
Merci.<br />
