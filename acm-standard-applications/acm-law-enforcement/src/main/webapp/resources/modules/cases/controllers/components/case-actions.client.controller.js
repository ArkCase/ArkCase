'use strict';

angular.module('cases').controller('Cases.ActionsController', ['$scope', '$state', '$stateParams', '$q', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Authentication', 'Object.LookupService', 'Case.LookupService'
    , 'Object.SubscriptionService', 'Object.ModelService', 'Case.InfoService', 'Case.MergeSplitService'
    , 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $q, $modal
        , Util, ConfigService, ObjectService, Authentication, ObjectLookupService, CaseLookupService
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

        var promiseQueryUser = Authentication.queryUserInfo();
        var promiseGetGroups = ObjectLookupService.getGroups();

        var onObjectInfoRetrieved = function (objectInfo) {
            var group = ObjectModelService.getGroup(objectInfo);
            var assignee = ObjectModelService.getAssignee(objectInfo);
            var promiseGetApprovers = CaseLookupService.getApprovers(group, assignee);
            $q.all([promiseQueryUser, promiseGetGroups, promiseGetApprovers]).then(function (data) {
                var userInfo = data[0];
                var groups = data[1];
                var assignees = data[2];
                $scope.restricted = ObjectModelService.checkRestriction(userInfo.userId, assignee, group, assignees, groups);
            });

            promiseQueryUser.then(function (userInfo) {
                $scope.userId = userInfo.userId;
                ObjectSubscriptionService.getSubscriptions(userInfo.userId, ObjectService.ObjectTypes.CASE_FILE, $scope.objectInfo.id).then(function (subscriptions) {
                    var found = _.find(subscriptions, {
                        userId: userInfo.userId,
                        subscriptionObjectType: ObjectService.ObjectTypes.CASE_FILE,
                        objectId: $scope.objectInfo.id
                    });
                    $scope.showBtnSubscribe = Util.isEmpty(found);
                    $scope.showBtnUnsubscribe = !$scope.showBtnSubscribe;
                });
            });
        };

        $scope.restricted = false;
        $scope.onClickRestrict = function ($event) {
            if ($scope.restricted != $scope.objectInfo.restricted) {
                $scope.objectInfo.restricted = $scope.restricted;

                var caseInfo = Util.omitNg($scope.objectInfo);
                CaseInfoService.saveCaseInfo(caseInfo);
            }
        };

        $scope.createNew = function () {
            $state.go("frevvo", {
                name: "new-case"
            });
        };

        $scope.edit = function (caseInfo) {
            $state.go("frevvo", {
                name: "edit-case"
                , arg: {
                    caseId: caseInfo.id
                    , caseNumber: caseInfo.caseNumber
                    , mode: "edit"
                    , containerId: caseInfo.container.id
                    , folderId: caseInfo.container.folder.id
                }
            });
        };

        $scope.changeStatus = function (caseInfo) {
            $state.go("frevvo", {
                name: "change-case-status"
                , arg: {
                    caseId: caseInfo.id
                    , caseNumber: caseInfo.caseNumber //or is it actionNumber?
                    , status: caseInfo.status
                }
            });
        };
        $scope.reinvestigate = function (caseInfo) {
            $state.go("frevvo", {
                name: "reinvestigate"
                , arg: {
                    caseId: caseInfo.id
                    , caseNumber: caseInfo.caseNumber
                    , mode: "reinvestigate"
                    , containerId: caseInfo.container.id
                    , folderId: caseInfo.container.folder.id
                }
            });
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


