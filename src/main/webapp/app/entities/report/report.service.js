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
