var Eschoppe = (function(Eschoppe) {

  Eschoppe.ThumbnailEditor = function(image, preview, originalDimensions) {

    this.image = image;
    this.preview = preview;
    this.previewWidth = this.preview.width();
    this.previewHeight = this.preview.height();
    this.canvasWidth = this.image.width();
    this.canvasHeight = this.image.height();
    this.originalDimensions = originalDimensions;

    this.image.Jcrop({
      setSelect: [0,0,500,500],
      onChange: this.onChangeOrSelect.bind(this),
      onSelect: this.onChangeOrSelect.bind(this),
      aspectRatio: (this.previewWidth / this.previewHeight)
    });
  };

  $.extend(Eschoppe.ThumbnailEditor.prototype, {
    onChangeOrSelect: function(coords) {
      this.coords = coords
      var rx = this.previewWidth / coords.w;
      var ry = this.previewHeight / coords.h;

      this.preview.css({
        width: Math.round(rx * this.canvasWidth) + 'px',
        height: Math.round(ry * this.canvasHeight) + 'px',
        marginLeft: '-' + Math.round(rx * coords.x) + 'px',
        marginTop: '-' + Math.round(ry * coords.y) + 'px'
      });
    },

    getCoordinates: function() {
      return {
        x: parseInt(this.coords.x * this.originalDimensions.width / this.canvasWidth),
        y: parseInt(this.coords.y * this.originalDimensions.height / this.canvasHeight),
        width: parseInt(this.coords.w * this.originalDimensions.width / this.canvasWidth),
        height: parseInt(this.coords.h * this.originalDimensions.height / this.canvasHeight)
      }
    }
  });

  return Eschoppe;

})(Eschoppe || {})

$(document).ready(function(){

  $("span[rel='modal']").click(function(event) {
    $('#preview-modal').modal({
      backdrop:'static'
    }).modal('show');
    var saveURI = $(event.currentTarget).data('save-uri')
    $.ajax($(event.currentTarget).data('edit-uri'), {
      success:function(transport) {
        $('.modal-body').removeClass("loading").html(transport);
        var aspectRatio = $("#preview").width() / $('#preview').height(),
            original = $('.modal-body .thumbnail');
        var te = new Eschoppe.ThumbnailEditor(
          original,
          $('#preview'),
          {
            width: original.data('original-width'),
            height: original.data('original-height')
          }
        );
        $('.modal-footer .btn.primary').bind("click", function(){
          var coords = te.getCoordinates();
          $.ajax(saveURI, {
            data: {
              width:coords.width,
              height:coords.height,
              x:coords.x,
              y:coords.y
            },
            success:function(transport) {
              console.log(transport);
            }
          })
          console.log(te.getCoordinates());
        });
      }
    })
  });

});
