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
var app = angular.module('awesome-agile', ['ngRoute', 'ui.bootstrap']);

app.factory('authService', function ($rootScope, $http, $q) {

    $rootScope.user = {
        isAuthed: false,
        name: ''
    };

    var authService = {};

    authService.isAuthed = function () {
        var deferred = $q.defer();

        $http.get('/api/user').then(function (response) {
            if (response.data) {
                $rootScope.user.isAuthed = true;
                $rootScope.user.name = response.data.displayName;
                deferred.resolve(true);
            } else {
                deferred.resolve(false);
            }
        }, function () {
            deferred.resolve(false);
        });

        return deferred.promise;
    };

    return authService;

});

app.factory('dashboardService', function ($rootScope, $http, $q) {

    var dashboardService = {};

    dashboardService.getInfo = function () {
        var deferred = $q.defer();

        $http.get('/api/dashboard').then(function (response) {
            if (response.data.documents) {
                $rootScope.documents = response.data.documents;
                deferred.resolve(true);
            } else {
                deferred.resolve(false);
            }
        }, function () {
            deferred.resolve(false);
        });

        return deferred.promise;
    };

    return dashboardService;

});

app.factory('documentsService', function ($rootScope, $http, $q) {

    $rootScope.documents = {};

    var documentsService = {};

    documentsService.createDefReady = function () {
        var deferred = $q.defer();

        var request = {};
        $http.post('/api/hackpad/defnready', request).then(function (response) {
            if (response.data) {
                $rootScope.documents.defready = response.data.url;
                deferred.resolve(true);
            } else {
                deferred.resolve(false);
            }
        }, function () {
            deferred.resolve(false);
        });

        return deferred.promise;
    };

    return documentsService;

});

app.config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'partials/landingPage.html',
            allowAnon: true
        })
        .when('/dashboard', {
            templateUrl:'partials/dashboard.html'
        });
}]);

app.run(['$rootScope', '$location', 'authService', function ($rootScope, $location, authService) {
    $rootScope.$on('$routeChangeStart', function (event, next, current) {
        authService.isAuthed().then(function (isAuthed) {
            if (!isAuthed && next && !next.allowAnon) {
                event.preventDefault();
                $location.path('/');
            } else if (isAuthed && next && next.templateUrl === 'partials/landingPage.html') {
                event.preventDefault();
                $location.path('/dashboard');
            }
        });
    });
}]);

app.directive('agileWorkflow', function () {
    return {
        templateUrl: 'partials/agileWorkflow.html'
    };
});

app.controller('aaController',  function($scope, $uibModal) {

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
            'description': 'Test driven development is a software development process where work is completed in a cycle. It begins with writing a test, which fails, continuing with writing just enough code to make the test pass, then refactoring the code to improve its design, meaning making a few steps of tiny changes in source code each of which still passes the test. This cycle is then repeated.'
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
            templateUrl: 'partials/loginModal.html',
            controller: 'loginModalController',
            size: 'sm'
        });
    };

    $scope.open = function (scrumEvent) {
        var modalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'partials/aaModalContent.html',
            controller: 'aaModalController',
            resolve: {
                scrumEvent: function () {
                    return $scope.scrumEvents[scrumEvent];
                }
            }
        });
    };

    $scope.artifact = {
        backlogRefinement: "Backlog refinement is the process where the scrum team reviews and revises the product backlog.  This includes providing more clarity to the backlog items, identifying dependencies, as well as adjusting the prioritization of backlog items.",
        continuousIntegration: "Continuous integration is a development practice where developers integrate code into a common repository multiple times a day.  Each delivery to the common repository triggers automation to verify the quality latest changes as a means of identifying problems early.",
        definitionOfDone: "The definition of done is an artifact serving as a checklist for scrum teams to identify what needs to be completed in order for a backlog item to be deemed complete.  Examples of this includes requiring a certain level of code quality or writing sufficient documentation.",
        definitionOfReady: "The definition of ready is an artifact serving as a checklist for scrum teams to identify when a backlog item is ready to be brought into a sprint for work.  This includes defining clear completion criteria and insuring the item is well defined.",
        estimation: "Estimation is an activity leveraged by scrum teams to size the product backlog of each item based on relative complexity to each other.  Determining these estimates help spark discussion amongst the team about the tasks as well as help them plan their work.",
        pairProgramming: "Pair programming is a development technique, in which two developers work together on a single workstation.  One developer drives, while the other navigates, and they regularly switch.  This motivates a high level of collaboration between the developers, assists with knowledge transfer, and helps maintain high code quality.",
        sourceCodeManagement: "Source code management provides development teams coordination of their work predominantly through file management and version control.  It provides a high level of accessibility and transparency amongst the team members on their work and provides a common record of fact.",
        userStories: "User stories serve as short stories of a feature from the perspective of the person who benefits from the new capability.  This is a shift from defining requirements to looking at the feature through eyes of the user in order to deliver more value."
    };
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

app.controller('aaToolsCarouselCtrl', function ($scope) {
    toolDescriptions = {
        hackpad: {
            'title': 'Hackpad',
            'image': 'images/hackpad.png',
            'description': 'Hackpad is a web based text editor tool. It offers real-time collaboration across devices and users.',
            'link': "https://hackpad.com/"
        },
        trello: {
            'title': 'Trello',
            'image': 'images/trello.png',
            'description': "Trello is a collaboration tool that organizes your projects into boards. In one glance, Trello tells you what's being worked on, who's working on what, and where something is in a process.",
            'link': "https://trello.com/"
        },
        github: {
            'title': 'GitHub',
            'image': 'images/github.png',
            'description': "GitHub is a source code repository hosting service, based on Git. While Git is a command line tool, GitHub provides a Web-based graphical interface. It also provides access control and several collaboration features, such as a wikis and basic task management tools for every project.",
            'link': "https://github.com/"
        },
        travisci: {
            'title': 'Travis CI',
            'image': 'images/TravisCI.png',
            'description': "Travis CI is a hosted continuous integration platform that is free for all open source projects hosted on Github. With just a file called .travis.yml containing some information about our project, we can trigger automated builds with every change to our code base in the master branch, other branches or even a pull request.",
            'link': "https://travis-ci.org/"
        }
    };

    $scope.myInterval = 5000;
    $scope.noWrapSlides = true;
    var slides = $scope.slides = [];
    if (toolDescriptions) {
        slides.push(toolDescriptions.hackpad);
        slides.push(toolDescriptions.trello);
        slides.push(toolDescriptions.github);
        slides.push(toolDescriptions.travisci);
    }

});

app.controller('aaToolsCtrl', function ($rootScope, $scope, $window, documentsService, dashboardService) {

    $scope.defReadyLoading = false;

    var init = function () {
        dashboardService.getInfo();
    };

    init();

    $scope.createDefReady = function () {
        $scope.defReadyLoading = true;
        documentsService.createDefReady().then(function () {
            $scope.defReadyLoading = false;
        });
    };

    $scope.viewDefReady = function () {
        if ($rootScope.documents.defready) {
            $window.open($rootScope.documents.defready, '_blank');
        }
    };
});