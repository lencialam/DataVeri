(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .factory('BondSearch', BondSearch);

    BondSearch.$inject = ['$resource'];

    function BondSearch($resource) {
        var resourceUrl =  'api/_search/bonds/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
