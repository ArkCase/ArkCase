'use strict';

angular.module('complaints').controller('ComplaintsController', ['$scope', '$state', '$stateParams'
    , 'UtilService', 'ConfigService', 'Complaint.InfoService', 'ObjectService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams
        , Util, ConfigService, ComplaintInfoService, ObjectService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Content({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "complaints"
            , resetObjectInfo: ComplaintInfoService.resetComplaintInfo
            , getObjectInfo: ComplaintInfoService.getComplaintInfo
            , updateObjectInfo: ComplaintInfoService.updateComplaintInfo
            , getObjectIdFromInfo: function (complaintInfo) {
                return Util.goodMapValue(complaintInfo, "complaintId");
            }
            , getObjectTypeFromInfo: function (objectInfo) {
                return ObjectService.ObjectTypes.COMPLAINT;
            }
            //, initComponentLinks: function (config) {
            //    return HelperObjectBrowserService.createComponentLinks(config, ObjectService.ObjectTypes.COMPLAINT);
            //}
            //, selectComponentLinks: function (selectedObject) {
            //    return $scope.componentLinks;
            //}
        });
    }
]);