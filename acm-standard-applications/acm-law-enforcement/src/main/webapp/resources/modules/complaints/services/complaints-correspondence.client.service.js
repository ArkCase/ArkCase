'use strict';

angular.module('services').factory('ComplaintCorrespondence.MultiTemplate', ['$http', function($http) {

    var _generateMultiTemplateCorrespondence = function(templates, parentObjectType, parentObjectId, folderId) {
        return $http({
            method: "POST",
            url: "api/latest/service/correspondence/multitemplate",
            data: templates,
            params: {
                parentObjectType: parentObjectType,
                parentObjectId: parentObjectId,
                folderId: folderId
            },
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    return {
        generateMultiTemplateCorrespondence: _generateMultiTemplateCorrespondence
    }
}]);
