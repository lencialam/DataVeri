(function() {
    'use strict';
    angular
        .module('dataVeriApp')
        .factory('Report', Report);

    Report.$inject = ['$resource', 'DateUtils'];

    function Report ($resource, DateUtils) {
        var resourceUrl =  'api/reports/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.reportDate = DateUtils.convertDateTimeFromServer(data.reportDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }

})();

(function() {
    'use strict';
    angular
        .module('dataVeriApp')
        .factory('ReportGenerate', ReportGenerate);

    ReportGenerate.$inject = ['$resource', 'DateUtils'];

       function ReportGenerate ($resource, DateUtils) {
            var resourceUrl =  'api/reports/generate';

            return $resource(resourceUrl, {}, {
                'query': { method: 'GET' }
            });
        }

})();

(function() {
    'use strict';
    angular
        .module('dataVeriApp')
        .factory('ReportCalculate', ReportCalculate);

    ReportCalculate.$inject = ['$resource', 'DateUtils'];

       function ReportCalculate ($resource, DateUtils) {
            var resourceUrl =  'api/reports/calculate';

            return $resource(resourceUrl, {}, {
                'query': { method: 'GET' }
            });
        }

})();
