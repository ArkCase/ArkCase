'use strict';

angular.module('complaints').controller('ComplaintsController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Complaint.InfoService', 'ObjectService', 'Helper.ObjectTreeService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ConfigService, ComplaintInfoService, ObjectService, HelperObjectTreeService) {

        var promiseGetModuleConfig = ConfigService.getModuleConfig("complaints").then(function (config) {
            $scope.config = config;
            $scope.componentLinks = HelperObjectTreeService.createComponentLinks(config, ObjectService.ObjectTypes.COMPLAINT);
            $scope.activeLinkId = "main";
            return config;
        });

        $scope.$on('req-component-config', function (e, componentId) {
            promiseGetModuleConfig.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        });

        $scope.$on('report-complaint-updated', function (e, complaintInfo) {
            ComplaintInfoService.updateComplaintInfo(complaintInfo);
            $scope.complaintInfo = complaintInfo;
            $scope.$broadcast('complaint-updated', complaintInfo);
        });

        $scope.$on('req-select-complaint', function (e, selectedComplaint) {
            var components = Util.goodArray(selectedComplaint.components);
            $scope.activeLinkId = (1 == components.length) ? components[0] : "main";
        });

        $scope.getActive = function (linkId) {
            return ($scope.activeLinkId == linkId) ? "active" : ""
        };

        $scope.onClickComponentLink = function (linkId) {
            $scope.activeLinkId = linkId;
            $state.go('complaints.' + linkId, {
                id: $stateParams.id
            });
        };

        $scope.progressMsg = $translate.instant("complaints.progressNoComplaint");
        $scope.$on('req-select-complaint', function (e, selectedComplaint) {
            $scope.$broadcast('complaint-selected', selectedComplaint);

            var id = Util.goodMapValue(selectedComplaint, "nodeId", null);
            loadComplaint(id);
        });


        var loadComplaint = function (id) {
            if (Util.goodPositive(id)) {
                if ($scope.complaintInfo && $scope.complaintInfo.complaintId != id) {
                    $scope.complaintInfo = null;
                }
                $scope.progressMsg = $translate.instant("complaints.progressLoading") + " " + id + "...";

                ComplaintInfoService.getComplaintInfo(id).then(
                    function (complaintInfo) {
                        $scope.progressMsg = null;
                        $scope.complaintInfo = complaintInfo;
                        $scope.$broadcast('complaint-updated', complaintInfo);
                        return complaintInfo;
                    }
                    , function (error) {
                        $scope.complaintInfo = null;
                        $scope.progressMsg = $translate.instant("complaints.progressError") + " " + id;
                        return error;
                    }
                );
            }
        };


        loadComplaint($stateParams.id);
    }
]);