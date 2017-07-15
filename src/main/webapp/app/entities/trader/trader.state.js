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
        });
    }

})();
