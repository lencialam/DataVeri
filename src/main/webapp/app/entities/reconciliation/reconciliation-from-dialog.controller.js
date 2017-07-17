(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('ReconciliationFromDialogController', ReconciliationFromDialogController);

    ReconciliationFromDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Reconciliation', 'Report'];

    function ReconciliationFromDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Reconciliation, Report) {
        var vm = this;

        vm.reconciliation = entity;
        vm.clear = clear;
        vm.save = save;
        vm.reports = Report.query({filter: 'reconciliation-is-null'});
        vm.report = Report.get({id : $stateParams.reportId});
        console.log($stateParams.reportId);
        $q.all([vm.reconciliation.$promise, vm.report.$promise]).then(function() {
            if (!vm.reconciliation.report || !vm.reconciliation.report.id) {
                vm.reconciliation.report = vm.report;
                console.log(vm.report);
                return $q.reject();
            }
            return Report.get({id : vm.reconciliation.report.id}).$promise;
        }).then(function(report) {

        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.reconciliation.id !== null) {
                Reconciliation.update(vm.reconciliation, onSaveSuccess, onSaveError);
            } else {
                Reconciliation.save(vm.reconciliation, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('dataVeriApp:reconciliationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
