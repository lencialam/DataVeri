(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('stock', {
            parent: 'entity',
            url: '/stock?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Stocks'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/stock/stocks.html',
                    controller: 'StockController',
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
        .state('stock-detail', {
            parent: 'stock',
            url: '/stock/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Stock'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/stock/stock-detail.html',
                    controller: 'StockDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Stock', function($stateParams, Stock) {
                    return Stock.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'stock',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('stock-detail.edit', {
            parent: 'stock-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stock/stock-dialog.html',
                    controller: 'StockDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Stock', function(Stock) {
                            return Stock.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('stock.new', {
            parent: 'stock',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stock/stock-dialog.html',
                    controller: 'StockDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                symbol: null,
                                quote: null,
                                close: null,
                                currency: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('stock', null, { reload: 'stock' });
                }, function() {
                    $state.go('stock');
                });
            }]
        })
        .state('stock.edit', {
            parent: 'stock',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stock/stock-dialog.html',
                    controller: 'StockDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Stock', function(Stock) {
                            return Stock.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('stock', null, { reload: 'stock' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('stock.delete', {
            parent: 'stock',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/stock/stock-delete-dialog.html',
                    controller: 'StockDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Stock', function(Stock) {
                            return Stock.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('stock', null, { reload: 'stock' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
