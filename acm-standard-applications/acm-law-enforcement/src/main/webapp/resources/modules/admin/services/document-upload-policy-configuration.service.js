angular.module('admin').factory('Admin.DocumentUploadPolicyService', [ '$http', function($http) {
    return {

        saveDocumentUploadPolicyConfiguration: function(config) {
            return $http({
                method: 'POST',
                url: 'api/latest/plugin/admin/documentUploadPolicy',
                data: config
            });
        },
        getDocumentUploadPolicyConfiguration: function() {
            return $http({
                method: 'GET',
                url: 'api/latest/plugin/admin/documentUploadPolicy'
            });
        }
    };
}]);
