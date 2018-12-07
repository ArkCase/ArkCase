'use strict';

/**
 * @ngdoc directive
 * @name global.directive:mentionTextarea
 * @restrict E
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/mention/mention-textarea.client.directive.js directives/mention/mention-textarea.client.directive.js}
 *
 * @param {string} ngModel - ngModel for the input.
 * @param {Object} params - it contains emailAddresses and usersMentioned array, which contain the email addresses and user names of the users that are mentioned in the input.
 * @param {string} placeholder - string for the input placeholder.
 * @param {boolean} required - if the textarea is required or not.
 *
 * The mentionTextarea directive is textarea with mention option.
 * Gets the email addresses and user names of the mentioned users and sets them in params.
 */
angular.module('directives').directive('mentionTextarea', [ 'UtilService', 'Mentions.Service', function(Util, MentionsService) {
    return {
        restrict: 'E',
        templateUrl: 'directives/mention/mention-textarea.client.directive.html',
        scope: {
            ngModel: '=',
            params: '=',
            placeholder: '@',
            required: '@'
        },
        link: function($scope) {
            if(Util.isObjectEmpty($scope.params)){
                $scope.params = {
                    emailAddresses: [],
                    usersMentioned: []
                };
            }

            if(Util.isEmpty($scope.required)){
                $scope.required = false;
            }

            // Obtains a list of all users in ArkCase
            MentionsService.getUsers().then(function (users) {
                $scope.people = users;
            });

            $scope.getMentionedUsers = function (item) {
                $scope.params.emailAddresses.push(item.email_lcs);
                $scope.params.usersMentioned.push('@' + item.name);
                return '@' + item.name;
            };
        }
    };

} ]);