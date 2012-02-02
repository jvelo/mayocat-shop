class UrlMappings {

	static mappings = {

		"500"(view:'/error')

    // Resources
    // ---------
    "/resources/**"(controller:"resource", action:"serve")
    "/category/resources/**"(controller:"resource", action:"serve")
    "/product/resources/**"(controller:"resource", action:"serve")
    "/cart/resources/**"(controller:"resource", action:"serve")

    // Public area
    // -----------

    // Home
    "/"(controller:"home", action:"expose")
    // Product
    "/product/$byname"(controller:"product", action:"expose")
    "/product/$byname/images/$imageid/$filename"(controller:"imageSet", action:"expose")
    "/product/$byname/images/$imageid/$size?/$filename?"(controller:"imageSet", action:"expose")
    // Category
    "/category/$byname"(controller:"category", action:"expose")

    // Cart
    "/cart/"(controller:"cart", action:"expose")
    "/cart/$action"(controller:"cart")

    // Admin area
    // ----------

    // Admin -> product
		"/admin/"(controller:"admin", action:"dashboard") 
		"/admin/product/"(controller:"product", action:"list") 
		"/admin/product/create"(controller:"product", action:"create") 
		"/admin/product/save"(controller:"product", action:"save") 
		"/admin/product/update"(controller:"product", action:"update")
		"/admin/product/show"(controller:"product", action:"show")
		"/admin/product/edit"(controller:"product", action:"edit")
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

	}
}
