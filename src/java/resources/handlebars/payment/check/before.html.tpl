<div class="order-before">
Merci de faire un chèque du montant de votre commande ({{order.displayTotal}} euros) à l'ordre de :

<div class="well">
{{configuration.orderto}}
</div>
<p>
Pensez s'il vous plaît à inscrire le numéro de votre commande (n&deg;{{order.id}}) au dos ce celui-ci.
</p>
<p>
Envoyez le chèque à l'adresse suivante :
</p>
<div class="adr">
{{configuration.sendto}}
</div>
</div>

<style>
div.order-before div.adr {
  white-space:pre;
}
</style>
