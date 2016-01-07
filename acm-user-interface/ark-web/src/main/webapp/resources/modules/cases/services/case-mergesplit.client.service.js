'use strict';

/**
 * @ngdoc service
 * @name services:Case.MergeSplitService
 *
 *  {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cases/services/case-mergesplit.client.service.js modules/cases/services/case-mergesplit.client.service.js}
 *
 * 
 * @description text  
 *      MergeSplit Service provides functions for merging and splitting case files.
 *  
 */
angular.module('services').factory('Case.MergeSplitService', ['$resource', '$translate', 'StoreService', 'UtilService',
    'Object.InfoService', 'Case.InfoService',
    function ($resource, $translate, Store, Util, ObjectInfoService, CaseInfoService) {
        
        var Service = $resource('proxy/arkcase/api/v1/plugin', {}, {
            /**
             * ngdoc method
             * name _mergeCaseFiles
             * 
             * @description
             * Adds selected case to original case.
             * 
             * @param {Object} data
             * @return {Object} Promise
             * 
             */
            _mergeCaseFiles:{
                url: 'proxy/arkcase/api/v1/plugin/merge-casefiles',
                method: 'POST',
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
                url:'proxy/arkcase/api/v1/plugin/copyCaseFile',
                method: 'POST'
            }
        });
        
        /**
         * @ngdoc method
         * @name mergeCaseFile
         * 
         * @description 
         *  Validates data, merges cases.
         *  
         * @param {Object} sourceId
         * @param {Object} targetId
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
        
        Service.splitCaseFile = function(targetCase){
            return Util.serviceCall({
                service: Service._splitCaseFiles
                , data: JSON.stringify(targetCase)
                , onSuccess: function(data){
                    if(CaseInfoService.validateCaseInfo(data)){
                        return data;
                    }
                }
            });
        };
        
        return Service;
    }
]);            