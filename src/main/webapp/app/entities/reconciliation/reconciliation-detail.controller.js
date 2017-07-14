(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('ReconciliationDetailController', ReconciliationDetailController);

    ReconciliationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Reconciliation', 'Report'];

    function ReconciliationDetailController($scope, $rootScope, $stateParams, previousState, entity, Reconciliation, Report) {
        var vm = this;

        vm.reconciliation = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('dataVeriApp:reconciliationUpdate', function(event, result) {
            vm.reconciliation = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
