(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('BondDialogController', BondDialogController);

    BondDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Bond', 'Trader'];

    function BondDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Bond, Trader) {
        var vm = this;

        vm.bond = entity;
        vm.clear = clear;
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
            if (vm.bond.id !== null) {
                Bond.update(vm.bond, onSaveSuccess, onSaveError);
            } else {
                Bond.save(vm.bond, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('dataVeriApp:bondUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
