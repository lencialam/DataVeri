(function() {
    'use strict';
    angular
        .module('dataVeriApp')
        .factory('Bond', Bond);

    Bond.$inject = ['$resource'];

    function Bond ($resource) {
        var resourceUrl =  'api/bonds/:id';

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
