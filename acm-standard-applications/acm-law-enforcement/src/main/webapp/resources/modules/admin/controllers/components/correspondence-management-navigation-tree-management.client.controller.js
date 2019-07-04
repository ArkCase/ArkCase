'use strict';

angular.module('admin').controller('Admin.NavigationTreeCMTemplateController', ['$scope', '$modalInstance', '$translate', function ($scope, $modalInstance, $translate) {
        setTimeout(function () {
            $scope.myTree.collapse_all();
        });

        var copyVariablePath = $translate.instant("contextMenu.options.copyVariablePath");

        $scope.onClickCancel = function () {
            $modalInstance.dismiss('Cancel');
        };

        $scope.contextMenuOptions = function () {
            $scope.variablePath = '${';

            $scope.navigationTreeMilestones = [];

            var menuOptions = [
                {
                    text: copyVariablePath,
                    click: $scope.selectParentBranch()
                }
            ];

            //Remove the keyRoot
            $scope.navigationTreeMilestones.pop();

            for (var i = $scope.navigationTreeMilestones.length - 1; i >= 0; i--) {
                var milestoneLabel = $scope.navigationTreeMilestones[i].label;
                //Removing the dot on the last label
                if (i == 0) {
                    $scope.variablePath += milestoneLabel.substring(0, milestoneLabel.length - 1);
                } else {
                    $scope.variablePath += milestoneLabel;
                }
            }

            $scope.variablePath += '}';

            $scope.copyToClipboard($scope.variablePath);
            return menuOptions;
        };

        $scope.copyToClipboard = function (str) {
            var el = document.createElement('textarea');
            el.value = str;
            document.body.appendChild(el);
            el.select();
            document.execCommand('copy');
            document.body.removeChild(el);
        };

        $scope.selectParentBranch = function (branch) {
            var parent;
            var index = 0;
            if (branch == null) {
                branch = $scope.myTree.get_selected_branch();
                var branchLabel = branch.label;
                if (branch.data == "array") {
                    branchLabel += '[X].';
                } else {
                    branchLabel += '.';
                }

                var milestoneBranch = {
                    label: branchLabel,
                    index: index
                };

                index++;
                $scope.navigationTreeMilestones.push(milestoneBranch);
            }
            if (branch != null) {
                parent = $scope.myTree.get_parent_branch(branch);
                if (parent != undefined) {
                    var parentLabel = parent.label;
                    if (parent.data == "array") {
                        parentLabel += '[X].';
                    } else {
                        parentLabel += '.';
                    }
                    var milestoneParent = {
                        label: parentLabel,
                        index: index
                    };

                    index++;
                    $scope.navigationTreeMilestones.push(milestoneParent);
                    $scope.selectParentBranch(parent);
                }
            }

        };
    }
]);