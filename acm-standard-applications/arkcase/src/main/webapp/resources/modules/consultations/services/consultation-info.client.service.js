'use strict';

/**
 * @ngdoc service
 * @name services:Consultation.InfoService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/consultations/services/consultation-info.client.service.js modules/consultations/services/consultation-info.client.service.js}
 *
 * Consultation.InfoService provides functions for Consultation database data
 */
angular.module('services').factory('Consultation.InfoService', [ '$resource', '$translate', 'UtilService', 'CacheFactory', 'ObjectService', 'Acm.StoreService', '$http', function($resource, $translate, Util, CacheFactory, ObjectService, Store, $http) {

    var consultationCache = CacheFactory(ObjectService.ObjectTypes.CONSULTATION, {
        maxAge: 1 * 60 * 1000, // Items added to this cache expire after 1 minute
        cacheFlushInterval: 60 * 60 * 1000, // This cache will clear itself every hour
        deleteOnExpire: 'aggressive', // Items will be deleted from this cache when they expire
        capacity: 1
    });
    var consultationGetUrl = 'api/latest/plugin/consultation/byId/';
    var consultationUrl = 'api/latest/plugin/consultation/';

    var Service = $resource('api/latest/plugin', {}, {
        /**
         * @ngdoc method
         * @name save
         * @methodOf services:Consultation.InfoService
         *
         * @description
         * Save consultation data
         *
         * @param {Object} params Map of input parameter.
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Consultation returned by $resource
         */
        save: {
            method: 'POST',
            url: 'api/latest/plugin/consultation'
        },

        /**
         * @ngdoc method
         * @name get
         * @methodOf services:Consultation.InfoService
         *
         * @description
         * Query consultation data from database.
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.id  Object ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        get: {
            method: 'GET',
            url: consultationGetUrl + ':id',
            cache: consultationCache,
            isArray: false
        },

        /**
         * @ngdoc method
         * @name get
         * @methodOf services:Consultation.InfoService
         *
         * @description
         * Query consultation data from database.
         *
         * @param {String} consultationNumber  consultationNumber
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        getByNumber: {
            method: 'GET',
            url: consultationUrl + "bynumber",
            cache: false,
            isArray: false
        },

        /**
         * @ngdoc method
         * @name post
         * @methodOf services:Consultation.InfoService
         *
         * @description
         * Change consultation state.
         *
         * @param {Object} params Map of input parameter.
         * @param {Object} params.mode  Object mode
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        changeState: {
            method: 'POST',
            url: consultationUrl + "change/status/" + ':mode',
            cache: false
        },
        /**
         * @ngdoc method
         * @name _queryConsultationTasks
         * @methodOf services:Object.TaskService
         *
         * @description
         * Query child tasks for an object.
         *
         * @param {Object} Object of input parameter
         * @returns {Object} Object returned by $resource
         */
        _queryConsultationTasks: {
            method: 'POST',
            url: consultationUrl + ':consultationId/tasks',
            cache: false
        }
    });

    Service.CacheNames = {
        CHILD_TASK_DATA: "ChildTaskData",
    };

    /**
     * @ngdoc method
     * @name resetConsultationInfo
     * @methodOf services:Consultation.InfoService
     *
     * @description
     * Reset consultation info
     *
     * @returns None
     */
    Service.resetConsultationInfo = function(consultationInfo) {
        if (consultationInfo && consultationInfo.id) {
            consultationCache.remove(consultationGetUrl + consultationInfo.id);
        }
    };

    /**
     * @ngdoc method
     * @name getConsultationInfo
     * @methodOf services:Consultation.InfoService
     *
     * @description
     * Query consultation data
     *
     * @param {Number} id  Consultation ID
     *
     * @returns {Object} Promise
     */
    Service.getConsultationInfo = function(id) {
        return Util.serviceCall({
            service: Service.get,
            param: {
                id: id
            },
            onSuccess: function(data) {
                if (Service.validateConsultationInfo(data)) {
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name getConsultationInfoByNumber
     * @methodOf services:Consultation.InfoService
     *
     * @description
     * Query consultation data by number
     *
     * @param {String} consultationNumber consultationNumber
     *
     * @returns {Object} Promise
     */
    Service.getConsultationInfoByNumber = function(consultationNumber) {
        return Util.serviceCall({
            service: Service.getByNumber,
            param: {
                consultationNumber: consultationNumber
            },
            onSuccess: function(data) {
                if (Service.validateConsultationInfo(data)) {
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name saveConsultationInfo
     * @methodOf services:Consultation.InfoService
     *
     * @description
     * Save consultation data
     *
     * @param {Object} consultationInfo  Consultation data
     *
     * @returns {Object} Promise
     */
    Service.saveConsultationInfo = function(consultationInfo) {
        if (!Service.validateConsultationInfo(consultationInfo)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }
        return Util.serviceCall({
            service: Service.save,
            data: JSOG.encode(consultationInfo),
            headers: {
                'Content-Type': 'application/json'
            },
            onSuccess: function(data) {
                if (Service.validateConsultationInfo(data)) {
                    var consultationInfo = data;
                    if (consultationInfo.id) {
                        consultationCache.put(consultationGetUrl + consultationInfo.id, consultationInfo);
                    }
                    return consultationCache;
                }
            }
        })
    };

    /**
     * @ngdoc method
     * @name saveConsultationWithFiles
     * @methodOf services:Consultation.InfoService
     *
     * @description
     * Save consultation data with files
     *
     * @param {Object} consultationInfo  Consultation data
     *
     * @returns {Object} Promise
     */
    Service.saveConsultationWithFiles = function(formData) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/consultation',
            data: formData,
            headers: {
                'Content-Type': undefined
            }
        })
    };

    /**
     * @ngdoc method
     * @name changeConsultationState
     * @methodOf services:Consultation.InfoService
     *
     * @description
     * Change/Save consultation state
     *
     * @param {String} mode mode
     *
     * @data {Object} data ChangeConsultationStatus
     *
     * @returns {Object} Promise
     */
    Service.changeConsultationState = function (mode, data) {
        return Util.serviceCall({
            service: Service.changeState,
            param: {
                mode: mode
            },
            data: data,
            onSuccess: function(data) {
                return data;
            }
        });
    };
    


    /**
     * @ngdoc method
     * @name queryConsultationTasks
     * @methodOf services:Object.TaskService
     *
     * @description
     * Query child tasks for an object.
     *
     * @param {Number} consultationId  Consultation ID
     * @param {Object} childDocumentSearch document for which is needed to search child tasks
     * @param {String} sortBy  (Optional)Sort property
     * @param {String} sortDir  (Optional)Sort direction. Value can be 'asc' or 'desc'
     *
     * @returns {Object} Promise
     */
    Service.queryConsultationTasks = function(consultationId, childDocumentSearch, sortBy, sortDir) {
        var cacheChildTaskData = new Store.CacheFifo(Service.CacheNames.CHILD_TASK_DATA);
        var cacheKey = childDocumentSearch.parentType + "." + childDocumentSearch.parentId + "." + childDocumentSearch.startRow + "." + childDocumentSearch.maxRows + "." + sortBy + "." + sortDir;
        var taskData = cacheChildTaskData.get(cacheKey);

        if (!Util.isEmpty(sortBy)) {
            childDocumentSearch.sort = sortBy + " " + Util.goodValue(sortDir, "asc");
        }
        else {
            childDocumentSearch.sort = "";
        }

        return Util.serviceCall({
            service: Service._queryConsultationTasks,
            param: {
                consultationId: consultationId
            },
            data: childDocumentSearch,
            result: taskData,
            onSuccess: function(data) {
                if (Service.validateConsultationInfoNewConsultation(data)) {
                    taskData = data;
                    cacheChildTaskData.put(cacheKey, taskData);
                    return taskData;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name validateConsultationInfo
     * @methodOf services:Consultation.InfoService
     *
     * @description
     * Validate consultation data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateConsultationInfo = function (data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (0 >= Util.goodValue(data.id, 0)) {
            return false;
        }
        if (Util.isEmpty(data.consultationNumber)) {
            return false;
        }
        if (!Util.isArray(data.childObjects)) {
            return false;
        }
        if (!Util.isArray(data.participants)) {
            return false;
        }
        if (!Util.isArray(data.personAssociations)) {
            return false;
        }
        if (!Util.isArray(data.references)) {
            return false;
        }
        return true;
    };

    /**
     * @ngdoc method
     * @name validateConsultationInfoNewConsultation
     * @methodOf services:Consultation.InfoService
     *
     * @description
     * Validate consultation data when creating new Consultation
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateConsultationInfoNewConsultation = function (data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (data.id) {
            return false;
        }
        if (data.participants && !Util.isArray(data.participants)) {
            return false;
        }
        if (data.personAssociations && !Util.isArray(data.personAssociations)) {
            return false;
        }
        return true;
    };

    return Service;
} ]);