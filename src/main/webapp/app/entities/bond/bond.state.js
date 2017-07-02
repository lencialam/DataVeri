(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('bond', {
            parent: 'entity',
            url: '/bond?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Bonds'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/bond/bonds.html',
                    controller: 'BondController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }]
            }
        })
        .state('bond-detail', {
            parent: 'bond',
            url: '/bond/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Bond'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/bond/bond-detail.html',
                    controller: 'BondDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Bond', function($stateParams, Bond) {
                    return Bond.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'bond',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('bond-detail.edit', {
            parent: 'bond-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bond/bond-dialog.html',
                    controller: 'BondDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Bond', function(Bond) {
                            return Bond.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bond.new', {
            parent: 'bond',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bond/bond-dialog.html',
                    controller: 'BondDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                tradeId: null,
                                symbol: null,
                                quote: null,
                                position: null,
                                pnl: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('bond', null, { reload: 'bond' });
                }, function() {
                    $state.go('bond');
                });
            }]
        })
        .state('bond.edit', {
            parent: 'bond',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bond/bond-dialog.html',
                    controller: 'BondDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Bond', function(Bond) {
                            return Bond.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bond', null, { reload: 'bond' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bond.delete', {
            parent: 'bond',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bond/bond-delete-dialog.html',
                    controller: 'BondDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Bond', function(Bond) {
                            return Bond.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bond', null, { reload: 'bond' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
