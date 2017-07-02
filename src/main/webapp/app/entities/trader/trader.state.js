(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('trader', {
            parent: 'entity',
            url: '/trader?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Traders'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/trader/traders.html',
                    controller: 'TraderController',
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
        .state('trader-detail', {
            parent: 'trader',
            url: '/trader/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Trader'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/trader/trader-detail.html',
                    controller: 'TraderDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Trader', function($stateParams, Trader) {
                    return Trader.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'trader',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('trader-detail.edit', {
            parent: 'trader-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/trader/trader-dialog.html',
                    controller: 'TraderDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Trader', function(Trader) {
                            return Trader.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('trader.new', {
            parent: 'trader',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/trader/trader-dialog.html',
                    controller: 'TraderDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('trader', null, { reload: 'trader' });
                }, function() {
                    $state.go('trader');
                });
            }]
        })
        .state('trader.edit', {
            parent: 'trader',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/trader/trader-dialog.html',
                    controller: 'TraderDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Trader', function(Trader) {
                            return Trader.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('trader', null, { reload: 'trader' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('trader.delete', {
            parent: 'trader',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/trader/trader-delete-dialog.html',
                    controller: 'TraderDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Trader', function(Trader) {
                            return Trader.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('trader', null, { reload: 'trader' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
