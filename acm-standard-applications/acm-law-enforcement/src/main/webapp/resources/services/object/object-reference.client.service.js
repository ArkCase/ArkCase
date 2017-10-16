'use strict'

angular.module('services').factory('Object.ReferenceService', ['$resource', 'UtilService',
    function ($resource, UtilService) {
        var Service = $resource('api/latest/service', {}, {

            _addReference: {
                method: 'POST',
                url: 'api/latest/service/objectassociation/reference',
                cache: false
            },

            _addReferenceToTask: {
                method: 'POST',
                url: 'api/latest/plugin/task/saveReference',
                cache: false
            }
        });

        Service.addReference = function (reference) {
            return UtilService.serviceCall({
                service: Service._addReference,
                data: reference,
                onSuccess: function (data) {
                    return data;
                }
            });
        };

        Service.addReferenceToTask = function (reference) {
            return UtilService.serviceCall({
                service: Service._addReferenceToTask,
                data: reference,
                onSuccess: function (data) {
                    return data;
                }
            });
        };

        return Service;
    }
]);
