<%@ page import="org.mayocat.shop.grails.Page" %>

<div class="control-group fieldcontain ${hasErrors(bean: pageInstance, field: 'title', 'error')} ">
	<label for="title">
		<g:message code="page.title.label" default="Title" />
		
	</label>
  <div class="controls">
	<g:textField name="title" value="${pageInstance?.title}"/>
  </div>
</div>

<div class="control-group fieldcontain ${hasErrors(bean: pageInstance, field: 'content', 'error')} ">
	<label for="content">
		<g:message code="page.content.label" default="Content" />
		
	</label>
  <div class="controls">
	<g:textArea id="content" name="content" value="${pageInstance?.content}"/>

    <!-- Hook wysiwyg -->
    <script type="text/javascript">
      document.addEventListener('DOMContentLoaded', function() {
        document.removeEventListener('DOMContentLoaded', arguments.callee, true);
        var tryCounter = 10;
        (function() {
          // The load event is sometimes fired before the external JavaScript code is fully evaluated.
          if (typeof WysiwygEditor != 'undefined') {
            new WysiwygEditor({
              hookId: 'content',
              plugins: 'submit line separator embed text valign list indent history format symbol table image',
              menu: '[{"feature":"table", "subMenu":["inserttable", "insertcolbefore", "insertcolafter", "deletecol", "|", "insertrowbefore", "insertrowafter", "deleterow", "|", "deletetable"]}, {"feature" : "image", "subMenu":["imageInsertAttached", "imageInsertURL", "imageEdit", "imageRemove"]} ]',
              toolbar: 'bold italic underline strikethrough | subscript superscript | unorderedlist orderedlist | outdent indent | undo redo | format | hr symbol'
            });
          } else if (tryCounter-- > 0) {
            setTimeout(arguments.callee, 100);
          }
        })();
      }, true);
    </script>

  </div>
</div>
