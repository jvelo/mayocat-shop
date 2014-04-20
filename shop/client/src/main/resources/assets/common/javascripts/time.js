/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function () {
    'use strict';

    angular.module('mayocat.time', [])

    .factory('timeService', ['$rootScope', function ($rootScope) {

        var defaultPrintFormat = "YYYY-MM-DD HH:mm";

        // List of time zones and standard offset generated from joda DateTimeZone list
        var tzData = {
            Africa: {
                "Africa/Abidjan":                  { stdOffset: "+00:00", city: "Abidjan" },
                "Africa/Accra":                    { stdOffset: "+00:00", city: "Accra" },
                "Africa/Bamako":                   { stdOffset: "+00:00", city: "Bamako" },
                "Africa/Banjul":                   { stdOffset: "+00:00", city: "Banjul" },
                "Africa/Bissau":                   { stdOffset: "+00:00", city: "Bissau" },
                "Africa/Casablanca":               { stdOffset: "+00:00", city: "Casablanca" },
                "Africa/Conakry":                  { stdOffset: "+00:00", city: "Conakry" },
                "Africa/Dakar":                    { stdOffset: "+00:00", city: "Dakar" },
                "Africa/El_Aaiun":                 { stdOffset: "+00:00", city: "El Aaiun" },
                "Africa/Freetown":                 { stdOffset: "+00:00", city: "Freetown" },
                "Africa/Lome":                     { stdOffset: "+00:00", city: "Lome" },
                "Africa/Monrovia":                 { stdOffset: "+00:00", city: "Monrovia" },
                "Africa/Nouakchott":               { stdOffset: "+00:00", city: "Nouakchott" },
                "Africa/Ouagadougou":              { stdOffset: "+00:00", city: "Ouagadougou" },
                "Africa/Sao_Tome":                 { stdOffset: "+00:00", city: "Sao Tome" },
                "Africa/Algiers":                  { stdOffset: "+01:00", city: "Algiers" },
                "Africa/Bangui":                   { stdOffset: "+01:00", city: "Bangui" },
                "Africa/Brazzaville":              { stdOffset: "+01:00", city: "Brazzaville" },
                "Africa/Ceuta":                    { stdOffset: "+01:00", city: "Ceuta" },
                "Africa/Douala":                   { stdOffset: "+01:00", city: "Douala" },
                "Africa/Kinshasa":                 { stdOffset: "+01:00", city: "Kinshasa" },
                "Africa/Lagos":                    { stdOffset: "+01:00", city: "Lagos" },
                "Africa/Libreville":               { stdOffset: "+01:00", city: "Libreville" },
                "Africa/Luanda":                   { stdOffset: "+01:00", city: "Luanda" },
                "Africa/Malabo":                   { stdOffset: "+01:00", city: "Malabo" },
                "Africa/Ndjamena":                 { stdOffset: "+01:00", city: "Ndjamena" },
                "Africa/Niamey":                   { stdOffset: "+01:00", city: "Niamey" },
                "Africa/Porto-Novo":               { stdOffset: "+01:00", city: "Porto-Novo" },
                "Africa/Tunis":                    { stdOffset: "+01:00", city: "Tunis" },
                "Africa/Windhoek":                 { stdOffset: "+01:00", city: "Windhoek" },
                "Africa/Blantyre":                 { stdOffset: "+02:00", city: "Blantyre" },
                "Africa/Bujumbura":                { stdOffset: "+02:00", city: "Bujumbura" },
                "Africa/Cairo":                    { stdOffset: "+02:00", city: "Cairo" },
                "Africa/Gaborone":                 { stdOffset: "+02:00", city: "Gaborone" },
                "Africa/Harare":                   { stdOffset: "+02:00", city: "Harare" },
                "Africa/Johannesburg":             { stdOffset: "+02:00", city: "Johannesburg" },
                "Africa/Kigali":                   { stdOffset: "+02:00", city: "Kigali" },
                "Africa/Lubumbashi":               { stdOffset: "+02:00", city: "Lubumbashi" },
                "Africa/Lusaka":                   { stdOffset: "+02:00", city: "Lusaka" },
                "Africa/Maputo":                   { stdOffset: "+02:00", city: "Maputo" },
                "Africa/Maseru":                   { stdOffset: "+02:00", city: "Maseru" },
                "Africa/Mbabane":                  { stdOffset: "+02:00", city: "Mbabane" },
                "Africa/Tripoli":                  { stdOffset: "+02:00", city: "Tripoli" },
                "Africa/Addis_Ababa":              { stdOffset: "+03:00", city: "Addis Ababa" },
                "Africa/Asmara":                   { stdOffset: "+03:00", city: "Asmara" },
                "Africa/Dar_es_Salaam":            { stdOffset: "+03:00", city: "Dar es Salaam" },
                "Africa/Djibouti":                 { stdOffset: "+03:00", city: "Djibouti" },
                "Africa/Kampala":                  { stdOffset: "+03:00", city: "Kampala" },
                "Africa/Khartoum":                 { stdOffset: "+03:00", city: "Khartoum" },
                "Africa/Mogadishu":                { stdOffset: "+03:00", city: "Mogadishu" },
                "Africa/Nairobi":                  { stdOffset: "+03:00", city: "Nairobi" }
            },
            America: {
                "America/Adak":                    { stdOffset: "-10:00", city: "Adak" },
                "America/Anchorage":               { stdOffset: "-09:00", city: "Anchorage" },
                "America/Juneau":                  { stdOffset: "-09:00", city: "Juneau" },
                "America/Nome":                    { stdOffset: "-09:00", city: "Nome" },
                "America/Sitka":                   { stdOffset: "-09:00", city: "Sitka" },
                "America/Yakutat":                 { stdOffset: "-09:00", city: "Yakutat" },
                "America/Dawson":                  { stdOffset: "-08:00", city: "Dawson" },
                "America/Los_Angeles":             { stdOffset: "-08:00", city: "Los Angeles" },
                "America/Metlakatla":              { stdOffset: "-08:00", city: "Metlakatla" },
                "America/Santa_Isabel":            { stdOffset: "-08:00", city: "Santa Isabel" },
                "America/Tijuana":                 { stdOffset: "-08:00", city: "Tijuana" },
                "America/Vancouver":               { stdOffset: "-08:00", city: "Vancouver" },
                "America/Whitehorse":              { stdOffset: "-08:00", city: "Whitehorse" },
                "America/Boise":                   { stdOffset: "-07:00", city: "Boise" },
                "America/Cambridge_Bay":           { stdOffset: "-07:00", city: "Cambridge Bay" },
                "America/Chihuahua":               { stdOffset: "-07:00", city: "Chihuahua" },
                "America/Dawson_Creek":            { stdOffset: "-07:00", city: "Dawson Creek" },
                "America/Denver":                  { stdOffset: "-07:00", city: "Denver" },
                "America/Edmonton":                { stdOffset: "-07:00", city: "Edmonton" },
                "America/Hermosillo":              { stdOffset: "-07:00", city: "Hermosillo" },
                "America/Inuvik":                  { stdOffset: "-07:00", city: "Inuvik" },
                "America/Mazatlan":                { stdOffset: "-07:00", city: "Mazatlan" },
                "America/Ojinaga":                 { stdOffset: "-07:00", city: "Ojinaga" },
                "America/Phoenix":                 { stdOffset: "-07:00", city: "Phoenix" },
                "America/Yellowknife":             { stdOffset: "-07:00", city: "Yellowknife" },
                "America/Bahia_Banderas":          { stdOffset: "-06:00", city: "Bahia Banderas" },
                "America/Belize":                  { stdOffset: "-06:00", city: "Belize" },
                "America/Cancun":                  { stdOffset: "-06:00", city: "Cancun" },
                "America/Chicago":                 { stdOffset: "-06:00", city: "Chicago" },
                "America/Costa_Rica":              { stdOffset: "-06:00", city: "Costa Rica" },
                "America/El_Salvador":             { stdOffset: "-06:00", city: "El Salvador" },
                "America/Guatemala":               { stdOffset: "-06:00", city: "Guatemala" },
                "America/Indiana/Knox":            { stdOffset: "-06:00", city: "Indiana - Knox" },
                "America/Indiana/Tell_City":       { stdOffset: "-06:00", city: "Indiana - Tell City" },
                "America/Managua":                 { stdOffset: "-06:00", city: "Managua" },
                "America/Matamoros":               { stdOffset: "-06:00", city: "Matamoros" },
                "America/Menominee":               { stdOffset: "-06:00", city: "Menominee" },
                "America/Merida":                  { stdOffset: "-06:00", city: "Merida" },
                "America/Mexico_City":             { stdOffset: "-06:00", city: "Mexico City" },
                "America/Monterrey":               { stdOffset: "-06:00", city: "Monterrey" },
                "America/North_Dakota/Beulah":     { stdOffset: "-06:00", city: "North Dakota - Beulah" },
                "America/North_Dakota/Center":     { stdOffset: "-06:00", city: "North Dakota - Center" },
                "America/North_Dakota/New_Salem":  { stdOffset: "-06:00", city: "North Dakota - New Salem" },
                "America/Rainy_River":             { stdOffset: "-06:00", city: "Rainy River" },
                "America/Rankin_Inlet":            { stdOffset: "-06:00", city: "Rankin Inlet" },
                "America/Regina":                  { stdOffset: "-06:00", city: "Regina" },
                "America/Swift_Current":           { stdOffset: "-06:00", city: "Swift Current" },
                "America/Tegucigalpa":             { stdOffset: "-06:00", city: "Tegucigalpa" },
                "America/Winnipeg":                { stdOffset: "-06:00", city: "Winnipeg" },
                "America/Atikokan":                { stdOffset: "-05:00", city: "Atikokan" },
                "America/Bogota":                  { stdOffset: "-05:00", city: "Bogota" },
                "America/Cayman":                  { stdOffset: "-05:00", city: "Cayman" },
                "America/Detroit":                 { stdOffset: "-05:00", city: "Detroit" },
                "America/Grand_Turk":              { stdOffset: "-05:00", city: "Grand Turk" },
                "America/Guayaquil":               { stdOffset: "-05:00", city: "Guayaquil" },
                "America/Havana":                  { stdOffset: "-05:00", city: "Havana" },
                "America/Indiana/Indianapolis":    { stdOffset: "-05:00", city: "Indiana - Indianapolis" },
                "America/Indiana/Marengo":         { stdOffset: "-05:00", city: "Indiana - Marengo" },
                "America/Indiana/Petersburg":      { stdOffset: "-05:00", city: "Indiana - Petersburg" },
                "America/Indiana/Vevay":           { stdOffset: "-05:00", city: "Indiana - Vevay" },
                "America/Indiana/Vincennes":       { stdOffset: "-05:00", city: "Indiana - Vincennes" },
                "America/Indiana/Winamac":         { stdOffset: "-05:00", city: "Indiana - Winamac" },
                "America/Iqaluit":                 { stdOffset: "-05:00", city: "Iqaluit" },
                "America/Jamaica":                 { stdOffset: "-05:00", city: "Jamaica" },
                "America/Kentucky/Louisville":     { stdOffset: "-05:00", city: "Kentucky - Louisville" },
                "America/Kentucky/Monticello":     { stdOffset: "-05:00", city: "Kentucky - Monticello" },
                "America/Lima":                    { stdOffset: "-05:00", city: "Lima" },
                "America/Montreal":                { stdOffset: "-05:00", city: "Montreal" },
                "America/Nassau":                  { stdOffset: "-05:00", city: "Nassau" },
                "America/New_York":                { stdOffset: "-05:00", city: "New York" },
                "America/Argentina/San_Luis":      { stdOffset: "-03:00", city: "Argentina - San Luis" },
                "America/Aruba":                   { stdOffset: "-04:00", city: "Aruba" },
                "America/Asuncion":                { stdOffset: "-04:00", city: "Asuncion" },
                "America/Barbados":                { stdOffset: "-04:00", city: "Barbados" },
                "America/Blanc-Sablon":            { stdOffset: "-04:00", city: "Blanc-Sablon" },
                "America/Boa_Vista":               { stdOffset: "-04:00", city: "Boa Vista" },
                "America/Campo_Grande":            { stdOffset: "-04:00", city: "Campo Grande" },
                "America/Cuiaba":                  { stdOffset: "-04:00", city: "Cuiaba" },
                "America/Curacao":                 { stdOffset: "-04:00", city: "Curacao" },
                "America/Dominica":                { stdOffset: "-04:00", city: "Dominica" },
                "America/Eirunepe":                { stdOffset: "-04:00", city: "Eirunepe" },
                "America/Glace_Bay":               { stdOffset: "-04:00", city: "Glace Bay" },
                "America/Goose_Bay":               { stdOffset: "-04:00", city: "Goose Bay" },
                "America/Grenada":                 { stdOffset: "-04:00", city: "Grenada" },
                "America/Guadeloupe":              { stdOffset: "-04:00", city: "Guadeloupe" },
                "America/Guyana":                  { stdOffset: "-04:00", city: "Guyana" },
                "America/Halifax":                 { stdOffset: "-04:00", city: "Halifax" },
                "America/La_Paz":                  { stdOffset: "-04:00", city: "La Paz" },
                "America/Manaus":                  { stdOffset: "-04:00", city: "Manaus" },
                "America/Martinique":              { stdOffset: "-04:00", city: "Martinique" },
                "America/Moncton":                 { stdOffset: "-04:00", city: "Moncton" },
                "America/Montserrat":              { stdOffset: "-04:00", city: "Montserrat" },
                "America/Port_of_Spain":           { stdOffset: "-04:00", city: "Port of Spain" },
                "America/Porto_Velho":             { stdOffset: "-04:00", city: "Porto Velho" },
                "America/Puerto_Rico":             { stdOffset: "-04:00", city: "Puerto Rico" },
                "America/Rio_Branco":              { stdOffset: "-04:00", city: "Rio Branco" },
                "America/Santiago":                { stdOffset: "-04:00", city: "Santiago" },
                "America/Santo_Domingo":           { stdOffset: "-04:00", city: "Santo Domingo" },
                "America/St_Kitts":                { stdOffset: "-04:00", city: "St Kitts" },
                "America/St_Lucia":                { stdOffset: "-04:00", city: "St Lucia" },
                "America/St_Thomas":               { stdOffset: "-04:00", city: "St Thomas" },
                "America/St_Vincent":              { stdOffset: "-04:00", city: "St Vincent" },
                "America/Thule":                   { stdOffset: "-04:00", city: "Thule" },
                "America/Tortola":                 { stdOffset: "-04:00", city: "Tortola" },
                "America/St_Johns":                { stdOffset: "-03:30", city: "St Johns" },
                "America/Araguaina":               { stdOffset: "-03:00", city: "Araguaina" },
                "America/Argentina/Buenos_Aires":  { stdOffset: "-03:00", city: "Argentina - Buenos Aires" },
                "America/Argentina/Catamarca":     { stdOffset: "-03:00", city: "Argentina - Catamarca" },
                "America/Argentina/Cordoba":       { stdOffset: "-03:00", city: "Argentina - Cordoba" },
                "America/Argentina/Jujuy":         { stdOffset: "-03:00", city: "Argentina - Jujuy" },
                "America/Argentina/La_Rioja":      { stdOffset: "-03:00", city: "Argentina - La Rioja" },
                "America/Argentina/Mendoza":       { stdOffset: "-03:00", city: "Argentina - Mendoza" },
                "America/Argentina/Rio_Gallegos":  { stdOffset: "-03:00", city: "Argentina - Rio Gallegos" },
                "America/Argentina/Salta":         { stdOffset: "-03:00", city: "Argentina - Salta" },
                "America/Argentina/San_Juan":      { stdOffset: "-03:00", city: "Argentina - San Juan" },
                "America/Argentina/Tucuman":       { stdOffset: "-03:00", city: "Argentina - Tucuman" },
                "America/Argentina/Ushuaia":       { stdOffset: "-03:00", city: "Argentina - Ushuaia" },
                "America/Bahia":                   { stdOffset: "-03:00", city: "Bahia" },
                "America/Belem":                   { stdOffset: "-03:00", city: "Belem" },
                "America/Cayenne":                 { stdOffset: "-03:00", city: "Cayenne" },
                "America/Fortaleza":               { stdOffset: "-03:00", city: "Fortaleza" },
                "America/Godthab":                 { stdOffset: "-03:00", city: "Godthab" },
                "America/Maceio":                  { stdOffset: "-03:00", city: "Maceio" },
                "America/Miquelon":                { stdOffset: "-03:00", city: "Miquelon" },
                "America/Montevideo":              { stdOffset: "-03:00", city: "Montevideo" },
                "America/Paramaribo":              { stdOffset: "-03:00", city: "Paramaribo" },
                "America/Recife":                  { stdOffset: "-03:00", city: "Recife" },
                "America/Santarem":                { stdOffset: "-03:00", city: "Santarem" },
                "America/Sao_Paulo":               { stdOffset: "-03:00", city: "Sao Paulo" },
                "America/Noronha":                 { stdOffset: "-02:00", city: "Noronha" },
                "America/Scoresbysund":            { stdOffset: "-01:00", city: "Scoresbysund" },
                "America/Danmarkshavn":            { stdOffset: "+00:00", city: "Danmarkshavn" }
            },
            Antarctica: {
                "Antarctica/Palmer":               { stdOffset: "-04:00", city: "Palmer" },
                "Antarctica/Rothera":              { stdOffset: "-03:00", city: "Rothera" },
                "Antarctica/Syowa":                { stdOffset: "+03:00", city: "Syowa" },
                "Antarctica/Mawson":               { stdOffset: "+05:00", city: "Mawson" },
                "Antarctica/Vostok":               { stdOffset: "+06:00", city: "Vostok" },
                "Antarctica/Davis":                { stdOffset: "+07:00", city: "Davis" },
                "Antarctica/Casey":                { stdOffset: "+08:00", city: "Casey" },
                "Antarctica/DumontDUrville":       { stdOffset: "+10:00", city: "DumontDUrville" },
                "Antarctica/Macquarie":            { stdOffset: "+11:00", city: "Macquarie" },
                "Antarctica/McMurdo":              { stdOffset: "+12:00", city: "McMurdo" }
            },
            Asia: {
                "Asia/Amman":                      { stdOffset: "+02:00", city: "Amman" },
                "Asia/Beirut":                     { stdOffset: "+02:00", city: "Beirut" },
                "Asia/Damascus":                   { stdOffset: "+02:00", city: "Damascus" },
                "Asia/Gaza":                       { stdOffset: "+02:00", city: "Gaza" },
                "Asia/Jerusalem":                  { stdOffset: "+02:00", city: "Jerusalem" },
                "Asia/Nicosia":                    { stdOffset: "+02:00", city: "Nicosia" },
                "Asia/Aden":                       { stdOffset: "+03:00", city: "Aden" },
                "Asia/Baghdad":                    { stdOffset: "+03:00", city: "Baghdad" },
                "Asia/Bahrain":                    { stdOffset: "+03:00", city: "Bahrain" },
                "Asia/Kuwait":                     { stdOffset: "+03:00", city: "Kuwait" },
                "Asia/Qatar":                      { stdOffset: "+03:00", city: "Qatar" },
                "Asia/Riyadh":                     { stdOffset: "+03:00", city: "Riyadh" },
                "Asia/Tehran":                     { stdOffset: "+03:30", city: "Tehran" },
                "Asia/Baku":                       { stdOffset: "+04:00", city: "Baku" },
                "Asia/Dubai":                      { stdOffset: "+04:00", city: "Dubai" },
                "Asia/Muscat":                     { stdOffset: "+04:00", city: "Muscat" },
                "Asia/Tbilisi":                    { stdOffset: "+04:00", city: "Tbilisi" },
                "Asia/Yerevan":                    { stdOffset: "+04:00", city: "Yerevan" },
                "Asia/Kabul":                      { stdOffset: "+04:30", city: "Kabul" },
                "Asia/Aqtau":                      { stdOffset: "+05:00", city: "Aqtau" },
                "Asia/Aqtobe":                     { stdOffset: "+05:00", city: "Aqtobe" },
                "Asia/Ashgabat":                   { stdOffset: "+05:00", city: "Ashgabat" },
                "Asia/Dushanbe":                   { stdOffset: "+05:00", city: "Dushanbe" },
                "Asia/Karachi":                    { stdOffset: "+05:00", city: "Karachi" },
                "Asia/Oral":                       { stdOffset: "+05:00", city: "Oral" },
                "Asia/Samarkand":                  { stdOffset: "+05:00", city: "Samarkand" },
                "Asia/Tashkent":                   { stdOffset: "+05:00", city: "Tashkent" },
                "Asia/Colombo":                    { stdOffset: "+05:30", city: "Colombo" },
                "Asia/Kolkata":                    { stdOffset: "+05:30", city: "Kolkata" },
                "Asia/Kathmandu":                  { stdOffset: "+05:45", city: "Kathmandu" },
                "Asia/Almaty":                     { stdOffset: "+06:00", city: "Almaty" },
                "Asia/Bishkek":                    { stdOffset: "+06:00", city: "Bishkek" },
                "Asia/Dhaka":                      { stdOffset: "+06:00", city: "Dhaka" },
                "Asia/Qyzylorda":                  { stdOffset: "+06:00", city: "Qyzylorda" },
                "Asia/Thimphu":                    { stdOffset: "+06:00", city: "Thimphu" },
                "Asia/Yekaterinburg":              { stdOffset: "+06:00", city: "Yekaterinburg" },
                "Asia/Rangoon":                    { stdOffset: "+06:30", city: "Rangoon" },
                "Asia/Bangkok":                    { stdOffset: "+07:00", city: "Bangkok" },
                "Asia/Ho_Chi_Minh":                { stdOffset: "+07:00", city: "Ho Chi Minh" },
                "Asia/Hovd":                       { stdOffset: "+07:00", city: "Hovd" },
                "Asia/Jakarta":                    { stdOffset: "+07:00", city: "Jakarta" },
                "Asia/Novokuznetsk":               { stdOffset: "+07:00", city: "Novokuznetsk" },
                "Asia/Novosibirsk":                { stdOffset: "+07:00", city: "Novosibirsk" },
                "Asia/Omsk":                       { stdOffset: "+07:00", city: "Omsk" },
                "Asia/Phnom_Penh":                 { stdOffset: "+07:00", city: "Phnom Penh" },
                "Asia/Pontianak":                  { stdOffset: "+07:00", city: "Pontianak" },
                "Asia/Vientiane":                  { stdOffset: "+07:00", city: "Vientiane" },
                "Asia/Brunei":                     { stdOffset: "+08:00", city: "Brunei" },
                "Asia/Choibalsan":                 { stdOffset: "+08:00", city: "Choibalsan" },
                "Asia/Krasnoyarsk":                { stdOffset: "+08:00", city: "Krasnoyarsk" },
                "Asia/Kuala_Lumpur":               { stdOffset: "+08:00", city: "Kuala Lumpur" },
                "Asia/Kuching":                    { stdOffset: "+08:00", city: "Kuching" },
                "Asia/Macau":                      { stdOffset: "+08:00", city: "Macau" },
                "Asia/Makassar":                   { stdOffset: "+08:00", city: "Makassar" },
                "Asia/Manila":                     { stdOffset: "+08:00", city: "Manila" },
                "Asia/Shanghai":                   { stdOffset: "+08:00", city: "Shanghai" },
                "Asia/Singapore":                  { stdOffset: "+08:00", city: "Singapore" },
                "Asia/Taipei":                     { stdOffset: "+08:00", city: "Taipei" },
                "Asia/Ulaanbaatar":                { stdOffset: "+08:00", city: "Ulaanbaatar" },
                "Asia/Urumqi":                     { stdOffset: "+08:00", city: "Urumqi" },
                "Asia/Dili":                       { stdOffset: "+09:00", city: "Dili" },
                "Asia/Irkutsk":                    { stdOffset: "+09:00", city: "Irkutsk" },
                "Asia/Jayapura":                   { stdOffset: "+09:00", city: "Jayapura" },
                "Asia/Pyongyang":                  { stdOffset: "+09:00", city: "Pyongyang" },
                "Asia/Seoul":                      { stdOffset: "+09:00", city: "Seoul" },
                "Asia/Tokyo":                      { stdOffset: "+09:00", city: "Tokyo" },
                "Asia/Yakutsk":                    { stdOffset: "+10:00", city: "Yakutsk" },
                "Asia/Sakhalin":                   { stdOffset: "+11:00", city: "Sakhalin" },
                "Asia/Vladivostok":                { stdOffset: "+11:00", city: "Vladivostok" },
                "Asia/Anadyr":                     { stdOffset: "+12:00", city: "Anadyr" },
                "Asia/Kamchatka":                  { stdOffset: "+12:00", city: "Kamchatka" },
                "Asia/Magadan":                    { stdOffset: "+12:00", city: "Magadan" }
            },
            Atlantic: {
                "Atlantic/Bermuda":                { stdOffset: "-04:00", city: "Bermuda" },
                "Atlantic/Stanley":                { stdOffset: "-04:00", city: "Stanley" },
                "Atlantic/South_Georgia":          { stdOffset: "-02:00", city: "South Georgia" },
                "Atlantic/Azores":                 { stdOffset: "-01:00", city: "Azores" },
                "Atlantic/Cape_Verde":             { stdOffset: "-01:00", city: "Cape Verde" },
                "Atlantic/Canary":                 { stdOffset: "+00:00", city: "Canary" },
                "Atlantic/Faroe":                  { stdOffset: "+00:00", city: "Faroe" },
                "Atlantic/Madeira":                { stdOffset: "+00:00", city: "Madeira" },
                "Atlantic/Reykjavik":              { stdOffset: "+00:00", city: "Reykjavik" },
                "Atlantic/St_Helena":              { stdOffset: "+00:00", city: "St Helena" }
            },
            Australia: {
                "Australia/Perth":                 { stdOffset: "+08:00", city: "Perth" },
                "Australia/Eucla":                 { stdOffset: "+08:45", city: "Eucla" },
                "Australia/Adelaide":              { stdOffset: "+09:30", city: "Adelaide" },
                "Australia/Broken_Hill":           { stdOffset: "+09:30", city: "Broken Hill" },
                "Australia/Darwin":                { stdOffset: "+09:30", city: "Darwin" },
                "Australia/Brisbane":              { stdOffset: "+10:00", city: "Brisbane" },
                "Australia/Currie":                { stdOffset: "+10:00", city: "Currie" },
                "Australia/Hobart":                { stdOffset: "+10:00", city: "Hobart" },
                "Australia/Lindeman":              { stdOffset: "+10:00", city: "Lindeman" },
                "Australia/Melbourne":             { stdOffset: "+10:00", city: "Melbourne" },
                "Australia/Sydney":                { stdOffset: "+10:00", city: "Sydney" },
                "Australia/Lord_Howe":             { stdOffset: "+10:30", city: "Lord Howe" }
            },
            Europe: {
                "Europe/Dublin":                   { stdOffset: "+00:00", city: "Dublin" },
                "Europe/Lisbon":                   { stdOffset: "+00:00", city: "Lisbon" },
                "Europe/London":                   { stdOffset: "+00:00", city: "London" },
                "Europe/Amsterdam":                { stdOffset: "+01:00", city: "Amsterdam" },
                "Europe/Andorra":                  { stdOffset: "+01:00", city: "Andorra" },
                "Europe/Belgrade":                 { stdOffset: "+01:00", city: "Belgrade" },
                "Europe/Berlin":                   { stdOffset: "+01:00", city: "Berlin" },
                "Europe/Brussels":                 { stdOffset: "+01:00", city: "Brussels" },
                "Europe/Budapest":                 { stdOffset: "+01:00", city: "Budapest" },
                "Europe/Copenhagen":               { stdOffset: "+01:00", city: "Copenhagen" },
                "Europe/Gibraltar":                { stdOffset: "+01:00", city: "Gibraltar" },
                "Europe/Luxembourg":               { stdOffset: "+01:00", city: "Luxembourg" },
                "Europe/Madrid":                   { stdOffset: "+01:00", city: "Madrid" },
                "Europe/Malta":                    { stdOffset: "+01:00", city: "Malta" },
                "Europe/Monaco":                   { stdOffset: "+01:00", city: "Monaco" },
                "Europe/Oslo":                     { stdOffset: "+01:00", city: "Oslo" },
                "Europe/Paris":                    { stdOffset: "+01:00", city: "Paris" },
                "Europe/Prague":                   { stdOffset: "+01:00", city: "Prague" },
                "Europe/Rome":                     { stdOffset: "+01:00", city: "Rome" },
                "Europe/Stockholm":                { stdOffset: "+01:00", city: "Stockholm" },
                "Europe/Tirane":                   { stdOffset: "+01:00", city: "Tirane" },
                "Europe/Vaduz":                    { stdOffset: "+01:00", city: "Vaduz" },
                "Europe/Vienna":                   { stdOffset: "+01:00", city: "Vienna" },
                "Europe/Warsaw":                   { stdOffset: "+01:00", city: "Warsaw" },
                "Europe/Zurich":                   { stdOffset: "+01:00", city: "Zurich" },
                "Europe/Athens":                   { stdOffset: "+02:00", city: "Athens" },
                "Europe/Bucharest":                { stdOffset: "+02:00", city: "Bucharest" },
                "Europe/Chisinau":                 { stdOffset: "+02:00", city: "Chisinau" },
                "Europe/Helsinki":                 { stdOffset: "+02:00", city: "Helsinki" },
                "Europe/Istanbul":                 { stdOffset: "+02:00", city: "Istanbul" },
                "Europe/Kiev":                     { stdOffset: "+02:00", city: "Kiev" },
                "Europe/Minsk":                    { stdOffset: "+02:00", city: "Minsk" },
                "Europe/Riga":                     { stdOffset: "+02:00", city: "Riga" },
                "Europe/Simferopol":               { stdOffset: "+02:00", city: "Simferopol" },
                "Europe/Sofia":                    { stdOffset: "+02:00", city: "Sofia" },
                "Europe/Tallinn":                  { stdOffset: "+02:00", city: "Tallinn" },
                "Europe/Uzhgorod":                 { stdOffset: "+02:00", city: "Uzhgorod" },
                "Europe/Vilnius":                  { stdOffset: "+02:00", city: "Vilnius" },
                "Europe/Zaporozhye":               { stdOffset: "+02:00", city: "Zaporozhye" },
                "Europe/Kaliningrad":              { stdOffset: "+03:00", city: "Kaliningrad" },
                "Europe/Moscow":                   { stdOffset: "+04:00", city: "Moscow" },
                "Europe/Samara":                   { stdOffset: "+04:00", city: "Samara" },
                "Europe/Volgograd":                { stdOffset: "+04:00", city: "Volgograd" }
            },
            Indian: {
                "Indian/Antananarivo":             { stdOffset: "+03:00", city: "Antananarivo" },
                "Indian/Comoro":                   { stdOffset: "+03:00", city: "Comoro" },
                "Indian/Mayotte":                  { stdOffset: "+03:00", city: "Mayotte" },
                "Indian/Mahe":                     { stdOffset: "+04:00", city: "Mahe" },
                "Indian/Mauritius":                { stdOffset: "+04:00", city: "Mauritius" },
                "Indian/Reunion":                  { stdOffset: "+04:00", city: "Reunion" },
                "Indian/Kerguelen":                { stdOffset: "+05:00", city: "Kerguelen" },
                "Indian/Maldives":                 { stdOffset: "+05:00", city: "Maldives" },
                "Indian/Chagos":                   { stdOffset: "+06:00", city: "Chagos" },
                "Indian/Cocos":                    { stdOffset: "+06:30", city: "Cocos" },
                "Indian/Christmas":                { stdOffset: "+07:00", city: "Christmas" }
            },
            Pacific: {
                "Pacific/Apia":                    { stdOffset: "-11:00", city: "Apia" },
                "Pacific/Midway":                  { stdOffset: "-11:00", city: "Midway" },
                "Pacific/Niue":                    { stdOffset: "-11:00", city: "Niue" },
                "Pacific/Pago_Pago":               { stdOffset: "-11:00", city: "Pago Pago" },
                "Pacific/Fakaofo":                 { stdOffset: "-10:00", city: "Fakaofo" },
                "Pacific/Honolulu":                { stdOffset: "-10:00", city: "Honolulu" },
                "Pacific/Johnston":                { stdOffset: "-10:00", city: "Johnston" },
                "Pacific/Rarotonga":               { stdOffset: "-10:00", city: "Rarotonga" },
                "Pacific/Tahiti":                  { stdOffset: "-10:00", city: "Tahiti" },
                "Pacific/Marquesas":               { stdOffset: "-09:30", city: "Marquesas" },
                "Pacific/Gambier":                 { stdOffset: "-09:00", city: "Gambier" },
                "Pacific/Pitcairn":                { stdOffset: "-08:00", city: "Pitcairn" },
                "Pacific/Easter":                  { stdOffset: "-06:00", city: "Easter" },
                "Pacific/Galapagos":               { stdOffset: "-06:00", city: "Galapagos" },
                "Pacific/Palau":                   { stdOffset: "+09:00", city: "Palau" },
                "Pacific/Chuuk":                   { stdOffset: "+10:00", city: "Chuuk" },
                "Pacific/Guam":                    { stdOffset: "+10:00", city: "Guam" },
                "Pacific/Port_Moresby":            { stdOffset: "+10:00", city: "Port Moresby" },
                "Pacific/Saipan":                  { stdOffset: "+10:00", city: "Saipan" },
                "Pacific/Efate":                   { stdOffset: "+11:00", city: "Efate" },
                "Pacific/Guadalcanal":             { stdOffset: "+11:00", city: "Guadalcanal" },
                "Pacific/Kosrae":                  { stdOffset: "+11:00", city: "Kosrae" },
                "Pacific/Noumea":                  { stdOffset: "+11:00", city: "Noumea" },
                "Pacific/Pohnpei":                 { stdOffset: "+11:00", city: "Pohnpei" },
                "Pacific/Norfolk":                 { stdOffset: "+11:30", city: "Norfolk" },
                "Pacific/Auckland":                { stdOffset: "+12:00", city: "Auckland" },
                "Pacific/Fiji":                    { stdOffset: "+12:00", city: "Fiji" },
                "Pacific/Funafuti":                { stdOffset: "+12:00", city: "Funafuti" },
                "Pacific/Kwajalein":               { stdOffset: "+12:00", city: "Kwajalein" },
                "Pacific/Majuro":                  { stdOffset: "+12:00", city: "Majuro" },
                "Pacific/Nauru":                   { stdOffset: "+12:00", city: "Nauru" },
                "Pacific/Tarawa":                  { stdOffset: "+12:00", city: "Tarawa" },
                "Pacific/Wake":                    { stdOffset: "+12:00", city: "Wake" },
                "Pacific/Wallis":                  { stdOffset: "+12:00", city: "Wallis" },
                "Pacific/Chatham":                 { stdOffset: "+12:45", city: "Chatham" },
                "Pacific/Enderbury":               { stdOffset: "+13:00", city: "Enderbury" },
                "Pacific/Tongatapu":               { stdOffset: "+13:00", city: "Tongatapu" },
                "Pacific/Kiritimati":              { stdOffset: "+14:00", city: "Kiritimati" }
            },
            Other: {
                "UTC":                             { stdOffset: "+00:00", city: "UTC" }
            }
        };

        // Default lang
        moment.lang(localStorage.locale || (typeof Mayocat !== 'undefined' ? Mayocat.defaultLocale : "en"));

        // Listen to the `ui:localedChanged` event, see `mayocat.js` for the broadcast.
        $rootScope.$on('ui:localeChanged', function (event, locale) {
            moment.lang(locale);
        });

        return {

            getTimeZoneData: function () {
                return tzData;
            },

            convertTimestamp: function (timestamp, format) {
                if (typeof timestamp === "undefined") {
                    return undefined;
                }
                if (typeof timestamp === "number") {
                    timestamp.toString();
                }
                if (timestamp.length > 10) {
                    timestamp = timestamp.slice(0, 10);
                }
                return moment.unix(parseInt(timestamp, 10)).format(format || defaultPrintFormat);
            },

            convertISO8601toLocalDate: function (datestring, printfmt) {
                if (!datestring) {
                    return undefined;
                }
                // use ISO format without the timezone part to convert to local date
                // Note: moment will treat the date as not valid if it does not respect
                // the format (ex: 2013-04-09 won't be valid with this format)
                // this could be problematic in another context but here the source is
                // trusted, and moment is just being used internally
                return moment(datestring, "YYYY-MM-DDTHH:mm:ss").format(printfmt || defaultPrintFormat);
            },

            convert: function (input, format, outputFormat) {
                return moment(input, format).format(outputFormat);
            },

            convertISO: function (input, outputFormat) {
                return moment(input).format(outputFormat);
            }

        };

    }])

    .filter('timestampAsDate', ['timeService', function (timeService) {
        return function (timestamp, format) {
            return timeService.convertTimestamp(timestamp, format);
        };
    }])

    .filter('iso8601toLocalDate', ['timeService', function (timeService) {
        return function (string, format) {
            return timeService.convertISO8601toLocalDate(string, format);
        };
    }]);
})();
