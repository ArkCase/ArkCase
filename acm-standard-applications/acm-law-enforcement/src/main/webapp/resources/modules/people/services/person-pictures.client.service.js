'use strict';

/**
 * @ngdoc service
 * @name services:Person.PicturesService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/people/services/person-pictures.client.service.js modules/people/services/person-pictures.client.service.js}
 *
 * Person.PicturesService provides functions for Person pictures
 */
angular.module('services').factory('Person.PicturesService', ['$resource', '$translate', 'Acm.StoreService', 'UtilService', 'Upload',
    function ($resource, $translate, Store, Util, Upload) {
        var Service = $resource('api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name save
             * @methodOf services:Person.PicturesService
             *
             * @description
             * Upload image for person data
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.personId  Person ID
             * @param {boolean} params.defaultPicture
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            save: {
                method: 'POST',
                url: 'api/latest/plugin/people/:personId/images',
                cache: false
            },

            /**
             * @ngdoc method
             * @name list
             * @methodOf services:Person.PicturesService
             *
             * @description
             * Get person data
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.personId  Person ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            list: {
                method: 'GET',
                url: 'api/latest/plugin/people/:personId/images',
                cache: false
            }

        });

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            PERSON_PICTURES: "PersonPictures"
        };

        /**
         * @ngdoc method
         * @name listPersonPictures
         * @methodOf services:Person.PicturesService
         *
         * @description
         * Query person pictures
         *
         * @param {Number} id  Person ID
         *
         * @returns {Object} Promise
         */
        Service.listPersonPictures = function (id) {
            return Util.serviceCall({
                service: Service.list
                , param: {personId: id}
                , onSuccess: function (data) {
                    return data;
                }
            });
        };

        /**
         * @ngdoc method
         * @name savePersonPicture
         * @methodOf services:Person.PicturesService
         *
         * @description
         * Save person data
         *
         * @param {Long} personId  person id
         * @param {Object} file  file data
         * @param {boolean} isDefault  if is default picture or not
         *
         * @returns {Object} Promise
         */
        Service.savePersonPicture = function (personId, file, isDefault) {
            return Upload.upload({
                url: 'api/latest/plugin/people/' + personId + '/images',
                fields: {
                    default: isDefault
                },
                file: file
            });
        };

        return Service;
    }
]);
