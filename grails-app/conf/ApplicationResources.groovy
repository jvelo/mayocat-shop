modules = {
    application {
        resource url:'js/application.js'
    }

    twitterBootstrap {
      resource url: 'lib/bootstrap/css/bootstrap.css'
      resource url: 'lib/bootstrap/css/bootstrap-responsive.css'
      resource url: 'lib/bootstrap/js/bootstrap.js'
    }

    knockout {
      resource url: 'lib/knockout/knockout.js'
    }

    editor {
      // defaultBundle false

      resource url: 'lib/editor/editor.nocache.js'
    }

    styles {
        resource url:'css/styles.css'
        resource url:'css/products.css'
    }

    thumbnailEditor {
      resource url:'js/thumbnailEditor.js'  
      resource url:'lib/jcrop/jquery.Jcrop.min.js'
      resource url:'lib/jcrop/jquery.Jcrop.css'
    }

    productEditor {
      resource url:'js/product.js'
    }
}
