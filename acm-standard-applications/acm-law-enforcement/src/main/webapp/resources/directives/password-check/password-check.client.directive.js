'use strict';
/**
 *
 * @ngdoc directive
 * @name global.directive:passwordCheck
 *
 * @description
 *
 * The "password-check" directive validate the ldap password.
 * Check the complexity of the new password with regular expression (password should contain 7 characters with the combination of atleast one special character, number, uppercase and lowercase letters.).
 * Check if the new password contain the username.
 * Check if new password and confirmation password are different.
 *
 *
 * @param {string} usernameInput string for storing the userId
 * @param {string} newPassword string for storing the new password
 * @param {string} confirmNewPassword string for storing the confirmation of the new password
 * @param {string} [optional parameter] errorMessages string for storing the error messages
 * @param {string} newPasswordValidation binding the name of the password input field
 * @param {string} confirmNewPasswordValidation binding the name of the confirm password input field
 *
 *
 */

angular.module('directives').directive('passwordCheck', ['$translate' ,function($translate){
    return {
        resctrict: 'A',
        require: '^form',                            // access the FormController in a directive
        scope: {
            usernameInput: '=',
            newPassword: '=',
            confirmNewPassword: '=',
            errorMessages: '=',
            newPasswordValidation: '@',
            confirmNewPasswordValidation: '@'
        },
        link: function(scope, elem, attrs, formCtrl){
            scope.$watch('newPassword', function(){
                scope.checkPattern();
                scope.passwordContainsUsername();
                scope.differentPasswords();

            });
            scope.$watch('confirmNewPassword', function(){
                scope.differentPasswords();
            });

            scope.checkPattern = function(){
                var regExp = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{7,}$/;
                if( typeof scope.newPassword !== 'undefined' && !regExp.test(scope.newPassword)){
                    formCtrl[scope.newPasswordValidation].$setValidity('invalidPattern', false);
                    scope.errorMessages.invalidPatternMessage = $translate.instant("profile.modal.invalidPattern");
                }
                else{
                    formCtrl[scope.newPasswordValidation].$setValidity('invalidPattern', true);
                    scope.errorMessages.invalidPatternMessage = '';
                }
            };

            scope.passwordContainsUsername = function(){
                if(typeof scope.newPassword !== 'undefined' && scope.newPassword.indexOf(scope.usernameInput) !== -1){
                    formCtrl[scope.newPasswordValidation].$setValidity('containsUsername', false);
                    scope.errorMessages.containsUsernameMessage = $translate.instant("profile.modal.passwordContainsUsername");
                }
                else{
                    formCtrl[scope.newPasswordValidation].$setValidity('containsUsername', true);
                    scope.errorMessages.containsUsernameMessage = '';
                }
            };

            scope.differentPasswords = function(){
                if( typeof scope.confirmNewPassword !== 'undefined' && scope.newPassword !== scope.confirmNewPassword){
                    formCtrl[scope.newPasswordValidation].$setValidity('notSamePasswords', false);
                    formCtrl[scope.confirmNewPasswordValidation].$setValidity('notSamePasswords', false);
                    scope.errorMessages.notSamePasswordsMessage = $translate.instant("profile.modal.differentPasswords");
                }
                else{
                    formCtrl[scope.newPasswordValidation].$setValidity('notSamePasswords', true);
                    formCtrl[scope.confirmNewPasswordValidation].$setValidity('notSamePasswords', true);
                    scope.errorMessages.notSamePasswordsMessage = '';
                }
            };

        }

    }

}]);