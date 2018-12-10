'use strict';

/**
 * @ngdoc directive
 * @name global.directive:mentionInput
 * @restrict E
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/mention/mention-input.client.directive.js directives/mention/mention-input.client.directive.js}
 *
 * @param {string} ngModel - ngModel for the input.
 * @param {Object} params - it contains emailAddresses and usersMentioned array, which contain the email addresses and user names of the users that are mentioned in the input.
 * @param {string} placeholder - string for the input placeholder.
 *
 * The mentionInput directive is input with mention option.
 * Gets the email addresses and user names of the mentioned users and sets them in params.
 */
angular.module('directives').directive('mentionInput', [ 'UtilService', 'Mentions.Service', function(Util, MentionsService) {
    return {
        restrict: 'E',
        templateUrl: 'directives/mention/mention-input.client.directive.html',
        scope: {
            ngModel: '=',
            params: '=',
            placeholder: '@'
        },
        link: function($scope) {
            if(Util.isObjectEmpty($scope.params)){
                $scope.params = {
                    emailAddresses: [],
                    usersMentioned: []
                };
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