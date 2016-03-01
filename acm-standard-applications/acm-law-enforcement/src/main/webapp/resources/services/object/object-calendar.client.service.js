'use strict';

/**
 * @ngdoc service
 * @name services:Object.CalendarService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/object/object-calendar.client.service.js services/object/object-calendar.client.service.js}

 * Object.CalendarService includes group of REST calls to retrieve and save Calendar info;
 */
angular.module('services').factory('Object.CalendarService', ['$resource', 'UtilService','StoreService',
    function ($resource, Util, Store) {
        var Service = $resource('api/latest/plugin', {}, {
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
                url: 'api/v1/plugin/outlook/calendar' + "?folderId=:calendarFolderId",
                cache: false,
                isArray: false
            }
        });

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            CALENDAR_EVENTS: "CalendarEvents"
        };

        /**
         * @ngdoc method
         * @name queryCostsheets
         * @methodOf services:Object.CostService
         *
         * @description
         * Query cost sheets for an object.
         *
         * @param {String} objectType  Object type
         * @param {Number} objectId  Object ID
         *
         * @returns {Object} Promise
         */
        Service.queryCalendarEvents = function (calendarFolderId) {
            var cacheCalendarEvents = new Store.CacheFifo(Service.CacheNames.CALENDAR_EVENTS);
            var cacheKey = calendarFolderId;
            var calendarEvents = cacheCalendarEvents.get(cacheKey);

            return Util.serviceCall({
                service: Service._getCalendarEvents
                , param: {
                    calendarFolderId: calendarFolderId
                }
                , result: calendarEvents
                , onSuccess: function (data) {
                    if (Service.validateCalendarEvents(data)) {
                        calendarEvents = data;
                        cacheCalendarEvents.put(cacheKey, calendarEvents);
                        return calendarEvents;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateCalendarEvents
         * @methodOf services:Object.CalendarService
         *
         * @description
         * Validate calendar events data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateCalendarEvents= function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isNotArray(data.items)) {
                return false;
            }
            if (Acm.isEmpty(data.totalItems)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
