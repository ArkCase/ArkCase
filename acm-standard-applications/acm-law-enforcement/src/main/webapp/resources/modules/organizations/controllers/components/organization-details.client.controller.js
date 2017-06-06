'use strict';

angular.module('organizations').controller('Organizations.DetailsController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Organization.InfoService', 'MessageService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $translate
        , Util, ConfigService, OrganizationInfoService, MessageService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "organizations"
            , componentId: "details"
            , retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo
            , validateObjectInfo: OrganizationInfoService.validateOrganizationInfo
        });


        $scope.options = {
            focus: true,
            dialogsInBody: true
            //,height: 120
        };

        $scope.saveDetails = function () {
            var personInfo = Util.omitNg($scope.objectInfo);
            OrganizationInfoService.saveOrganizationInfo(personInfo).then(
                function (personInfo) {
                    MessageService.info($translate.instant("people.comp.details.informSaved"));
                    return personInfo;
                }
            );
        };
    }
]);