'use strict';

angular.module('organizations').controller('Organizations.DetailsController',
        [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'Organization.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'ObjectService', 'PermissionsService', function($scope, $stateParams, $translate, Util, ConfigService, OrganizationInfoService, MessageService, HelperObjectBrowserService, ObjectService, PermissionsService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "organizations",
                componentId: "details",
                retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo,
                validateObjectInfo: OrganizationInfoService.validateOrganizationInfo,
                onObjectInfoRetrieved: function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            // $scope.options = {
            //     focus: true,
            //     dialogsInBody: true
            // };
            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectInfo = objectInfo;
                PermissionsService.getActionPermission('editOrganization', $scope.objectInfo, {
                    objectType: ObjectService.ObjectTypes.ORGANIZATION
                }).then(function(result) {
                    if(result){
                        $('#summernote').summernote({
                            focus: true,
                            dialogsInBody: true
                        });
                        $('#summernote').summernote('insertText', $scope.objectInfo.details);
                    }else {
                        $('#summernote').summernote('disable');
                    }
                });
            };



            $scope.saveDetails = function() {
                $scope.objectInfo.details = $('#summernote').summernote('code');
                var personInfo = Util.omitNg($scope.objectInfo);
                OrganizationInfoService.saveOrganizationInfo(personInfo).then(function(personInfo) {
                    MessageService.info($translate.instant("organizations.comp.details.informSaved"));
                    return personInfo;
                });
            };
        } ]);