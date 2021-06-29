'use strict';

/**
 * @ngdoc service
 * @name services:Person.InfoService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/people/services/person-info.client.service.js modules/people/services/person-info.client.service.js}
 *
 * Person.InfoService provides functions for Person database data
 */
angular.module('services').factory('Person.InfoService', [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', '$http', '$q', 'MessageService', 'CacheFactory', 'ObjectService', function($resource, $translate, Store, Util, $http, $q, MessageService, CacheFactory, ObjectService) {

    var personCache = CacheFactory(ObjectService.ObjectTypes.PERSON, {
        maxAge: 1 * 60 * 1000, // Items added to this cache expire after 1 minute
        cacheFlushInterval: 60 * 60 * 1000, // This cache will clear itself every hour
        deleteOnExpire: 'aggressive', // Items will be deleted from this cache when they expire
        capacity: 1
    });
    var peopleBaseUrl = "api/latest/plugin/people/";
    var Service = $resource('api/latest/plugin', {}, {
        /**
         * @ngdoc method
         * @name save
         * @methodOf services:Person.InfoService
         *
         * @description
         * Save person data
         *
         * @param {Object} params Map of input parameter.
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        save: {
            method: 'POST',
            url: 'api/latest/plugin/people',
            transformRequest: function(data, headersGetter) {
                var contentType = headersGetter()['content-type'] || '';
                if (data && contentType.indexOf('application/json') > -1) {
                    //we need angular.copy just to remove angular specific fields
                    var encodedPerson = JSOG.encode(data);
                    return angular.toJson(Util.omitNg(encodedPerson));
                }
                return data;
            }
        },

        /**
         * @ngdoc method
         * @name get
         * @methodOf services:Person.InfoService
         *
         * @description
         * Get person data
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.id  Person ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        get: {
            method: 'GET',
            url: peopleBaseUrl + ':id',
            cache: personCache,
            isArray: false
        }

    });

    /**
     * @ngdoc method
     * @name resetPersonInfo
     * @methodOf services:Person.InfoService
     *
     * @description
     * Reset Person info
     *
     * @returns None
     */
    Service.resetPersonInfo = function(personInfo) {
        if (personInfo && personInfo.id) {
            personCache.remove(peopleBaseUrl + personInfo.id);
        }
    };

    /**
     * @ngdoc method
     * @name updatePersonInfo
     * @methodOf services:Person.InfoService
     *
     * @description
     * Update person data in local cache. No REST call to backend.
     *
     * @param {Object} personInfo  Person data
     *
     * @returns {Object} Promise
     */
    Service.updatePersonInfo = function(personInfo) {
        //TODO remove this method
    };

    /**
     * @ngdoc method
     * @name getPersonInfo
     * @methodOf services:Person.InfoService
     *
     * @description
     * Query person data
     *
     * @param {Number} id  Person ID
     *
     * @returns {Object} Promise
     */
    Service.getPersonInfo = function(id) {
        return Util.serviceCall({
            service: Service.get,
            param: {
                id: id
            },
            onSuccess: function (data) {
                if (Service.validatePersonInfo(data)) {
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name getPersons
     * @methodOf services:Person.InfoService
     *
     * @description
     * Query all person data
     *
     * @returns {Object} Promise
     */
    Service.getPersons = function () {
        return $http({
            method: 'GET',
            url: peopleBaseUrl,
            params: {
                n: 10000
            },
            cache: false,
            isArray: true
        });
    };

    /**
     * @ngdoc method
     * @name queryByEmail
     * @methodOf services:Person.InfoService
     *
     * @description
     * Query person data
     *
     * @param {String} email  Person email
     *
     * @returns {Object} Promise
     */
    Service.queryByEmail = function (email) {
        return $http({
            method: 'GET',
            url: peopleBaseUrl + 'queryByEmail',
            params: {
                emailAddress: email
            },
            cache: false,
            isArray: false,
            transformResponse: Util.transformSearchResponse
        });
    };

    /**
     * @ngdoc method
     * @name savePersonInfo
     * @methodOf services:Person.InfoService
     *
     * @description
     * Save person data
     *
     * @param {Object} personInfo  Person data
     *
     * @returns {Object} Promise
     */
    Service.savePersonInfo = function(personInfo) {
        if (!Service.validatePersonInfo(personInfo)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }
        //we need to make one of the fields is changed in order to be sure that update will be executed
        //if we change modified won't make any differences since is updated before update to database
        //but update will be trigger
        personInfo.modified = null;
        return Util.serviceCall({
            service: Service.save,
            param: {},
            data: personInfo,
            onSuccess: function(data) {
                if (Service.validatePersonInfo(data)) {
                    if (data.id) {
                        personCache.put(peopleBaseUrl + data.id, data);
                    }
                    return data;
                }
            },
            onError: function(error) {
                MessageService.error(error.data);
                return error;
            }
        });
    };

    /**
     * @ngdoc method
     * @name savePersonInfoWithPictures
     * @methodOf services:Person.InfoService
     *
     * @description
     * Save person data with pictures
     *
     * @param {Object} personInfo  Person data
     * @param {Array} pictures  Images array
     *
     * @returns {Object} Promise
     */
    Service.savePersonInfoWithPictures = function(personInfo, images) {
        if (!Service.validatePersonInfo(personInfo)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }

        var formData = new FormData();

        // First part: application/json
        // The browser will not set the content-type of the json object automatically,
        // so we need to set it manualy. The only way to do that is to convert the data to Blob.
        // In that way we can set the desired content-type.

        var data = new Blob([ angular.toJson(JSOG.encode(Util.omitNg(personInfo))) ], {
            type: 'application/json'
        });
        formData.append('person', data);

        // Second part: file type
        // The browser will automatically set the content-type for the files
        if (images) {
            for (var i = 0; i < images.length; i++) {
                //add each file to the form data
                formData.append('pictures', images[i]);
            }
        }

        // when we are sending data the request
        // needs to include a 'boundary' parameter which identifies the boundary
        // name between parts in this multi-part request and setting the Content-type of the Request Header
        // manually will not set this boundary parameter. Setting the Content-type to
        // undefined will force the request to automatically
        // populate the headers properly including the boundary parameter.

        var deferred = $q.defer();

        var savePersonInfoPromise = $http({
            method: 'POST',
            url: 'api/latest/plugin/people',
            data: formData,
            headers: {
                'Content-Type': undefined
            }
        });

        savePersonInfoPromise.then(function(payload) {
            if (Service.validatePersonInfo(payload.data)) {
                personCache.put(peopleBaseUrl + data.id, data);
            }
            deferred.resolve(payload);
        }, function(payload) {
            deferred.reject(payload);
        }, function(payload) {
            deferred.notify(payload);
        });

        return deferred.promise;
    };

    /**
     * @ngdoc method
     * @name validatePersonInfo
     * @methodOf services:Person.InfoService
     *
     * @description
     * Validate person data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validatePersonInfo = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (data.id && !Util.isArray(data.participants)) {
            return false;
        }
        return true;
    };

    return Service;
} ]);
