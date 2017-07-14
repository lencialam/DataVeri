'use strict';

describe('Controller Tests', function() {

    describe('Reconciliation Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockReconciliation, MockReport;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockReconciliation = jasmine.createSpy('MockReconciliation');
            MockReport = jasmine.createSpy('MockReport');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Reconciliation': MockReconciliation,
                'Report': MockReport
            };
            createController = function() {
                $injector.get('$controller')("ReconciliationDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'dataVeriApp:reconciliationUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
