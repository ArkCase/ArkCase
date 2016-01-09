'use strict';

angular.module('complaints').controller('ComplaintsController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Complaint.InfoService', 'ObjectService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ConfigService, ComplaintInfoService, ObjectService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Content({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "complaints"
            , getObjectInfo: ComplaintInfoService.getComplaintInfo
            , updateObjectInfo: ComplaintInfoService.updateComplaintInfo
            , getObjectIdFromInfo: function (complaintInfo) {
                return Util.goodMapValue(complaintInfo, "complaintId");
            }
            , initComponentLinks: function (config) {
                return HelperObjectBrowserService.createComponentLinks(config, ObjectService.ObjectTypes.COMPLAINT);
            }
            , selectComponentLinks: function (selectedObject) {
                return $scope.componentLinks;
            }
        });

        //
        //$scope.$on('report-object-updated', function (e, objectInfo) {
        //    ComplaintInfoService.updateComplaintInfo(objectInfo);
        //    $scope.objectInfo = objectInfo;
        //    $scope.$broadcast('object-updated', objectInfo);
        //});
        //
        //$scope.$on('req-select-object', function (e, selectedObject) {
        //    var components = Util.goodArray(selectedObject.components);
        //    $scope.activeLinkId = (1 == components.length) ? components[0] : "main";
        //});

        //$scope.getActive = function (linkId) {
        //    return ($scope.activeLinkId == linkId) ? "active" : ""
        //};
        //
        //$scope.onClickComponentLink = function (linkId) {
        //    $scope.activeLinkId = linkId;
        //    $state.go('complaints.' + linkId, {
        //        id: $stateParams.id
        //    });
        //};
        //
        //$scope.linksShown = false;
        //$scope.toggleShowLinks = function () {
        //    $scope.linksShown = !$scope.linksShown;
        //};

        //$scope.$on('req-select-object', function (e, selectedObject) {
        //    $scope.$broadcast('object-selected', selectedObject);
        //
        //    var id = Util.goodMapValue(selectedObject, "nodeId", null);
        //    loadComplaint(id);
        //});
        //
        //$scope.progressMsg = $translate.instant("common.objects.progressNoData");
        //var loadComplaint = function (id) {
        //    if (Util.goodPositive(id)) {
        //        if ($scope.objectInfo && $scope.objectInfo.complaintId != id) {
        //            $scope.objectInfo = null;
        //        }
        //        $scope.progressMsg = $translate.instant("common.objects.progressLoading") + " " + id + "...";
        //
        //        ComplaintInfoService.getComplaintInfo(id).then(
        //            function (objectInfo) {
        //                $scope.progressMsg = null;
        //                $scope.objectInfo = objectInfo;
        //                $scope.$broadcast('object-updated', objectInfo);
        //                return objectInfo;
        //            }
        //            , function (error) {
        //                $scope.objectInfo = null;
        //                $scope.progressMsg = $translate.instant("common.objects.progressError") + " " + id;
        //                return error;
        //            }
        //        );
        //    }
        //};
        //
        //
        //loadComplaint($stateParams.id);
    }
]);