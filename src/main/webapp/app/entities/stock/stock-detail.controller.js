(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('StockDetailController', StockDetailController);

    StockDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Stock'];

    function StockDetailController($scope, $rootScope, $stateParams, previousState, entity, Stock) {
        var vm = this;

        vm.stock = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('dataVeriApp:stockUpdate', function(event, result) {
            vm.stock = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
