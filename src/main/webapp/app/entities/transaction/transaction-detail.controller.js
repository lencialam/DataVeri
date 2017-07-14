(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('TransactionDetailController', TransactionDetailController);

    TransactionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Transaction', 'Trader'];

    function TransactionDetailController($scope, $rootScope, $stateParams, previousState, entity, Transaction, Trader) {
        var vm = this;

        vm.transaction = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('dataVeriApp:transactionUpdate', function(event, result) {
            vm.transaction = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
