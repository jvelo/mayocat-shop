class UrlMappings {

	static mappings = {

    "/product/$byname"(controller:"product", action:"expose")

		"/admin/product/$id?/$action?"(controller:"product") 
		"/admin/product/list"(controller:"product", action:"list") 
		"/admin/product/create"(controller:"product", action:"create") 
		"/admin/product/save"(controller:"product", action:"save") 
		"/admin/product/$productid/images/$id?/$action?"(controller:"imageSet") 
		"/admin/product/$productid/images/create"(controller:"imageSet", action:"create") 
		"/admin/product/$productid/images/save"(controller:"imageSet", action:"save") 
		"/admin/product/$productid/images/list"(controller:"imageSet", action:"list") 
		"/admin/category/$action?/$id?"(controller:"category") 

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
