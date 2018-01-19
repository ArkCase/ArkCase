'use strict';

/**
 * @ngdoc service
 * @name services:Organization.InfoService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/organizations/services/organization-info.client.service.js modules/organizations/services/organization-info.client.service.js}
 *
 * Organization.InfoService provides functions for Organization database data
 */
angular.module('services').factory(
        'Organization.InfoService',
        [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', 'CacheFactory',
                function($resource, $translate, Store, Util, CacheFactory) {
                    var organizationCache = CacheFactory('organizationCache', {
                        maxAge : 1 * 60 * 1000, // Items added to this cache expire after 1 minute
                        cacheFlushInterval : 60 * 60 * 1000, // This cache will clear itself every hour
                        deleteOnExpire : 'aggressive', // Items will be deleted from this cache when they expire
                        capacity : 1
                    });

                    var organizationsBaseUrl = "api/latest/plugin/organizations/";

                    var Service = $resource('api/latest/plugin', {}, {
                        /**
                         * @ngdoc method
                         * @name save
                         * @methodOf services:Organization.InfoService
                         *
                         * @description
                         * Save person data
                         *
                         * @param {Object} params Map of input parameter.
                         * @param {Number} params.id  Organization ID
                         * @param {Function} onSuccess (Optional)Callback function of success query.
                         * @param {Function} onError (Optional) Callback function when fail.
                         *
                         * @returns {Object} Object returned by $resource
                         */
                        save : {
                            method : 'POST',
                            url : 'api/latest/plugin/organizations',
                            transformRequest : function(data, headersGetter) {
                                var contentType = headersGetter()['content-type'] || '';
                                if (data && contentType.indexOf('application/json') > -1) {

                                    var encodedOrganization = JSOG.encode(data);
                                    return angular.toJson(Util.omitNg(encodedOrganization));
                                }
                                return data;
                            }
                        },

                        /**
                         * @ngdoc method
                         * @name get
                         * @methodOf services:Organization.InfoService
                         *
                         * @description
                         * Get Organization data
                         *
                         * @param {Object} params Map of input parameter.
                         * @param {Number} params.id  Person ID
                         * @param {Function} onSuccess (Optional)Callback function of success query.
                         * @param {Function} onError (Optional) Callback function when fail.
                         *
                         * @returns {Object} Object returned by $resource
                         */
                        get : {
                            method : 'GET',
                            url : 'api/latest/plugin/organizations/:id',
                            cache : organizationCache,
                            isArray : false
                        }
                    });

                    /**
                     * @ngdoc method
                     * @name resetOrganizationInfo
                     * @methodOf services:Organization.InfoService
                     *
                     * @description
                     * Reset Person info
                     *
                     * @returns None
                     */
                    Service.resetOrganizationInfo = function(organizationInfo) {
                        if (organizationInfo && organizationInfo.organizationId) {
                            organizationCache.remove(organizationsBaseUrl + organizationInfo.organizationId);
                        }
                    };

                    /**
                     * @ngdoc method
                     * @name updateOrganizationInfo
                     * @methodOf services:Organization.InfoService
                     *
                     * @description
                     * Update organization data in local cache. No REST call to backend.
                     *
                     * @param {Object} organizationInfo  Person data
                     *
                     * @returns {Object} Promise
                     */
                    Service.updateOrganizationInfo = function(organizationInfo) {
                        //TODO remove this method
                    };

                    /**
                     * @ngdoc method
                     * @name getOrganizationInfo
                     * @methodOf services:Organization.InfoService
                     *
                     * @description
                     * Query Organization data
                     *
                     * @param {Number} id  Organization ID
                     *
                     * @returns {Object} Promise
                     */
                    Service.getOrganizationInfo = function(id) {
                        return Util.serviceCall({
                            service : Service.get,
                            param : {
                                id : id
                            },
                            onSuccess : function(data) {
                                if (Service.validateOrganizationInfo(data)) {
                                    return data;
                                }
                            }
                        });
                    };

                    /**
                     * @ngdoc method
                     * @name saveOrganizationInfo
                     * @methodOf services:Organization.InfoService
                     *
                     * @description
                     * Save complaint data
                     *
                     * @param {Object} OrganizationInfo  organization data
                     *
                     * @returns {Object} Promise
                     */
                    Service.saveOrganizationInfo = function(organizationInfo) {
                        if (!Service.validateOrganizationInfo(organizationInfo)) {
                            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        }
                        //we need to make one of the fields is changed in order to be sure that update will be executed
                        //if we change modified won't make any differences since is updated before update to database
                        //but update will be trigger
                        organizationInfo.modified = null;
                        return Util.serviceCall({
                            service : Service.save,
                            param : {},
                            data : organizationInfo,
                            onSuccess : function(data) {
                                if (Service.validateOrganizationInfo(data)) {
                                    if (data.id) {
                                        organizationCache.put(organizationsBaseUrl + data.organizationId, data);
                                    }
                                    return data;
                                }
                            }
                        });
                    };

                    /**
                     * @ngdoc method
                     * @name validateOrganizationInfo
                     * @methodOf services:Organization.InfoService
                     *
                     * @description
                     * Validate Organization data
                     *
                     * @param {Object} data  Data to be validated
                     *
                     * @returns {Boolean} Return true if data is valid
                     */
                    Service.validateOrganizationInfo = function(data) {
                        if (Util.isEmpty(data)) {
                            return false;
                        }
                        if (data.organizationId && !Util.isArray(data.participants)) {
                            return false;
                        }
                        return true;
                    };

                    return Service;
                } ]);
