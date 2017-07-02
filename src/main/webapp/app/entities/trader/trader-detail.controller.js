(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('TraderDetailController', TraderDetailController);

    TraderDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Trader', 'User'];

    function TraderDetailController($scope, $rootScope, $stateParams, previousState, entity, Trader, User) {
        var vm = this;

        vm.trader = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('dataVeriApp:traderUpdate', function(event, result) {
            vm.trader = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
