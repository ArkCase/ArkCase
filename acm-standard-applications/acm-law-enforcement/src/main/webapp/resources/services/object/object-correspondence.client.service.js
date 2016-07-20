'use strict';

/**
 * @ngdoc service
 * @name services:Object.CorrespondenceService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-correspondence.client.service.js services/object/object-correspondence.client.service.js}

 * Object.CorrespondenceService includes group of REST calls related to correspondence.
 */
angular.module('services').factory('Object.CorrespondenceService', ['$resource', 'Acm.StoreService', 'UtilService',
    function ($resource, Store, Util) {
        var Service = $resource('api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name _queryCorrespondences
             * @methodOf services:Object.CorrespondenceService
             *
             * @description
             * Query list of correspondences for an object.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.parentType  Object type
             * @param {String} params.parentId  Object ID
             * @param {Number} params.start Zero based start number of record
             * @param {Number} params.n Max Number of list to return
             * @param {String} params.sort  Sort value, with format 'sortBy sortDir', sortDir can be 'asc' or 'desc'
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _queryCorrespondences: {
                method: 'GET',
                url: 'api/latest/service/ecm/bycategory/:parentType/:parentId?category=Correspondence&start=:start&n=:n&s=:sort',
                cache: false
            }

            /**
             * @ngdoc method
             * @name _createCorrespondence
             * @methodOf services:Object.CorrespondenceService
             *
             * @description
             * Create a new correspondence
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.template  Correspondence template
             * @param {String} params.parentType  Object type
             * @param {Number} params.parentId  Object ID
             * @param {String} params.folderId  Folder ID
             * @param {Object} data Empty object {}
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , _createCorrespondence: {
                method: 'POST',
                url: 'api/latest/service/correspondence?templateName=:template&parentObjectType=:parentType&parentObjectId=:parentId&folderId=:folderId',
                cache: false
            }

        });


        Service.SessionCacheNames = {};
        Service.CacheNames = {
            CORRESPONDENCES: "Correspondences"
        };

        /**
         * @ngdoc method
         * @name queryCorrespondences
         * @methodOf services:Object.CorrespondenceService
         *
         * @description
         * Query list of correspondences of an object
         *
         * @param {String} parentType  Object type
         * @param {String} parentId  Object ID
         * @param {Number} start Zero based start number of record
         * @param {Number} n Max Number of list to return
         * @param {String} sortBy  (Optional)Sort property
         * @param {String} sortDir  (Optional)Sort direction. Value can be 'asc' or 'desc'
         *
         * @returns {Object} Promise
         */
        Service.queryCorrespondences = function (parentType, parentId, start, n, sortBy, sortDir) {
            var cacheCorrespondences = new Store.CacheFifo(Service.CacheNames.CORRESPONDENCES);
            var cacheKey = parentType + "." + parentId + "." + start + "." + n + "." + sortBy + "." + sortDir;
            var correspondences = cacheCorrespondences.get(cacheKey);

            var sort = "";
            if (!Util.isEmpty(sortBy)) {
                sort = sortBy + " " + Util.goodValue(sortDir, "asc");
            }

            return Util.serviceCall({
                service: Service._queryCorrespondences
                , param: {
                    parentType: parentType
                    , parentId: parentId
                    , start: start
                    , n: n
                    , sort: sort
                }
                , result: correspondences
                , onSuccess: function (data) {
                    if (Service.validateCorrespondences(data)) {
                        correspondences = data;
                        cacheCorrespondences.put(cacheKey, correspondences);
                        return correspondences;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name createCorrespondence
         * @methodOf services:Object.CorrespondenceService
         *
         * @description
         * Create new correspondence
         *
         * @param {String} params.template  Correspondence template
         * @param {String} params.parentType  Object type
         * @param {Number} params.parentId  Object ID
         * @param {String} params.folderId  Selected folder ID
         *
         * @returns {Object} Promise
         */
        Service.createCorrespondence = function (template, objectType, objectId, folderId) {
            return Util.serviceCall({
                service: Service._createCorrespondence
                , param: {
                    template: template
                    , parentType: objectType
                    , parentId: objectId
                    , folderId: folderId
                }
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateNewCorrespondence(data)) {
                        //add new correspondence to cache
                        return data;
                    }
                }
            });
        };


        /**
         * @ngdoc method
         * @name validateCorrespondences
         * @methodOf services:Object.CorrespondenceService
         *
         * @description
         * Validate correspondences
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateCorrespondences = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.containerObjectId)) {
                return false;
            }
            if (Util.isEmpty(data.folderId)) {
                return false;
            }
            if (Util.isEmpty(data.totalChildren)) {
                return false;
            }
            if (!Util.isArray(data.children)) {
                return false;
            }
            for (var i = 0; i < data.children.length; i++) {
                if (!this.validateCorrespondence(data.children[i])) {
                    return false;
                }
            }
            if ("Correspondence" != data.category) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateCorrespondence
         * @methodOf services:Object.CorrespondenceService
         *
         * @description
         * Validate correspondence data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateCorrespondence = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.objectId)) {
                return false;
            }
            if (Util.isEmpty(data.name)) {
                return false;
            }
            if (Util.isEmpty(data.created)) {
                return false;
            }
            if (Util.isEmpty(data.creator)) {
                return false;
            }
            if ("file" != data.objectType) {
                return false;
            }
            if ("Correspondence" != data.category) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateNewCorrespondence
         * @methodOf services:Object.CorrespondenceService
         *
         * @description
         * Validate new correspondence data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateNewCorrespondence = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.category)) {
                return false;
            }
            if ("Correspondence" != data.category) {
                return false;
            }
            if (Util.isEmpty(data.created)) {
                return false;
            }
            if (Util.isEmpty(data.creator)) {
                return false;
            }
            if (Util.isEmpty(data.fileId)) {
                return false;
            }
            if (Util.isEmpty(data.fileName)) {
                return false;
            }
            if (Util.isEmpty(data.fileType)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
