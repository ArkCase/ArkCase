'use strict';
angular.module('profile').service('userPicService', function ($http, $q ,Upload) {
    return({
        changePic: changePic
    });
    function changePic(file,userID) {
                return Upload.upload({
                    url: 'proxy/arkcase/api/latest/service/ecm/upload',
                    fields: {parentObjectId: userID,
                             parentObjectType:'USER_ORG',
                             fileType:'user_profile'},
                    file: file
                });
    };
});