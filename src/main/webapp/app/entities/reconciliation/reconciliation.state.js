(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('reconciliation', {
            parent: 'entity',
            url: '/reconciliation?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Reconciliations'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/reconciliation/reconciliations.html',
                    controller: 'ReconciliationController',
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
        .state('reconciliation-detail', {
            parent: 'reconciliation',
            url: '/reconciliation/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Reconciliation'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/reconciliation/reconciliation-detail.html',
                    controller: 'ReconciliationDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Reconciliation', function($stateParams, Reconciliation) {
                    return Reconciliation.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'reconciliation',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('reconciliation-detail.edit', {
            parent: 'reconciliation-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/reconciliation/reconciliation-dialog.html',
                    controller: 'ReconciliationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Reconciliation', function(Reconciliation) {
                            return Reconciliation.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('reconciliation.new', {
            parent: 'reconciliation',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/reconciliation/reconciliation-dialog.html',
                    controller: 'ReconciliationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                symbol: null,
                                product: null,
                                position: null,
                                close: null,
                                pnl: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('reconciliation', null, { reload: 'reconciliation' });
                }, function() {
                    $state.go('reconciliation');
                });
            }]
        })
        .state('reconciliation.edit', {
            parent: 'reconciliation',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/reconciliation/reconciliation-dialog.html',
                    controller: 'ReconciliationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Reconciliation', function(Reconciliation) {
                            return Reconciliation.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('reconciliation', null, { reload: 'reconciliation' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('reconciliation.delete', {
            parent: 'reconciliation',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/reconciliation/reconciliation-delete-dialog.html',
                    controller: 'ReconciliationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Reconciliation', function(Reconciliation) {
                            return Reconciliation.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('reconciliation', null, { reload: 'reconciliation' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
