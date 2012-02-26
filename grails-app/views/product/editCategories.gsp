
  <g:form method="post" class="form-horizontal">
    <g:hiddenField name="id" value="${productInstance?.id}" />
    <g:hiddenField name="version" value="${productInstance?.version}" />
    <fieldset class="form">
    <ul>
      <g:each in="${categories}" var="c">
        <li>
          <input type="checkbox" name="categories" value="${c.id}" 
            <g:if test="${productInstance.categories.contains(c)}">checked</g:if>
          />
          ${c.title}
        </li>
      </g:each>
    </ul>
    </fieldset>
    <fieldset class="buttons actions">
      <g:actionSubmit class="save btn" action="updateCategories"
                      value="${message(code: 'admin.product.updateCategories', default: 'Update categories')}" />
    </fieldset>
  </g:form>
