'use strict';
angular.module('profile').service('Profile.ChangePasswordService', function ($http, $q, $modal) {
    return({
        changePassword: changePassword
    });
    function changePassword(newPassword) {
        var request = $http({
            method: "POST",
            url: "api/v1/plugin/profile/savepassword",
            data: newPassword
        });
        return(request.then(handleSuccess, handleError));
    };
    function handleError(response) {
        if (
                !angular.isObject(response.data) ||
                !response.data.message
                ) {
            return($q.reject("An unknown error occurred."));
        }
        return($q.reject(response.data.message));
    }
    function handleSuccess(response) {
        $modal.open({
                    templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-successChangedPassword.client.view.html',
                    size: 'sm'
    });
        return(response.data);
    }
});