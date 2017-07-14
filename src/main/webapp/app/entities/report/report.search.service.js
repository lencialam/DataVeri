(function() {
    'use strict';

    angular
        .module('dataVeriApp')
        .factory('ReportSearch', ReportSearch);

    ReportSearch.$inject = ['$resource'];

    function ReportSearch($resource) {
        var resourceUrl =  'api/_search/reports/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
