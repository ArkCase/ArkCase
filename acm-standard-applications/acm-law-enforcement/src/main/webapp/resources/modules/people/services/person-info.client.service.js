'use strict';

/**
 * @ngdoc service
 * @name services:Person.InfoService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/people/services/person-info.client.service.js modules/people/services/person-info.client.service.js}
 *
 * Person.InfoService provides functions for Person database data
 */
angular.module('services').factory('Person.InfoService', ['$resource', '$translate', 'Acm.StoreService', 'UtilService',
    function ($resource, $translate, Store, Util) {
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
                cache: false
                // ,
                // transformResponse: function (data, headers) {
                //     console.log("Before:");
                //     console.log(data);
                //     // if (data && headers['content-type'] === 'application/json') {
                //         data = JSOG.parse(data);
                //     console.log("After:");
                //     console.log(data);
                //
                //     return data;
                // }
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
