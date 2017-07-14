(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('ReportDialogController', ReportDialogController);

    ReportDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Report', 'Trader', 'Reconciliation'];

    function ReportDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Report, Trader, Reconciliation) {
        var vm = this;

        vm.report = entity;
        vm.clear = clear;
        vm.save = save;
        vm.traders = Trader.query();
        vm.reconciliations = Reconciliation.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.report.id !== null) {
                Report.update(vm.report, onSaveSuccess, onSaveError);
            } else {
                Report.save(vm.report, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('dataVeriApp:reportUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
