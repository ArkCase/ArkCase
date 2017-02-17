'use strict';

/**
 * @ngdoc service
 * @name services:Object.TagsService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-tags.client.service.js services/object/object-tags.client.service.js}

 * Object.TagsService includes group of REST calls related to tags.
 */
angular.module('services').factory('Object.TagsService', ['$resource', '$translate', 'UtilService',
    function ($resource, $translate, Util) {
        var Service = $resource('api/latest/service/tag', {}, {

            /**
             * @ngdoc method
             * @name _getTags
             * @methodOf services:Object.TagsService
             *
             * @description
             * Query all created tags.
             *
             * @param {Object} params Map of input parameter
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _getTags: {
                method: 'GET',
                url: 'api/latest/service/tag',
                isArray: true
            },

            /**
             * @ngdoc method
             * @name _getAssociateTags
             * @methodOf services:Object.TagsService
             *
             * @description
             * Query all tags that are associated to object.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.objectId  Object ID
             * @param {String} params.objectType  Object type
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _getAssociateTags: {
                method: 'GET',
                url: 'api/latest/service/tag/:objectId/:objectType',
                isArray: true
            },

            /**
             * @ngdoc method
             * @name _associateTag
             * @methodOf services:Object.TagsService
             *
             * @description
             * Associate new tag to object.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.objectId  Object ID
             * @param {String} params.objectType  Object type
             * @param {String} params.tagId  Tag ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _associateTag: {
                method: 'PUT',
                url: 'api/latest/service/tag/:objectId/:objectType/:tagId'
            },

            /**
             * @ngdoc method
             * @name _createTag
             * @methodOf services:Object.TagsService
             *
             * @description
             * Create new tag.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.tagName  Tag Name
             * @param {String} params.tagDesc  Tag Description
             * @param {String} params.tagText  Tag Text
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _createTag: {
                method: 'PUT',
                url: 'api/latest/service/tag?name=:tagName&desc=:tagDesc&text=:tagText'
            },

            /**
             * @ngdoc method
             * @name _removeTag
             * @methodOf services:Object.TagsService
             *
             * @description
             * Create new tag.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.objectId  Object ID
             * @param {String} params.objectType  Object type
             * @param {String} params.tagId  Tag ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _removeTag: {
                method: 'DELETE',
                url: 'api/latest/service/tag/:objectId/:objectType/:tagId'
            }

        });

        /**
         * @ngdoc method
         * @name getTags
         * @methodOf services:Object.TagsService
         *
         * @description
         * Query list of all created tags
         *
         *
         * @returns {Object} Promise
         */
        Service.getTags  = function () {
            return Util.serviceCall({
                service: Service._getTags
                , onSuccess: function (data) {
                    if (Service.validateTags(data)) {
                        return data;
                    }
                }
            })
        };

        /**
         * @ngdoc method
         * @name getAssociateTags
         * @methodOf services:Object.TagsService
         *
         * @description
         * Query list of all associated tags for an  object.
         *
         * @param {Number} objectId  Object ID
         * @param {String} objectType  Object type
         *
         * @returns {Object} Promise
         */
        Service.getAssociateTags = function (objectId, objectType) {
            return Util.serviceCall({
                service: Service._getAssociateTags
                , param: {
                    objectId: objectId,
                    objectType: objectType
                }
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateTags(data)) {
                        return data;
                    }
                }
            })
        };

        /**
         * @ngdoc method
         * @name associateTag
         * @methodOf services:Object.TagsService
         *
         * @description
         * Associate tag for an object.
         *
         * @param {Number} objectId  Object ID
         * @param {String} objectType  Object type
         * @param {String} parentTitle  Object (which happens to the the parent) title
         * @param {Number} tagId  Tag ID
         *
         * @returns {Object} Promise
         */
        Service.associateTag = function (objectId, objectType, parentTitle, tagId) {
            console.log(parentTitle);
            return Util.serviceCall({
                service: Service._associateTag
                , param: {
                    objectId: objectId,
                    objectType: objectType,
                    parentTitle: parentTitle,
                    tagId: tagId
                }
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateNewTagAssociation(data)) {
                        return data;
                    }
                }
            })
        };

        /**
         * @ngdoc method
         * @name createTag
         * @methodOf services:Object.TagsService
         *
         * @description
         * Create new tag.
         *
         * @param {String} tagName  Tag name
         * @param {String} tagDesc  Tag description
         * @param {String} tagText  Tag text
         *
         * @returns {Object} Promise
         */
        Service.createTag = function (tagName, tagDesc, tagText) {
            return Util.serviceCall({
                service: Service._createTag
                , param: {
                    tagName: tagName,
                    tagDesc: tagDesc,
                    tagText: tagText
                }
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateTag(data)) {
                        return data;
                    }
                }
            })
        };

        /**
         * @ngdoc method
         * @name removeAssociateTag
         * @methodOf services:Object.TagsService
         *
         * @description
         * Remove tag for an object.
         *
         * @param {number} objectId  Object ID
         * @param {String} objectType  Tag description
         * @param {number} tagId  Tag ID
         *
         * @returns {Object} Promise
         */
        Service.removeAssociateTag = function (objectId, objectType, tagId) {
            return Util.serviceCall({
                service: Service._removeTag
                , param: {
                    objectId: objectId,
                    objectType: objectType,
                    tagId: tagId
                }
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateRemovedAssociatedTag(data)) {
                        return data;
                    }
                }
            })
        };

        /**
         * @ngdoc method
         * @name validateTags
         * @methodOf services:Object.TagsService
         *
         * @description
         * Validate tags.
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Object} Promise
         */
        Service.validateTags = function(data){
            if (Util.isEmpty(data)) {
                return false;
            }
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateNewTagAssociation
         * @methodOf services:Object.TagsService
         *
         * @description
         * Validate tag that will be associated.
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Object} Promise
         */
        Service.validateNewTagAssociation = function(data){
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.creator)) {
                return false;
            }
            if (Util.isEmpty(data.parentId)) {
                return false;
            }
            if (Util.isEmpty(data.parentType)) {
                return false;
            }
            if (Util.isEmpty(data.tagId)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateTag
         * @methodOf services:Object.TagsService
         *
         * @description
         * Validate tag.
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Object} Promise
         */
        Service.validateTag = function(data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (Util.isEmpty(data.tagText)) {
                return false;
            }
            if (Util.isEmpty(data.tagDescription)) {
                return false;
            }
            if (Util.isEmpty(data.tagName)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateRemovedAssociatedTag
         * @methodOf services:Object.TagsService
         *
         * @description
         * Validate tag to be removed.
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Object} Promise
         */
        Service.validateRemovedAssociatedTag = function(data){
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.deletedAssociatedTagId)) {
                return false;
            }
            if (Util.isEmpty(data.tagId)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);