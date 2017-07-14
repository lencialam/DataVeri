(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('ReportDetailController', ReportDetailController);

    ReportDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Report', 'Trader'];

    function ReportDetailController($scope, $rootScope, $stateParams, previousState, entity, Report, Trader) {
        var vm = this;

        vm.report = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('dataVeriApp:reportUpdate', function(event, result) {
            vm.report = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
