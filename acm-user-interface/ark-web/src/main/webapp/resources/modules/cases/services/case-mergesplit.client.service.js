'use strict';

/**
 * @ngdoc service
 * @name services:Case.MergeSplitService
 * 
 * @description text  
 *      MergeSplit Service provides functions for merging and splitting case files.
 *  
 */
angular.module('services').factory('Case.MergeSplitService', ['$resource', '$translate', 'StoreService', 'UtilService',
    'Object.InfoService', 'Case.InfoService',
    function ($resource, $translate, Store, Util, ObjectInfoService, CaseInfoService) {
        
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * ngdoc method
             * name _mergeCaseFiles
             * 
             * @description
             * Adds selected case to original case.
             * 
             */
            _mergeCaseFiles:{
                url: '/api/v1/plugin/merge-casefiles',
                isArray: false
            }, 
            /**
             * ngdoc method
             * name _splitCaseFiles
             * 
             * @description
             * Creates a new case for selected file.
             */
            _splitCaseFiles:{
                url:'/api/v1/plugin/copyCaseFile'
            }
        });
        
        /**
         * @ngdoc method
         * @name mergeCaseFile
         * 
         * @description 
         *  Validates data, merges cases.
         *  
         * @param {type} sourceId
         * @param {type} targetId
         * @returns {Object} Promise
         */
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
