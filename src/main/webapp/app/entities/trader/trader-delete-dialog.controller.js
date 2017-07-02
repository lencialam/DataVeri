(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('TraderDeleteController',TraderDeleteController);

    TraderDeleteController.$inject = ['$uibModalInstance', 'entity', 'Trader'];

    function TraderDeleteController($uibModalInstance, entity, Trader) {
        var vm = this;

        vm.trader = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Trader.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
