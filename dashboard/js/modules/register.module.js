config.controller('register', ['$scope', '$rootScope', 'dataService', '$timeout', '$interval', '$location', '$sce', '$routeParams', 'ngDialog', '$window', function($scope, $rootScope, dataService, $timeout, $interval, $location, $sce, $routeParams, ngDialog, $window) {
	var tmrStatus = null;
	$scope.loading = false;
	$scope.code = '';
	$scope.hasRegistered = dataService.listLocations().length > 0;

	$scope.init = function() {
	};

    $scope.setStatus = function(status) {
        if (tmrStatus) $timeout.cancel(tmrStatus);
        tmrStatus = null;
        $scope.status = status;
        if ($scope.status) {
            tmrStatus = $timeout(function() { $scope.setStatus(); }, 10000);
        }
    }

    $scope.$on('$destroy', function() {
		if (tmrStatus) $timeout.cancel(tmrStatus);
    });


	$scope.register = function() {
		$scope.loading = true;
		dataService.registerDashboard($scope.code).then(function(data) {
			if (data && (data.length >= 80) && (data.length <= 180)) {
				$location.path('/init/' + data);
			} else {
				$scope.setStatus("Sorry, the registration code you provided did not work...");
			}
			$scope.loading = false;
		});
    };



	$scope.cancel = function() {
		$location.path('/');
	};


    //init
	$scope.init();
	var userAgent = navigator.userAgent || navigator.vendor || window.opera;
	if( userAgent.match( /Android/i ) ) {
		$scope.android = true;
	}
	$scope.url = window.location.href;
	$scope.mobile = window.mobileCheck();
	$scope.tablet = (!$scope.mobile) && (window.mobileOrTabletCheck());
	$scope.formatTime = formatTime;
    $scope.utcToString = utcToString;
}]);
