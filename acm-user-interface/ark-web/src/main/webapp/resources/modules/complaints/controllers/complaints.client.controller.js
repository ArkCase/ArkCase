'use strict';

angular.module('complaints').controller('ComplaintsController', ['$scope', '$stateParams', '$translate', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'ConfigService', 'ComplaintsService',
    function ($scope, $stateParams, $translate, Store, Util, Validator, Helper, ConfigService, ComplaintsService) {
        var cacheComplaintsConfig = new Store.SessionData(Helper.SessionCacheNames.COMPLAINTS_CONFIG);
        var config = cacheComplaintsConfig.get();
        var promiseGetModule = Util.serviceCall({
            service: ConfigService.getModule
            , param: {moduleId: 'complaints'}
            , result: config
            , onSuccess: function (data) {
                if (Validator.validateComplaintsConfig(data)) {
                    config = data;
                    cacheComplaintsConfig.set(config);
                    return config;
                }
            }
        }).then(
            function (config) {
                $scope.config = config;
                return config;
            }
        );
        $scope.$on('req-component-config', function (e, componentId) {
            promiseGetModule.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        });


        $scope.progressMsg = $translate.instant("complaints.progressNoComplaint");
        $scope.$on('req-select-complaint', function (e, selectedComplaint) {
            $scope.$broadcast('complaint-selected', selectedComplaint);

            var id = Util.goodMapValue(selectedComplaint, "nodeId", null);
            loadComplaint(id);
        });

        var cacheComplaintInfo = new Store.CacheFifo(Helper.CacheNames.COMPLAINT_INFO);
        var loadComplaint = function (id) {
            if (id) {
                $scope.complaintInfo = null;
                $scope.progressMsg = $translate.instant("complaints.progressLoading") + " " + id + "...";
                var complaintInfo = cacheComplaintInfo.get(id);
                Util.serviceCall({
                    service: ComplaintsService.get
                    , param: {id: id}
                    , result: complaintInfo
                    , onSuccess: function (data) {
                        if (Validator.validateComplaint(data)) {
                            cacheComplaintInfo.put(id, data);
                            return data;
                        }
                    }
                }).then(
                    function (complaintInfo) {
                        $scope.progressMsg = null;
                        $scope.complaintInfo = complaintInfo;
                        $scope.$broadcast('complaint-retrieved', complaintInfo);
                        return complaintInfo;
                    }
                    , function (errorData) {
                        $scope.complaintInfo = null;
                        $scope.progressMsg = $translate.instant("complaints.progressError") + " " + id;
                        return errorData;
                    }
                );
            }
        };
        var id = Util.goodMapValue($stateParams, "id", null);
        loadComplaint(id);
    }
]);