(function() {
    'use strict';
    angular
        .module('dataVeriApp')
        .factory('Trader', Trader);

    Trader.$inject = ['$resource'];

    function Trader ($resource) {
        var resourceUrl =  'api/traders/:id';

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
