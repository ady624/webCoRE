config.controller('fuel', ['$scope', '$rootScope', 'dataService', '$timeout', '$interval', '$location', '$sce', '$routeParams', 'ngDialog', '$window', function($scope, $rootScope, dataService, $timeout, $interval, $location, $sce, $routeParams, ngDialog, $window) {
	var tmrStatus = null;
	var tmrClock = null;
	var tmrActivity = null;
	$scope.initialized = false;
    $scope.loading = true;
	$scope.canisters = [];
    $scope.fuelStreams = [];
	$scope.selectedCanister = '';
    $scope.error = '';
	$scope.designer = {};
	$scope.locations = null;
	$scope.instances = null;
	$scope.requestId = 0;
	$scope.activePistons = 0;
	$scope.pausedPistons = 0;
	$scope.dropDownMenu = false;
	$scope.view = 'piston';

	$scope.init = function(instance, uri, pin) {
		if ($scope.$$destroyed) return;
        dataService.setStatusCallback($scope.setStatus);
		dataService.listFuelStreams().then(function(data) {
				if ($scope.$$destroyed) return;
				$scope.loading = false;							
				$scope.initialized = true;
				if (!data || !data.fuelStreams || !(data.fuelStreams instanceof Array)) return;			
				$scope.fuelStreams = data.fuelStreams;
				var canisters = [];
				for (i in $scope.fuelStreams) {
					var fuelStream = $scope.fuelStreams[i];
					canisters.push(fuelStream.c);
				}
				$scope.canisters = canisters.unique().sort();
			});
	};

	$scope.selectCanister = function(canister) {
		$scope.selectedCanister = canister;
	}

	$scope.selectFuelStream = function(fuelStream) {
		for(i in $scope.fuelStreams) {
			$scope.fuelStreams[i].selected = ($scope.fuelStreams[i] == fuelStream);
		}
		$scope.prepareFuelStream(fuelStream);
		return;
		$scope.selectedFuelStream = fuelStream;
		var name = ($scope.selectedCanister ? $scope.selectedCanister + ' \\ ' : '') + $scope.selectedFuelStream.n;
		dataService.listFuelStreamData($scope.selectedFuelStream.i).then(function(data) {
			if (data && data.points && (data.points instanceof Array)) {
				for (i in data.points) {
		            $scope.chart.labels.push(new Date(data.points[i].t));
		            $scope.chart.data[0].push(data.points[i].d);
				}
			}
		});
	}


	$scope.prepareFuelStream = function(fuelStream) {
		if (fuelStream.selected && !fuelStream.data) {
			dataService.listFuelStreamData(fuelStream.i).then(function(data) {
				if (data && data.points && (data.points instanceof Array)) {
					fuelStream.data = data.points;
					$scope.populateChart();
				}
			});
		} else {
			$scope.populateChart();
		}
	}

	$scope.populateChart = function() {
		var count = 0;
		var names = [];
		var series = [];
		for (i in $scope.fuelStreams) {
			if (!!$scope.fuelStreams[i].selected && !!$scope.fuelStreams[i].data) {
				var fuelStream = $scope.fuelStreams[i];
				names.push((fuelStream.c ? fuelStream.c + ' \\ ' : '') + fuelStream.n);
				var s = [];
				for (j in fuelStream.data) {
					s.push({x: new Date(fuelStream.data[j].t), y: fuelStream.data[j].d});
				}
				series.push(s);
				count++;
			}
		}
		$scope.initChart(names, series);
		
	}

	$scope.toggleFuelStream = function(fuelStream) {
		fuelStream.selected = !fuelStream.selected;
		$scope.prepareFuelStream(fuelStream);
	}

    $scope.setStatus = function(status) {
        if (tmrStatus) $timeout.cancel(tmrStatus);
        tmrStatus = null;
        $scope.status = status;
        if ($scope.status) {
            tmrStatus = $timeout(function() { $scope.setStatus(); }, 10000);
        }
    }


    $scope.sortByDisplay = function(a,b) {
        return (a.d > b.d) ? 1 : ((b.d > a.d) ? -1 : 0);
    }

    $scope.sortByName = function(a,b) {
        return (a.n > b.n) ? 1 : ((b.n > a.n) ? -1 : 0);
    }


    $scope.home = function() {
        $scope.initialized = false;
        $location.path('/');
    }


    $scope.initChart = function(names, series) {
        $scope.chart = {
            series: names,
			type: 'line',
            data: series,
            options: {
					elements: {
						line: {
							fill: true,
		                    tension: 0
						}
					},
                    legend: {display: true},
                    showLines: true,
					responsive: true,
					maintainAspectRatio: false,
                    scales: {
                        xAxes: [{
                            type: 'time',
                        }],
                        yAxes: [
                            {
                                id: 'y-axis-1',
                                type: 'linear',
                                display: true,
                                position: 'left',
								ticks: {
                                	//beginAtZero: true,
	                                steps: 10,
	                                stepValue: 5
	                                //max: 100
    	                        }
                            }
                        ]
                    },
                    pan: {
                            // Boolean to enable panning
                            enabled: true,
                            // Panning directions. Remove the appropriate direction to disable
                            // Eg. 'y' would only allow panning in the y direction
                            mode: 'x'
                        },
                        // Container for zoom options
                        zoom: {
                            // Boolean to enable zooming
                            enabled: true,
                            // Zooming directions. Remove the appropriate direction to disable
                            // Eg. 'y' would only allow zooming in the y direction
                            mode: 'x',
                        }
            }

        };
    };




    //init
	var userAgent = navigator.userAgent || navigator.vendor || window.opera;
	if( userAgent.match( /Android/i ) ) {
		$scope.android = true;
	}
	$scope.url = window.location.href;
	$scope.mobile = window.mobileCheck();
	$scope.tablet = (!$scope.mobile) && (window.mobileOrTabletCheck());
	$scope.formatTime = formatTime;
    $scope.utcToString = utcToString;
	$scope.$$postDigest(function() {$window.FB.XFBML.parse()});
	var tmrInit = setInterval(function() {
		if (dataService.ready()) {
			clearInterval(tmrInit);
			$scope.init();
		}
	}, 1);

}]);
