(function () {
    'use strict';

    var frLocalization = {
        entity: {
            action: {
                update: "Mettre à jour",
                back: "Retour"
                remove: "Retirer",

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
        }
    };

    // Expose the localization to the Mayocat settings
    Mayocat.localizations.fr = frLocalization;

})();