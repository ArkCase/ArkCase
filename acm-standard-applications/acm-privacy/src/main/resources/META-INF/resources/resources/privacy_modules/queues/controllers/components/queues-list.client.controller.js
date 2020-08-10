'use strict';

angular.module('queues').controller('Queues.QueuesListController', [ '$scope', '$stateParams', 'Queues.QueuesService', function($scope, $stateParams, QueuesService) {
    $scope.$on('component-config', applyConfig);
    $scope.$emit('req-component-config', 'queues');

    QueuesService.queryQueues().then(function(queues) {

        //$scope.queues = queues;
        //remove appeal and hold queues from list and add them to queuesDown
        $scope.queuesDown = [];
        $scope.queuesUp = [];

        for (var i = 0; i < queues.length; i++) {
            if (queues[i].name == 'Hold') {
                $scope.queuesDown.push(queues[i]);
            } else {
                $scope.queuesUp.push(queues[i]);
            }

        }

        // Original : Select first item by default;
        // Update : SBI-769
        // Queue Module - Default should be the Transcribe Queue
        if (queues.length > 0) {
            if ($stateParams['name']) {
                var queueName = $stateParams['name'];
                for (var i = 0; i < queues.length; i++) {
                    if (queueName == queues[i].name) {
                        selectQueue(null, queues[i]);
                        break;
                    }
                }
            } else {
                if (queues.length > 0) {
                    selectQueue(null, queues[0]);
                }
            }
        }
    });

    $scope.selectedQueue = null;
    $scope.selectQueue = selectQueue;

    /**
     * Apply component config
     * @param e
     * @param componentId
     * @param config
     */
    function applyConfig(e, componentId, config) {
        if (componentId == 'queues') {
            $scope.config = config;
        }
    }

    /**
     * Performs queue selection action
     * @param $event
     * @param queue
     */
    function selectQueue($event, queue) {
        if ($event) {
            $event.preventDefault();
        }
        $scope.selectedQueue = queue;
        $scope.$emit('req-select-queue', queue);
    }
} ]);