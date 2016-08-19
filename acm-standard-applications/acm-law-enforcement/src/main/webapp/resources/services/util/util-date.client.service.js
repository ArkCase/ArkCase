'use strict';

/**
 * @ngdoc service
 * @name services:Util.DateService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/util/util-date.client.service.js services/util/util-date.client.service.js}
 *
 * Date and time functions.
 */

angular.module('services').factory('Util.DateService', ['$translate', 'UtilService'
    , function ($translate, Util) {
        var Service = {
            defaultDateFormat: $translate.instant("common.defaultDateFormat")
            , defaultTimeFormat: $translate.instant("common.defaultTimeFormat")
            , defaultDateTimeFormat: $translate.instant("common.defaultDateTimeFormat")
            , defaultDatePickerFormat: $translate.instant("common.defaultDatePickerFormat")
            , defaultDateUIFormat: $translate.instant("common.defaultDateUIFormat")


            /**
             * @ngdoc method
             * @name dateToIso
             * @methodOf services:Util.DateService
             *
             * @description
             * Converts a date object into an ISO format string
             *
             * @param {Date} Date object
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @Returns {String} ISO formatted date string YYYY-MM-DDTHH:mm:ss.SSSZZ
             */
            , dateToIso: function (date, replacement) {
                var replacedWith = (undefined === replacement) ? "" : replacement;

                if (date && date instanceof Date) {
                    return moment(date).format("YYYY-MM-DDTHH:mm:ss.SSSZZ");
                } else {
                    return replacedWith;
                }
            }

            /**
             * @ngdoc method
             * @name localDateToIso
             * @methodOf services:Util.DateService
             *
             * @description
             * Converts a date (java LocalDate) object into an ISO format string that holds only date without time
             *
             * @param {Date} Date object
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @Returns {String} ISO formatted date string YYYY-MM-DD
             */
            , localDateToIso: function (date, replacement) {
                var replacedWith = (undefined === replacement) ? "" : replacement;

                if (date && date instanceof Date) {
                    return moment(date).format("YYYY-MM-DD");
                } else {
                    return replacedWith;
                }
            }
            //, dateToIso: function(d, replacement) {
            //    if (Util.isEmpty(d)) {
            //        return Util.goodValue(d, replacement);
            //    }
            //    return moment(d).format("YYYY-MM-DDTHH:mm:ss.SSSZZ");
            //}

            /**
             * @ngdoc method
             * @name isoToDate
             * @methodOf services:Util.DateService
             *
             * @description
             * Converts a date object into an ISO format string
             *
             * @param {String} isoDateTime ISO formatted date string YYYY-MM-DDTHH:mm:ss.SSSZZ
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to null
             *
             * @Returns {Date} Date object
             */
            , isoToDate: function (isoDateTime, replacement) {
                var replacedWith = (undefined === replacement) ? null : replacement;

                if (!Util.isEmpty(isoDateTime)) {
                    return moment(isoDateTime).toDate();
                } else {
                    return replacedWith;
                }
            }


            /**
             * @ngdoc method
             * @name goodIsoDate
             * @methodOf services:Util.DateService
             *
             * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ss.SSSZZ
             * @param {String} format (Optional)Date format. If not provided, a default defined in common en.json is used
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @description
             * Convert an ISO date time string to a date string.
             */
            , goodIsoDate: function (isoDateTime, format, replacement) {
                if (Util.isEmpty(isoDateTime)) {
                    return Util.goodValue(isoDateTime, replacement);
                }

                format = format || Service.defaultDateFormat;
                return moment(isoDateTime).format(format);
            }

            /**
             * @ngdoc method
             * @name getDatePart
             * @methodOf services:Util.DateService
             *
             * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ss.SSSZZ
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @description
             * Extract date part from an ISO Datetime in default format.
             */
            , getDatePart: function (isoDateTime, replacement) {
                return Service.goodIsoDate(isoDateTime, Service.defaultDateFormat, replacement);
            }

            /**
             * @ngdoc method
             * @name getTimePart
             * @methodOf services:Util.DateService
             *
             * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ss.SSSZZ
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @description
             * Extract time part from an ISO Datetime in default format.
             */
            , getTimePart: function (isoDateTime, replacement) {
                return Service.goodIsoDate(isoDateTime, Service.defaultTimeFormat, replacement);
            }

            /**
             * @ngdoc method
             * @name getDateTimePart
             * @methodOf services:Util.DateService
             *
             * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ss.SSSZZ
             * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
             *
             * @description
             * Extract datetime from an ISO Datetime in default format.
             */
            , getDateTimePart: function (isoDateTime, replacement) {
                //format = format || Service.defaultDateTimeFormat;
                //return moment(isoDateTime).format(format);
                var dt = moment(isoDateTime);
                return (dt.isValid()) ? dt.format(Service.defaultDateTimeFormat) : replacement;
            }

            /**
             * @ngdoc method
             * @name getTimeZoneOffset
             * @methodOf services:Util.DateService
             *
             * @description
             * Get user's time difference between UTC time and local time
             *
             * @Returns {String} (eg: UTC-2:30)
             */
            , getTimeZoneOffset: function () {
                var currentTimeZoneOffsetInMinutes = new Date().getTimezoneOffset();
                var currentTimeZoneOffsetInHours = Math.floor(currentTimeZoneOffsetInMinutes / 60);
                currentTimeZoneOffsetInMinutes = Math.abs(currentTimeZoneOffsetInMinutes % 60);
                var currentTimeZoneOffset = "UTC" + currentTimeZoneOffsetInHours + ":" + currentTimeZoneOffsetInMinutes;
                return currentTimeZoneOffset;
            }

        };

        return Service;
    }
]);