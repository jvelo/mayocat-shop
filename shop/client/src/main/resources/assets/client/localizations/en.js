(function () {
    'use strict';

    var enLocalization = {
        entity: {
            action: {
                update: "Update",
                back: "Back"
                remove: "Remove",

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
        }
    };

    // Expose the localization to the Mayocat settings
    Mayocat.localizations.en = enLocalization;

})();