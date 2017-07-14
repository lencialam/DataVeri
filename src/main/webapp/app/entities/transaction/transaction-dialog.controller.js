(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('TransactionDialogController', TransactionDialogController);

    TransactionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Transaction', 'Trader'];

    function TransactionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Transaction, Trader) {
        var vm = this;

        vm.transaction = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.traders = Trader.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.transaction.id !== null) {
                Transaction.update(vm.transaction, onSaveSuccess, onSaveError);
            } else {
                Transaction.save(vm.transaction, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('dataVeriApp:transactionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.tradeTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
