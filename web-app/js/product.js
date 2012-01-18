$(document).ready(function(){

  $("input[name='exposed']").bind("click", function(event){
    var saveURI = $(event.currentTarget).data('update-uri');
    saveURI += "&exposed=" + ($(event.currentTarget).is(':checked') ? "true" : "false");
    event.currentTarget
    $.post(saveURI), {
      success: function(tranport) {
        console.log(transport);
      }
    }

  });
});
