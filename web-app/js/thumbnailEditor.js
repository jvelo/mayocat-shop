var Eschoppe = (function(Eschoppe) {

  Eschoppe.ThumbnailEditor = function(image, preview) {
    this.image = image;
    this.preview = preview;
    this.previewWidth = this.preview.width();
    this.previewHeight = this.preview.height();
    this.image.src.Jcrop({
      setSelect: [0,0,500,500],
      onChange: this.onChangeOrSelect.bind(this),
      onSelect: this.onChangeOrSelect.bind(this),
      aspectRatio: (this.preview.width() / this.preview.height())
    });
  };

  $.extend(Eschoppe.ThumbnailEditor.prototype, {
    onChangeOrSelect: function(coords) {
      var rx = this.previewWidth / coords.w;
      var ry = this.previewHeight / coords.h;

      this.preview.css({
        width: Math.round(rx * this.image.width) + 'px',
        height: Math.round(ry * this.image.height) + 'px',
        marginLeft: '-' + Math.round(rx * coords.x) + 'px',
        marginTop: '-' + Math.round(ry * coords.y) + 'px'
      });
    }
  });

  return Eschoppe;

})(Eschoppe || {})

$(document).ready(function(){


  $("span[rel='modal']").click(function(event) {
    $('#preview-modal').modal({
      backdrop:'static'
    }).modal('show');
    $.ajax($(event.currentTarget).data('edit-uri'), {
      success:function(transport) {
        $('.modal-body').removeClass("loading").html(transport);
        var aspectRatio = $("#preview").width() / $('#preview').height();
        console.log(aspectRatio);
        var te = new Eschoppe.ThumbnailEditor(
          {
            src: $('.modal-body .thumbnail'),
            width: $('.modal-body .thumbnail').width(),
            height: $('.modal-body .thumbnail').height()
          },
          $('#preview')
        );
        console.log(te);
      }
    })
  });

});
