(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .factory('StockSearch', StockSearch);

    StockSearch.$inject = ['$resource'];

    function StockSearch($resource) {
        var resourceUrl =  'api/_search/stocks/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
