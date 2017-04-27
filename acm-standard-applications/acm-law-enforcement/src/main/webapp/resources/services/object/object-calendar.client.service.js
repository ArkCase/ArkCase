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
angular.module('services').factory('Object.CalendarService', ['$resource', 'UtilService', 'Acm.StoreService', 'Upload', '$q',
    function ($resource, Util, Store, Upload, $q) {
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
        Service.createNewEvent = function(eventData, files) {
            var upload = Upload.upload({
                url: 'api/upload',
                method: 'POST',
                data: angular.toJson(eventData),
                file: files
            });

            return upload;
        };

        /**
         * @ngdoc method
         * @name getCalendarEvents
         * @methodOf services:Object.CalendarService
         *
         * @description
         * Get list of calendar events in the given date range.
         *
         * @param {Date} start  start of the date range
         * @param {Date} end  end of the date range
         *
         * @returns {Object} Promise
         */
        Service.getCalendarEvents = function(start, end) {
            var deferred = $q.defer();

            var events = [
                {
                    subject: 'Test Event 1',
                    start: new Date(),
                    end: new Date(),
                    allDayEvent: true
                },
                {
                    subject: 'Test Event 2',
                    start: new Date(),
                    end: new Date(),
                    allDayEvent: false
                }
            ];
            deferred.resolve(events);
            return deferred.promise;
        };

        /**
         * @ngdoc method
         * @name getCalendarEventDetails
         * @methodOf services:Object.CalendarService
         *
         * @description
         *
         *
         * @param {Date} start
         *
         * @returns {Object} Promise
         */
        Service.getCalendarEventDetails = function(eventId) {
            var deferred = $q.defer();

            var event = [
                {
                    'subject' : 'Test Subject',
                    'location' : 'Armedia',
                    'start' : (new Date()).toISOString(),
                    'end' : (new Date()).toISOString(),
                    'allDayEvent' : false,
                    'recurrenceDetails' : {
                        dayOfWeek: "WEDNESDAY",
                        endBy: (new Date()).toISOString(),
                        interval: 1,
                        recurrenceType: "MONTHLY",
                        weekOfMonth: "FOURTH"
                    },
                    'details' : 'details',
                    'remindIn' : 30,
                    'privateEvent' : true,
                    'priority' : 'LOW',
                    'sendEmails' : false,
                    'attendees' : [
                        {
                            email: 'testemail@gmail.com',
                            type: 'REQUIRED',
                            status: 'ACCEPTED'
                        },
                        {
                            email: 'testemail2@gmail.com',
                            type: 'OPTIONAL',
                            status: 'DECLINED'
                        },
                        {
                            email: 'testemail3@gmail.com',
                            type: 'OPTIONAL',
                            status: 'TENTATIVE'
                        },
                        {
                            email: 'testemail4@gmail.com',
                            type: 'OPTIONAL',
                            status: 'NONE'
                        },
                        {
                            email: 'testemail5@gmail.com',
                            type: 'REQUIRED',
                            status: 'ORGANIZER'
                        }


                    ],
                    'files': [
                        {
                            name: 'file_name_1.jpg'
                        },
                        {
                            name: 'file_name_2.jpg'
                        },
                        {
                            name: 'file_name_3.jpg'
                        }
                    ]
                }
            ];
            deferred.resolve(event);
            return deferred.promise;
        };

        return Service;
    }
]);
