'use strict';

angular.module('cases').controller('Cases.ActionsController', ['$scope', '$state', '$stateParams', '$q', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Case.LookupService'
    , 'Object.SubscriptionService', 'Object.ModelService', 'Case.InfoService', 'Case.MergeSplitService'
    , 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $q, $modal
        , Util, ConfigService, ObjectService, Authentication, CaseLookupService
        , ObjectSubscriptionService, ObjectModelService, CaseInfoService, MergeSplitService
        , HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "actions"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });


        ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
            $scope.caseFileSearchConfig = _.find(moduleConfig.components, {id: "merge"});
        });

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.restricted = objectInfo.restricted;

            Authentication.queryUserInfo().then(function (userInfo) {
                $scope.userId = userInfo.userId;
                ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.CASE_FILE, $scope.objectInfo.id).then(function (subscriptions) {
                    var found = _.find(subscriptions, {
                        userId: userInfo.userId,
                        subscriptionObjectType: ObjectService.ObjectTypes.CASE_FILE,
                        objectId: $scope.objectInfo.id
                    });
                    $scope.showBtnSubscribe = Util.isEmpty(found);
                    $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                    ObjectModelService.checkIfUserCanRestrict($scope.userId, objectInfo).then(function (result) {
                        $scope.isUserAbleToRestrict = result;
                    });
                });
            });

            $scope.editParams = {
                caseId: objectInfo.id
                , caseNumber: objectInfo.caseNumber
                , containerId: objectInfo.container.id
                , folderId: objectInfo.container.folder.id
            };

            $scope.reinvestigateParams = {
                caseId: objectInfo.id
                , caseNumber: objectInfo.caseNumber
                , containerId: objectInfo.container.id
                , folderId: objectInfo.container.folder.id
            };

            $scope.changeCaseStatusParams = {
                caseId: objectInfo.id
                , caseNumber: objectInfo.caseNumber
                , status: objectInfo.status
            };
        };

        $scope.onClickRestrict = function ($event) {
            if ($scope.isUserAbleToRestrict && $scope.restricted != $scope.objectInfo.restricted) {
                $scope.objectInfo.restricted = $scope.restricted;

                var caseInfo = Util.omitNg($scope.objectInfo);
                CaseInfoService.saveCaseInfo(caseInfo).then(function () {

                }, function () {
                    $scope.restricted = !$scope.restricted;
                });
            }
        };

        $scope.subscribe = function (caseInfo) {
            ObjectSubscriptionService.subscribe($scope.userId, ObjectService.ObjectTypes.CASE_FILE, caseInfo.id).then(function (data) {
                $scope.showBtnSubscribe = false;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };
        $scope.unsubscribe = function (caseInfo) {
            ObjectSubscriptionService.unsubscribe($scope.userId, ObjectService.ObjectTypes.CASE_FILE, caseInfo.id).then(function (data) {
                $scope.showBtnSubscribe = true;
                $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                return data;
            });
        };

        $scope.merge = function (caseInfo) {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/cases/views/components/case-merge.client.view.html',
                controller: 'Cases.MergeController',
                size: 'lg',
                resolve: {
                    config: function () {
                        return $scope.caseFileSearchConfig;
                    }
                    //filter: function () {
                    //    return $scope.caseFileSearchConfig.caseInfoFilter;
                    //}
                }
            });
            modalInstance.result.then(function (caseSummary) {
                if (caseSummary) {

                    MergeSplitService.mergeCaseFile(caseInfo.id, caseSummary.object_id_s).then(
                        function (data) {
                            ObjectService.gotoUrl(ObjectService.ObjectTypes.CASE_FILE, data.id);
                        });

                }
            });
        };

        $scope.split = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/cases/views/components/case-split.client.view.html',
                controller: 'Cases.SplitController',
                size: 'lg'
            });
            modalInstance.result.then(function (caseSummary) {
                if (caseSummary) {
                    if (caseSummary != null) {
                        MergeSplitService.splitCaseFile(caseSummary).then(
                            function (data) {
                                ObjectService.gotoUrl(ObjectService.ObjectTypes.CASE_FILE, data.id);
                            });
                    }
                }
            });
        };

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };
    }

]);


