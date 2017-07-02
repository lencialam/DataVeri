(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .factory('TraderSearch', TraderSearch);

    TraderSearch.$inject = ['$resource'];

    function TraderSearch($resource) {
        var resourceUrl =  'api/_search/traders/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
