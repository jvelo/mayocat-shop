$(document).ready(function(){

  $("span[rel='modal']").click(function(event) {
    $('#preview-modal').modal({
      backdrop:'static'
    }).modal('show');
    $.ajax($(event.currentTarget).data('edit-uri'), {
      success:function(transport) {
        $('.modal-body').removeClass("loading").html(transport);
      }
    })
  });

});
