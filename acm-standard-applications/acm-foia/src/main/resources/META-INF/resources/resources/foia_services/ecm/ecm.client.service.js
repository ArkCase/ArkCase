'use strict';

/**
 * @ngdoc service
 * @name services:Ecm.EcmService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/services/ecm/ecm.client.service.js services/ecm/ecm.client.service.js}

 * EcmService contains functions to related to document management.
 */
angular.module('services').factory('EcmService', [ '$resource', 'UtilService', function($resource, Util) {

    var Service = $resource('api/latest/service', {}, {
        retrieveFolderList: {
            method: 'GET',
            url: 'api/latest/service/ecm/folder/:objType/:objId/:folderId?start=:start&n=:n&s=:sortBy&dir=:sortDir',
            cache: false,
            isArray: false
        },
        retrieveFlatSearchResultList: {
            method: 'GET',
            url: 'api/latest/service/ecm/folder/:objType/:objId/:folderId/search?fq=:filter&start=:start&n=:n&s=:sortBy&dir=:sortDir',
            cache: false,
            isArray: false
        },
        retrieveFlatSearchResultListAdvanced: {
            method: 'GET',
            url: 'api/latest/service/ecm/folder/:objType/:objId/searchAdvanced?fq=:filter&start=:start&n=:n&s=:sortBy&dir=:sortDir',
            cache: false,
            isArray: false
        },
        createFolder: {
            method: 'PUT',
            url: 'api/latest/service/ecm/folder/:parentId/:folderName'
        },
        deleteFolder: {
            method: 'POST',
            url: 'api/latest/service/ecm/folder/deleteTemporary/:folderId'
        },
        getDeleteFolderInfo: {
            method: 'GET',
            url: 'api/latest/service/ecm/folder/:folderId/count'
        },
        uploadFiles: {
            method: 'POST',
            url: 'api/latest/service/ecm/upload',
            headers: {
                'Content-Type': undefined
            },
            isArray: true
        },
        replaceFile: {
            method: 'POST',
            url: 'api/latest/service/ecm/replace/:fileId',
            headers: {
                'Content-Type': undefined
            }
        },
        deleteFile: {
            method: 'DELETE',
            url: 'api/latest/service/ecm/id/:fileId'
        },
        deleteFileTemporary: {
            method: 'POST',
            url: 'api/latest/service/ecm/file/deleteTemporary/:fileId'
        },
        renameFolder: {
            method: 'POST',
            url: 'api/latest/service/ecm/folder/:folderId/:folderName'
        },
        renameFile: {
            method: 'POST',
            url: 'api/latest/service/ecm/file/:fileId/rename?newName=:name'
        },
        moveFile: {
            method: 'POST',
            url: 'api/latest/service/ecm/moveToAnotherContainer/:objType/:objId'
        },
        copyFile: {
            method: 'POST',
            url: 'api/latest/service/ecm/copyToAnotherContainer/:objType/:objId'
        },
        copyFileAsLink: {
            method: 'POST',
            url: 'api/latest/service/ecm/copyToAnotherContainerAsLink/:objType/:objId'
        },
        moveFolder: {
            method: 'POST',
            url: 'api/latest/service/ecm/folder/move/:subFolderId/:folderId'
        },
        copyFolder: {
            method: 'POST',
            url: 'api/latest/service/ecm/folder/copy/:subFolderId/:folderId/:objType/:objId'
        },
        copyFolderAsLink: {
            method: 'POST',
            url: 'api/latest/service/ecm/folder/copyAsLink/:subFolderId/:folderId/:objType/:objId'
        },
        setActiveVersion: {
            method: 'POST',
            url: 'api/latest/service/ecm/file/:fileId?versionTag=:version'
        }
        //, sendEmail: {
        //    method: 'POST',
        //    url: 'api/latest/service/notification/email'
        //}
        //, sendEmailWithAttachments: {
        //    method: 'POST',
        //    url: 'api/latest/service/notification/email/withattachments'
        //}
        ,
        getFile: {
            method: 'GET',
            url: 'api/latest/service/ecm/file/:fileId'
        },
        getFiles: {
            method: 'GET',
            url: 'api/latest/service/ecm/files',
            isArray: true
        },
        getFileEvents: {
            method: 'GET',
            url: 'api/latest/plugin/audit/FILE/:fileId'
        },
        getFileParticipants: {
            method: 'GET',
            url: 'api/v1/service/participant/FILE/:fileId',
            isArray: true
        },
        getFolderDocumentCounts: {
            method: 'GET',
            url: 'api/latest/service/ecm/folder/counts/:objType/:objId/',
            cache: false,
            isArray: false
        },
        updateFileType: {
            method: 'POST',
            url: 'api/latest/service/ecm/file/:fileId/type/:fileType',
            cache: false
        },
        updateFile: {
            method: 'POST',
            url: 'api/latest/service/ecm/file/metadata/:fileId'
        },

        //FOIA changes start
        setFileReviewStatus: {
            method: 'POST',
            url: 'api/latest/service/ecm/file/:fileId/version/:fileVersion/review/:reviewStatus'
        },
        setFileRedactionStatus: {
            method: 'POST',
            url: 'api/latest/service/ecm/file/:fileId/version/:fileVersion/redaction/:redactionStatus'
        },
        //FOIA changes end
        findFileByContainerAndFileType: {
            method: 'GET',
            url: 'api/latest/service/ecm/container/file/:containerId/:fileType'
        },
        getFileLinks: {
            method: 'GET',
            url: 'api/latest/service/ecm/fileLinks/:fileId',
            cache: false,
            isArray: true
        },
        getLinkTargetFile: {
            method: 'GET',
            url: 'api/latest/service/ecm/file/:fileId/targetLink'
        },
        getFileDuplicates: {
            method: 'GET',
            url: 'api/latest/service/ecm/fileDuplicates/:fileId',
            cache: false,
            isArray: true
        }
    });

    Service._getFolderDocumentCounts = function(params) {
        return Util.serviceCall({
            service: Service.getFolderDocumentCounts,
            param: params,
            onSuccess: function(data) {
                return data;
            },
            onError: function(errData) {
                alert("Error.");
            }
        });
    };

    Service.getLinkTargetEcmFile = function (fileId) {
        return Util.serviceCall({
            service: Service.getLinkTargetFile,
            param: fileId,
            onSuccess: function (data) {
                return data;
            },
            onError: function (error) {
                return error;
            }
        })
    };

    Service.getEcmFiles = function (fileIds) {
        return Util.serviceCall({
            service: Service.getFiles,
            param: fileIds,
            onSuccess: function (data) {
                return data;
            },
            onError: function (error) {
                return error;
            }
        })
    };

    return Service;
} ]);