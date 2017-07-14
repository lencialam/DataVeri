(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('StockDeleteController',StockDeleteController);

    StockDeleteController.$inject = ['$uibModalInstance', 'entity', 'Stock'];

    function StockDeleteController($uibModalInstance, entity, Stock) {
        var vm = this;

        vm.stock = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Stock.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
