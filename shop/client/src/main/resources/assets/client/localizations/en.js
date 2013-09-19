(function () {
    'use strict';

    var enLocalization = {
        entity: {
            misc: {
                defaultModel: "Default model"
            },

            title: {
                model: "Model",
                confirmDeletion: "Confirm deletion"
            },

            action: {
                create: "Create",
                edit: "Edit",
                save: "Save",
                update: "Update",
                publish: "Publish online",
                cancel: "Cancel",
                remove: "Remove",
                back: "Back",

                editThumbnails: "Edit thumbnails"
            },

            alert: {
                notFound: "Nothing to see here."
            }
        },

        home: {
            title: {
                welcome: "Welcome"
            },

            explanation: {
                home: "Handle your daily purchase orders in this administration panel. You can also modify the contents of your pages and products."
            }
        },

        authentication: {
            misc: {
                title: "Authentication required",
                username: "User name or email",
                password: "Password",
                remember: "Remember me"
            },

            action: {
                login: "Login",
                logout: "Logout"
            },

            alert: {
                failure: "We could not authenticate you with these credentials."
            }
        },

        upload: {
            misc: {
                uploading: "Uploading...",
                uploaded: "Uploaded!",
                failed: "Failed"
            },

            explanation: {
                dropHere: "Drag images from your computer and drop them in this area",
            },

            placeholder: {
                title: "Image's title (alternative text)",
                description: "Description (optional)"
            },

            action: {
                browse: "Browse...",
                dismiss: "Dismiss",
                upload: "Upload",
                uploadAll: "Upload all"
            }
        },

        order: {
            misc: {
                statusShort: "Status",
                statusLong: "Order status",
                date: "Date",
                amount: "Amount",
                quantity: "Quantity",
                total: "Total",
                unitPrice: "Unit price",
                shipping: "Shipping",
                deliveryAddress: "Delivery address",
                
                orderName: "Order \\#{slug}",
                ordersNumber: "for {numberOfOrders} {numberOfOrders, plural, one{order} other{orders}}",

                itemsNumber: "{numberOfItems} {numberOfItems, plural, one{item} other{items}}",
                itemsTotal: "Items total (without shipping)",
                shippingCalculation: "{shippingTitle} (calculated by {shippingStrategy})"
            },

            title: {
                orders: "Orders",
                order: "Manage order",
                items: "Items",
                item: "Item",
                turnover: "Turnover",
                customer: "Customer"
            },

            period: {
                today: "Today",
                weekly: "Last 7 days",
                monthly: "Last 30 days",
                overall: "Overall"
            },

            status: {
                waitingForPayment: "Waiting for payment",
                paid: "Paid",
                prepared: "Prepared",
                shipped: "Shipped"
            },

            action: {
                paymentReceived: "Payment received",
                commandPrepared: "Command prepared",
                commandShipped: "Command shipped"
            },

            alert: {
                empty: "There are no orders yet."
            }
        },

        product: {
            misc: {
                price: "Price",
                weight: "Weight",
                stock: "Stock",

                uncategorized: "Uncategorized",
                description: "Description"
            },

            title: {
                products: "Products",
                allProducts: "All products",
                newProduct: "New product",
                editProduct: "Edit product",

                collections: "Collections",
                newCollection: "New collection",
                editCollection: "Edit collection",

                catalog: "Catalog"
            },

            explanation: {
                product: "You can edit your products to change their characteristics or images, and also manage their availability and price.",
                collection: "Collections let you categorize your products and group them together in a single page on your website."
            },

            placeholder: {
                productTitle: "Product's title",
                collectionTitle: "Collection's title",
                initialStock: "Initial stock"
            },

            status: {
                onShelf: "On shelf",
                withdrawnFromSale: "Withdrawn from sale"
            },

            action: {
                addProduct: "Add a new product",
                deleteProduct: "Delete this product",
                manageProduct: "Manage product",
                addCollection: "Add a new collection",
                deleteCollection: "Delete this collection"
            },

            alert: {
                confirmProductDeletion: "Please confirm you want to delete this product. There's no coming back!",
                confirmCollectionDeletion: "Please confirm you want to delete this collection. There's no coming back!"
            }
        },

        image: {
            misc: {
                actions: "Actions"
            },

            title: {
                images: "Images ({imagesLength})"
            },

            explanation: {
                noImage: "- use the area below to add images to this product."
            },

            action: {
                editMetadata: "Edit metadata"
            },

            alert: {
                noImage: "No image yet"
            }
        },

        content: {
            misc: {
                content: "Content"
            },

            title: {
                pages: "Pages",
                allPages: "All pages",
                newPage: "New page",
                editPage: "Edit page",

                news: "News",
                allArticles: "All news articles",
                newArticle: "New article",
                editArticle: "Edit article",

                contents: "Contents"
            },

            explanation: {
                page: "This area lists all content pages of your web site. For each page you can modify its content, title, images and layout. You can also create new pages and delete existing ones.",
                news: "Just like a blog, you can add here news article that will be highlighted on your web site and that you can publish on social networks."
            },

            placeholder: {
                pageTitle: "Page's title",
                articleTitle: "Article's title"
            },

            status: {
                published: "Published",
                unpublished: "Unpublished",
                publishedOn: "Published on {articleDate}"
            },

            action: {
                createPage: "Create a new page",
                deletePage: "Delete this page",

                createArticle: "Create a news article",
                deleteArticle: "Delete this article",

                changePublicationDate: "Change publication date",
                changeDate: "Change date"
            },

            alert: {
                confirmPageDeletion: "Please confirm you want to delete this page. There's no coming back!",
                confirmArticleDeletion: "Please confirm you want to delete this article. There's no coming back!"
            }
        }
    };

    // Expose the localization to the Mayocat settings
    Mayocat.localizations.en = enLocalization;

})();