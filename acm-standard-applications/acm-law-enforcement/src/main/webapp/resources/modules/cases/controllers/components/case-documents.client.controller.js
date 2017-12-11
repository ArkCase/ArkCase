'use strict';

angular.module('cases').controller('Cases.DocumentsController', ['$scope', '$stateParams', '$modal', '$q', '$timeout', '$translate'
    , 'UtilService', 'Config.LocaleService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Case.InfoService', 'DocTreeService'
    , 'Helper.ObjectBrowserService', 'Authentication', 'PermissionsService', 'Object.ModelService'
    , 'DocTreeExt.WebDAV', 'DocTreeExt.Checkin', 'Admin.CMTemplatesService', 'DocTreeExt.Email', 'ModalDialogService', 'Admin.EmailSenderConfigurationService'
    , function ($scope, $stateParams, $modal, $q, $timeout, $translate
        , Util, LocaleService, ConfigService, ObjectService, ObjectLookupService, CaseInfoService, DocTreeService
        , HelperObjectBrowserService, Authentication, PermissionsService, ObjectModelService
        , DocTreeExtWebDAV, DocTreeExtCheckin, CorrespondenceService, DocTreeExtEmail, ModalDialogService, EmailSenderConfigurationService) {
        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.user = userInfo.userId;
                return userInfo;
            }
        );

        EmailSenderConfigurationService.getEmailSenderConfiguration().then(function (emailData) {
            $scope.sendEmailEnabled = emailData.data.allowDocuments;
        });

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "documents"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var promiseFormTypes = ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.CASE_FILE);
        var promiseFileTypes = ObjectLookupService.getFileTypes();
        var promiseCorrespondenceForms = CorrespondenceService.getActivatedTemplatesData(ObjectService.ObjectTypes.CASE_FILE);
        var promiseFileLanguages = LocaleService.getSettings();
        var onConfigRetrieved = function (config) {
            $scope.treeConfig = config.docTree;
            $scope.allowParentOwnerToCancel = config.docTree.allowParentOwnerToCancel;

            $q.all([promiseFormTypes, promiseFileTypes, promiseCorrespondenceForms, promiseFileLanguages]).then(
                function (data) {
                    $scope.treeConfig.formTypes = data[0];
                    $scope.treeConfig.fileTypes = [];
                    for(var i = 0; i < data[1].length; i++){
                        $scope.treeConfig.fileTypes.push({"key": data[1][i].key, "value":$translate.instant(data[1][i].value)});
                    }
                    $scope.treeConfig.correspondenceForms = data[2];
                    $scope.treeConfig.fileLanguages = data[3];
                    if (!Util.isEmpty($scope.treeControl)) {
                        $scope.treeControl.refreshTree();
                    }
                });
        };


        $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
        $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.objectId = objectInfo.id;
            $scope.assignee = ObjectModelService.getAssignee(objectInfo);
        };


        $scope.uploadForm = function (type, folderId, onCloseForm) {
            var fileTypes = Util.goodArray($scope.treeConfig.fileTypes);
            fileTypes = fileTypes.concat(Util.goodArray($scope.treeConfig.formTypes));
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, fileTypes);
        };

        $scope.onInitTree = function (treeControl) {
            $scope.treeControl = treeControl;
            DocTreeExtCheckin.handleCheckout(treeControl, $scope);
            DocTreeExtCheckin.handleCheckin(treeControl, $scope);
            DocTreeExtCheckin.handleCancelEditing(treeControl, $scope);
            DocTreeExtWebDAV.handleEditWithWebDAV(treeControl, $scope);

            //$scope.treeControl.addCommandHandler({
            //    name: "sample"
            //    , onAllowCmd: function(nodes) {
            //        return "disable";
            //    }
            //    , onPreCmd: function(nodes, args) {
            //        console.log("onPreCmd of sample command");
            //        return false;
            //    }
            //    , onPostCmd: function(nodes, args) {
            //        console.log("onPostCmd of sample command");
            //    }
            //    , execute: function(nodes, args) {
            //        console.log("Possible to add onPreCmd code here");
            //        var promise = this.prevHandler.execute(nodes, args);
            //        $q.when(promise).then(function () {
            //            console.log("Possible to add onPostCmd code here, too");
            //        });
            //        console.log("Possible to add onPostCmd code here");
            //    }
            //});
        };

        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

        $scope.sendEmail = function() {
            var nodes = $scope.treeControl.getSelectedNodes();
            var DocTree = $scope.treeControl.getDocTreeObject();
            DocTreeExtEmail.openModal(DocTree, nodes);
        };

        $scope.createNewTask = function () {
            var modalMetadata = {
                moduleName: 'tasks',
                templateUrl: 'modules/tasks/views/components/task-new-task.client.view.html',
                controllerName: 'Tasks.NewTaskController',
                params: {
                    parentType: ObjectService.ObjectTypes.CASE_FILE,
                    parentObject: $scope.objectInfo.caseNumber,
                    parentTitle: $scope.objectInfo.title,
                    parentId: $scope.objectInfo.id,
                    documentsToReview: $scope.selectedDocuments,
                    taskType: 'REVIEW_DOCUMENT'
                }
            };
            ModalDialogService.showModal(modalMetadata);
        };

        $scope.selectedDocuments = [];

        $scope.onCheckNode = function(node) {
            if(!node.folder) {
                var idx = _.findIndex($scope.selectedDocuments, function(d) { return d.data.objectId == node.data.objectId; });

                if (idx > -1) {
                    $scope.selectedDocuments.splice(idx, 1);
                } else {
                    $scope.selectedDocuments.push(node);
                }
            }
        };

        $scope.onToggleAllNodesChecked = function(nodes) {
            $scope.selectedDocuments = _.filter(nodes, function (node) {
                return !node.folder;
            });
        };

        $scope.$bus.subscribe('docTreeNodeChecked', function (node) {
            $scope.onCheckNode(node);
        });

        $scope.$bus.subscribe('toggleAllNodesChecked', function (nodes) {
            $scope.onToggleAllNodesChecked(nodes);
        });

        $scope.onFilter = function () {
            $scope.$bus.publish('onFilterDocTree', {filter: $scope.filter});
        };

        $scope.onSearch = function () {
            $scope.$bus.publish('onSearchDocTree', {searchFilter: $scope.searchFilter});
        };

        $scope.$bus.subscribe('removeSearchFilter', function () {
            $scope.searchFilter = null;
        });
    }
]);
