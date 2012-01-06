class UrlMappings {

	static mappings = {

    "/product/$byname"(controller:"product", action:"expose")

		"/admin/product/$action?/$id?"(controller:"product") 
		"/admin/category/$action?/$id?"(controller:"category") 

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
