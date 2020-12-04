angular.module('services').factory('FileEditingEnabled', ['$http', function ($http) {
    return {
        getFileEditingEnabled: getFileEditingEnabled
    }


    function getFileEditingEnabled() {
        return $http({
            method: 'GET',
            url: 'api/latest/service/ecm/file/isEditingEnabled',
        });
    }
}]);