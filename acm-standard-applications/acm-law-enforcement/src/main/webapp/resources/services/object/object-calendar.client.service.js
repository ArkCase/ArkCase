'use strict';

/**
 * @ngdoc service
 * @name services:Object.CalendarService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-calendar.client.service.js services/object/object-calendar.client.service.js}

 * Object.CalendarService includes group of REST calls to retrieve and save Calendar info;
 */
angular.module('services').factory('Object.CalendarService', ['$resource', 'UtilService', 'Acm.StoreService',
    function ($resource, Util, Store) {
        var Service = this;
        Service.SessionCacheNames = {};
        Service.CacheNames = {
            CALENDAR_EVENTS: "CalendarEvents"
        };


        /**
         * @ngdoc method
         * @name resetCalendarEventsCache
         * @methodOf services:Object.CalendarService
         *
         * @description
         * Delete calendar events cached items.
         *
         * @param {String} calendarFolderId  Calendar Folder ID
         */
        Service.resetCalendarEventsCache = function (calendarFolderId) {
            var cacheCalendarEvents = new Store.CacheFifo(Service.CacheNames.CALENDAR_EVENTS);
            var cacheKey = calendarFolderId;
            cacheCalendarEvents.remove(cacheKey);
        };

        /**
         * @ngdoc method
         * @name queryCostsheets
         * @methodOf services:Object.CalendarService
         *
         * @description
         * Query calendar events.
         *
         * @param {String} calendarFolderId  Calendar Folder ID
         * @param {Number} objectId  Object ID
         *
         * @returns {Object} Promise
         */
        Service.queryCalendarEvents = function (calendarFolderId) {
            var service = $resource('api/latest/plugin', {}, {
                /**
                 * @ngdoc method
                 * @name get
                 * @methodOf services:Object.CalendarService
                 *
                 * @description
                 * Query calendar data from database.
                 *
                 * @param {Number} calendarFolderId  Calendar Folder ID
                 * @param {Function} onSuccess (Optional)Callback function of success query.
                 * @param {Function} onError (Optional) Callback function when fail.
                 *
                 * @returns {Object} Object returned by $resource
                 */
                _getCalendarEvents: {
                    method: 'GET',
                    url: 'api/v1/plugin/outlook/calendar?folderId=' + encodeURIComponent(calendarFolderId),
                    cache: false,
                    isArray: false
                }
            });

            var cacheCalendarEvents = new Store.CacheFifo(Service.CacheNames.CALENDAR_EVENTS);
            var cacheKey = calendarFolderId;
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
         * @name addNewEvent
         * @methodOf services:Object.CalendarService
         *
         * @description
         * Add new event to the calendar.
         *
         * @param {Object} eventData  the data of the calendar event
         *
         * @returns {Object} Promise
         */
        Service.addNewEvent = function(eventData) {
            //TO DO
            //implement http call when backend service is ready
        };

        /**
         * @ngdoc method
         * @name getEventDetails
         * @methodOf services:Object.CalendarService
         *
         * @description
         * Get the details for existing event.
         *
         * @param {Object} eventId  ID of the existing event
         *
         * @returns {Object} Promise
         */
        Service.getEventDetails = function(eventId) {
            //TO DO
            //implement http call when backend service is ready
        };

        return Service;
    }
]);
