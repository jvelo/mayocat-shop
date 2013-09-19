(function () {
    'use strict';

    var frLocalization = {
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
        }
    };

    // Expose the localization to the Mayocat settings
    Mayocat.localizations.fr = frLocalization;

})();