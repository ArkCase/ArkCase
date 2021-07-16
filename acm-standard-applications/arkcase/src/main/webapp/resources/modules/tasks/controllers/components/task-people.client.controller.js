'use strict';

angular.module('tasks').controller(
    'Tasks.PeopleController',
    [ '$timeout','$scope', '$q', '$stateParams', '$translate', '$modal', 'UtilService', 'Object.TaskService', 'Task.InfoService', 'Authentication', 'Object.LookupService', 'ObjectService','Helper.UiGridService', 'Helper.ObjectBrowserService', 'Person.InfoService', 'Task.PeopleService', 'PersonAssociation.Service',
        function($timeout, $scope, $q, $stateParams, $translate, $modal, Util, ObjectTaskService, TaskInfoService, Authentication, ObjectLookupService, ObjectService, HelperUiGridService, HelperObjectBrowserService, PersonInfoService, TaskPeopleService, PersonAssociationService) {

            var personTypeCreator = 'Creator';

            ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.CASE_FILE).then(function(personTypes) {
                $scope.personTypes = personTypes;
                return personTypes;
            });

            var componentHelper = new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "tasks",
                componentId: "people",
                retrieveObjectInfo: TaskInfoService.getTaskInfo,
                validateObjectInfo: TaskInfoService.validateTaskInfo,
                onConfigRetrieved: function(componentConfig) {
                    return onConfigRetrieved(componentConfig);
                },
                onObjectInfoRetrieved: function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            var assocTypeLabel = $translate.instant("tasks.comp.people.type.label");

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            var promiseUsers = gridHelper.getUsers();

            var onConfigRetrieved = function(config) {
                $scope.config = config;

                gridHelper.setUserNameFilterToConfig(promiseUsers, config).then(function(updatedConfig) {
                    $scope.config = updatedConfig;
                    if ($scope.gridApi != undefined)
                        $scope.gridApi.core.refresh();
                    gridHelper.addButton(updatedConfig, "delete", null, null, "isDeleteDisabled");
                    gridHelper.setColumnDefs(updatedConfig);
                    gridHelper.setBasicOptions(updatedConfig);
                    gridHelper.disableGridScrolling(updatedConfig);
                });
            };

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectInfo = objectInfo;
                TaskPeopleService.findPeople(objectInfo.taskId).then(function (data) {
                    $scope.gridOptions.data = data;
                    $scope.gridOptions.totalItems = data.length;
                })
            };

            $scope.addPerson = function () {
                var association = {};
                var params = {};
                params.types = $scope.personTypes;
                params.assocTypeLabel = assocTypeLabel;

                var modalInstance = $modal.open({
                    scope: $scope,
                    animation: true,
                    templateUrl: 'modules/common/views/add-person-modal.client.view.html',
                    controller: 'Common.AddPersonModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function(data) {
                    if (data.isNew) {
                        PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function(response) {
                            updatePersonAssociationData(association, response.data, data);
                        });
                    } else {
                        PersonInfoService.getPersonInfo(data.personId).then(function(person) {
                            updatePersonAssociationData(association, person, data);
                        })
                    }
                });
            }

            function updatePersonAssociationData(association, person, data) {
                association.person =  person;
                association.parentId = $scope.objectInfo.taskId;
                association.parentType = "TASK";
                association.personType = data.type;
                PersonAssociationService.savePersonAssociation(association).then(function(response) {
                    var newPerson = {};
                    newPerson.personType = response.personType;
                    newPerson.name = person.givenName + ' ' + person.familyName + ' (' + response.personType + ')';
                    newPerson.child_id_s = response.person.id;
                    newPerson.type_lcs = response.personType;
                    $scope.gridOptions.data.push(newPerson);
                });
            }


            $scope.deleteRow = function(rowEntity) {
                if (rowEntity.id) {
                    PersonAssociationService.deletePersonAssociationInfo(rowEntity.object_id_s).then(function(response) {
                        _.remove($scope.gridOptions.data, function(item) {
                            return item === rowEntity;
                        });
                    });
                }
            };

            $scope.editRow = function(rowEntity) {
                pickPerson(rowEntity);
            };

            function saveObjectInfoAndRefresh(association) {
                var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                if (TaskInfoService.validateTaskInfo($scope.objectInfo)) {
                    var objectInfo = Util.omitNg($scope.objectInfo);
                    promiseSaveInfo = TaskInfoService.saveTaskInfo(objectInfo);
                    promiseSaveInfo.then(function(objectInfo) {
                        $scope.$emit("report-object-updated", objectInfo);
                        return objectInfo;
                    }, function(error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    });
                }
                return promiseSaveInfo;
            }

            $scope.isDeleteDisabled = function(rowEntity) {
                return rowEntity.personType == personTypeCreator;
            };
        } ]);
