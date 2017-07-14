(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('ReconciliationDeleteController',ReconciliationDeleteController);

    ReconciliationDeleteController.$inject = ['$uibModalInstance', 'entity', 'Reconciliation'];

    function ReconciliationDeleteController($uibModalInstance, entity, Reconciliation) {
        var vm = this;

        vm.reconciliation = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Reconciliation.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
