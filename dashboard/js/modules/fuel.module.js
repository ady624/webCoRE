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
		$scope.initChart();
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
	}


	$scope.prepareFuelStream = function(fuelStream) {
		if (fuelStream.selected && !fuelStream.data) {
			$scope.loading = true;
			dataService.listFuelStreamData(fuelStream.i).then(function(data) {				
				if (data && data.points && (data.points instanceof Array)) {
					fuelStream.data = data.points;
					$scope.populateChart();
				}
				$scope.loading = false;
			});
		} else {
			$scope.populateChart();
		}
	}

	$scope.populateChart = function() {
		var count = 0;
		var names = [];
		var series = [];
		var data = null;
		var dpcount = 0;
		var stacked = !!$scope.chart.options.isStacked;

		function isNumeric(n) {
		  return !isNaN(parseFloat(n)) && isFinite(n);
		};

		var selectedCount = 0;
		for (i in $scope.fuelStreams) {
			if (!!$scope.fuelStreams[i].selected && !!$scope.fuelStreams[i].data && $scope.fuelStreams[i].data.length) {
				selectedCount++;
			}
		}

		for (i in $scope.fuelStreams) {
			if (!!$scope.fuelStreams[i].selected && !!$scope.fuelStreams[i].data && $scope.fuelStreams[i].data.length) {
				var fuelStream = $scope.fuelStreams[i];

				data = data ? data : {'cols':[{'id':'time','label':'Time','type':'datetime'}], 'rows':[]};

				/*
				names.push((fuelStream.c ? fuelStream.c + ' \\ ' : '') + fuelStream.n);
				var s = [];
				for (j in fuelStream.data) {
					s.push({x: new Date(fuelStream.data[j].t), y: fuelStream.data[j].d});
				}
				series.push(s);
				*/

//google charts
				// Expand compact data format `[t1, d1, t2, d2, t3, d3]`
				if (typeof fuelStream.data[0] !== 'object') {
					var data = [];
					for (var i = 0; i < fuelStream.data.length; i += 2) {
						data.push({ t: fuelStream.data[i], d: fuelStream.data[i + 1] });
					}
					fuelStream.data = data;
				}
				var t = isNumeric(fuelStream.data[0].d) ? 'number' : 'string';
				data.cols.push({'id': i, 'label': (fuelStream.c ? fuelStream.c + ' \\ ' : '') + fuelStream.n, 'type': t});
				var ch = [];
				for(k = 0; k < count; k++) ch.push({'v': stacked ? 0 : null});
				//var cf = [];
				//for(k = count + 1; k < selectedCount; k++) cf.push({'v': stacked ? 0 : null});
				for (j in fuelStream.data) {
					var r = [{'v': new Date(fuelStream.data[j].t)}].concat(ch);
					var v = fuelStream.data[j].d;
					r = (r.push({'v': v, 't': t}) ? r : null);//.concat(cf);
					data.rows.push({'c': r});
					dpcount++;
				}		
				count++;
			}
		}
		//$scope.initChart(names, series);
		if (data) data.rows.sort(function(a,b) { return a.c[0].v - b.c[0].v });
		$scope.chart.data = !!data && !!dpcount ? data : null;
		while ($scope.chart.view.columns.length < count + 1) $scope.chart.view.columns.push($scope.chart.view.columns.length);
		while ($scope.chart.view.columns.length > count + 1) $scope.chart.view.columns.pop();

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
/*        $scope.chart = {
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
*/

		$scope.chart = {
			'type': 'AreaChart',
			'displayed': false,
			'data': null,
			'options': {
			    'isStacked': false,
			    'fill': 20,
				'displayExactValues': true,
				'interpolateNulls': true,
				'explorer': { 'axis': 'horizontal' },
				'is3D': true,
				'width': '100%',
				'height': '100%',
				'pointSize': 6,
				'dataOpacity': 0.5,
				'pointShape': 'square',
				'series': {
					0: { pointShape: 'circle' },
					1: { pointShape: 'square' },
					2: { pointShape: 'diamond' },
					3: { pointShape: 'polygon' },
					4: { pointShape: 'triangle' },
					5: { pointShape: 'star' }
				},
				'chartArea': {
					'left': 96,
					'top': 16,
					'right': 16,
					'width': '100%',
					'height': '80%'
				},
               	'legend': {
					'position': 'bottom'
				}
			},
			'hAxis': {
				'title': 'Date/Time'
			},
			'formatters': {},
			'view': {'columns':[]}
		};

    };



	$scope.hideSeries = function(selectedItem) {
		var col = selectedItem.column;
		if (selectedItem.row === null) {
			if ($scope.chart.view.columns[col] == col) {
				$scope.chart.view.columns[col] = {
					label: $scope.chart.data.cols[col].label,
					type: $scope.chart.data.cols[col].type,
					calc: function() {
						return null;
					}
				};
				//$scope.chart.options.colors[col - 1] = '#CCCCCC';
			} else {
				$scope.chart.view.columns[col] = col;
				//$scope.chart.options.colors[col - 1] = '';//$scope.chart.options.defaultColors[col - 1];
			}
		}
	}


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
	//$scope.$$postDigest(function() {$window.FB.XFBML.parse()});
	window.scope = $scope;
	dataService.whenReady().then(function() {
		$scope.init();
	});

}]);
