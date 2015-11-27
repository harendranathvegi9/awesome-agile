/*
 * ================================================================================================
 * Awesome Agile
 * %%
 * Copyright (C) 2015 Mark Warren, Phillip Heller, Matt Kubej, Linghong Chen, Stanislav Belov, Qanit Al
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------------------------------------------
 */
var app = angular.module('awesome-agile', ['ui.bootstrap']);

app.controller('aaController',  function($scope, $uibModal, $http, $rootScope) {

    $scope.loggedIn = !!$rootScope.user;

    $scope.auth = function () {
        $http.get('/api/user').then(function (response) {
            if (response.data) {
                $rootScope.user = response.data;
                $scope.loggedIn = true;
                $scope.loggedOut = false;
            }
        }, function () {
            $scope.loggedOut = true;
        });
    };

    $scope.init = function () {
        $scope.auth();
    };

    $scope.init();


    $scope.scrumEvents = {
        'sprintPlanning': {
            'title': 'Sprint Planning',
            'description': 'Sprint planning occurs at the beginning of a sprint and the development team leverages this time to determine how to accomplish their work.  During this time the team prepares the sprint backlog and decomposes their work into tasks.'
        },
        'dailyScrum': {
            'title': 'Daily Scrum',
            'description': 'All members of the development team meet daily to deliver information on what he or she accomplished yesterday, will accomplish today, and if any blockers exist.  The gathering runs in a lightning round fashion.'
        },
        'tdd': {
            'title': 'Test Driven Development',
            'description': 'Test driven development is a software development process where work is completed in a cycle.  It begins with writing a test, which fails, then writing just enough code to make it pass and repeat.'
        },
        'sprintReview': {
            'title': 'Sprint Review',
            'description': 'Sprint review occurs at the end of a sprint to review and present the work completed over the course of the sprint to stakeholders.  Incomplete work should not be demonstrated during this time.'
        },
        'sprintRetrospective': {
            'title': 'Sprint Retrospective',
            'description': 'The sprint retrospective is the last event within a sprint and provides time to review what went well over the sprint and what could use improvement.  Upon conclusion of this meeting, the team should have actionable items to improve their upcoming sprints.'
        }
    };

    $scope.openLogin = function () {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: '/partials/loginModal.html',
            controller: 'loginModalController',
            size: 'sm'
        });
    };

    $scope.open = function (scrumEvent) {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: '/partials/aaModalContent.html',
            controller: 'aaModalController',
            resolve: {
                scrumEvent: function () {
                    return $scope.scrumEvents[scrumEvent];
                }
            }
        });
    }
});

app.controller('aaModalController', function ($scope, $uibModalInstance, scrumEvent) {

    if (scrumEvent) {
        $scope.title = scrumEvent.title;
        $scope.description = scrumEvent.description;
    }

    $scope.close = function () {
        $uibModalInstance.close();
    };
});

app.controller('loginModalController', function ($scope, $uibModalInstance) {

    $scope.close = function () {
        $uibModalInstance.close();
    };

});

