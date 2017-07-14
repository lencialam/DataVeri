(function() {
    'use strict';
    angular
        .module('dataVeriApp')
        .factory('Reconciliation', Reconciliation);

    Reconciliation.$inject = ['$resource'];

    function Reconciliation ($resource) {
        var resourceUrl =  'api/reconciliations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
