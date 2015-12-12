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
describe("awesome agile", function() {
    beforeEach(module('awesome-agile'));

    var $controller, $rootScope, $q, $window, authService, documentsService, dashboardService, httpLocalBackend, uibModalMock, uibModalInstanceMock;

    beforeEach(inject(function(_$controller_, _$rootScope_, _$q_, _$window_, _authService_, _documentsService_, _dashboardService_, $httpBackend){
        $controller = _$controller_;
        $rootScope = _$rootScope_;
        $q = _$q_;
        $window = _$window_;
        authService = _authService_;
        dashboardService = _dashboardService_;
        documentsService = _documentsService_;
        httpLocalBackend = $httpBackend;

        uibModalMock = {
            open: function () {}
        };

        uibModalInstanceMock = {
            close: function () {}
        };

        spyOn(uibModalMock, "open");
        spyOn(uibModalInstanceMock, "close");
    }));

    describe('$scope.open', function() {
        it('should open a dialog', function() {
            var $scope = {};
            var controller = $controller('aaController', {
                $scope: $scope,
                $uibModal: uibModalMock
            });

            $scope.open();

            expect(uibModalMock.open).toHaveBeenCalled();
        });

        it('the dialog should resolve with the sprint planning meta when called with sprintPlanning', function() {
            var $scope = {};
            var controller = $controller('aaController', {
                $scope: $scope,
                $uibModal: uibModalMock
            });

            $scope.open('sprintPlanning');

            expect(uibModalMock.open.calls.mostRecent().args[0].resolve.scrumEvent().title).toEqual('Sprint Planning');
        });

        it('the dialog should resolve with the daily scrum meta when called with dailyScrum', function() {
            var $scope = {};
            var controller = $controller('aaController', {
                $scope: $scope,
                $uibModal: uibModalMock
            });

            $scope.open('dailyScrum');

            expect(uibModalMock.open.calls.mostRecent().args[0].resolve.scrumEvent().title).toEqual('Daily Scrum');
        });

        it('the dialog should resolve with the test driven development meta when called with tdd', function() {
            var $scope = {};
            var controller = $controller('aaController', {
                $scope: $scope,
                $uibModal: uibModalMock
            });

            $scope.open('tdd');

            expect(uibModalMock.open.calls.mostRecent().args[0].resolve.scrumEvent().title).toEqual('Test Driven Development');
        });

        it('the dialog should resolve with the sprint review meta when called with sprintReview', function() {
            var $scope = {};
            var controller = $controller('aaController', {
                $scope: $scope,
                $uibModal: uibModalMock
            });

            $scope.open('sprintReview');

            expect(uibModalMock.open.calls.mostRecent().args[0].resolve.scrumEvent().title).toEqual('Sprint Review');
        });

        it('the dialog should resolve with the sprint retrospective meta when called with sprintRetrospective', function() {
            var $scope = {};
            var controller = $controller('aaController', {
                $scope: $scope,
                $uibModal: uibModalMock
            });

            $scope.open('sprintRetrospective');

            expect(uibModalMock.open.calls.mostRecent().args[0].resolve.scrumEvent().title).toEqual('Sprint Retrospective');
        });
    });

    describe('authService', function () {
        it('should set a user to authed when user data is returned from the /api/user service', function () {
            var url = '/api/user';
            var httpResponse = {
                displayName: 'test user'
            };
            httpLocalBackend.expectGET(url).respond(200, httpResponse);

            authService.isAuthed().then(function (result) {
                expect(result).toEqual(true);
            });

            httpLocalBackend.flush();
        });

        it('should NOT set a user to authed when user data is NOT returned from the /api/user service', function () {
            var url = '/api/user';
            var httpResponse = {};
            httpLocalBackend.expectGET(url).respond(401, httpResponse);

            authService.isAuthed().then(function (result) {
                expect(result).toEqual(false);
            });

            httpLocalBackend.flush();
        });

        it('should NOT set a user to authed when no user data is returned from the /api/user service', function () {
            var url = '/api/user';
            var httpResponse = null;
            httpLocalBackend.expectGET(url).respond(200, httpResponse);

            authService.isAuthed().then(function (result) {
                expect(result).toEqual(false);
            });

            httpLocalBackend.flush();
        });
    });

    describe('documentsService', function () {
        it('should set the definition of ready document within the documents object on a successful /api/hackpad/defready POST with a valid response', function () {
            var url = '/api/hackpad/DEFINITION_OF_READY';
            var httpResponse = {
                url: 'http://hackpad.com/someid'
            };
            httpLocalBackend.expectPOST(url).respond(200, httpResponse);

            documentsService.createDefReady().then(function () {
                expect($rootScope.documents.DEFINITION_OF_READY).toBe(httpResponse.url);
            });

            httpLocalBackend.flush();
        });

        it('should NOT set the definition of ready document within the documents object on a successful /api/hackpad/defready POST without a valid response', function () {
            var url = '/api/hackpad/DEFINITION_OF_READY';
            var httpResponse = {
                foo: 'bar'
            };
            httpLocalBackend.expectPOST(url).respond(200, httpResponse);

            documentsService.createDefReady().then(function () {
                expect($rootScope.documents.DEFINITION_OF_READY).toBeUndefined();
            });

            httpLocalBackend.flush();
        });

        it('should NOT set the definition of ready document within the documents object on a failed /api/hackpad/defready POST', function () {
            var url = '/api/hackpad/DEFINITION_OF_READY';
            var httpResponse = {
                foo: 'bar'
            };
            httpLocalBackend.expectPOST(url).respond(401, httpResponse);

            documentsService.createDefReady().then(function () {
                expect($rootScope.documents.DEFINITION_OF_READY).toBeUndefined();
            });

            httpLocalBackend.flush();
        });
    });

    describe('dashboardService', function () {
        it('should add the returned documents to the rootScope on successful GET to /api/dashboard with a getInfo call', function () {
            var url = '/api/dashboard';
            var httpResponse = {
                documents: {
                    DEFINITION_OF_READY: 'http://hackpad.com/someid'
                }
            };
            httpLocalBackend.expectGET(url).respond(200, httpResponse);

            dashboardService.getInfo().then(function () {
                expect($rootScope.documents).toEqual(httpResponse.documents);
            });

            httpLocalBackend.flush();
        });

        it('should NOT add any documents to the rootScope on successful GET to /api/dashboard with an improper response with a getInfo call', function () {
            var url = '/api/dashboard';
            var httpResponse = {
                foo: 'bar'
            };
            httpLocalBackend.expectGET(url).respond(200, httpResponse);

            dashboardService.getInfo().then(function () {
                expect($rootScope.documents).toEqual({});
            });

            httpLocalBackend.flush();
        });

        it('should NOT change the rootSCope on a failed GET to the /api/dashboard with a getInfo call', function () {
            var url = '/api/dashboard';
            var httpResponse = {
                foo: 'bar'
            };
            httpLocalBackend.expectGET(url).respond(401, httpResponse);

            dashboardService.getInfo().then(function () {
                expect($rootScope.documents).toEqual({});
            });

            httpLocalBackend.flush();
        });
    });

    describe('routeProvider', function () {
        it('should navigate the user to the landing page if not authed', function () {
            inject(function ($route, $location) {
                var url = '/api/user';
                var httpResponse = {};
                httpLocalBackend.expectGET(url).respond(401, httpResponse);

                httpLocalBackend.whenGET('partials/landingPage.html').respond(200, '');

                expect($route.current).toBeUndefined();
                $location.path('/');
                $rootScope.$digest();

                expect($route.current.templateUrl).toBe('partials/landingPage.html');
            });
        });

        it('should navigate the user to the dashboard if user is authed', function () {
            inject(function ($route, $location) {
                var url = '/api/user';
                var httpResponse = {
                    displayName: 'test user'
                };
                httpLocalBackend.expectGET(url).respond(200, httpResponse);

                httpLocalBackend.whenGET('partials/landingPage.html').respond(200, '');
                httpLocalBackend.whenGET('partials/dashboard.html').respond(200, '');

                expect($route.current).toBeUndefined();
                $location.path('/dashboard');
                $rootScope.$digest();

                expect($route.current.templateUrl).toBe('partials/dashboard.html');
            });
        });
    });

    describe('$scope.open', function() {
        it('should open a dialog', function () {
            var $scope = {};
            var controller = $controller('aaController', {
                $scope: $scope,
                $uibModal: uibModalMock
            });

            $scope.open();

            expect(uibModalMock.open).toHaveBeenCalled();
        });
    });

    describe('$scope.openLogin', function() {
        it('should open a dialog', function () {
            var $scope = {};
            var controller = $controller('aaController', {
                $scope: $scope,
                $uibModal: uibModalMock
            });

            $scope.openLogin();

            expect(uibModalMock.open).toHaveBeenCalled();
        });
    });

    describe('loginModalController', function () {
        it('$scope.close', function () {
            var $scope = {};
            var controller = $controller('loginModalController', {
                $scope: $scope,
                $uibModalInstance: uibModalInstanceMock
            });

            $scope.close();

            expect(uibModalInstanceMock.close).toHaveBeenCalled();
        });
    });

    describe('aaModalController', function () {
        it('$scope.close', function () {
            var $scope = {};
            var scrumEvent = {
                'title': 'testTitle',
                'description': 'testDescription'
            };
            var controller = $controller('aaModalController', {
                $scope: $scope,
                $uibModalInstance: uibModalInstanceMock,
                scrumEvent: scrumEvent
            });

            $scope.close();

            expect(uibModalInstanceMock.close).toHaveBeenCalled();
        });

        it('should set the title of the modal based on the scrum event', function () {
            var $scope = {};
            var scrumEvent = {
                'title': 'testTitle',
                'description': 'testDescription'
            };
            var controller = $controller('aaModalController', {
                $scope: $scope,
                $uibModalInstance: uibModalInstanceMock,
                scrumEvent: scrumEvent
            });

            expect($scope.title).toBe(scrumEvent.title);
        });

        it('should set the description of the modal based on the scrum event', function () {
            var $scope = {};
            var scrumEvent = {
                'title': 'testTitle',
                'description': 'testDescription'
            };
            var controller = $controller('aaModalController', {
                $scope: $scope,
                $uibModalInstance: uibModalInstanceMock,
                scrumEvent: scrumEvent
            });

            expect($scope.description).toBe(scrumEvent.description);
        })
    });

    describe('aaToolsCarouselCtrl', function () {
        it('should set assigned number to myInterval', function () {
            var $scope = {};
            var controller = $controller('aaToolsCarouselCtrl', {
                $scope: $scope
            });

            expect($scope.myInterval).toBe(5000);
        });
        it('should set noWrapSlides to true', function () {
            var $scope = {};
            var controller = $controller('aaToolsCarouselCtrl', {
                $scope: $scope
            });

            expect($scope.noWrapSlides).toBe(true);
        });
        it('should add hackpad tool to carousel slides', function () {
            var $scope = $rootScope.$new();
            var controller = $controller('aaToolsCarouselCtrl', {
                $scope: $scope
            });

            expect($scope.slides[0].title).toBe('Hackpad');
        });
        it('should add Trello tool to carousel slides', function () {
            var $scope = $rootScope.$new();
            var controller = $controller('aaToolsCarouselCtrl', {
                $scope: $scope
            });

            expect($scope.slides[1].title).toBe('Trello');
        });
        it('should add github tool to carousel slides', function () {
            var $scope = $rootScope.$new();
            var controller = $controller('aaToolsCarouselCtrl', {
                $scope: $scope
            });

            expect($scope.slides[2].title).toBe('GitHub');
        });
        it('should add Travis CI tool to carousel slides', function () {
            var $scope = $rootScope.$new();
            var controller = $controller('aaToolsCarouselCtrl', {
                $scope: $scope
            });

            expect($scope.slides[3].title).toBe('Travis CI');
        });
    });

    describe('aaToolsCtrl', function () {
        it('should call the dashboard service on init and set any documents to the scope', function () {
            var $scope = $rootScope.$new();

            var url = '/api/dashboard';
            var httpResponse = {
                documents: {
                    DEFINITION_OF_READY: 'http://hackpad.com/someid'
                }
            };
            httpLocalBackend.expectGET(url).respond(200, httpResponse);

            var controller = $controller('aaToolsCtrl', {
                $rootScope: $rootScope,
                $scope: $scope,
                $window: $window,
                documentsService: documentsService,
                dashboardService: dashboardService
            });

            httpLocalBackend.flush();

            expect($rootScope.documents.DEFINITION_OF_READY).toBe(httpResponse.documents.DEFINITION_OF_READY);
        });

        it('should have the loading state of getting the definition of ready set to false by default', function () {
            var $scope = $rootScope.$new();
            var controller = $controller('aaToolsCtrl', {
                $rootScope: $rootScope,
                $scope: $scope,
                $window: $window,
                documentsService: documentsService,
                dashboardService: dashboardService
            });

            expect($scope.defReadyLoading).toBe(false);
        });

        it('should call the documentsService createDefReady function when createDefReady is executed and set definition of ready loading to true while in process', function () {
            var $scope = $rootScope.$new();
            var controller = $controller('aaToolsCtrl', {
                $rootScope: $rootScope,
                $scope: $scope,
                $window: $window,
                documentsService: documentsService,
                dashboardService: dashboardService
            });

            var url = '/api/dashboard';
            var httpResponse = {
                documents: {
                    DEFINITION_OF_READY: 'http://hackpad.com/someid'
                }
            };
            httpLocalBackend.expectGET(url).respond(200, httpResponse);

            var url = '/api/hackpad/DEFINITION_OF_READY';
            var httpResponse = {
                url: 'http://hackpad.com/someid'
            };
            httpLocalBackend.expectPOST(url).respond(200, httpResponse);

            $scope.createDefReady();

            expect($scope.defReadyLoading).toBe(true);

            httpLocalBackend.flush();
        });

        it('should call the documentsService createDefReady function when createDefReady is executed and set definition of ready loading to false on success', function () {
            var $scope = $rootScope.$new();
            var controller = $controller('aaToolsCtrl', {
                $rootScope: $rootScope,
                $scope: $scope,
                $window: $window,
                documentsService: documentsService,
                dashboardService: dashboardService
            });

            var url = '/api/dashboard';
            var httpResponse = {
                documents: {
                    DEFINITION_OF_READY: 'http://hackpad.com/someid'
                }
            };
            httpLocalBackend.expectGET(url).respond(200, httpResponse);

            var url = '/api/hackpad/DEFINITION_OF_READY';
            var httpResponse = {
                url: 'http://hackpad.com/someid'
            };
            httpLocalBackend.expectPOST(url).respond(200, httpResponse);

            $scope.createDefReady();

            httpLocalBackend.flush();

            expect($scope.defReadyLoading).toBe(false);
        });

        it('should open a new tab with the definition of ready when viewDefReady is executed', function () {
            var $scope = $rootScope.$new();
            $rootScope.documents.DEFINITION_OF_READY = 'https://hackpad.com/someid';
            var controller = $controller('aaToolsCtrl', {
                $rootScope: $rootScope,
                $scope: $scope,
                $window: $window,
                documentsService: documentsService,
                dashboardService: dashboardService
            });

            spyOn($window, "open");

            $scope.viewDefReady();

            expect($window.open).toHaveBeenCalledWith($rootScope.documents.DEFINITION_OF_READY, '_blank');
        });
    });
});

describe('Directive', function() {
    var $compile,
        $rootScope,
        httpLocalBackend;

    beforeEach(module('awesome-agile'));

    beforeEach(inject(function(_$compile_, _$rootScope_, $httpBackend){
        $compile = _$compile_;
        $rootScope = _$rootScope_;
        httpLocalBackend = $httpBackend;
    }));

    it('Replace agileWorkflow with appropriate content', function() {
        var element = $compile("<div agile-workflow></div agile-workflow>")($rootScope);

        httpLocalBackend.whenGET('partials/agileWorkflow.html').respond(200, 'Agile Sprint');

        $rootScope.$digest();

        httpLocalBackend.flush();

        expect(element.html()).toContain("Agile Sprint");
    });
});