<table class="table">
  <thead>
    <tr style="border-bottom:1px solid black;">
      <th></th>
      <th style="font-weight:bold"><g:message code="order.table.item" default="Item name" /></th>
      <th style="font-weight:bold"><g:message code="order.table.description" default="Description" /></th>
      <th style="font-weight:bold"><g:message code="order.table.unitPrice" default="Unit price" /></th>
      <th style="font-weight:bold"><g:message code="order.table.quantity" default="Quantity" /></th>
      <th style="font-weight:bold"><g:message code="order.table.itemTotal" default="Item total" /></th>
    </tr>
  </thead>

  <g:each var="item" in="${order.items}" status="count">
    <tr>
      <td>${count + 1}.</td>
      <td>${item.title}</td>
      <td>${item.description}</td>
      <td>${item.unitPrice} ${order.currency.getSymbol(Locale.UK)}</td> 
          %{-- Using the UK locale gives a euro symbol for EUR 
               TODO : Maybe adapt the code from the second post at http://www.java.net/node/691596 in a grails taglib
               OR, checkout the enhancements in JDK7 at https://blogs.oracle.com/naotoj/entry/currency_enhancements_in_jdk71
            --}%
      <td>${item.quantity}</td>
      <td>${item.quantity * item.unitPrice} ${order.currency.getSymbol(Locale.UK)}</td>
    </tr>
  </g:each>

  <tfoot>
    <tr class="productsTotal" style="border-top:3px double black;">
      <td></td>
      <td colspan="4"><g:message code="order.productsTotal" default="Products total" /></td>
      <td class="price">${order.totalProducts} ${order.currency.getSymbol(Locale.UK)}</td>
    </tr>
    <g:if test="${order.shipping}">
      <tr class="shipping">
        <td></td>
        <td colspan="4"><g:message code="order.shipping" default="Shipping" /></td>
        <td class="price">${order.shipping} ${order.currency.getSymbol(Locale.UK)}</td>
      </tr>
    </g:if>
    <tr class="grandTotal" style="border-top:2px solid black">
      <td></td>
      <td colspan="4"><g:message code="order.grandTotal" default="Grand total" /></td>
      <td class="price">${order.grandTotal} ${order.currency.getSymbol(Locale.UK)}</td>
    </tr>
  </tfoot>
</table>
