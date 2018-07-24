'use strict';

angular.module('services').factory('MultiCorrespondence.Service', ['$http', 'UtilService', function($http, Util) {

    var generateMultiTemplateCorrespondence = function(templates, parentObjectType, parentObjectId, folderId, multiCorrespondenceDocumentName) {
        return $http({
            method: "POST",
            url: "api/latest/service/correspondence/multitemplate",
            data: templates,
            params: {
                parentObjectType: parentObjectType,
                parentObjectId: parentObjectId,
                folderId: folderId,
                documentName: multiCorrespondenceDocumentName
            },
            headers: {
                "Content-Type": "application/json"
            }
        });
    };

    var _createMultiTemplateCorrespondence = function(requestData, names, template, selectedTemplates, documentName) {
        if(selectedTemplates.length > 0){
            requestData.docTree._addingFileNodes(requestData.nodes[0], names, names[0]).then(function(tmpFileNodes) {
            var fileNodes = tmpFileNodes;
            generateMultiTemplateCorrespondence(selectedTemplates, requestData.docTree.getObjType(), requestData.docTree.getObjId(), requestData.nodes[0].data.objectId, documentName).then(function(tmpUploadedFile){
                var cacheKey = requestData.docTree.getCacheKeyByNode(requestData.nodes[0]);
                var uploadedFile = tmpUploadedFile.data;
                var file = requestData.docTree.fileToSolrData(uploadedFile);
                var folderList = requestData.docTree.cacheFolderList.get(cacheKey);

                if (requestData.docTree.Validator.validateFolderList(folderList)) {
                    folderList.children.push(file);
                    folderList.totalChildren++;
                    requestData.docTree.cacheFolderList.put(cacheKey, folderList);
                    if (!Util.isEmpty(uploadedFile) && requestData.docTree.Validator.validateFancyTreeNodes(fileNodes)) {
                        var type = Util.goodValue(uploadedFile.fileType);
                        var fileNode = requestData.docTree._matchFileNode(type, type, fileNodes);
                        if (fileNode) {
                            requestData.docTree._fileDataToNodeData(file, fileNode);
                            fileNode.renderTitle();
                            fileNode.setStatus("ok");
                        }
                    }
                }
            });
          });
        }
    };

    return {
        createMultiTemplateCorrespondence: _createMultiTemplateCorrespondence
    }
}]);
