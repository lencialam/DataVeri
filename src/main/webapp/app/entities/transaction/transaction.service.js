(function() {
    'use strict';
    angular
        .module('dataVeriApp')
        .factory('Transaction', Transaction);

    Transaction.$inject = ['$resource', 'DateUtils'];

    function Transaction ($resource, DateUtils) {
        var resourceUrl =  'api/transactions/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.tradeTime = DateUtils.convertDateTimeFromServer(data.tradeTime);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
