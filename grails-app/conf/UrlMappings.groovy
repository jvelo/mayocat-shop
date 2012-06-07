class UrlMappings {

	static mappings = {

		"500"(view:'/error')

    // Resources
    // ---------
    "/resources/**"(controller:"resource", action:"serve")
    "/category/resources/**"(controller:"resource", action:"serve")
    "/product/resources/**"(controller:"resource", action:"serve")
    "/cart/resources/**"(controller:"resource", action:"serve")
    "/checkout/resources/**"(controller:"resource", action:"serve")
    "/page/resources/**"(controller:"resource", action:"serve")

    // Public area
    // -----------

    // Home
    "/"(controller:"home", action:"expose")
    // Product
    "/product/" (controller:"product", action:"all")
    "/product/$byname"(controller:"product", action:"expose")
    "/product/$byname/images/$imageid/$size?/$filename"(controller:"imageSet", action:"expose")
    // Category
    "/category/$byname"(controller:"category", action:"expose")
    // Page
    "/page/$byname"(controller:"page", action:"expose")

    // Cart
    "/cart/"(controller:"cart", action:"expose")
    "/cart/$action"(controller:"cart")

    // Checkout
    "/checkout/"(controller:"checkout") {
      action = [GET:"expose", POST:"createOrder"]
    }
    name checkoutPaymentBefore: "/checkout/payment/"(controller:"checkout") {
        // Restful URLs needs to be named for reverse routing to work.
        action = [GET:"selectPaymentMethod", POST:"doBeforePayment"]
      }
    name checkoutPaymentAfter: "/checkout/payment/success"(controller:"checkout") {
      // Restful URLs needs to be named for reverse routing to work.
      action = [GET:"doPaymentSuccess", POST:"doPaymentSuccess"]
    }
    
    "/checkout/payment/$method/ack"(controller:"checkout", action:"doPaymentAck")
    "/checkout/serve/logo/$filename?"(controller:"shop", action:"serveLogo")
    "/checkout/serve/css/$filename"(controller:"shop", action:"serveCss")
    
    name checkoutPaymentFailure: "/checkout/payment/failure"(controller:"checkout") {
        // Restful URLs needs to be named for reverse routing to work.
        action = [GET:"", POST:"doPaymentFailure"]
      }

    // Admin area
    // ----------

    "/login/$action?"(controller: "login")
    "/logout/$action?"(controller: "logout")
		"/admin/"(controller:"admin", action:"dashboard") 

    // Admin -> Preferences
    "/admin/preferences/"(controller:"shop", action:"edit")
    "/admin/preferences/edit"(controller:"shop", action:"edit")
    "/admin/preferences/update"(controller:"shop", action:"update")
    "/admin/preferences/updateCheckoutPages"(controller:"shop", action:"updateCheckoutPages")
    "/admin/preferences/index"(controller:"shop", action:"index")
    "/admin/preferences/payments"(controller:"shop", action:"editPaymentMethods")
    "/admin/preferences/payments/$id"(controller:"shop", action: "configurePaymentMethod", parseRequest:false)
    "/admin/preferences/checkout"(controller:"shop", action:"editCheckoutPages")

    "/shop/"(controller:"shop") // FIXME -> keep everything "shop" under admin/preferences

    // Admin -> product
		"/admin/product/"(controller:"product", action:"list") 
		"/admin/product/create"(controller:"product", action:"create") 
		"/admin/product/save"(controller:"product", action:"save") 
		"/admin/product/update"(controller:"product", action:"update")
		"/admin/product/show"(controller:"product", action:"show")
		"/admin/product/edit"(controller:"product", action:"edit")
		"/admin/product/index"(controller:"product", action:"index")
		"/admin/product/update"(controller:"product", action:"update")
		"/admin/product/editCategories"(controller:"product", action:"editCategories")

    // Admin -> category
		"/admin/category/"(controller:"category", action:"list") 
		"/admin/category/create"(controller:"category", action:"create") 
		"/admin/category/save"(controller:"category", action:"save") 
		"/admin/category/update"(controller:"category", action:"update")
		"/admin/category/show"(controller:"category", action:"show")
		"/admin/category/edit"(controller:"category", action:"edit")
		"/admin/category/index"(controller:"category", action:"index")
		"/admin/category/update"(controller:"category", action:"update")

    // Admin -> pages
		"/admin/page/"(controller:"page", action:"list") 
		"/admin/page/create"(controller:"page", action:"create") 
		"/admin/page/save"(controller:"page", action:"save") 
		"/admin/page/update"(controller:"page", action:"update")
		"/admin/page/show"(controller:"page", action:"show")
		"/admin/page/edit"(controller:"page", action:"edit")
		"/admin/page/index"(controller:"page", action:"index")
		"/admin/page/update"(controller:"page", action:"update")

    // Admin -> orders
		"/admin/order/"(controller:"order", action:"list") 
		"/admin/order/update"(controller:"order", action:"update")
		"/admin/order/show"(controller:"order", action:"show")
		"/admin/order/index"(controller:"order", action:"index")
		"/admin/order/update"(controller:"order", action:"update")

    // Admin -> page or product images
		"/admin/$type/$itemid/images/create"(controller:"imageSet", action:"create") 
		"/admin/$type/$itemid/images/$id/delete"(controller:"imageSet", action:"delete")
		"/admin/$type/$itemid/images/$id/index"(controller:"imageSet", action:"index")
		"/admin/$type/$itemid/images/$id/edit"(controller:"imageSet", action:"edit")
		"/admin/$type/$itemid/images/$id/update"(controller:"imageSet", action:"update")
		"/admin/$type/$itemid/images/save"(controller:"imageSet", action:"save")
		"/admin/$type/$itemid/images/list"(controller:"imageSet", action:"list") 
		"/admin/$type/$itemid/images/$id?/$action"(controller:"imageSet")
	}
}
