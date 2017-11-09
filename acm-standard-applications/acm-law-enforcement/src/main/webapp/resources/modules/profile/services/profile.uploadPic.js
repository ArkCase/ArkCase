'use strict';
angular.module('profile').service('Profile.ProfilePictureService', function ($http, $q ,Upload) {
    return({
        changePic: changePic,
        changeSignature: changeSignature
    });

    function changePic(file, userID) {
        return Upload.upload({
            url: 'api/latest/service/ecm/upload',
            fields: {
                parentObjectId: userID,
                parentObjectType: 'USER_ORG',
                fileType: 'user_profile'
            },
            file: file
        });
    };

    function changeSignature(file, userID) {
        return Upload.upload({
            url: 'api/latest/service/ecm/upload',
            fields: {
                parentObjectId: userID,
                parentObjectType: 'USER_ORG',
                fileType: 'user_signature'
            },
            file: file
        });
    };
});
