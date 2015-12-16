'use strict';

/**
 * @ngdoc service
 * @name services:Util.DateService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/util/util-date.client.service.js services/util/util-date.client.service.js}
 *
 * Date and time functions.
 */

angular.module('services').factory('Util.DateService', ['$translate', 'UtilService'
    , function ($translate, Util) {
        var Service = {
            defaultDateFormat: $translate.instant("common.defaultDateFormat")
            , defaultTimeFormat: $translate.instant("common.defaultTimeFormat")
            , defaultDatetimeFormat: $translate.instant("common.defaultDatetimeFormat")

            /**
             * @ngdoc method
             * @name isoToDate
             * @methodOf services:Util.DateService
             *
             * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ss.SSSZZ
             * @param {String} format (Optional)Date format. If not provided, a default defined in common en.json is used
             *
             * @description
             * Convert an ISO Datetime string to a Datetime string.
             */
            , isoToDate: function (isoDatetime, format) {
                format = format || Service.defaultDateFormat;
                return moment(isoDatetime).format(format);
            }

            /**
             * @ngdoc method
             * @name goodDate
             * @methodOf services:Util.DateService
             *
             * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ss.SSSZZ
             * @param {String} format (Optional)Date format. If not provided, a default defined in common en.json is used
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @description
             * Convert an ISO date time string to a date string.
             */
            , goodDate: function (isoDatetime, format, replacement) {
                if (Util.isEmpty(isoDatetime)) {
                    return Util.goodValue(isoDatetime, replacement);
                }
                return Service.isoToDate(isoDatetime, format);
            }

            /**
             * @ngdoc method
             * @name getDate
             * @methodOf services:Util.DateService
             *
             * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ss.SSSZZ
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @description
             * Extract date part from an ISO Datetime in default format.
             */
            , getDate: function (isoDatetime, replacement) {
                return Service.goodDate(isoDatetime, Service.defaultDateFormat, replacement);
            }

            /**
             * @ngdoc method
             * @name getTime
             * @methodOf services:Util.DateService
             *
             * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ss.SSSZZ
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @description
             * Extract time part from an ISO Datetime in default format.
             */
            , getTime: function (isoDatetime, replacement) {
                return Service.goodDate(isoDatetime, Service.defaultTimeFormat, replacement);
            }

            /**
             * @ngdoc method
             * @name getDate
             * @methodOf services:Util.DateService
             *
             * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ss.SSSZZ
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @description
             * Extract datetime from an ISO Datetime in default format.
             */
            , getDatetime: function (isoDatetime, replacement) {
                format = format || Service.defaultDatetimeFormat;
                return moment(isoDatetime).format(format);
            }




            //get day string in "yyyy-mm-dd" format
            //parameter d is java Date() format; for some reason getDate() is 1 based while getMonth() is zero based
            , __copy_fr_old_app_dateToString: function (d) {
                if (null == d) {
                    return "";
                }
                var month = d.getMonth() + 1;
                var day = d.getDate();
                var year = d.getFullYear();
                return this._padZero(month)
                    + "/" + this._padZero(day)
                    + "/" + year;
            }

            , __copy_fr_old_app_getCurrentDay: function () {
                var d = new Date();
                return this.dateToString(d);
            }

        };

        return Service;
    }
]);