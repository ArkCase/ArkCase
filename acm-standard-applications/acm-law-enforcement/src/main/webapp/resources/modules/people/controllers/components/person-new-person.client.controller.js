'use strict';

angular.module('people').controller('People.NewPersonController', ['$scope', '$stateParams', '$translate'
    , 'Person.InfoService', '$state', 'Object.LookupService', 'MessageService', '$timeout'
    , function ($scope, $stateParams, $translate, PersonInfoService, $state, ObjectLookupService, MessageService, $timeout) {

        var contactMethodsCounts = {
            'url': 0,
            'phone': 0,
            'email': 0
        };
        //new person with predefined values
        $scope.person = {
            className: 'com.armedia.acm.plugins.person.model.Person',
            contactMethods: [],
            defaultEmail: {
                type: 'email'
            },
            defaultPhone: {
                type: 'phone'
            },
            defaultUrl: {
                type: 'url'
            }
        };

        ObjectLookupService.getContactMethodTypes().then(function (contactMethodTypes) {
            $scope.phoneTypes = _.find(contactMethodTypes, {type: 'phone'}).subTypes;
            $scope.emailTypes = _.find(contactMethodTypes, {type: 'email'}).subTypes;
            $scope.urlTypes = _.find(contactMethodTypes, {type: 'url'}).subTypes;
        });

        $scope.addContactMethod = function (contactType) {
            $timeout(function () {
                contactMethodsCounts[contactType]++;
                $scope.person.contactMethods.push({
                    type: contactType
                });
            }, 0);
        };

        $scope.removeContactMethod = function (contact) {
            $timeout(function () {
                contactMethodsCounts[contact.type]--;
                _.remove($scope.person.contactMethods, function (object) {
                    return object === contact;
                });
            }, 0);
        };

        $scope.showAddAnotherContactMethod = function (contactType) {
            return contactMethodsCounts[contactType] < 1;
        };

        $scope.save = function () {

            var promiseSavePerson = PersonInfoService.savePersonInfo(clearNotFilledElements(_.cloneDeep($scope.person)));
            promiseSavePerson.then(
                function (objectInfo) {
                    $scope.$emit("report-object-updated", objectInfo);
                    MessageService.info($translate.instant("people.comp.newPerson.informSaved"));
                    $state.go('people');
                    return objectInfo;
                }
                , function (error) {
                    $scope.$emit("report-object-update-failed", error);
                    return error;
                }
            );
        };
        function clearNotFilledElements(person) {
            if (!person.defaultPhone.value) {
                person.defaultPhone = null;
            }
            if (!person.defaultEmail.value) {
                person.defaultEmail = null;
            }
            if (!person.defaultUrl.value) {
                person.defaultUrl = null;
            }
            //TODO do same for aliases, address, etc...
            return person;
        }
    }
]);