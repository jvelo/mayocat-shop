(function () {
    'use strict'

    angular.module('money', [])

        .factory('moneyService', function () {

            var currencies = {
                // Adapted from joda money MoneyData.csv
                // See https://github.com/JodaOrg/joda-money/tree/master/src/main/resources/org/joda/money
                "AED": { code: 784, decimals: 2, countries: ['AE'] },
                "AFN": { code: 971, decimals: 2, countries: ['AF'] },
                "ALL": { code: 8, decimals: 2, countries: ['AL'] },
                "AMD": { code: 51, decimals: 0, countries: ['AM'] },
                "ANG": { code: 532, decimals: 2, countries: ['AN'] },
                "AOA": { code: 973, decimals: 1, countries: ['AO'] },
                "ARS": { code: 32, decimals: 2, countries: ['AR'] },
                "AUD": { code: 36, decimals: 2, countries: ['AU', 'CX', 'CC', 'HM', 'KI', 'NR', 'NF', 'TV'] },
                "AWG": { code: 533, decimals: 2, countries: ['AW'] },
                "AZN": { code: 944, decimals: 2, countries: ['AZ'] },
                "BAM": { code: 977, decimals: 2, countries: ['BA'] },
                "BBD": { code: 52, decimals: 2, countries: ['BB'] },
                "BDT": { code: 50, decimals: 2, countries: ['BD'] },
                "BGN": { code: 975, decimals: 2, countries: ['BG'] },
                "BHD": { code: 48, decimals: 3, countries: ['BH'] },
                "BIF": { code: 108, decimals: 0, countries: ['BI'] },
                "BMD": { code: 60, decimals: 2, countries: ['BM'] },
                "BND": { code: 96, decimals: 2, countries: ['BN'] },
                "BOB": { code: 68, decimals: 2, countries: ['BO'] },
                // "BOV" : { code: 984, decimals: 2, countries: ['BO'] },
                "BRL": { code: 986, decimals: 2, countries: ['BR'] },
                "BSD": { code: 44, decimals: 2, countries: ['BS'] },
                "BTN": { code: 64, decimals: 2, countries: ['BT'] },
                "BWP": { code: 72, decimals: 2, countries: ['BW'] },
                "BYR": { code: 974, decimals: 0, countries: ['BY'] },
                "BZD": { code: 84, decimals: 2, countries: ['BZ'] },
                "CAD": { code: 124, decimals: 2, countries: ['CA'] },
                "CDF": { code: 976, decimals: 2, countries: ['CD'] },
                // "CHE" : { code: 947, decimals: 2, countries: ['CH'] },
                "CHF": { code: 756, decimals: 2, countries: ['CH', 'LI'] },
                // "CHW" : { code: 948, decimals: 2, countries: ['CH'] },
                // "CLF" : { code: 990, decimals: 0, countries: ['CL'] },
                "CLP": { code: 152, decimals: 0, countries: ['CL'] },
                "CNY": { code: 156, decimals: 1, countries: ['CN'] },
                "COP": { code: 170, decimals: 0, countries: ['CO'] },
                // "COU" : { code: 970, decimals: 2, countries: ['CO'] },
                "CRC": { code: 188, decimals: 2, countries: ['CR'] },
                // "CUC" : { code: 931, decimals: 2, countries: ['CU'] },
                "CUP": { code: 192, decimals: 2, countries: ['CU'] },
                "CVE": { code: 132, decimals: 2, countries: ['CV'] },
                "CZK": { code: 203, decimals: 2, countries: ['CZ'] },
                "DJF": { code: 262, decimals: 0, countries: ['DJ'] },
                "DKK": { code: 208, decimals: 2, countries: ['DK', 'FO', 'GL'] },
                "DOP": { code: 214, decimals: 2, countries: ['DO'] },
                "DZD": { code: 12, decimals: 2, countries: ['DZ'] },
                "EGP": { code: 818, decimals: 2, countries: ['EG'] },
                "ERN": { code: 232, decimals: 2, countries: ['ER'] },
                "ETB": { code: 230, decimals: 2, countries: ['ET'] },
                "EUR": { code: 978, decimals: 2, countries: ['IE', 'FR', 'ES', 'PT', 'FI', 'BE', 'NL', 'LU', 'DE', 'AT', 'IT', 'MT', 'SK', 'SI', 'GR', 'CY', 'AD', 'MC', 'ME', 'SM', 'VA', 'EE'] },
                "FJD": { code: 242, decimals: 2, countries: ['FJ'] },
                "FKP": { code: 238, decimals: 2, countries: ['FK'] },
                "GBP": { code: 826, decimals: 2, countries: ['GB', 'IM', 'JE', 'GG', 'GS', 'IO'] },
                "GEL": { code: 981, decimals: 2, countries: ['GE'] },
                "GHS": { code: 936, decimals: 2, countries: ['GH'] },
                "GIP": { code: 292, decimals: 2, countries: ['GI'] },
                "GMD": { code: 270, decimals: 2, countries: ['GM'] },
                "GNF": { code: 324, decimals: 0, countries: ['GN'] },
                "GTQ": { code: 320, decimals: 2, countries: ['GT'] },
                "GYD": { code: 328, decimals: 2, countries: ['GY'] },
                "HKD": { code: 344, decimals: 2, countries: ['HK'] },
                "HNL": { code: 340, decimals: 2, countries: ['HN'] },
                "HRK": { code: 191, decimals: 2, countries: ['HR'] },
                "HTG": { code: 332, decimals: 2, countries: ['HT'] },
                "HUF": { code: 348, decimals: 2, countries: ['HU'] },
                "IDR": { code: 360, decimals: 0, countries: ['ID'] },
                "ILS": { code: 376, decimals: 2, countries: ['IL'] },
                "INR": { code: 356, decimals: 2, countries: ['IN'] },
                "IQD": { code: 368, decimals: 0, countries: ['IQ'] },
                "IRR": { code: 364, decimals: 0, countries: ['IR'] },
                "ISK": { code: 352, decimals: 0, countries: ['IS'] },
                "JMD": { code: 388, decimals: 2, countries: ['JM'] },
                "JOD": { code: 400, decimals: 3, countries: ['JO'] },
                "JPY": { code: 392, decimals: 0, countries: ['JP'] },
                "KES": { code: 404, decimals: 2, countries: ['KE'] },
                "KGS": { code: 417, decimals: 2, countries: ['KG'] },
                "KHR": { code: 116, decimals: 0, countries: ['KH'] },
                "KMF": { code: 174, decimals: 0, countries: ['KM'] },
                "KPW": { code: 408, decimals: 0, countries: ['KP'] },
                "KRW": { code: 410, decimals: 0, countries: ['KR'] },
                "KWD": { code: 414, decimals: 3, countries: ['KW'] },
                "KYD": { code: 136, decimals: 2, countries: ['KY'] },
                "KZT": { code: 398, decimals: 2, countries: ['KZ'] },
                "LAK": { code: 418, decimals: 0, countries: ['LA'] },
                "LBP": { code: 422, decimals: 2, countries: ['LB'] },
                "LKR": { code: 144, decimals: 2, countries: ['LK'] },
                "LRD": { code: 430, decimals: 2, countries: ['LR'] },
                "LSL": { code: 426, decimals: 2, countries: ['LS'] },
                "LTL": { code: 440, decimals: 2, countries: ['LT'] },
                "LVL": { code: 428, decimals: 2, countries: ['LV'] },
                "LYD": { code: 434, decimals: 3, countries: ['LY'] },
                "MAD": { code: 504, decimals: 2, countries: ['MA', 'EH'] },
                "MDL": { code: 498, decimals: 2, countries: ['MD'] },
                "MGA": { code: 969, decimals: 1, countries: ['MG'] },
                "MKD": { code: 807, decimals: 2, countries: ['MK'] },
                "MMK": { code: 104, decimals: 0, countries: ['MM'] },
                "MNT": { code: 496, decimals: 2, countries: ['MN'] },
                "MOP": { code: 446, decimals: 1, countries: ['MO'] },
                "MRO": { code: 478, decimals: 1, countries: ['MR'] },
                "MUR": { code: 480, decimals: 2, countries: ['MU'] },
                "MVR": { code: 462, decimals: 2, countries: ['MV'] },
                "MWK": { code: 454, decimals: 2, countries: ['MW'] },
                "MXN": { code: 484, decimals: 2, countries: ['MX'] },
                // "MXV" : { code: 979, decimals: 2, countries: ['MX'] },
                "MYR": { code: 458, decimals: 2, countries: ['MY'] },
                "MZN": { code: 943, decimals: 2, countries: ['MZ'] },
                "NAD": { code: 516, decimals: 2, countries: ['NA'] },
                "NGN": { code: 566, decimals: 2, countries: ['NG'] },
                "NIO": { code: 558, decimals: 2, countries: ['NI'] },
                "NOK": { code: 578, decimals: 2, countries: ['NOBV'] },
                "NPR": { code: 524, decimals: 2, countries: ['NP'] },
                "NZD": { code: 554, decimals: 2, countries: ['NZ', 'CK', 'NU', 'PN', 'TK'] },
                "OMR": { code: 512, decimals: 3, countries: ['OM'] },
                "PAB": { code: 590, decimals: 2, countries: ['PA'] },
                "PEN": { code: 604, decimals: 2, countries: ['PE'] },
                "PGK": { code: 598, decimals: 2, countries: ['PG'] },
                "PHP": { code: 608, decimals: 2, countries: ['PH'] },
                "PKR": { code: 586, decimals: 2, countries: ['PK'] },
                "PLN": { code: 985, decimals: 2, countries: ['PL'] },
                "PYG": { code: 600, decimals: 0, countries: ['PY'] },
                "QAR": { code: 634, decimals: 2, countries: ['QA'] },
                "RON": { code: 946, decimals: 2, countries: ['RO'] },
                "RSD": { code: 941, decimals: 2, countries: ['RS'] },
                "RUB": { code: 643, decimals: 2, countries: ['RU'] },
                "RUR": { code: 810, decimals: 2, countries: ['SU'] },
                "RWF": { code: 646, decimals: 0, countries: ['RW'] },
                "SAR": { code: 682, decimals: 2, countries: ['SA'] },
                "SBD": { code: 90, decimals: 2, countries: ['SB'] },
                "SCR": { code: 690, decimals: 2, countries: ['SC'] },
                "SDG": { code: 938, decimals: 2, countries: ['SD'] },
                "SEK": { code: 752, decimals: 2, countries: ['SE'] },
                "SGD": { code: 702, decimals: 2, countries: ['SG'] },
                "SHP": { code: 654, decimals: 2, countries: ['SH'] },
                "SLL": { code: 694, decimals: 0, countries: ['SL'] },
                "SOS": { code: 706, decimals: 2, countries: ['SO'] },
                "SRD": { code: 968, decimals: 2, countries: ['SR'] },
                "STD": { code: 678, decimals: 0, countries: ['ST'] },
                "SYP": { code: 760, decimals: 2, countries: ['SY'] },
                "SZL": { code: 748, decimals: 2, countries: ['SZ'] },
                "THB": { code: 764, decimals: 2, countries: ['TH'] },
                "TJS": { code: 972, decimals: 2, countries: ['TJ'] },
                "TMT": { code: 934, decimals: 2, countries: ['TM'] },
                "TND": { code: 788, decimals: 3, countries: ['TN'] },
                "TOP": { code: 776, decimals: 2, countries: ['TO'] },
                "TRY": { code: 949, decimals: 2, countries: ['TR'] },
                "TTD": { code: 780, decimals: 2, countries: ['TT'] },
                "TWD": { code: 901, decimals: 1, countries: ['TW'] },
                "TZS": { code: 834, decimals: 2, countries: ['TZ'] },
                "UAH": { code: 980, decimals: 2, countries: ['UA'] },
                "UGX": { code: 800, decimals: 0, countries: ['UG'] },
                "USD": { code: 840, decimals: 2, countries: ['US', 'AS', 'EC', 'SV', 'GU', 'MH', 'FM', 'MP', 'PW', 'PR', 'TL', 'TC', 'VG', 'VI'] },
                // "USN" : { code: 997, decimals: 2, countries: ['US'] },
                // "USS" : { code: 998, decimals: 2, countries: ['US'] },
                "UYU": { code: 858, decimals: 2, countries: ['UY'] },
                "UZS": { code: 860, decimals: 2, countries: ['UZ'] },
                "VEF": { code: 937, decimals: 2, countries: ['VE'] },
                "VND": { code: 704, decimals: 0, countries: ['VN'] },
                "VUV": { code: 548, decimals: 0, countries: ['VU'] },
                "WST": { code: 882, decimals: 2, countries: ['WS'] },
                "XAF": { code: 950, decimals: 0, countries: ['CM', 'CF', 'CG', 'TD', 'GQ', 'GA'] },
                "XAG": { code: 961, decimals: -1, countries: [] },
                "XAU": { code: 959, decimals: -1, countries: [] },
                "XBA": { code: 955, decimals: -1, countries: [] },
                "XBB": { code: 956, decimals: -1, countries: [] },
                "XBC": { code: 957, decimals: -1, countries: [] },
                "XBD": { code: 958, decimals: -1, countries: [] },
                "XCD": { code: 951, decimals: 2, countries: ['AI', 'AG', 'DM', 'GD', 'MS', 'KN', 'LC', 'VC'] },
                "XDR": { code: 960, decimals: -1, countries: [] },
                "XFU": { code: -1, decimals: -1, countries: [] },
                "XOF": { code: 952, decimals: 0, countries: ['BJ', 'BF', 'CI', 'GW', 'ML', 'NE', 'SN', 'TG'] },
                "XPD": { code: 964, decimals: -1, countries: [] },
                "XPF": { code: 953, decimals: 0, countries: ['PF', 'NC', 'WF'] },
                "XPT": { code: 962, decimals: -1, countries: [''] },
                "XTS": { code: 963, decimals: -1, countries: [] },
                "XXX": { code: 999, decimals: -1, countries: [] },
                "YER": { code: 886, decimals: 0, countries: ['YE'] },
                "ZAR": { code: 710, decimals: 2, countries: ['ZA'] },
                "ZMK": { code: 894, decimals: 0, countries: ['ZM'] },
                "ZWL": { code: 932, decimals: 2, countries: ['ZW'] }
            };

            return {
                getCurrency: function (code) {
                    return currencies[code];
                }
            }

        })
        .directive('moneyAmount', ['moneyService', function (moneyService) {
            return {
                require: 'ngModel',
                restrict: 'E',
                template: '<div class="input-append">' +
                    '<input ng-model="amount" class="span2" type="text">' +
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
                        $scope.amount = "" + controller.$viewValue;
                        $scope.format();
                    };
                    $scope.currencyCode = attrs.currency;
                    $scope.currency = moneyService.getCurrency($scope.currencyCode);
                    $scope.$watch('amount', function (value) {
                        controller.$setViewValue(parseFloat(value));
                    });
                }
            }
        }]);
})();


