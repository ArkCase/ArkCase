'use strict';

/**
 * @ngdoc directive
 * @name global.directive:mentionSummernote
 * @restrict E
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/mention/mention-summernote.client.directive.js directives/mention/mention-summernote.client.directive.js}
 *
 * @param {string} ngModel - ngModel for the input.
 * @param {Object} params - it contains emailAddresses and usersMentioned array, which contain the email addresses and user names of the users that are mentioned in the input.
 * @param {boolean} focus - focus on the summernote or not.
 *
 * The mentionSummernote directive is summernote with mention option.
 * Gets the email addresses and user names of the mentioned users and sets them in params.
 */
angular.module('directives').directive('mentionSummernote', [ 'UtilService', 'Mentions.Service', 'PermissionsService', '$timeout', function(Util, MentionsService, PermissionsService, $timeout) {
    return {
        restrict: 'E',
        templateUrl: 'directives/mention/mention-summernote.client.directive.html',
        scope: {
            ngModel: '=',
            params: '=',
            permissions: '=',
            focus: '@'

        },
        controller: function ($scope) {
            $scope.options = {
                focus: $scope.focus,
                dialogsInBody: true,
                hint: {
                    match: /\B@(\w*)$/,
                    search: function(keyword, callback) {
                        callback($.grep($scope.people, function(item) {
                            return item.indexOf(keyword) == 0;
                        }));
                    },
                    content: function(item) {
                        var index = $scope.people.indexOf(item);
                        $scope.params.emailAddresses.push($scope.peopleEmails[index]);
                        $scope.params.usersMentioned.push('@' + item);
                        return '@' + item;
                    }
                }
            };

            $scope.init = function () {
                if(!Util.isEmpty($scope.permissions)) {
                    PermissionsService.getActionPermission($scope.permissions.actionName, $scope.permissions.objectProperties,
                        $scope.permissions.opts).then(function (result) {
                        if (!result) {
                            $timeout(function () {
                                $scope.editor.summernote('disable');
                            }, 0);
                        }
                    });
                }
            };

            $scope.$watch('permissions', function(){
                //called any time $scope.permissions changes
                $scope.init();
            });
        },
        link: function($scope) {
            if(Util.isEmpty($scope.params)){
                $scope.params = {
                    emailAddresses: [],
                    usersMentioned: [],
                };
            }

            $scope.people = [];
            $scope.peopleEmails = [];

            // Obtains a list of all users in ArkCase
            MentionsService.getUsers().then(function(users) {
                _.forEach(users, function(user) {
                    $scope.people.push(user.name);
                    $scope.peopleEmails.push(user.email_lcs);
                });
            });
        }
    };

} ]);