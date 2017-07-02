(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .controller('BondDetailController', BondDetailController);

    BondDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Bond', 'Trader'];

    function BondDetailController($scope, $rootScope, $stateParams, previousState, entity, Bond, Trader) {
        var vm = this;

        vm.bond = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('dataVeriApp:bondUpdate', function(event, result) {
            vm.bond = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
