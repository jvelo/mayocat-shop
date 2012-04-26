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
      action = [GET:"expose", POST:"exposeDoCheckout"]
    }

    // Admin area
    // ----------

    "/login/$action?"(controller: "login")
    "/logout/$action?"(controller: "logout")
		"/admin/"(controller:"admin", action:"dashboard") 

    // Admin -> Preferences
    "/admin/preferences/"(controller:"shop", action:"edit")
    "/admin/preferences/edit"(controller:"shop", action:"edit")
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
		"/admin/product/$productid/images/$id/delete"(controller:"imageSet", action:"delete") 
		"/admin/product/$productid/images/$id/index"(controller:"imageSet", action:"index")
		"/admin/product/$productid/images/$id?/$action?"(controller:"imageSet")
		"/admin/product/$productid/images/create"(controller:"imageSet", action:"create") 
		"/admin/product/$productid/images/save"(controller:"imageSet", action:"save") 
		"/admin/product/$productid/images/list"(controller:"imageSet", action:"list") 

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
	}
}
