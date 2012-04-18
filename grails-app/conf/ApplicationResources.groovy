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

    styles {
        resource url:'less/styles.less', attrs:[rel: "stylesheet/less", type:'css'], bundle:'bundle_styles'
        resource url:'less/products.less', attrs:[rel: "stylesheet/less", type:'css'], bundle:'bundle_styles'
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
