'use strict';

/**
 * @ngdoc service
 * @name services:Ecm.EcmService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/ecm/ecm.client.service.js services/ecm/ecm.client.service.js}

 * EcmService contains functions to related to document management.
 */
angular.module('services').factory('EcmService', ['$resource', 'StoreService', 'UtilService'
    , function ($resource, StoreService, UtilService) {

        var Service = $resource('proxy/arkcase/api/latest/service', {}, {
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
                url: 'proxy/arkcase/api/latest/service/ecm/replace/:fileId',
                headers: {'Content-Type': undefined}
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
                url: 'proxy/arkcase/api/latest/service/ecm/file/:fileId?versionTag=:version'
            }
            //, sendEmail: {
            //    method: 'POST',
            //    url: 'proxy/arkcase/api/latest/service/notification/email'
            //}
            //, sendEmailWithAttachments: {
            //    method: 'POST',
            //    url: 'proxy/arkcase/api/latest/plugin/outlook/email/withattachments'
            //}
            , getFile: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/service/ecm/file/:fileId'
            }
            , getFileEvents: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/audit/FILE/:fileId'
            }
            , getFileNotes: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/note/file/:fileId',
                isArray: true
            }
            , getFileParticipants: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/service/participant/FILE/:fileId',
                isArray: true
            }
            , getFolderDocumentCounts: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/service/ecm/folder/counts/:objType/:objId/?start=:start&n=:n&s=:sortBy&dir=:sortDir',
                cache: false,
                isArray: false
            }
        });

        return Service;
    }
]);