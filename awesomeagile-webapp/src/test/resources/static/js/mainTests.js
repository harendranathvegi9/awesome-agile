describe("landing page", function() {
    beforeEach(module('awesome-agile'));

    var $controller, uibModalMock, uibModalInstanceMock;

    beforeEach(inject(function(_$controller_){
        $controller = _$controller_;

        uibModalMock = {
            open: function () {}
        };

        uibModalInstanceMock = {
            close: function () {}
        };

        spyOn(uibModalMock, "open");
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

    describe('aaModalController', function () {
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
});