(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('StockDialogController', StockDialogController);

    StockDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Stock'];

    function StockDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Stock) {
        var vm = this;

        vm.stock = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.stock.id !== null) {
                Stock.update(vm.stock, onSaveSuccess, onSaveError);
            } else {
                Stock.save(vm.stock, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('dataVeriApp:stockUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
