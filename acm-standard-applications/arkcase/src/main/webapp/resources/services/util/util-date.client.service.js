'use strict';

/**
 * @ngdoc service
 * @name services:Util.DateService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/services/util/util-date.client.service.js services/util/util-date.client.service.js}
 *
 * Date and time functions.
 */

angular.module('services').factory('Util.DateService', [ '$translate', 'UtilService', function($translate, Util) {
    var Service = {
        defaultDateFormat: $translate.instant("common.defaultDateFormat"),
        defaultTimeFormat: $translate.instant("common.defaultTimeFormat"),
        defaultDateTimeFormat: $translate.instant("common.defaultDateTimeFormat"),
        defaultDateLongTimeFormat: $translate.instant("common.defaultDateLongTimeFormat"),
        defaultDatePickerFormat: $translate.instant("common.defaultDatePickerFormat"),
        defaultDateTimePickerFormat: $translate.instant("common.defaultDateTimePickerFormat"),
        defaultDateTimeLongUIFormatWithYearAsNumber: $translate.instant("common.defaultDateTimeLongUIFormatWithYearAsNumber")

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
         * @Returns {String} ISO formatted date string YYYY-MM-DDTHH:mm:ssZZ
         */
        ,
        dateToIso: function(date, replacement) {
            var replacedWith = (undefined === replacement) ? "" : replacement;

            // if we use both moment-picker and ng-model properties (this is case when we have calendar button and
            // input text together date is saved as string otherwise it is as Date object
            if (date && (typeof date === 'string' || date instanceof Date)) {
                return moment(date).format("YYYY-MM-DDTHH:mm:ssZZ");
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
        ,
        localDateToIso: function(date, replacement) {
            var replacedWith = (undefined === replacement) ? "" : replacement;

            // if we use both moment-picker and ng-model properties (this is case when we have calendar button and
            // input text together date is saved as string otherwise it is as Date object
            if (date && (typeof date === 'string' || date instanceof Date)) {
                return moment(date).format("YYYY-MM-DD");
            } else {
                return replacedWith;
            }
        }
        ,
        /**
         * @ngdoc method
         * @name dateTimeToIso
         * @methodOf services:Util.DateService
         *
         * @description
         * Converts a date object into an DateTimeFormat.ISO.DATE_TIME format string
         *
         * @param {Date} date object
         * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
         *
         * @Returns {String} ISO formatted date string YYYY-MM-DDTHH:mm:ss
         */
        dateTimeToIso: function(date, replacement) {
            var replacedWith = (undefined === replacement) ? "" : replacement;

            // if we use both moment-picker and ng-model properties (this is case when we have calendar button and
            // input text together date is saved as string otherwise it is as Date object
            if (date && (typeof date === 'string' || date instanceof Date)) {
                return moment(date).format("YYYY-MM-DDTHH:mm:ss");
            } else {
                return replacedWith;
            }
        }

        /**
         * @ngdoc method
         * @name isoToDate
         * @methodOf services:Util.DateService
         *
         * @description
         * Converts a date object into an ISO format string
         *
         * @param {String} isoDateTime ISO formatted date string YYYY-MM-DDTHH:mm:ssZZ
         * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to null
         *
         * @Returns {Date} Date object
         */
        ,
        isoToDate: function(isoDateTime, replacement) {
            var replacedWith = (undefined === replacement) ? null : replacement;

            if (!Util.isEmpty(isoDateTime)) {
                return moment(isoDateTime).toDate();
            } else {
                return replacedWith;
            }
        }

        /**
         * @ngdoc method
         * @name dateToIsoDateTime
         * @methodOf services:Util.DateService
         *
         * @description
         * Converts a date object into an ISO format string
         *
         * @param {String} isoDateTime ISO formatted date string YYYY-MM-DDTHH:mm:ss.sss
         *
         * @Returns {String} String
         */
        ,
        dateToIsoDateTime: function(isoDateTime) {
            if (!Util.isEmpty(isoDateTime)) {
                return moment.utc(isoDateTime).format("YYYY-MM-DDTHH:mm:ss.sss");
            } else {
                return "";
            }
        }

        /**
         * @ngdoc method
         * @name isoToLocalDateTime
         * @methodOf services:Util.DateService
         *
         * @description
         * Converts a ISO format string to LocalDateTime
         *
         * @param {String} isoDateTime ISO formatted date string YYYY-MM-DDTHH:mm:ss
         *
         * @Returns {String} String
         */
        ,
        isoToLocalDateTime: function (isoDateTime) {
            if (!Util.isEmpty(isoDateTime)) {
                return moment.utc(isoDateTime).local().format("YYYY-MM-DDTHH:mm:ss.sssZ");
            } else {
                return "";
            }
        }

        /**
         * @ngdoc method
         * @name goodIsoDate
         * @methodOf services:Util.DateService
         *
         * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ssZZ
         * @param {String} format (Optional)Date format. If not provided, a default defined in common en.json is used
         * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
         *
         * @description
         * Convert an ISO date time string to a date string.
         */
        ,
        goodIsoDate: function(isoDateTime, format, replacement) {
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
         * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ssZZ
         * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
         *
         * @description
         * Extract date part from an ISO Datetime in default format.
         */
        ,
        getDatePart: function(isoDateTime, replacement) {
            console.log("WARNING: Util.DateService.getDatePart() is obsolete, because it is not i18n compliant.");
            return Service.goodIsoDate(isoDateTime, Service.defaultDateFormat, replacement);
        }

        /**
         * @ngdoc method
         * @name getTimePart
         * @methodOf services:Util.DateService
         *
         * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ssZZ
         * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
         *
         * @description
         * Extract time part from an ISO Datetime in default format.
         */
        ,
        getTimePart: function(isoDateTime, replacement) {
            console.log("WARNING: Util.DateService.getTimePart() is obsolete, because it is not i18n compliant.");
            return Service.goodIsoDate(isoDateTime, Service.defaultTimeFormat, replacement);
        }

        /**
         * @ngdoc method
         * @name getDateTimePart
         * @methodOf services:Util.DateService
         *
         * @param {String} isoDateTime Date time as ISO8601 format, yyyy-MM-dd'T'HH:mm:ssZZ
         * @param {Object} replacement (Optional)Object or value used if 'val' is empty. If not provided, it defaults to ""
         *
         * @description
         * Extract datetime from an ISO Datetime in default format.
         */
        ,
        getDateTimePart: function(isoDateTime, replacement) {
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
        ,
        getTimeZoneOffset: function() {
            var currentTimeZoneOffsetInMinutes = new Date().getTimezoneOffset();
            var currentTimeZoneOffsetInHours = Math.floor(currentTimeZoneOffsetInMinutes / 60);
            currentTimeZoneOffsetInMinutes = Math.abs(currentTimeZoneOffsetInMinutes % 60);
            var currentTimeZoneOffset = "UTC" + currentTimeZoneOffsetInHours + ":" + currentTimeZoneOffsetInMinutes;
            return currentTimeZoneOffset;
        }

        /**
         * @ngdoc method
         * @name getTimeZoneOffsetTime
         * @methodOf services:Util.DateService
         *
         * @description
         * Get user's time difference between UTC time and local time
         *
         * @Returns {String} (eg: -02:00)
         */
        ,
        getTimeZoneOffsetTime: function () {
            var currentTimeZoneOffsetInMinutes = new Date().getTimezoneOffset();
            var currentTimeZoneOffsetInHours = Math.floor(currentTimeZoneOffsetInMinutes / 60);
            currentTimeZoneOffsetInMinutes = Math.abs(currentTimeZoneOffsetInMinutes % 60);
            if (currentTimeZoneOffsetInMinutes < 10) {
                currentTimeZoneOffsetInMinutes += "0";
            }
            if (currentTimeZoneOffsetInHours > 0 && currentTimeZoneOffsetInHours < 10) {
                currentTimeZoneOffsetInHours = "-0" + currentTimeZoneOffsetInHours;
            }
            else if (currentTimeZoneOffsetInHours > 0 && currentTimeZoneOffsetInHours > 10 || currentTimeZoneOffsetInHours < -10) {
                currentTimeZoneOffsetInHours *= -1;
            }
            else if (currentTimeZoneOffsetInHours < 0 && currentTimeZoneOffsetInHours > -10) {
                currentTimeZoneOffsetInHours = "+0" + currentTimeZoneOffsetInHours * -1;
            }
            var currentTimeZoneOffsetTime = currentTimeZoneOffsetInHours + ":" + currentTimeZoneOffsetInMinutes;
            return currentTimeZoneOffsetTime;
        }

        /**
         * @ngdoc method
         * @name convertToCurrentTime
         * @methodOf services:Util.DateService
         *
         * @description
         * Computates the time difference between UTC time and local time
         *
         * @Returns {Date} Date object
         */
        ,
        convertToCurrentTime: function(date) {
            var now = new Date();
            var convertedTime = new Date(date.getFullYear(), date.getMonth(), date.getDate(), now.getHours(), now.getMinutes(), now.getSeconds(), now.getMilliseconds());
            return convertedTime;
        }
        /**
         * @ngdoc method
         * @name setSameTime
         * @methodOf services:Util.DateService
         *
         * @description
         * Setter which sets the same time for firstDate and secondDate by setting the hours, minutes, seconds and milisecond from the secondDate to the firstDate
         *
         * @Returns {Date} Date object
         */
        ,
        setSameTime: function(firstDate, secondDate) {
            firstDate = new Date(firstDate.getFullYear(), firstDate.getMonth(), firstDate.getDate(), secondDate.getHours(), secondDate.getMinutes(), secondDate.getSeconds(), secondDate.getMilliseconds());
            return firstDate;
        }
        /**
         * @ngdoc method
         * @name validateFromDate
         * @methodOf services:Util.DateService
         *
         * @description
         * Date format: mm/d/yyyy
         * Validations:
         *      - on load sets today date
         *      - it doesn't accept invalid date:
         *          - characters that aren't numbers
         *          - date format
         *          - date after today
         *
         *
         * @Returns {Object} {from: from, to: to}
         */
        ,
        validateFromDate: function(from, to) {
            var todayDate = new Date();
            if (Util.isEmpty(from)) {
                from = new Date();
            } else {
                from = this.convertToCurrentTime(from);
            }

            if (moment(from).isAfter(todayDate)) {
                from = this.convertToCurrentTime(todayDate);
            }

            if (moment(from).isAfter(to)) {
                to = this.convertToCurrentTime(from);
            }

            from = this.setSameTime(from, to);

            return {
                from: from,
                to: to
            };
        }
        /**
         * @ngdoc method
         * @name validateToDate
         * @methodOf services:Util.DateService
         *
         * @description
         * Date format: mm/d/yyyy
         * Validations:
         *      - on load sets today date
         *      - it doesn't accept invalid date:
         *          - characters that aren't numbers
         *          - date format
         *          - date before startDay
         *
         *
         * @Returns {Object} {from: from, to: to}
         */
        ,
        validateToDate: function(from, to) {
            if (Util.isEmpty(to)) {
                to = this.convertToCurrentTime(from);
            } else {
                to = this.convertToCurrentTime(to);
            }

            if (moment(to).isBefore(from)) {
                to = this.convertToCurrentTime(from);
            }

            to = this.setSameTime(to, from);

            return {
                from: from,
                to: to
            };
        },

        /**
         * @ngdoc method
         * @name fixStartAndEndDirectiveDates
         * @methodOf services:Util.DateService
         *
         * @description
         * Date format: mm/d/yyyy
         * Validations:
         *      - on load sets today date
         *      - it doesn't accept invalid date:
         *          - characters that aren't numbers
         *          - date format
         *      - it validates dates that are used as part of date-time-picker directive
         *
         *
         * @Returns {Object} {from: from, to: to}
         */

        fixStartAndEndDirectiveDates: function(start, end, oldValue, newValue, convertToDateFormat){
            var todayDate = new Date();
            var oldDate = moment(oldValue).format("DD/MM/YYYY hh:mm:ss");
            var newDate = moment(newValue).format("DD/MM/YYYY hh:mm:ss");

            var startDate = typeof(start) === "object" ? start : new Date(start);
            var startDateConverted;
            var endDateConverted;
            var startDateHelper = this.convertUTCDateToLocalDate(startDate);
            if (moment(startDateHelper).isBefore(moment(todayDate)) && oldDate !== newDate) {
                start = todayDate;
            }else {
                if(moment(newValue).isAfter(todayDate)){
                    start = newValue;
                }
            }
            if(convertToDateFormat){
                startDateConverted = this.isoToLocalDateTime(start);
                endDateConverted = this.isoToLocalDateTime(end);
            }else {
                startDateConverted = this.isoToLocalDateTime(start);
                endDateConverted = this.isoToLocalDateTime(end);
            }

            if (moment(startDateConverted).isAfter(moment(endDateConverted))) {
                end = start;
            }
            return {
                start: start,
                end: end
            }
        },

        /**
         * @ngdoc method
         * @name fixDirectiveEndDate
         * @methodOf services:Util.DateService
         *
         * @description
         * Date format: mm/d/yyyy
         * Validations:
         *      - on load sets today date
         *      - it doesn't accept invalid date:
         *      - date format
         *      - it validates dates that are used as part of date-time-picker directive
         *      - characters that aren't numbers
         *
         * @Returns {Object} {endDate: endDate}
         */

        fixDirectiveEndDate: function(startDate, endDate) {
            var todayDate = new Date();
            if (Util.isEmpty(endDate)) {
                endDate = todayDate;
            }

            if (moment(endDate).isBefore(startDate)) {
                endDate = startDate;
            }
            endDate;
        },

        /**
         * @ngdoc method
         * @name convertUTCDateToLocalDate
         * @methodOf services:Util.DateService
         *
         *
         * @Returns {Object} {endDate: endDate}
         */
        convertUTCDateToLocalDate: function(date) {
            var newDate = new Date(date.getTime()+date.getTimezoneOffset()*60*1000);

            var offset = date.getTimezoneOffset() / 60;
            var hours = date.getHours();

            newDate.setHours(hours - offset);

            return newDate;
        },

        /**
         * @ngdoc method
         * @name compareDatesForUpdate
         * @methodOf services:Util.DateService
         *
         *
         * @Returns {Object} {endDate: endDate}
         */
        compareDatesForUpdate: function(pickerDate, originalDate) {
            // when switching between object type nodes (cases,complaints..) we are using same controller, just changing objectInfo data which means change detection for
            // datepicker will be invoked. That is why we need to check if current data in datepicker is not same as change detection date
            // so we won't make unnecessary backend call when switching and also we need another condition to check if chosen date is not same
            // with received date in which case we want to make backend call (user entered new value from datepicker)
            return !moment(moment(pickerDate).format(Service.defaultDateTimePickerFormat)).isSame(moment(originalDate).format(Service.defaultDateTimePickerFormat));
        }
    };

    return Service;
} ]);
