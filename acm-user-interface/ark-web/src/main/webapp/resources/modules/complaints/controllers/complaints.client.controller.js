'use strict';

angular.module('complaints').controller('ComplaintsController', ['$scope', '$stateParams', '$translate', 'UtilService', 'CallConfigService', 'Complaint.InfoService',
    function ($scope, $stateParams, $translate, Util, CallConfigService, ComplaintInfoService) {
        var promiseGetModuleConfig = CallConfigService.getModuleConfig("complaints").then(function (config) {
            $scope.config = config;
            return config;
        });
        $scope.$on('req-component-config', function (e, componentId) {
            promiseGetModuleConfig.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        });
        $scope.$on('report-complaint-updated', function (e, complaintInfo) {
            ComplaintInfoService.updateTaskInfo(complaintInfo);
            $scope.$broadcast('complaint-updated', complaintInfo);
        });



        $scope.progressMsg = $translate.instant("complaints.progressNoComplaint");
        $scope.$on('req-select-complaint', function (e, selectedComplaint) {
            $scope.$broadcast('complaint-selected', selectedComplaint);

            var id = Util.goodMapValue(selectedComplaint, "nodeId", null);
            loadComplaint(id);
        });


        var loadComplaint = function (id) {
            if (id) {
                if ($scope.complaintInfo && $scope.complaintInfo.id != id) {
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