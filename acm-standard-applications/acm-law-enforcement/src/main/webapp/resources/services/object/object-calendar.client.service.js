'use strict';

/**
 * @ngdoc service
 * @name services:Object.CalendarService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-calendar.client.service.js services/object/object-calendar.client.service.js}

 * Object.CalendarService includes group of REST calls to retrieve and save Calendar info;
 */
angular.module('services').factory('Object.CalendarService', ['$resource', 'UtilService', 'Acm.StoreService', 'Upload', '$q', '$http', '$httpParamSerializer',
    function ($resource, Util, Store, Upload, $q, $http, $httpParamSerializer) {
        var Service = this;
        Service.SessionCacheNames = {};
        Service.CacheNames = {
            CALENDAR_EVENTS: 'CalendarEvents'
        };


        /**
         * @ngdoc method
         * @name resetCalendarEventsCache
         * @methodOf services:Object.CalendarService
         *
         * @description
         * Delete calendar events cached items.
         *
         * @param {String} objectType
         * @param {String} objectId
         */
        Service.resetCalendarEventsCache = function (objectType, objectId) {
            var cacheCalendarEvents = new Store.CacheFifo(Service.CacheNames.CALENDAR_EVENTS);
            var cacheKey = objectType + '_' + objectId;
            cacheCalendarEvents.remove(cacheKey);
        };

        /**
         * @ngdoc method
         * @name createNewEvent
         * @methodOf services:Object.CalendarService
         *
         * @description
         * Add new event to the calendar.
         *
         * @param {Object} eventData  the data of the calendar event
         * @param {Array} files the files that should be attached on the event
         *
         * @returns {Object} Promise
         */
        Service.createNewEvent = function(calendarId, eventData, files) {
            var formData = new FormData();

            // First part: application/json
            // The browser will not set the content-type of the json object automatically,
            // so we need to set it manualy. The only way to do that is to convert the data to Blob.
            // In that way we can set the desired content-type.
            var data = new Blob([angular.toJson(eventData)], {
                type: 'application/json'
            });
            formData.append('data', data);

            // Second part: file type
            // The browser will automatically set the content-type for the files
            for (var i = 0; i < files.length; i++) {
                //add each file to the form data
                formData.append('file', files[i]);
            }

            // when we are sending data the request
            // needs to include a 'boundary' parameter which identifies the boundary
            // name between parts in this multi-part request and setting the Content-type of the Request Header
            // manually will not set this boundary parameter. Setting the Content-type to
            // undefined will force the request to automatically
            // populate the headers properly including the boundary parameter.
            return $http({
                method: 'POST',
                url: 'api/latest/service/calendar?calendarId=' + encodeURIComponent(calendarId),
                data: formData,
                headers: {'Content-Type': undefined}
            });

        };

        /**
         * @ngdoc method
         * @name updateEvent
         * @methodOf services:Object.CalendarService
         *
         * @description
         * Update the details of existing calendar event.
         *
         * @param {Object} eventData  the data of the calendar event
         * @param {Array} files the files that should be attached on the event
         *
         * @returns {Object} Promise
         */
        Service.updateEvent = function(eventData, files) {
            var formData = new FormData();

            // First part: application/json
            // The browser will not set the content-type of the json object automatically,
            // so we need to set it manualy. The only way to do that is to convert the data to Blob.
            // In that way we can set the desired content-type.
            var data = new Blob([angular.toJson(eventData)], {
                type: 'application/json'
            });
            formData.append('data', data);

            // Second part: file type
            // The browser will automatically set the content-type for the files
            for (var i = 0; i < files.length; i++) {
                //add each file to the form data
                formData.append('file', files[i]);
            }

            // when we are sending data the request
            // needs to include a 'boundary' parameter which identifies the boundary
            // name between parts in this multi-part request and setting the Content-type of the Request Header
            // manually will not set this boundary parameter. Setting the Content-type to
            // undefined will force the request to automatically
            // populate the headers properly including the boundary parameter.
            return $http({
                method: 'PUT',
                url: 'api/latest/service/calendar',
                data: formData,
                headers: {'Content-Type': undefined}
            });
        };

        /**
         * @ngdoc method
         * @name deleteEvent
         * @methodOf services:Object.CalendarService
         *
         * @description
         * Delete existing calendar event.
         *
         * @param {String} objectType
         * @param {String} objectId
         * @param {String} eventId
         *
         * @returns {Object} Promise
         */
        Service.deleteEvent = function(objectType, objectId, eventId) {
            return $http({
                method: 'DELETE',
                url: 'api/latest/service/calendar/calendarevents/' + objectType +'/' + objectId +'/' + eventId
            });
        };

        /**
         * @ngdoc method
         * @name getCalendarEvents
         * @methodOf services:Object.CalendarService
         *
         * @description
         * Get list of calendar events in the given date range.
         *
         * @param {Date} startDate  start of the date range
         * @param {Date} endDate  end of the date range
         * @param {String} objectType
         * @param {String} objectId
         *
         * @returns {Object} Promise
         */
        Service.getCalendarEvents = function(startDate, endDate, objectType, objectId) {
            var params = {
                after: startDate,
                before: endDate
            };

            var urlArgs = $httpParamSerializer(params);
            // after=' + encodeURIComponent(startDate) + '&before=' + encodeURIComponent(endDate),
            var service = $resource('api/latest/service', {}, {
                _getCalendarEvents: {
                    method: 'GET',
                    url: 'api/latest/service/calendar/calendarevents/' + objectType +'/' + objectId + '?' + urlArgs,
                    cache: false,
                    isArray: true
                }
            });

            var cacheCalendarEvents = new Store.CacheFifo(Service.CacheNames.CALENDAR_EVENTS);
            var cacheKey = objectType + '_' + objectId;
            var calendarEvents = cacheCalendarEvents.get(cacheKey);

            return Util.serviceCall({
                service: service._getCalendarEvents
                , param: {}
                , result: calendarEvents
                , onSuccess: function (data) {
                    if (!Util.isEmpty(data) && Util.isArray(data.items) && !Util.isEmpty(data.totalItems)) {
                        calendarEvents = data;
                        cacheCalendarEvents.put(cacheKey, calendarEvents);
                        return calendarEvents;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name getCalendarEventDetails
         * @methodOf services:Object.CalendarService
         *
         * @description
         *
         * Get full details for specific calendar event
         *
         * @param {String} objectType
         * @param {String} objectId
         * @param {String} eventId
         *
         * @returns {Object} Promise
         */
        Service.getCalendarEventDetails = function(objectType, objectId, eventId) {
            return $http({
                method: 'GET',
                url: 'api/latest/service/calendar/calendarevents/' + objectType +'/' + objectId +'/' + eventId
            });
        };

        /**
         * @ngdoc method
         * @name getCalendar
         * @methodOf services:Object.CalendarService
         *
         * @description
         * Get the calendar for the object
         *
         * @param {String} objectType
         * @param {String} objectId
         *
         * @returns {Object} Promise
         */
        Service.getCalendar = function(objectType, objectId) {
            return $http({
                method: 'GET',
                url: 'api/latest/service/calendar/calendars/' + objectType +'/' + objectId
            });
        };
        return Service;
    }
]);
