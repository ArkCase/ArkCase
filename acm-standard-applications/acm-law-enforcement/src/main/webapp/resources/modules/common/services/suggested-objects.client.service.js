'use strict';

angular.module('services').factory('SuggestedObjectsService', ['$http', 'base64', function ($http, base64) {

    return ({
        getSuggestedObjects: getSuggestedObjects
    });

    function getSuggestedObjects(title, type, id) {
        return $http({
            url: 'api/latest/service/suggestion/' + base64.urlencode(title),
            method: 'GET',
            params: {
                objectId: id,
                objectType: type
            }
        });
    }

}]);
