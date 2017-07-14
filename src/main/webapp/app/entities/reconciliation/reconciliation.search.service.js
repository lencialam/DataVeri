(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .factory('ReconciliationSearch', ReconciliationSearch);

    ReconciliationSearch.$inject = ['$resource'];

    function ReconciliationSearch($resource) {
        var resourceUrl =  'api/_search/reconciliations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
