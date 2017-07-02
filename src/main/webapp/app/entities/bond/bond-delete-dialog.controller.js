(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('BondDeleteController',BondDeleteController);

    BondDeleteController.$inject = ['$uibModalInstance', 'entity', 'Bond'];

    function BondDeleteController($uibModalInstance, entity, Bond) {
        var vm = this;

        vm.bond = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Bond.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
