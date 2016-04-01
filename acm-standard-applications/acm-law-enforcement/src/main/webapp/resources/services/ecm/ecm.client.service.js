'use strict';

/**
 * @ngdoc service
 * @name services:Ecm.EcmService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/ecm/ecm.client.service.js services/ecm/ecm.client.service.js}

 * EcmService contains functions to related to document management.
 */
angular.module('services').factory('EcmService', ['$resource', 'Acm.StoreService', 'UtilService'
    , function ($resource, StoreService, Util) {

        var Service = $resource('api/latest/service', {}, {
            retrieveFolderList: {
                method: 'GET',
                url: 'api/latest/service/ecm/folder/:objType/:objId/:folderId?start=:start&n=:n&s=:sortBy&dir=:sortDir',
                cache: false,
                isArray: false
            }
            , createFolder: {
                method: 'PUT',
                url: 'api/latest/service/ecm/folder/:parentId/:folderName'
            }
            , deleteFolder: {
                method: 'DELETE',
                url: 'api/latest/service/ecm/folder/:folderId'
            }
            , uploadFiles: {
                method: 'POST',
                url: 'api/latest/service/ecm/upload',
                headers: {'Content-Type': undefined},
                isArray: true
            }
            , replaceFile: {
                method: 'POST',
                url: 'api/latest/service/ecm/replace/:fileId',
                headers: {'Content-Type': undefined}
            }
            , deleteFile: {
                method: 'DELETE',
                url: 'api/latest/service/ecm/id/:fileId'
            }
            , renameFolder: {
                method: 'POST',
                url: 'api/latest/service/ecm/folder/:folderId/:folderName'
            }
            , renameFile: {
                method: 'POST',
                url: 'api/latest/service/ecm/file/:fileId/rename?newName=:name'
            }
            , moveFile: {
                method: 'POST',
                url: 'api/latest/service/ecm/moveToAnotherContainer/:objType/:objId'
            }
            , copyFile: {
                method: 'POST',
                url: 'api/latest/service/ecm/copyToAnotherContainer/:objType/:objId'
            }
            , moveFolder: {
                method: 'POST',
                url: 'api/latest/service/ecm/folder/move/:subFolderId/:folderId'
            }
            , copyFolder: {
                method: 'POST',
                url: 'api/latest/service/ecm/folder/copy/:subFolderId/:folderId/:objType/:objId'
            }
            , setActiveVersion: {
                method: 'POST',
                url: 'api/latest/service/ecm/file/:fileId?versionTag=:version'
            }
            //, sendEmail: {
            //    method: 'POST',
            //    url: 'api/latest/service/notification/email'
            //}
            //, sendEmailWithAttachments: {
            //    method: 'POST',
            //    url: 'api/latest/plugin/outlook/email/withattachments'
            //}
            , getFile: {
                method: 'GET',
                url: 'api/latest/service/ecm/file/:fileId'
            }
            , getFileEvents: {
                method: 'GET',
                url: 'api/latest/plugin/audit/FILE/:fileId'
            }
            , getFileNotes: {
                method: 'GET',
                url: 'api/latest/plugin/note/file/:fileId',
                isArray: true
            }
            , getFileParticipants: {
                method: 'GET',
                url: 'api/v1/service/participant/FILE/:fileId',
                isArray: true
            }
            , getFolderDocumentCounts: {
                method: 'GET',
                url: 'api/latest/service/ecm/folder/counts/:objType/:objId/',
                cache: false,
                isArray: false
            }
            , _lockFile: {
                method: 'POST',
                url: 'api/latest/service/ecm/file/lock/:fileId'
            }
            , _unlockFile: {
                method: 'POST',
                url: 'api/latest/service/ecm/file/unlock/:fileId'
            }
        });

        Service.lockFile = function (fileId) {
            var failed = "";
            return Util.serviceCall({
                service: Service._lockFile
                , param: {
                    fileId: fileId
                }
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateLockFile(data)) {
                        return data;
                    }
                }
                , onInvalid: function (data) {
                    return failed;
                }
            });
        };

        Service.unlockFile = function (fileId) {
            var failed = "";
            return Util.serviceCall({
                service: Service._unlockFile
                , param: {
                    fileId: fileId
                }
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateUnlockFile(data)) {
                        return data;
                    }
                }
                , onInvalid: function (data) {
                    return failed;
                }
            });
        };

        Service.validateLockFile = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.fileId)) {
                return false;
            }
            return true;
        };

        Service.validateUnlockFile = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.fileId)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);