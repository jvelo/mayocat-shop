var Eschoppe = (function(Eschoppe) {

  Eschoppe.ThumbnailEditor = function(image, preview, originalDimensions, position) {

    this.image = image;
    this.preview = preview;
    this.previewWidth = this.preview.width();
    this.previewHeight = this.preview.height();
    this.canvasWidth = this.image.width();
    this.canvasHeight = this.image.height();
    this.originalDimensions = originalDimensions;
    this.position = (position == undefined ? undefined : [
      this.getRelativePosition(position.x1, position.y1),
      this.getRelativePosition(position.x2, position.y2)
    ]);

    this.image.Jcrop({
      setSelect: this.position ? [this.position[0].x, this.position[0].y, this.position[1].x, this.position[1].y] : [0,0,500,500],
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

    getRelativePosition: function(x, y) {
      return {
        x: parseInt(x * this.canvasWidth / this.originalDimensions.width),
        y: parseInt(y * this.canvasHeight / this.originalDimensions.height),
      }
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
            original = $('.modal-body .thumbnail'),
            dimensions = {
               width: original.data('original-width'),
               height: original.data('original-height')
            },
            position = !original.data('target-x1') ? undefined : {
               x1: original.data('target-x1'),
               y1: original.data('target-y1'),
               x2: original.data('target-x2'),
               y2: original.data('target-y2')
            };
        var te = new Eschoppe.ThumbnailEditor(
          original,
          $('#preview'),
          dimensions,
          position
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
        });
      }
    })
  });

});
