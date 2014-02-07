/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function () {
    'use strict'

    angular.module('money', [])

        .factory('moneyService', function () {

            var currencies = {
                // Adapted from joda money MoneyData.csv
                // See https://github.com/JodaOrg/joda-money/tree/master/src/main/resources/org/joda/money
                // + Bitcoins (which is non ISO-4217)
                "AED": { code: 784, decimals: 2, countries: ['AE'], name: "Arab Emirates Dirham" },
                "AFN": { code: 971, decimals: 2, countries: ['AF'], name: "Afghanistan Afghani" },
                "ALL": { code: 8, decimals: 2, countries: ['AL'], name: "Albanian Lek" },
                "AMD": { code: 51, decimals: 0, countries: ['AM'], name: "Armenian Dram" },
                "ANG": { code: 532, decimals: 2, countries: ['AN'], name: "Netherlands Antillean Guilder" },
                "AOA": { code: 973, decimals: 1, countries: ['AO'], name: "Angolan Kwanza" },
                "ARS": { code: 32, decimals: 2, countries: ['AR'], name: "Argentine Peso" },
                "AUD": { code: 36, decimals: 2, countries: ['AU', 'CX', 'CC', 'HM', 'KI', 'NR', 'NF', 'TV'], name: "Australian Dollar" },
                "AWG": { code: 533, decimals: 2, countries: ['AW'], name: "Aruban Guilder" },
                "AZN": { code: 944, decimals: 2, countries: ['AZ'], name: "Azerbaijan New Manat" },
                "BAM": { code: 977, decimals: 2, countries: ['BA'], name: "Marka" },
                "BBD": { code: 52, decimals: 2, countries: ['BB'], name: "Barbados Dollar" },
                "BDT": { code: 50, decimals: 2, countries: ['BD'], name: "Bangladeshi Taka" },
                "BGN": { code: 975, decimals: 2, countries: ['BG'], name: "Bulgarian Lev" },
                "BHD": { code: 48, decimals: 3, countries: ['BH'], name: "Bahraini Dinar" },
                "BIF": { code: 108, decimals: 0, countries: ['BI'], name: "Burundi Franc" },
                "BMD": { code: 60, decimals: 2, countries: ['BM'], name: "Bermudian Dollar" },
                "BND": { code: 96, decimals: 2, countries: ['BN'], name: "Brunei Dollar" },
                "BOB": { code: 68, decimals: 2, countries: ['BO'], name: "Boliviano" },
                // "BOV" : { code: 984, decimals: 2, countries: ['BO'] },
                "BRL": { code: 986, decimals: 2, countries: ['BR'], name: "Brazilian Real" },
                "BSD": { code: 44, decimals: 2, countries: ['BS'], name: "Bahamian Dollar" },
                "BTN": { code: 64, decimals: 2, countries: ['BT'], name: "Bhutan Ngultrum" },
                "BWP": { code: 72, decimals: 2, countries: ['BW'], name: "Botswana Pula" },
                "BYR": { code: 974, decimals: 0, countries: ['BY'], name: "Belarussian Ruble" },
                "BZD": { code: 84, decimals: 2, countries: ['BZ'], name: "Belize Dollar" },
                "CAD": { code: 124, decimals: 2, countries: ['CA'], name: "Canadian Dollar" },
                "CDF": { code: 976, decimals: 2, countries: ['CD'], name: "Francs" },
                // "CHE" : { code: 947, decimals: 2, countries: ['CH'] },
                "CHF": { code: 756, decimals: 2, countries: ['CH', 'LI'], name: "Swiss Franc" },
                // "CHW" : { code: 948, decimals: 2, countries: ['CH'] },
                // "CLF" : { code: 990, decimals: 0, countries: ['CL'] },
                "CLP": { code: 152, decimals: 0, countries: ['CL'], name: "Chilean Peso" },
                "CNY": { code: 156, decimals: 1, countries: ['CN'], name: "Yuan Renminbi" },
                "COP": { code: 170, decimals: 0, countries: ['CO'], name: "Colombian Peso" },
                // "COU" : { code: 970, decimals: 2, countries: ['CO'] },
                "CRC": { code: 188, decimals: 2, countries: ['CR'], name: "Costa Rican Colon" },
                // "CUC" : { code: 931, decimals: 2, countries: ['CU'] },
                "CUP": { code: 192, decimals: 2, countries: ['CU'], name: "Cuban Peso" },
                "CVE": { code: 132, decimals: 2, countries: ['CV'], name: "Cape Verde Escudo" },
                "CZK": { code: 203, decimals: 2, countries: ['CZ'], name: "Czech Koruna" },
                "DJF": { code: 262, decimals: 0, countries: ['DJ'], name: "Djibouti Franc" },
                "DKK": { code: 208, decimals: 2, countries: ['DK', 'FO', 'GL'], name: "Danish Krone" },
                "DOP": { code: 214, decimals: 2, countries: ['DO'], name: "Dominican Peso" },
                "DZD": { code: 12, decimals: 2, countries: ['DZ'], name: "Algerian Dinar" },
                "EGP": { code: 818, decimals: 2, countries: ['EG'], name: "Egyptian Pound" },
                "ERN": { code: 232, decimals: 2, countries: ['ER'], name: "Eritrean Nakfa" },
                "ETB": { code: 230, decimals: 2, countries: ['ET'], name: "Ethiopian Birr" },
                "EUR": { code: 978, decimals: 2, countries: ['IE', 'FR', 'ES', 'PT', 'FI', 'BE', 'NL', 'LU', 'DE', 'AT', 'IT', 'MT', 'SK', 'SI', 'GR', 'CY', 'AD', 'MC', 'ME', 'SM', 'VA', 'EE'], name: "Euro" },
                "FJD": { code: 242, decimals: 2, countries: ['FJ'], name: "Fiji Dollar" },
                "FKP": { code: 238, decimals: 2, countries: ['FK'], name: "Falkland Islands Pound" },
                "GBP": { code: 826, decimals: 2, countries: ['GB', 'IM', 'JE', 'GG', 'GS', 'IO'], name: "Pound Sterling" },
                "GEL": { code: 981, decimals: 2, countries: ['GE'], name: "Georgian Lari" },
                "GHS": { code: 936, decimals: 2, countries: ['GH'], name: "Ghanaian Cedi" },
                "GIP": { code: 292, decimals: 2, countries: ['GI'], name: "Gibraltar Pound" },
                "GMD": { code: 270, decimals: 2, countries: ['GM'], name: "Gambian Dalasi" },
                "GNF": { code: 324, decimals: 0, countries: ['GN'], name: "Guinea Franc" },
                "GTQ": { code: 320, decimals: 2, countries: ['GT'], name: "Guatemalan Quetzal" },
                "GYD": { code: 328, decimals: 2, countries: ['GY'], name: "Guyana Dollar" },
                "HKD": { code: 344, decimals: 2, countries: ['HK'], name: "Hong Kong Dollar" },
                "HNL": { code: 340, decimals: 2, countries: ['HN'], name: "Honduran Lempira" },
                "HRK": { code: 191, decimals: 2, countries: ['HR'], name: "Croatian Kuna" },
                "HTG": { code: 332, decimals: 2, countries: ['HT'], name: "Haitian Gourde" },
                "HUF": { code: 348, decimals: 2, countries: ['HU'], name: "Hungarian Forint" },
                "IDR": { code: 360, decimals: 0, countries: ['ID'], name: "Indonesian Rupiah" },
                "ILS": { code: 376, decimals: 2, countries: ['IL'], name: "Israeli New Shekel" },
                "INR": { code: 356, decimals: 2, countries: ['IN'], name: "Indian Rupee" },
                "IQD": { code: 368, decimals: 0, countries: ['IQ'], name: "Iraqi Dinar" },
                "IRR": { code: 364, decimals: 0, countries: ['IR'], name: "Iranian Rial" },
                "ISK": { code: 352, decimals: 0, countries: ['IS'], name: "Iceland Krona" },
                "JMD": { code: 388, decimals: 2, countries: ['JM'], name: "Jamaican Dollar" },
                "JOD": { code: 400, decimals: 3, countries: ['JO'], name: "Jordanian Dina" },
                "JPY": { code: 392, decimals: 0, countries: ['JP'], name: "Japanese Yen" },
                "KES": { code: 404, decimals: 2, countries: ['KE'], name: "Kenyan Shilling"},
                "KGS": { code: 417, decimals: 2, countries: ['KG'], name: "Som" },
                "KHR": { code: 116, decimals: 0, countries: ['KH'], name: "Kampuchean Riel" },
                "KMF": { code: 174, decimals: 0, countries: ['KM'], name: "Comoros Franc" },
                "KPW": { code: 408, decimals: 0, countries: ['KP'], name: "North Korean Won" },
                "KRW": { code: 410, decimals: 0, countries: ['KR'], name: "Korean Won" },
                "KWD": { code: 414, decimals: 3, countries: ['KW'], name: "Kuwaiti Dinar" },
                "KYD": { code: 136, decimals: 2, countries: ['KY'], name: "Cayman Islands Dollar" },
                "KZT": { code: 398, decimals: 2, countries: ['KZ'], name: "Kazakhstan Tenge" },
                "LAK": { code: 418, decimals: 0, countries: ['LA'], name: "Lao Kip" },
                "LBP": { code: 422, decimals: 2, countries: ['LB'], name: "Lebanese Pound" },
                "LKR": { code: 144, decimals: 2, countries: ['LK'], name: "Sri Lanka Rupee" },
                "LRD": { code: 430, decimals: 2, countries: ['LR'], name: "Liberian Dollar" },
                "LSL": { code: 426, decimals: 2, countries: ['LS'], name: "Lesotho Loti" },
                "LTL": { code: 440, decimals: 2, countries: ['LT'], name: "Lithuanian Litas" },
                "LVL": { code: 428, decimals: 2, countries: ['LV'], name: "Latvian Lats" },
                "LYD": { code: 434, decimals: 3, countries: ['LY'], name: "Libyan Dinar" },
                "MAD": { code: 504, decimals: 2, countries: ['MA', 'EH'], name: "Moroccan Dirham" },
                "MDL": { code: 498, decimals: 2, countries: ['MD'], name: "Moldovan Leu" },
                "MGA": { code: 969, decimals: 1, countries: ['MG'], name: "Malagasy ariary" },
                "MKD": { code: 807, decimals: 2, countries: ['MK'], name: "Denar" },
                "MMK": { code: 104, decimals: 0, countries: ['MM'], name: "Myanmar Kyat" },
                "MNT": { code: 496, decimals: 2, countries: ['MN'], name: "Mongolian Tugrik" },
                "MOP": { code: 446, decimals: 1, countries: ['MO'], name: "Macau Pataca" },
                "MRO": { code: 478, decimals: 1, countries: ['MR'], name: "Mauritanian Ouguiya" },
                "MUR": { code: 480, decimals: 2, countries: ['MU'], name: "Mauritius Rupee" },
                "MVR": { code: 462, decimals: 2, countries: ['MV'], name: "Maldive Rufiyaa" },
                "MWK": { code: 454, decimals: 2, countries: ['MW'], name: "Malawi Kwacha" },
                "MXN": { code: 484, decimals: 2, countries: ['MX'], name: "Mexican Nuevo Peso" },
                // "MXV" : { code: 979, decimals: 2, countries: ['MX'] },
                "MYR": { code: 458, decimals: 2, countries: ['MY'], name: "Malaysian Ringgit" },
                "MZN": { code: 943, decimals: 2, countries: ['MZ'], name: "Mozambique Metical" },
                "NAD": { code: 516, decimals: 2, countries: ['NA'], name: "Namibian Dollar" },
                "NGN": { code: 566, decimals: 2, countries: ['NG'], name: "Nigerian Naira" },
                "NIO": { code: 558, decimals: 2, countries: ['NI'], name: "Nicaraguan Cordoba Oro" },
                "NOK": { code: 578, decimals: 2, countries: ['NO', 'BV'], name: "Norwegian Krone" },
                "NPR": { code: 524, decimals: 2, countries: ['NP'], name: "Nepalese Rupee" },
                "NZD": { code: 554, decimals: 2, countries: ['NZ', 'CK', 'NU', 'PN', 'TK'], name: "New Zealand Dollar" },
                "OMR": { code: 512, decimals: 3, countries: ['OM'], name: "Omani Rial" },
                "PAB": { code: 590, decimals: 2, countries: ['PA'], name: "Panamanian Balboa" },
                "PEN": { code: 604, decimals: 2, countries: ['PE'], name: "Peruvian Nuevo Sol" },
                "PGK": { code: 598, decimals: 2, countries: ['PG'], name: "Papua New Guinea Kina" },
                "PHP": { code: 608, decimals: 2, countries: ['PH'], name: "Philippine Peso" },
                "PKR": { code: 586, decimals: 2, countries: ['PK'], name: "Pakistan Rupee" },
                "PLN": { code: 985, decimals: 2, countries: ['PL'], name: "Polish Zloty" },
                "PYG": { code: 600, decimals: 0, countries: ['PY'], name: "Paraguay Guarani" },
                "QAR": { code: 634, decimals: 2, countries: ['QA'], name: "Qatari Rial" },
                "RON": { code: 946, decimals: 2, countries: ['RO'], name: "Romanian New Leu" },
                "RSD": { code: 941, decimals: 2, countries: ['RS'], name: "Dinar" },
                "RUB": { code: 643, decimals: 2, countries: ['RU'], name: "Russian Ruble" },
                "RUR": { code: 810, decimals: 2, countries: ['SU'], name: "Old Russian Ruble" },
                "RWF": { code: 646, decimals: 0, countries: ['RW'], name: "Rwanda Franc" },
                "SAR": { code: 682, decimals: 2, countries: ['SA'], name: "Saudi Riyal" },
                "SBD": { code: 90, decimals: 2, countries: ['SB'], name: "Solomon Islands Dollar" },
                "SCR": { code: 690, decimals: 2, countries: ['SC'], name: "Seychelles Rupee" },
                "SDG": { code: 938, decimals: 2, countries: ['SD'], name: "Sudanese Pound" },
                "SEK": { code: 752, decimals: 2, countries: ['SE'], name: "Swedish Krona" },
                "SGD": { code: 702, decimals: 2, countries: ['SG'], name: "Singapore Dollar" },
                "SHP": { code: 654, decimals: 2, countries: ['SH'], name: "Saint Helena pound" },
                "SLL": { code: 694, decimals: 0, countries: ['SL'], name: "Sierra Leone Leone" },
                "SOS": { code: 706, decimals: 2, countries: ['SO'], name: "Somali Shilling" },
                "SRD": { code: 968, decimals: 2, countries: ['SR'], name: "Surinam Dollar" },
                "STD": { code: 678, decimals: 0, countries: ['ST'], name: "Dobra" },
                "SYP": { code: 760, decimals: 2, countries: ['SY'], name: "Syrian Pound" },
                "SZL": { code: 748, decimals: 2, countries: ['SZ'], name: "Swaziland Lilangeni" },
                "THB": { code: 764, decimals: 2, countries: ['TH'], name: "Thai Baht" },
                "TJS": { code: 972, decimals: 2, countries: ['TJ'], name: "Tajik Somoni" },
                "TMT": { code: 934, decimals: 2, countries: ['TM'], name: "Manat" },
                "TND": { code: 788, decimals: 3, countries: ['TN'], name: "Tunisian Dollar" },
                "TOP": { code: 776, decimals: 2, countries: ['TO'], name: "Tongan Pa'anga" },
                "TRY": { code: 949, decimals: 2, countries: ['TR'], name: "Turkish Lira" },
                "TTD": { code: 780, decimals: 2, countries: ['TT'], name: "Trinidad and Tobago Dollar" },
                "TWD": { code: 901, decimals: 1, countries: ['TW'], name: "Taiwan Dollar" },
                "TZS": { code: 834, decimals: 2, countries: ['TZ'], name: "Tanzanian Shilling" },
                "UAH": { code: 980, decimals: 2, countries: ['UA'], name: "Ukraine Hryvnia" },
                "UGX": { code: 800, decimals: 0, countries: ['UG'], name: "Uganda Shilling" },
                "USD": { code: 840, decimals: 2, countries: ['US', 'AS', 'EC', 'SV', 'GU', 'MH', 'FM', 'MP', 'PW', 'PR', 'TL', 'TC', 'VG', 'VI'], name: "US Dollar" },
                // "USN" : { code: 997, decimals: 2, countries: ['US'] },
                // "USS" : { code: 998, decimals: 2, countries: ['US'] },
                "UYU": { code: 858, decimals: 2, countries: ['UY'], name: "Uruguayan Peso" },
                "UZS": { code: 860, decimals: 2, countries: ['UZ'], name: "Uzbekistan Sum" },
                "VEF": { code: 937, decimals: 2, countries: ['VE'], name: "Venezuelan Bolivar" },
                "VND": { code: 704, decimals: 0, countries: ['VN'], name: "Vietnamese Dong" },
                "VUV": { code: 548, decimals: 0, countries: ['VU'], name: "Vanuatu Vatu" },
                "WST": { code: 882, decimals: 2, countries: ['WS'], name: "Samoan Tala" },
                "XAF": { code: 950, decimals: 0, countries: ['CM', 'CF', 'CG', 'TD', 'GQ', 'GA'], name: "CFA Franc BEAC" },
                //"XAG": { code: 961, decimals: -1, countries: [] }, // Silver
                //"XAU": { code: 959, decimals: -1, countries: [] }, // Gold
                //"XBA": { code: 955, decimals: -1, countries: [] }, // European Composite Unit
                //"XBB": { code: 956, decimals: -1, countries: [] }, // European Monetary Unit
                //"XBC": { code: 957, decimals: -1, countries: [] },
                //"XBD": { code: 958, decimals: -1, countries: [] },
                "XCD": { code: 951, decimals: 2, countries: ['AI', 'AG', 'DM', 'GD', 'MS', 'KN', 'LC', 'VC'], name: "East Caribbean Dollar" },
                // "XDR": { code: 960, decimals: -1, countries: [] }, // Special drawing rights
                // "XFU": { code: -1, decimals: -1, countries: [] }, // UIC Franc (virtual currency unit used by the International Union of Railways)
                "XOF": { code: 952, decimals: 0, countries: ['BJ', 'BF', 'CI', 'GW', 'ML', 'NE', 'SN', 'TG'], name: "CFA Franc BCEAO" },
                // "XPD": { code: 964, decimals: -1, countries: [] }, // Palladium Ounce
                "XPF": { code: 953, decimals: 0, countries: ['PF', 'NC', 'WF'], name: "CFP Franc" },
                // "XPT": { code: 962, decimals: -1, countries: [''] }, // Platinium Ounce
                "XTS": { code: 963, decimals: -1, countries: [], name: "Testing" },
                "XXX": { code: 999, decimals: -1, countries: [], name: "No currency" },
                "YER": { code: 886, decimals: 0, countries: ['YE'], name: "Yemeni Rial" },
                "ZAR": { code: 710, decimals: 2, countries: ['ZA'], name: "South African Rand" },
                "ZMK": { code: 894, decimals: 0, countries: ['ZM'], name: "Zambian kwacha" },
                "ZWL": { code: 932, decimals: 2, countries: ['ZW'], name: "Zimbabwean dollar" },

                // Non-iso
                "BTC" : {code: undefined, decimals: 8, countries: [], name: "Bitcoin" }
            };

            return {
                /**
                 * Export list of currencies.
                 */
                currencies: Object.keys(currencies),

                /**
                 * Access to a single currency data.
                 *
                 * @param code the currency code to access
                 * @returns {Object} the currency data
                 */
                getCurrency: function (code) {
                    return currencies[code];
                }
            }

        })

        .filter('money', ['moneyService', function (moneyService) {
            return function (amount, currencyCode) {
                if (typeof amount === 'undefined' || amount === null) {
                    // Garbage in, garbage out
                    return;
                }
                var currency = moneyService.getCurrency(currencyCode),
                    decimals = currency ? currency.decimals : 2;
                return amount.toFixed(decimals);
            }
        }])

        .directive('currencyPicker', ['moneyService', function (moneyService) {
            /**
             * A directive that act as a currency select list picker.
             *
             * Example usage :
             *
             * <currency-picker model="someScope.someVariable"
             *                  disabled="!someCondition()"
             *                  defaultValue="'EUR'" />
             */
            return {
                scope: {
                    model: '=',
                    disabled: '=',
                    defaultValue: '='
                },
                restrict: 'E',
                template: '<select ng-model=model ng-disabled=disabled class="input-xlarge">' +
                    '<option ng-repeat="currency in currencies"' +
                    '        value="{{ currency }}"' +
                    '        ng-selected="isSelected(currency)">' +
                    '{{currency}} ({{getName(currency)}})' +
                    '</option>' +
                    '</select>',
                controller: function ($scope) {
                    $scope.currencies = moneyService.currencies;
                    $scope.getName = function (currency) {
                        return moneyService.getCurrency(currency).name;
                    }
                    $scope.isSelected = function (currency) {
                        if ($scope.value === "") {
                            return currency === $scope.defaultValue;
                        }
                        else {
                            return currency === $scope.model;
                        }
                    }
                }
            };
        }])

        .directive('currencyListPicker', ['moneyService', function (moneyService) {
            return {
                scope: {
                    model: '=',
                    disabled: '='
                },
                restrict: 'E',
                template: '<div><ul class="pickerElements"><li ng-repeat="currency in model">' +
                    '<button class="btn btn-mini" ng-click="remove(currency)">{{currency}} &times;</span></button>' +
                    '</li></ul></div><div class="clearfix"></div>' +
                    '<currency-picker model="new"></currency-picker>' +
                    '<input type="submit" class="btn" ng-disabled="!new" value="{{\'global.actions.add\' | translate}}" ng-click="add()">',
                controller: function ($scope) {
                    $scope.add = function() {
                        if ($scope.model.indexOf($scope.new) < 0) {
                            $scope.model.push($scope.new);
                        }
                        $scope.new = "";
                    }
                    $scope.remove = function(currency) {
                        $scope.model.splice($scope.model.indexOf(currency), 1);
                    }
                }
            };
        }])

        .directive('moneyAmount', ['moneyService', function (moneyService) {
            return {
                require: 'ngModel',
                restrict: 'E',
                template: '<div class="input-append">' +
                    '<input ng-model="amount" class="span2" placeholder="{{placeholder}}" type="text">' +
                    '<span class="add-on">{{currencyCode}}</span>' +
                    '</div>',
                controller: function ($scope) {
                    $scope.format = function () {
                        if (typeof $scope.amount === 'undefined') {
                            return;
                        }
                        $scope.amount = $scope.amount.replace(',', '.');
                        if ($scope.amount[$scope.amount.length - 1] == '.') {
                            $scope.amount = $scope.amount + "0";
                        }
                        $scope.actualAmount = parseFloat($scope.amount);
                        var decimals = $scope.currency ? $scope.currency.decimals : 2;
                        $scope.amount = $scope.actualAmount.toFixed(decimals);
                        if (isNaN($scope.amount)) {
                            $scope.amount = "";
                        }
                        $scope.amount
                    }
                },
                link: function ($scope, element, attrs, controller) {
                    $(element).find("input").on("blur", function () {
                        $scope.$apply(function () {
                            $scope.format();
                        });
                    });
                    controller.$render = function () {
                        $scope.placeholder = attrs.placeholder;
                        $scope.amount = "" + controller.$viewValue;
                        $scope.format();
                    };
                    $scope.$watch(function() {return attrs.currency }, function(newValue){
                        $scope.currencyCode = newValue;
                        $scope.currency = moneyService.getCurrency($scope.currencyCode);
                    });
                    $scope.$watch('amount', function (value) {
                        if (!isNaN(parseFloat(value))) {
                            controller.$setViewValue(parseFloat(value));
                        }
                        else if (value === "") {
                            // If the money field has been cleared, we clear the value
                            controller.$setViewValue(undefined);
                        }
                    });
                }
            }
        }])
})();


