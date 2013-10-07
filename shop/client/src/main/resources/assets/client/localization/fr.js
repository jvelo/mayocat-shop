(function () {
    'use strict';

    var frLocalization = {

        global: {
            actions: {
                add: "Ajouter"
            }
        },
        entity: {
            misc: {
                defaultModel: "Modèle par défaut"
            },

            title: {
                model: "Modèle",
                confirmDeletion: "Confirmer la suppression"
            },

            action: {
                create: "Créer",
                edit: "Modifier",
                save: "Sauvegarder",
                update: "Mettre à jour",
                publish: "Mettre en ligne",
                cancel: "Annuler",
                remove: "Retirer",
                back: "Retour",

                editThumbnails: "Modifier les miniatures"
            },

            alert: {
                notFound: "Il n'y a rien à voir ici."
            }
        },

        home: {
            title: {
                welcome: "Bienvenue"
            },

            explanation: {
                home: "Gérez vos commandes quotidiennes avec cette interface d'administration. Vous pouvez aussi modifier les contenus de vos pages et de vos produits."
            }
        },

        authentication: {
            misc: {
                title: "Authentification requise",
                username: "Nom d'utilisateur ou adresse email",
                password: "Mot de passe",
                remember: "Se souvenir de moi"
            },

            action: {
                login: "Connexion",
                logout: "Déconnexion"
            },

            alert: {
                failure: "Nous ne pouvons pas vous connecter avec ces identifiants."
            }
        },

        upload: {
            misc: {
                uploading: "En cours de téléchargement...",
                uploaded: "Téléchargement terminé !",
                failed: "Échec"
            },

            explanation: {
                dropHere: "Déplacez des images de votre ordinateur et déposez-les dans cette zone"
            },

            placeholder: {
                title: "Titre de l'image (texte alternatif)",
                description: "Description (facultatif)"
            },

            action: {
                browse: "Parcourir...",
                dismiss: "Abandonner",
                upload: "Envoyer",
                uploadAll: "Tout envoyer"
            }
        },

        thumbnailEditor: {
            misc: {
                editing: "Modification de :",
            },

            title: {
                editThumbnails: "Modification des miniatures"
            },

            action: {
                saveAndNext: "Sauvegarder et continuer",
                close: "Fermer"
            }
        },

        order: {
            misc: {
                statusShort: "État",
                statusLong: "État de la commande",
                date: "Date",
                amount: "Montant",
                quantity: "Quantité",
                total: "Total",
                unitPrice: "Prix unitaire",
                shipping: "Frais de port",
                deliveryAddress: "Adresse de livraison",

                orderName: "Commande \\#{slug}",
                ordersNumber: "pour {numberOfOrders} {numberOfOrders, plural, one{commande} other{commandes}}",

                itemsNumber: "{numberOfItems} {numberOfItems, plural, one{article} other{articles}}",
                itemsTotal: "Coût total (sans frais de port)",
                shippingCalculation: "{title} (calculé par {strategy})"
            },

            title: {
                orders: "Commandes",
                order: "Gestion de la commande",
                items: "Articles",
                item: "Article",
                turnover: "Chiffre d'affaires",
                customer: "Client"
            },

            period: {
                today: "Aujourd'hui",
                weekly: "7 derniers jours",
                monthly: "30 derniers jours",
                overall: "Depuis toujours"
            },

            status: {
                waitingForPayment: "En attente de paiement",
                paid: "Payée",
                prepared: "Préparée",
                shipped: "Expédiée"
            },

            action: {
                paymentReceived: "Paiement reçu",
                commandPrepared: "Colis préparé",
                commandShipped: "Colis expédié"
            },

            alert: {
                empty: "Il n'y a aucune commande pour le moment."
            }
        },

        product: {
            misc: {
                price: "Prix",
                weight: "Poids",
                stock: "Stock",

                uncategorized: "Non {numberOfProducts, plural, one{classé} other{classés}}",
                description: "Description"
            },

            title: {
                products: "Produits",
                allProducts: "Tous les produits",
                newProduct: "Nouveau produit",
                editProduct: "Modifier le produit",

                collections: "Collections",
                newCollection: "Nouvelle collection",
                editCollection: "Modifier la collection",

                catalog: "Catalogue"
            },

            explanation: {
                product: "Vous pouvez éditer les caractéristiques et les images de vos produits, ainsi que gérer leur disponibilité et leur prix.",
                collection: "Les collections vous permettent de classer vos produits et de les grouper sur une seule et même page au sein de votre site web."
            },

            placeholder: {
                productTitle: "Titre du produit",
                collectionTitle: "Titre de la collection",
                initialStock: "Stock initial"
            },

            status: {
                onShelf: "En rayon",
                withdrawnFromSale: "Retiré de la vente"
            },

            action: {
                addProduct: "Ajouter un nouveau produit",
                deleteProduct: "Supprimer ce produit",
                manageProduct: "Gérer le produit",
                addCollection: "Ajouter une nouvelle collection",
                deleteCollection: "Supprimer cette collection"
            },

            alert: {
                confirmProductDeletion: "Veuillez confirmer la suppression de ce produit. Cette opération ne peut pas être annulée !",
                confirmCollectionDeletion: "Veuillez confirmer la suppression de cette collection. Cette opération ne peut pas être annulée !"
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
                noImage: "- utiliser la section ci-dessous pour ajouter de nouvelles images à ce produit."
            },

            action: {
                editMetadata: "Éditer les métadonnées"
            },

            alert: {
                noImage: "Aucune image"
            }
        },

        content: {
            misc: {
                content: "Contenu"
            },

            title: {
                pages: "Pages",
                allPages: "Toutes les pages",
                newPage: "Nouvelle page",
                editPage: "Modifier la page",

                news: "Actualités",
                allArticles: "Toutes les actualités",
                newArticle: "Nouvelle actualité",
                editArticle: "Modifier l'actualité",

                contents: "Contenus"
            },

            explanation: {
                page: "Cette section liste toutes les pages de votre site web. Pour chaque page, vous pouvez modifier son contenu, son titre, ses images et sa disposition. Vous pouvez aussi créer de nouvelles pages et supprimer celles existantes.",
                news: "Tout comme un blog, vous pouvez ajouter ici de nouvelles actualités qui seront mises en avant sur votre site web et que vous pouvez publier sur les réseaux sociaux."
            },

            placeholder: {
                pageTitle: "Titre de la page",
                articleTitle: "Titre de l'actualité"
            },

            status: {
                published: "Publiée",
                unpublished: "Non-publié",
                publishedOn: "Publié le {articleDate}"
            },

            action: {
                createPage: "Créer une nouvelle page",
                deletePage: "Supprimer cette page",

                createArticle: "Créer une actualité",
                deleteArticle: "Supprimer cette actualité",

                changePublicationDate: "Modifier la date de publication",
                changeDate: "Modifier la date"
            },

            alert: {
                confirmPageDeletion: "Veuillez confirmer la suppression de cette page. Cette opération ne peut pas être annulée !",
                confirmArticleDeletion: "Veuillez confirmer la suppression de cette actualité. Cette opération ne peut pas être annulée !"
            }
        },

        settings: {
            misc: {
                general: "Général",
                shop: "Boutique",
                catalog: "Catalogue",
                shipping: "Frais de port",

                timeZone: "Fuseau horaire",
                selectTimeZone: "Sélectionnez un fuseau horaire ",
                defaultLanguage: "Langage par défaut",
                otherLanguages: "Autres langages",
                selectALanguage: "-- Choisir un langage --",
                theme: "Thème",

                shopName: "Nom de la boutique",
                shopLogo: "Logo de la boutique",

                byWeight: "Selon le poids",
                byPrice: "Selon le prix",
                flatRate: "Selon un coût forfaitaire",
                none: "Aucun",

                defaultCurrency: "Devise par défaut",
                otherCurrencies: "Autres devises",
                categoriesManagement: "Gestion des catégories",
                stockManagement: "Gestion de stocks",
                weightManagement: "Gestion du poids (laissez ceci coché si vous souhaitez calculer les frais de port en fonction du poids)",
                weightUnit: "Unité de poids",

                selectedDestinations: "{numberOfSelectedDestinations, plural, one{Destination sélectionnée} other{Destinations sélectionnées}} :",
                carrierName: "Nom",
                carrierDescription: "Description (facultatif)",

                deliveryTimePart1: "Délai de livraison de",
                deliveryTimePart2: "à",
                deliveryTimePart3: "{maximumDaysSelected, plural, one{jour} other{jours}}",

                weight: "Poids",
                orderAmount: "Montant de la commande",
                shippingPrice: "Coût des frais de port",

                carrierValuePart1: "De",
                carrierValuePart2: "jusqu'à",

                additionalWeight: "{weightUnit} additionnels",
                currencyPerOrder: "{mainCurrency} par commande",
                currencyPerItem: "{mainCurrency} par article",

                carrierDestination: "{numberOfDestinations, plural, one{Destination} other{Destinations}} :",
                deliveryTime: "Temps de livraison :",
                deliveryDays: "{deliveryMaximumDays, plural, one{jour} other{jours}}"
            },

            title: {
                settings: "Paramètres",
                sections: "Sections",

                general: "Paramètres généraux",
                shop: "À propos de la boutique",
                catalog: "Catalogue",
                shipping: "Frais de port",

                currencies: "Devises",
                geographicDestinations: "Destinations géographiques",
                carrier: "Transporteur",
                pricing: "Tarifs"
            },

            explanation: {
                shipping: "Vous pouvez choisir de facturer les frais de port en fonction du poids ou du prix, ou bien choisir un prix fixe par produit en plus d'un coût forfaitaire pour chaque commande.",
                weight: "La stratégie de facturation des frais de port en fonction du poids n'est pas activée. Activez-la si vous souhaitez facturer les frais de port à vos clients en fonction du poids des produits.",
                price: "La stratégie de facturation des frais de port en fonction du montant total de la commande n'est pas activée. Activez-la si vous souhaitez facturer les frais de port à vos clients en fonction du montant total de la commande.",
                flatRate: "La stratégie de facturation des frais de port selon un coût forfaitaire n'est pas activée. Activez-la si vous souhaitez facturer les frais de port à vos clients en fonction du nombre d'articles en plus d'un coût forfaitaire.",
                none: "Utilisez cette section si vous souhaitez désactiver toute facturation des frais de ports.",
                costsDisabled: "La facturation des frais de port est actuellement désactivée. Si vous souhaitez facturer les frais de ports à vos clients, choisissez d'abord une stratégie de facturation disponible ci-dessus : en fonction du poids, du prix ou bien selon un coût forfaitaire."
            },

            placeholder: {
                shopName: "Entrez le nom de la boutique"
            },

            action: {
                addZone: "Ajouter une nouvelle zone",
                editZone: "Modifier la zone",
                deleteZone: "Supprimer la zone",

                activateWeight: "Activer la stratégie de poids",
                activatePrice: "Activer la stratégie de prix",
                activateFlatRate: "Activer la stratégie de coût forfaitaire",
                disableShippingCosts: "Désactiver les frais de ports"
            },

            alert: {
                deliveryTime: "La période de livraison n'est pas valide",
                carrierValueInferior: "La dernière valeur est inférieure à la première",
                carrierValueInvalid: "Format non-conforme"
            }
        }
    };

    // Expose the localization to the Mayocat settings
    Mayocat.localization.fr = frLocalization;

})();