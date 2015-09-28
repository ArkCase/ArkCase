'use strict';

angular.module('cases').controller('Cases.TimeController', ['$scope', '$stateParams', '$q', '$window', '$translate', 'UtilService', 'ValidationService', 'LookupService', 'CasesService',
    function ($scope, $stateParams, $q, $window, $translate, Util, Validator, LookupService, CasesService) {
        $scope.$emit('req-component-config', 'time');

        $scope.config = null;
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'time') {
                $scope.config = config;

                Util.uiGrid.typicalOptions(config, $scope);
                //Util.uiGrid.externalPaging(config, $scope, updatePageData);
                $scope.gridOptions.columnDefs = config.columnDefs;

                for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                    if ("name" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].cellTemplate = "<a href='#' ng-click='grid.appScope.showUrl($event, row.entity)'>{{row.entity.acm$_formName}}</a>";
                    } else if ("tally" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].field = "acm$_hours";
                    }
                }
            }
        }

        $scope.$on('case-retrieved', function (e, data) {
            if (Validator.validateCaseFile(data)) {
                $scope.caseInfo = data;

                CasesService.queryTimesheets({
                    objectType: "CASE_FILE",
                    objectId: $scope.caseInfo.id
                }, function (data) {

                    //for testing
                    data = [
                        {
                            "id": 104,
                            "user": {
                                "userId": "sally-acm",
                                "fullName": "Sally Supervisor",
                                "firstName": "Sally",
                                "lastName": "Supervisor",
                                "userDirectoryName": "armedia",
                                "created": "2015-07-24T04:00:00Z",
                                "modified": "2015-09-26T04:00:00Z",
                                "userState": "VALID",
                                "mail": "sally-acm@armedia.com",
                                "distinguishedName": null
                            },
                            "startDate": "2015-08-09T00:00:00.000-0400",
                            "endDate": "2015-08-15T00:00:00.000-0400",
                            "times": [
                                {
                                    "id": 106,
                                    "objectId": 107,
                                    "code": "20150806_107",
                                    "type": "CASE_FILE",
                                    "date": "2015-08-11T00:00:00.000-0400",
                                    "value": 2,
                                    "creator": "sally-acm",
                                    "created": "2015-08-14T11:08:04.171-0400",
                                    "modifier": "sally-acm",
                                    "modified": "2015-08-14T11:08:04.171-0400"
                                }
                            ],
                            "status": "DRAFT",
                            "details": null,
                            "creator": "sally-acm",
                            "created": "2015-08-14T11:08:04.168-0400",
                            "modifier": "sally-acm",
                            "modified": "2015-08-14T11:08:04.168-0400",
                            "participants": [],
                            "container": {
                                "id": 159,
                                "created": "2015-08-14T11:08:04.164-0400",
                                "creator": "sally-acm",
                                "modified": "2015-08-14T11:08:04.164-0400",
                                "modifier": "sally-acm",
                                "containerObjectType": "TIMESHEET",
                                "containerObjectId": 104,
                                "containerObjectTitle": "Timesheet 8/09/2015-8/15/2015",
                                "folder": {
                                    "id": 239,
                                    "created": "2015-08-14T11:08:04.161-0400",
                                    "creator": "sally-acm",
                                    "modified": "2015-08-14T11:08:04.161-0400",
                                    "modifier": "sally-acm",
                                    "name": "/Sites/acm/documentLibrary/Timesheets/sally-acm/Timesheet 8_09_2015-8_15_2015",
                                    "cmisFolderId": "workspace://SpacesStore/85133deb-93c6-4a4a-b765-a4f3796ae98e",
                                    "parentFolderId": null,
                                    "status": "ACTIVE",
                                    "participants": [
                                        {
                                            "id": 353,
                                            "objectType": "FOLDER",
                                            "objectId": 239,
                                            "participantType": "*",
                                            "participantLdapId": "*",
                                            "created": "2015-08-14T11:08:04.169-0400",
                                            "creator": "sally-acm",
                                            "modified": "2015-09-04T08:56:10.733-0400",
                                            "modifier": "sally-acm",
                                            "privileges": [
                                                {
                                                    "id": 510,
                                                    "created": "2015-08-14T11:08:04.170-0400",
                                                    "creator": "sally-acm",
                                                    "modified": "2015-08-14T11:08:04.170-0400",
                                                    "modifier": "sally-acm",
                                                    "objectAction": "read",
                                                    "accessType": "grant",
                                                    "accessReason": "policy"
                                                }
                                            ]
                                        }
                                    ],
                                    "parentFolderParticipants": []
                                },
                                "attachmentFolder": {
                                    "id": 239,
                                    "created": "2015-08-14T11:08:04.161-0400",
                                    "creator": "sally-acm",
                                    "modified": "2015-08-14T11:08:04.161-0400",
                                    "modifier": "sally-acm",
                                    "name": "/Sites/acm/documentLibrary/Timesheets/sally-acm/Timesheet 8_09_2015-8_15_2015",
                                    "cmisFolderId": "workspace://SpacesStore/85133deb-93c6-4a4a-b765-a4f3796ae98e",
                                    "parentFolderId": null,
                                    "status": "ACTIVE",
                                    "participants": [
                                        {
                                            "id": 353,
                                            "objectType": "FOLDER",
                                            "objectId": 239,
                                            "participantType": "*",
                                            "participantLdapId": "*",
                                            "created": "2015-08-14T11:08:04.169-0400",
                                            "creator": "sally-acm",
                                            "modified": "2015-09-04T08:56:10.733-0400",
                                            "modifier": "sally-acm",
                                            "privileges": [
                                                {
                                                    "id": 510,
                                                    "created": "2015-08-14T11:08:04.170-0400",
                                                    "creator": "sally-acm",
                                                    "modified": "2015-08-14T11:08:04.170-0400",
                                                    "modifier": "sally-acm",
                                                    "objectAction": "read",
                                                    "accessType": "grant",
                                                    "accessReason": "policy"
                                                }
                                            ]
                                        }
                                    ],
                                    "parentFolderParticipants": []
                                },
                                "calendarFolderId": null
                            }
                        },

                        {
                            "id": 105,
                            "user": {
                                "userId": "sally-acm",
                                "fullName": "Sally Bi",
                                "firstName": "Sally",
                                "lastName": "Supervisor",
                                "userDirectoryName": "armedia",
                                "created": "2015-07-24T04:00:00Z",
                                "modified": "2015-09-26T04:00:00Z",
                                "userState": "VALID",
                                "mail": "sally-acm@armedia.com",
                                "distinguishedName": null
                            },
                            "startDate": "2015-08-09T00:00:00.000-0400",
                            "endDate": "2015-08-15T00:00:00.000-0400",
                            "times": [
                                {
                                    "id": 106,
                                    "objectId": 107,
                                    "code": "20150806_107",
                                    "type": "CASE_FILE",
                                    "date": "2015-08-11T00:00:00.000-0400",
                                    "value": 2,
                                    "creator": "sally-acm",
                                    "created": "2015-08-14T11:08:04.171-0400",
                                    "modifier": "sally-acm",
                                    "modified": "2015-08-14T11:08:04.171-0400"
                                },
                                {
                                    "id": 107,
                                    "objectId": 107,
                                    "code": "20150806_107",
                                    "type": "CASE_FILE",
                                    "date": "2015-08-11T00:00:00.000-0400",
                                    "value": 3,
                                    "creator": "sally-acm",
                                    "created": "2015-08-14T11:08:04.171-0400",
                                    "modifier": "sally-acm",
                                    "modified": "2015-08-14T11:08:04.171-0400"
                                }
                            ],
                            "status": "DRAFT",
                            "details": null,
                            "creator": "sally-acm",
                            "created": "2015-08-14T11:08:04.168-0400",
                            "modifier": "sally-acm",
                            "modified": "2015-08-14T11:08:04.168-0400",
                            "participants": [],
                            "container": {
                                "id": 159,
                                "created": "2015-08-14T11:08:04.164-0400",
                                "creator": "sally-acm",
                                "modified": "2015-08-14T11:08:04.164-0400",
                                "modifier": "sally-acm",
                                "containerObjectType": "TIMESHEET",
                                "containerObjectId": 104,
                                "containerObjectTitle": "Timesheet 8/09/2015-8/15/2015",
                                "folder": {
                                    "id": 239,
                                    "created": "2015-08-14T11:08:04.161-0400",
                                    "creator": "sally-acm",
                                    "modified": "2015-08-14T11:08:04.161-0400",
                                    "modifier": "sally-acm",
                                    "name": "/Sites/acm/documentLibrary/Timesheets/sally-acm/Timesheet 8_09_2015-8_15_2015",
                                    "cmisFolderId": "workspace://SpacesStore/85133deb-93c6-4a4a-b765-a4f3796ae98e",
                                    "parentFolderId": null,
                                    "status": "ACTIVE",
                                    "participants": [
                                        {
                                            "id": 353,
                                            "objectType": "FOLDER",
                                            "objectId": 239,
                                            "participantType": "*",
                                            "participantLdapId": "*",
                                            "created": "2015-08-14T11:08:04.169-0400",
                                            "creator": "sally-acm",
                                            "modified": "2015-09-04T08:56:10.733-0400",
                                            "modifier": "sally-acm",
                                            "privileges": [
                                                {
                                                    "id": 510,
                                                    "created": "2015-08-14T11:08:04.170-0400",
                                                    "creator": "sally-acm",
                                                    "modified": "2015-08-14T11:08:04.170-0400",
                                                    "modifier": "sally-acm",
                                                    "objectAction": "read",
                                                    "accessType": "grant",
                                                    "accessReason": "policy"
                                                }
                                            ]
                                        }
                                    ],
                                    "parentFolderParticipants": []
                                },
                                "attachmentFolder": {
                                    "id": 239,
                                    "created": "2015-08-14T11:08:04.161-0400",
                                    "creator": "sally-acm",
                                    "modified": "2015-08-14T11:08:04.161-0400",
                                    "modifier": "sally-acm",
                                    "name": "/Sites/acm/documentLibrary/Timesheets/sally-acm/Timesheet 8_09_2015-8_15_2015",
                                    "cmisFolderId": "workspace://SpacesStore/85133deb-93c6-4a4a-b765-a4f3796ae98e",
                                    "parentFolderId": null,
                                    "status": "ACTIVE",
                                    "participants": [
                                        {
                                            "id": 353,
                                            "objectType": "FOLDER",
                                            "objectId": 239,
                                            "participantType": "*",
                                            "participantLdapId": "*",
                                            "created": "2015-08-14T11:08:04.169-0400",
                                            "creator": "sally-acm",
                                            "modified": "2015-09-04T08:56:10.733-0400",
                                            "modifier": "sally-acm",
                                            "privileges": [
                                                {
                                                    "id": 510,
                                                    "created": "2015-08-14T11:08:04.170-0400",
                                                    "creator": "sally-acm",
                                                    "modified": "2015-08-14T11:08:04.170-0400",
                                                    "modifier": "sally-acm",
                                                    "objectAction": "read",
                                                    "accessType": "grant",
                                                    "accessReason": "policy"
                                                }
                                            ]
                                        }
                                    ],
                                    "parentFolderParticipants": []
                                },
                                "calendarFolderId": null
                            }
                        }
                    ];


                    if (Validator.validateTimesheets(data)) {
                        var timesheets = data;
                        for (var i = 0; i < timesheets.length; i++) {
                            timesheets[i].acm$_formName = $translate.instant("cases.comp.time.formNamePrefix") + " " + timesheets[i].startDate + " - " + timesheets[i].endDate;
                            timesheets[i].acm$_hours = _.reduce(Util.goodArray(timesheets[i].times), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        $scope.gridOptions.data = timesheets;
                        $scope.gridOptions.totalItems = Util.goodValue(timesheets.length, 0);
                    }

                });
            } //end validate
        });


        $scope.showUrl = function (event, rowEntity) {
            event.preventDefault();
            Util.uiGrid.showObject("TIMESHEET", Util.goodMapValue([rowEntity, "id"], 0), $scope);
        };

    }
]);