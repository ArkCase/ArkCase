'use strict';

angular.module('services').factory('Case.MergeSplitService', ['$resource', '$translate', 'StoreService', 'UtilService',
    'Object.InfoService', 'Case.InfoService',
    function ($resource, $translate, Store, Util, ObjectInfoService, CaseInfoService) {
        
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
            _mergeCaseFiles:{
                url: '/api/v1/plugin/merge-casefiles',
                isArray: false
            }, 
            _splitCaseFiles:{
                url:'/api/v1/plugin/copyCaseFile'
            }
        });
        
        Service.mergeCaseFile= function(sourceId, targetId){
            return Util.serviceCall({
                service: Service._mergeCaseFiles
                , data: {sourceCaseFileId: sourceId, targetCaseFileId: targetId}
                , onSuccess: function(data){
                    if(CaseInfoService.validateCaseInfo(data)){
                        return data;
                    }
                }
            });
        };
        
        Service.splitCaseFile = function(){
            
        };
        
        return Service;
    }
]);
