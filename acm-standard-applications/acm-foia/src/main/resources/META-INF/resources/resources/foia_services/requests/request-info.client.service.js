'use strict';

angular.module('services').factory('Request.InfoService', [ '$resource', 'UtilService', 'Object.InfoService', 'ObjectService', '$http', function($resource, Util, ObjectInfoService, ObjectService, $http) {
    var Service = $resource('api/latest/plugin', {}, {
        _saveNewPortalUser: {
            method: 'POST',
            url: 'api/latest/service/portalgateway/:portalId/users/registrations/requester',
            isArray: false,
            cache: false
        }
    });

    Service.saveRequestInfoWithFiles = function (formData) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/foiarequest',
            data: formData,
            headers: {
                'Content-Type': undefined
            }
        })
    };

    Service.saveNewPortalUser = function (user, portalId, requestId) {
        return Util.serviceCall({
            service: Service._saveNewPortalUser,
            data: user,
            param: {
                portalId: portalId,
                requestId: requestId
            },
            headers: {
                "Content-Type": "application/json"
            }
        })
    };
    
    /**
     * @ngdoc method
     * @name saveRequestInfo
     * @methodOf services:Request.InfoService
     *
     * @description
     * Save case data
     *
     * @param {Object} requestInfo  Request data
     *
     * @returns {Object} Promise
     */
    Service.saveRequestInfo = function(requestInfo) {

        return Util.serviceCall({
            service: ObjectInfoService.save,
            param: {
                type: "casefile"
            },
            data: requestInfo,
            onSuccess: function(requestInfo) {
                return requestInfo;
            }
        });
    };

    /**
     * @ngdoc method
     * @name registerFileUpdateHandler
     * @methodOf services:Request.InfoService
     *
     * @description
     * Register a handler to be called when a file inside this request is updated
     *
     * @param {Object} scope  Angular scope
     * @param {String} parentObjectType type of the object that owns the files we care about
     * @param {Number} parentObjectId Id of the object that owns the files we care about
     * @param {function} handler  Function to be called; typically the function would refresh the doc tree so the
     * doc tree shows the new version of the file.
     */
    Service.registerFileUpdateHandler = function(scope, parentObjectType, parentObjectId, handler) {
        // in FOIA we can view many files at once, so we have to listen for the case file changed event instead of
        // a specific file id
        var caseUpdateEvent = "object.changed/" + parentObjectType + "/" + parentObjectId;

        scope.$bus.subscribe(caseUpdateEvent, function(data) {
            // first make sure the event is a file event
            if (ObjectService.ObjectTypes.FILE === data.objectType) {
                handler();
            }
        });
    };

    return Service;
} ]);
