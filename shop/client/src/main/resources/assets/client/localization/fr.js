/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function (Mayocat) {
    'use strict';

    var frLocalization = {

        global: {
            actions: {
                add: "Ajouter",
                remove: "Retirer",
                edit: "Modifier",
                close: "Fermer",
                save: "Sauvegarder"
            },
            validation: {
                required : "Requis"
            },
            conflict: "Conflit",
            filter: "Filtrer",
            select: "Selectionner"
        },

        routes: {
            title: {
                home: "Accueil",
                orders: "Commandes",
                customers: "Clients",
                homepage: "Page d'accueil",
                news: "Actualités",
                pages: "Pages",
                products: "Produits",
                catalog: "Catalogue",
                settings: "Paramètres"
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
                back: "Retour"
            },

            alert: {
                notFound: "Il n'y a rien à voir ici."
            },

            addons: {
                sequenceAddElementText: "Ajouter un element"
            }
        },

        dashboard: {
            title: "Bienvenue",
            introduction: "Gérez vos commandes quotidiennes avec cette interface d'administration. Vous pouvez aussi modifier les contenus de vos pages et de vos produits.",
            latestOrders: "Dernières commandes",
            seeAllOrders: "Voir toutes les commandes"
        },

        authentication: {
            misc: {
                title: "Bienvenue sur Mayocat Shop",
                username: "Nom d'utilisateur",
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
            explanation: {
                dropCollection: "Déposez vos fichiers pour les ajouter à la collection « {collectionTitle} »",
                dropProduct: "Déposez vos fichiers pour les ajouter au produit « {productTitle} »",
                dropPage: "Déposez vos fichiers pour les ajouter à la page « {pageTitle} »",
                dropArticle: "Déposez vos fichiers pour les ajouter à l'actualité « {articleTitle} »",
                dropLogo: "Déposez votre logo"
            },

            status: {
                progress: "Téléchargement de {filesNumber} images en cours",
                success: "Vos images ont été téléchargées avec succès"
            },

            action: {
                add: "Ajouter une image",
                select: "Choisir un fichier"
            }
        },

        imageEditor: {
            misc: {
                width: "de largeur",
                height: "de hauteur"
            },

            title: {
                editImage: "Editer l'image"
            },

            placeholder: {
                title: "Titre de l'image (texte alternatif)",
                description: "Description (facultatif)"
            },

            action: {
                save: "Sauvegarder",
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
                billingAddress: "Adresse de facturation",
                additionalInformation:  "Informations complémentaires",
                changeStatus: "Changer l'état",

                orderName: "Commande \\#{slug}",
                ordersNumber: "pour {numberOfOrders} {numberOfOrders, plural, one{commande} other{commandes}}",

                itemsNumber: "{numberOfItems} {numberOfItems, plural, one{article} other{articles}}",
                itemsTotal: "Coût total (sans frais de port)",
                shippingCalculation: "{title} (calculé par {strategy, select, flat{forfait} weight{poids} price{prix} other{NA}})"
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
                shipped: "Expédiée",
                cancelled: "Annulée"
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
                withdrawnFromSale: "Retiré de la vente",
                moved: "Le produit a bien été déplacé"
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
            },

            variants: {
                title: "Déclinaisons",
                price: "Prix",
                stock: "Stock",
                add: "Ajouter une déclinaison",
                edit: "Editer",
                delete: "Supprimer",
                create: "Créer la déclinaison",
                edition: "Déclinaison: {variantTitle}",
                update: "Mettre à jour",
                conflict: "Cette déclinaison existe déjà.",
                confirmVariantDeletion: "Veuillez confirmer la suppression de cette déclinaison. Cette opération ne peut pas être annulée !",
                deleteVariant: "Supprimer la déclinaison"
            }
        },

        image: {
            title: {
                images: "Images ({imagesLength})"
            },

            status: {
                moved: "L'image a bien été déplacée"
            },

            action: {
                less: "Voir moins d'images",
                more: "Voir plus d'images"
            }
        },

        content: {
            misc: {
                content: "Contenu"
            },

            title: {
                homePage: "Page d'accueil",

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
            },

            homePage: {
                featuredProducts: "Produits à la une",
                addFeaturedProduct: "Ajouter un produit",
                addFeaturedProductModalTitle: "Ajouter un produit à la une",
                emptyFeaturedProductsList: "Vous pouvez ici gérer une liste de produits à la une qui apparaîtront sur la page d'accueil de votre boutique (si votre thème les supportes)."
            }
        },

        settings: {

            tenant: {
                shopName: "Nom de la boutique",
                shopNamePlaceholder: "Entrez le nom de la boutique",
                shopDescription: "Description de la boutique",
                contactEmail: "Email de contact",
                contactEmailPlaceholder: "L'email auquel seront envoyées les notifications",
                shopLogo: "Logo de la boutique"
            },

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
                languagesGroup: "Langages",
                variantsGroup: "Variantes",
                theme: "Thème",

                byWeight: "Selon le poids",
                byPrice: "Selon le prix",
                flatRate: "Selon un coût forfaitaire",
                none: "Aucun",

                defaultCurrency: "Devise par défaut",
                otherCurrencies: "Autres devises",
                collectionsManagement: "Gestion des collections",
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
                deliveryDays: "{deliveryMaximumDays, plural, one{jour} other{jours}}",
                costPerOrderAndPerItem: "{costPerOrder} {mainCurrency} par commande, {costPerItem} {mainCurrency} par article"
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

    Mayocat.localization = Mayocat.localization || {};

    // Expose the localization to the Mayocat settings
    Mayocat.localization.fr = frLocalization;

})(Mayocat);