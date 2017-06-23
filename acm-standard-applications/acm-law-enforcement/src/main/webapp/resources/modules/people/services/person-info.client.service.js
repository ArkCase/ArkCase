'use strict';

/**
 * @ngdoc service
 * @name services:Person.InfoService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/people/services/person-info.client.service.js modules/people/services/person-info.client.service.js}
 *
 * Person.InfoService provides functions for Person database data
 */
angular.module('services').factory('Person.InfoService', ['$resource', '$translate', 'Acm.StoreService', 'UtilService', '$http',
    function ($resource, $translate, Store, Util, $http) {
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
                transformRequest: function (data, headersGetter) {
                    var contentType = headersGetter()['content-type'] || '';
                    if (data && contentType.indexOf('application/json') > -1) {
                        //we need angular.copy just to remove angular specific fields
                        var encodedPerson = JSOG.encode(data);
                        return angular.toJson(Util.omitNg(encodedPerson));
                    }
                    return data;
                },
                cache: false
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
                url: 'api/latest/plugin/people/:id',
                cache: false,
                isArray: false
            }

        });

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            PERSON_INFO: "PersonInfo"
        };

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
        Service.resetPersonInfo = function () {
            var cacheInfo = new Store.CacheFifo(Service.CacheNames.PERSON_INFO);
            cacheInfo.reset();
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
        Service.updatePersonInfo = function (personInfo) {
            if (Service.validatePersonInfo(personInfo)) {
                var cachePersonInfo = new Store.CacheFifo(Service.CacheNames.PERSON_INFO);
                cachePersonInfo.put(personInfo.id, personInfo);
            }
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
        Service.getPersonInfo = function (id) {
            var cachePersonInfo = new Store.CacheFifo(Service.CacheNames.PERSON_INFO);
            var personInfo = cachePersonInfo.get(id);
            return Util.serviceCall({
                service: Service.get
                , param: {id: id}
                , result: personInfo
                , onSuccess: function (data) {
                    if (Service.validatePersonInfo(data)) {
                        cachePersonInfo.put(id, data);
                        return data;
                    }
                }
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
        Service.savePersonInfo = function (personInfo) {
            if (!Service.validatePersonInfo(personInfo)) {
                return Util.errorPromise($translate.instant("common.service.error.invalidData"));
            }
            //we need to make one of the fields is changed in order to be sure that update will be executed
            //if we change modified won't make any differences since is updated before update to database
            //but update will be trigger
            personInfo.modified = null;
            return Util.serviceCall({
                service: Service.save
                , param: {}
                , data: personInfo
                , onSuccess: function (data) {
                    if (Service.validatePersonInfo(data)) {
                        var personInfo = data;
                        var cachePersonInfo = new Store.CacheFifo(Service.CacheNames.PERSON_INFO);
                        cachePersonInfo.put(personInfo.id, personInfo);
                        return personInfo;
                    }
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
        Service.savePersonInfoWithPictures = function (personInfo, images) {
            if (!Service.validatePersonInfo(personInfo)) {
                return Util.errorPromise($translate.instant("common.service.error.invalidData"));
            }

            var formData = new FormData();

            // First part: application/json
            // The browser will not set the content-type of the json object automatically,
            // so we need to set it manualy. The only way to do that is to convert the data to Blob.
            // In that way we can set the desired content-type.

            var data = new Blob([angular.toJson(JSOG.encode(personInfo))], {
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
            return $http({
                method: 'POST',
                url: 'api/latest/plugin/people',
                data: formData,
                headers: {'Content-Type': undefined}
            });
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
        Service.validatePersonInfo = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
