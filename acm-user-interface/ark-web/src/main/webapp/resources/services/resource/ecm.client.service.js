'use strict';

angular.module('services').factory('EcmService', ['$resource',
    function ($resource) {
        return $resource('proxy/arkcase/api/latest/service', {}, {
            retrieveFolderList: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/service/ecm/folder/:objType/:objId/:folderId?start=:start&n=:n&s=:sortBy&dir=:sortDir',
                cache: false,
                isArray: false
            }
            , createFolder: {
                method: 'PUT',
                url: 'proxy/arkcase/api/latest/service/ecm/folder/:parentId/:folderName'
            }
            , deleteFolder: {
                method: 'DELETE',
                url: 'proxy/arkcase/api/latest/service/ecm/folder/:folderId'
            }
            , uploadFiles: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/service/ecm/upload',
                headers: {'Content-Type': undefined},
                isArray: true
            }
            , replaceFile: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/service/ecm/replace/:fileId'
            }
            , deleteFile: {
                method: 'DELETE',
                url: 'proxy/arkcase/api/latest/service/ecm/id/:fileId'
            }
            , renameFolder: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/service/ecm/folder/:folderId/:folderName'
            }
            , renameFile: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/service/ecm/file/:fileId/:name/:ext'
            }
            , moveFile: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/service/ecm/moveToAnotherContainer/:objType/:objId'
            }
            , copyFile: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/service/ecm/copyToAnotherContainer/:objType/:objId'
            }
            , moveFolder: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/service/ecm/folder/move/:subFolderId/:folderId'
            }
            , copyFolder: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/service/ecm/folder/copy/:subFolderId/:folderId/:objType/:objId'
            }
            , setActiveVersion: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/service/ecm/file/:fileId/:version'
            }
            , sendEmail: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/service/notification/email'
            }
            , sendEmailWithAttachments: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/outlook/email/withattachments'
            }

        });
    }
]);
