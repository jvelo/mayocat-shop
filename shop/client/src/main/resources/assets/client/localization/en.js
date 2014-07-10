/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function (Mayocat) {
    'use strict';

    var enLocalization = {

        /**
         * Global translations, not linked to a particular aspect of the app
         */
        global: {
            actions: {
                add: "Add",
                remove: "Remove",
                edit: "Edit",
                close: "Close",
                save: "Save"
            },
            validation: {
                required : "Required"
            },
            conflict: "Conflict",
            filter: "Filter",
            select: "Select"
        },

        /**
         * All the titles used for the routes
         */
        routes: {
            title: {
                home: "Home",
                orders: "Orders",
                customers: "Customers",
                homepage: "Home page",
                news: "News",
                pages: "Pages",
                products: "Products",
                catalog: "Catalog",
                settings: "Settings"
            }
        },

        /**
         * Translations linked to generic Mayocat entities
         */
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
                back: "Back"
            },

            alert: {
                notFound: "Nothing to see here."
            },

            addons: {
                sequenceAddElementText: "Add element"
            }
        },

        /**
         * Translations linked to the homepage / dashboard
         */

        dashboard: {
            title: "Welcome",
            introduction: "Handle your daily purchase orders in this administration panel. You can also modify the contents of your pages and products.",
            latestOrders: "Latest orders",
            seeAllOrders: "See all orders"
        },

        /**
         * Translations linked to the authentication module
         */

        authentication: {
            misc: {
                title: "Welcome to Mayocat Shop",
                username: "Username",
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

        /**
         * Translations linked to the image upload module
         */

        upload: {
            explanation: {
                dropCollection: "Drop your files to add theme to “{collectionTitle}” collection",
                dropProduct: "Drop your files to add theme to “{productTitle}” product",
                dropPage: "Drop your files to add theme to “{pageTitle}” page",
                dropArticle: "Drop your files to add theme to “{articleTitle}” article",
                dropLogo: "Drop your logo"
            },

            status: {
                progress: "Upload of {filesNumber} images in progress",
                success: "Your images have been successfully uploaded"
            },

            action: {
                add: "Add an image",
                select: "Select file"
            }
        },

        /**
         * Translations linked to the thumbnail editor
         */

        imageEditor: {
            misc: {
                width: "width",
                height: "height"
            },

            title: {
                editImage: "Edit image"
            },

            placeholder: {
                title: "Image's title (alternative text)",
                description: "Description (optional)"
            },

            action: {
                save: "Save",
                close: "Close"
            }
        },

        /**
         * Translations linked to the orders and the turnover
         */

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
                billingAddress: "Billing address",
                additionalInformation:  "Additional information",
                changeStatus: "Change status",

                orderName: "Order \\#{slug}",
                ordersNumber: "for {numberOfOrders} {numberOfOrders, plural, one{order} other{orders}}",

                itemsNumber: "{numberOfItems} {numberOfItems, plural, one{item} other{items}}",
                itemsTotal: "Items total (without shipping)",
                shippingCalculation: "{title} (calculated by {strategy})"
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
                shipped: "Shipped",
                cancelled: "Cancelled"
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

        /**
         * Translations linked to the collections and their products
         */

        product: {
            misc: {
                price: "Price",
                weight: "Weight",
                stock: "Stock",

                uncategorized: "Not categorized",
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
                withdrawnFromSale: "Withdrawn from sale",
                moved: "The product has been moved"
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
            },

            variants: {
                title: "Variants",
                price: "Price",
                stock: "Stock",
                add: "Add variant",
                delete: "Delete",
                edit: "Edit",
                create: "Create variant",
                edition: "Variant: {variantTitle}",
                update: "Update variant",
                conflict: "This variant already exists.",
                confirmVariantDeletion: "Please confirm you want to delete this variant. There's no coming back!",
                deleteVariant: "Delete variant"
            }
        },

        /**
         * Translations linked to the image module used for the forms which accepts image upload
         */

        image: {
            title: {
                images: "Images ({imagesLength})"
            },

            status: {
                moved: "The image has been moved"
            },

            action: {
                less: "Display less images",
                more: "Display more images"
            }
        },

        /**
         * Translations linked to the content section (which includes pages and news articles)
         */

        content: {
            misc: {
                content: "Content"
            },

            title: {
                homePage: "Home page",

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
            },

            homePage: {
                featuredProducts: "Featured products",
                addFeaturedProduct: "Add product",
                addFeaturedProductModalTitle: "Add a featured product",
                emptyFeaturedProductsList: "Here you can manage a list of products that will be featured on the home page of your shop (if your theme supports it)."
            }
        },

        /**
         * Translations linked to the whole settings
         */

        settings: {

            tenant: {
                shopName: "Shop name",
                shopNamePlaceholder: "Enter the shop name",
                shopDescription: "Shop description",
                contactEmail: "Contact email",
                contactEmailPlaceholder: "Email at which notifications will be sent",
                shopLogo: "Shop logo"
            },

            misc: {
                general: "General",
                shop: "Shop information",
                catalog: "Catalog",
                shipping: "Shipping",

                timeZone: "Time zone",
                selectTimeZone: "Select a time zone",
                defaultLanguage: "Default language",
                otherLanguages: "Other languages",
                selectALanguage: "-- Select a language --",
                languagesGroup: "Languages",
                variantsGroup: "Variants",
                theme: "Theme",

                byWeight: "By weight",
                byPrice: "By price",
                flatRate: "Flat rate",
                none: "None",

                defaultCurrency: "Default currency",
                otherCurrencies: "Other currencies",
                collectionsManagement: "Collections management",
                stockManagement: "Stock management",
                weightManagement: "Weight management (keep this checked if you want to calculate shipping costs by weight)",
                weightUnit: "Weight unit",

                selectedDestinations: "Selected {numberOfSelectedDestinations, plural, one{destination} other{destinations}}:",
                carrierName: "Name",
                carrierDescription: "Description (optional)",

                deliveryTimePart1: "Delivery time from",
                deliveryTimePart2: "to",
                deliveryTimePart3: "{maximumDaysSelected, plural, one{day} other{days}}",

                weight: "Weight",
                orderAmount: "Order amount",
                shippingPrice: "Shipping price",

                carrierValuePart1: "From",
                carrierValuePart2: "up to",

                additionalWeight: "Additional {weightUnit}",
                currencyPerOrder: "{mainCurrency} per order",
                currencyPerItem: "{mainCurrency} per item",

                carrierDestination: "{numberOfDestinations, plural, one{Destination} other{Destinations}}:",
                deliveryTime: "Delivery time:",
                deliveryDays: "{deliveryMaximumDays, plural, one{day} other{days}}",
                costPerOrderAndPerItem: "{costPerOrder} {mainCurrency} per order, {costPerItem} {mainCurrency} per item"
            },

            title: {
                settings: "Settings",
                sections: "Sections",

                general: "General settings",
                shop: "Shop information",
                catalog: "Catalog settings",
                shipping: "Shipping settings",

                currencies: "Currencies",
                geographicDestinations: "Geographic destinations",
                carrier: "Carrier",
                pricing: "Pricing"
            },

            explanation: {
                shipping: "You can choose to charge shipping costs according to order weight or price, or have a flat rate by number of products plus fixed rate by order.",
                weight: "Shipping pricing strategy by weight is not active. Activate it if you want to charge customers for shipping according to products weight.",
                price: "Shipping pricing strategy by order total amount is not active. Activate it if you want to charge customers for shipping according to order total amount.",
                flatRate: "Flat rate shipping pricing strategy is not active. Activate it if you want to charge customers for shipping according to number of items purchased plus per-order constant.",
                none: "Use this if you wish to disable shipping costs altogether.",
                costsDisabled: "Shipping costs are currently disabled. If you wish to charge customers for shipping costs, first pick up a pricing strategy from the strategies available above: per weight, per price, or flat rate."
            },

            action: {
                addZone: "Add new zone",
                editZone: "Edit zone",
                deleteZone: "Delete zone",

                activateWeight: "Activate weight strategy",
                activatePrice: "Activate price strategy",
                activateFlatRate: "Activate flat rate strategy",
                disableShippingCosts: "Disable shipping costs"
            },

            alert: {
                deliveryTime: "Delivery time range is not valid",
                carrierValueInferior: "The \"up to\" value is inferior to \"from\" value",
                carrierValueInvalid: "Invalid format"
            }
        }
    };

    Mayocat.localization = Mayocat.localization || {};

    // Expose the localization to the Mayocat settings
    Mayocat.localization.en = enLocalization;

})(Mayocat);
