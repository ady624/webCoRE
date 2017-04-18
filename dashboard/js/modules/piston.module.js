config.controller('piston', ['$scope', '$rootScope', 'dataService', '$timeout', '$interval', '$location', '$sce', '$routeParams', 'ngDialog', '$window', function($scope, $rootScope, dataService, $timeout, $interval, $location, $sce, $routeParams, ngDialog, $window) {
	var tmrReveal;
	var tmrStatus;
	var tmrActivity;
	var tmrClock;
	$scope.lastLogEntry = 0;
	$scope.error = '';
	$scope.loading = true;
	$scope.initialized = false;
	$scope.mode = 'view';
	$scope.data = null;
	$scope.error = '';
	$scope.pistonId = $routeParams.pistonId;
	$scope.piston = null;
	$scope.designer = {};
	$scope.showAdvancedOptions = false;
	$scope.dk = 'N7zqL6a8Texs4wY5y&y2YPLzus+_dZ%s';
	$scope.params = $location.search();
	$scope.insertIndexes = {};
	if ($scope.params) $location.search({});
	$scope.stack = {
		undo: [],
		redo: []
	};
	$scope.weekDays = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
	$scope.yearMonths = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];

	$scope.render = function(cancelTimer) {
		//do nothing, but rerenders
		if (($scope.mode == 'view') && ($scope.view.trace)) {
			if (!tmrClock) {
				tmrClock = $interval($scope.render, 1000);
			}
			if ($scope.trace) {
			}

		} else {
			if (tmrClock) $timeout.cancel(tmrClock);
			tmrClock = null;
		}
	}

	$scope.setStatus = function(status) {
		if (status) console.log(status);
		if (tmrStatus) $timeout.cancel(tmrStatus);
		tmrStatus = null;
		$scope.status = status;
		if ($scope.status) {
			tmrStatus = $timeout(function() { $scope.setStatus(); }, 10000);
		}
	}

    $scope.listAllPistons = function() {
        var result = [];
        var locations = dataService.listLocations();
        for (l in locations) {
            var instances = dataService.listInstances(locations[l].id);
            for (i in instances) {
				for (p in instances[i].pistons) {
	                result.push({ v: instances[i].pistons[p].id, n: locations[l].name + ' \\ ' + instances[i].name + ' \\ ' + instances[i].pistons[p].name });
				}
            }
        }
        return result;
    };

    $scope.getPistonName = function(pistonId) {
        var locations = dataService.listLocations();
        for (l in locations) {
            var instances = dataService.listInstances(locations[l].id);
            for (i in instances) {
				for (p in instances[i].pistons) {
					if (instances[i].pistons[p].id == pistonId) {
						return locations[l].name + ' \\ ' + instances[i].name + ' \\ ' + instances[i].pistons[p].name;
					}
				}
            }
        }
        return pistonId;
    };

	$scope.getModeName = function(modeId) {
		for(modeIndex in $scope.location.modes) {
			if ($scope.location.modes[modeIndex].id == modeId) {
				return $scope.location.modes[modeIndex].name;
			}
		}
		return modeId;
	}

	$scope.updateActivity = function(init) {	
		if ($scope.$$destroyed) return;	
		if ($scope.mode != 'view') return;
		if (tmrActivity) $timeout.cancel(tmrActivity);
		if (init) {
			tmrActivity = $timeout($scope.updateActivity, 10000);
			return;
		}
		dataService.getActivity($scope.pistonId, $scope.lastLogEntry).then(function (response) {
			if ($scope.$$destroyed) return;
			if (response.error == 'ERR_INVALID_ID') {
				//the app has been deleted
				$scope.home();
				return;
			}
			if (response && response.activity) {
				//we got data
				if (response.activity.state) $scope.state = response.activity.state;
				if (response.activity.logs && response.activity.logs.length) $scope.logs = response.activity.logs.concat($scope.logs);
				if (response.activity.trace) $scope.trace = response.activity.trace;
				if (response.activity.localVars) $scope.localVars = response.activity.localVars;
				if (response.activity.memory) $scope.memory = response.activity.memory;
				if (response.activity.lastExecuted) $scope.lastExecuted = response.activity.lastExecuted;
				if (response.activity.nextSchedule) $scope.nextSchedule = response.activity.nextSchedule;
				if (response.activity.schedules) $scope.schedules = response.activity.schedules;
				if (response.activity.name) $scope.meta.name = response.activity.name;
				if ($scope.logs && $scope.logs.length) $scope.lastLogEntry =$scope.logs[0].t;
			}
			tmrActivity = $timeout($scope.updateActivity, 3000);
		});
	}

	$scope.init = function() {
		if ($scope.$$destroyed) return;	
		dataService.setStatusCallback($scope.setStatus);
		$scope.loading = true;
		if ($scope.piston) $scope.loading = true;
		dataService.getPiston($scope.pistonId).then(function (response) {
			if ($scope.$$destroyed) return;
			try {
				var showOptions = $scope.piston ? !!$scope.showOptions : false;
				if (!response || !response.data || !response.data.piston) {
					$scope.error = $sce.trustAsHtml('Sorry, an error occurred while retrieving the piston data.');
					$scope.loading = false;
					return;
				}	
				$scope.piston = response.data.piston;
				$scope.meta = response.data.meta ? response.data.meta : {}
				//database
				$scope.db = response.db;
				$scope.location = dataService.getLocation();
				$scope.instance = dataService.getInstance();
				$scope.view = dataService.loadFromStore('view') || {
					variables: false,
					elseIfs: false,
					restrictions: false,
					whens: false,
					advancedStatements: false
				};
				$scope.subscriptions = response.data.subscriptions ? response.data.subscriptions : {};
				$scope.logs = response.data.logs ? response.data.logs : [];
				$scope.lastLogEntry = ($scope.logs && $scope.logs.length) ? $scope.logs[0].t : 0;
				$scope.stats = response.data.stats ? response.data.stats : {};
				$scope.state = response.data.state ? response.data.state : '';
				$scope.trace = response.data.trace ? response.data.trace : {};
				$scope.memory = response.data.memory ? response.data.memory : 0;
				$scope.lastExecuted = response.data.lastExecuted;
				$scope.nextSchedule = response.data.nextSchedule;
				$scope.schedules = response.data.schedules;
				
				$scope.initChart();
				$scope.devices = $scope.listAvailableDevices();
				$scope.virtualDevices = $scope.listAvailableVirtualDevices();
				window.scope = $scope;
				$scope.localVars = response.data.localVars;
				$scope.globalVars = response.data.globalVars;
				$scope.systemVars = response.data.systemVars;
				$scope.systemVarNames = []; //fix for angular ignoring keys that start with $
				for(name in $scope.systemVars) $scope.systemVarNames.push(name);
				$scope.meta.build = $scope.meta.build ? 1 * $scope.meta.build : 0;
				if ($scope.piston && ($scope.meta.build == 0)) {
					$scope.piston.z = $scope.params && $scope.params.description ? $scope.params.description : '';
					$scope.mode = 'edit';
					if ($scope.params && $scope.params.type != 'blank') {
						switch ($scope.params.type) {
							case 'duplicate':
								if ($scope.params.piston) {
									$scope.loading = true;
									dataService.getPiston($scope.params.piston).then(function (response) {
										$scope.loading = false;
										if (response && response.data && response.data.piston) {
											$scope.piston.o = response.data.piston.o ? response.data.piston.o : {};
											$scope.piston.r = response.data.piston.r ? response.data.piston.r : [];
											$scope.piston.rn = !!response.data.piston.rn;
											$scope.piston.rop = response.data.piston.rop ? response.data.piston.rop : 'and';
											$scope.piston.s = response.data.piston.s ? response.data.piston.s : [];
											$scope.piston.v = response.data.piston.v ? response.data.piston.v : [];
										}
										$scope.initialized = true;
										$scope.loading = false;
									});
									return;
								}
								break;
							case 'restore':
								if ($scope.params.bin) {
									$scope.loading = true;
									dataService.loadFromBin($scope.params.bin).then(function (response) {
										var piston = response.data;
										$scope.loading = false;
										if (piston) {
											$scope.piston.o = piston.o ? piston.o : {};
											$scope.piston.r = piston.r ? piston.r : [];
											$scope.piston.rn = !!piston.rn;
											$scope.piston.rop = piston.rop ? piston.rop : 'and';
											$scope.piston.s = piston.s ? piston.s : [];
											$scope.piston.v = piston.v ? piston.v : [];
											$scope.piston.z = piston.z ? piston.z : '';
										}
										$scope.initialized = true;
										$scope.loading = false;
									});
									return;
								}
								break;
						}
					}
				}
				if ($scope.mode == 'edit') {
					$scope.loadStack();
				} else {
						$scope.updateActivity(true);
				}	

				$scope.piston.o = $scope.piston.o ? $scope.piston.o : {cto: 0, ced: 0};
				$scope.piston.r = $scope.piston.r ? $scope.piston.r : [];
				$scope.piston.s = $scope.piston.s ? $scope.piston.s : [];
				$scope.piston.rop = $scope.piston.rop ? $scope.piston.rop : 'and';
				$scope.piston.rn = !!$scope.piston.rn;
				$scope.piston.v = $scope.piston.v ? $scope.piston.v : [];
				$scope.piston.z = $scope.piston.z || '';
	
				$scope.initialized = true;
				$scope.loading = false;
				$scope.render();
			} catch(e) { alert(e); }
		});
	};


	$scope.initChart = function() {
		$scope.chart = {
			type: 'bar',
			labels: [],
			series: ['Event delay', 'Load time', 'Execution time', 'Update time'],
			data: [[], [], [], []],
			onClick: function (points, evt) {
			},
			datasetOverride: [
				{
					cubicInterpolationMode: 'monotone',
					lineTension: 0,
					yAxisID: 'y-axis-1',
					fill: true,
					pointRadius: 0,
					borderColor: '#88bbee',
					borderWidth: 0,
					backgroundColor: '#99ccff'
				},
				{
					cubicInterpolationMode: 'monotone',
					lineTension: 0,
					yAxisID: 'y-axis-1',
					fill: true,
					pointRadius: 0,
					borderColor: '#eebb88',
					borderWidth: '0px',
					backgroundColor: '#ffcc99'
				},
				{
					cubicInterpolationMode: 'monotone',
					lineTension: 0,
					yAxisID: 'y-axis-1',
					fill: true,
					pointRadius: 0,
					borderColor: '#ee88bb',
					borderWidth: '0px',
					backgroundColor: '#ff99cc'
				},
				{
					cubicInterpolationMode: 'monotone',
					lineTension: 0,
					yAxisID: 'y-axis-1',
					fill: true,
					pointRadius: 0,
					borderColor: '#999',
					borderWidth: 1,
					backgroundColor: '#ccff99'

				}
			],
			options: {
					legend: {display: true},
					multiTooltipTemplate: "<%=datasetLabel%> : <%= value %>ms",
					showLines: true,
					fill: true,
					scales: {
						xAxes: [{
							type: 'time',
						}],
						yAxes: [
							{
								id: 'y-axis-1',
								stacked: true,
								type: 'linear',
								display: true,
								position: 'left'
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
		if ($scope.stats && $scope.stats.timing) {
			for(var i=0; i < $scope.stats.timing.length; i++) {
				$scope.chart.labels.push(new Date($scope.stats.timing[i].t));
				$scope.chart.data[0].push($scope.stats.timing[i].d);
				$scope.chart.data[1].push($scope.stats.timing[i].l);
				$scope.chart.data[2].push($scope.stats.timing[i].e);
				$scope.chart.data[3].push($scope.stats.timing[i].u);
			}
		}
	};

	
	$scope.$on('$destroy', function() {
		if (tmrStatus) $timeout.cancel(tmrStatus);
		if (tmrReveal) $timeout.cancel(tmrReveal);
		if (tmrActivity) $timeout.cancel(tmrActivity);
		if (tmrClock) $timeout.cancel(tmrClock);

	});

	$scope.copy = function(object) {
		return angular.fromJson(angular.toJson(object));
	};

	$scope.home = function() {
		$scope.initialized = false;
		$location.path('/');
	}

	$scope.toggleView = function(item) {
		if(item) $scope.view[item] = !$scope.view[item];
		dataService.saveToStore('view', $scope.view);

	}

	$scope.revealBin = function() {
		$scope.revealing = !$scope.revealing;
		if (tmrReveal) $timeout.cancel(tmrReveal);
		if ($scope.revealing) {
			tmrReveal = $timeout(function() {$scope.revealing = false; tmrReveal = null;}, 10000);
		}
	}

	$scope.edit = function() {
		$scope.mode = 'edit';
		$scope.init();
	}

	$scope.cancel = function() {
		$scope.mode = 'view';
		$scope.init();
	}

	$scope.save = function() {
		$scope.loading = true;
		var piston = {
			id: $scope.pistonId,
			o: $scope.piston.o,
			s: $scope.piston.s,
			v: $scope.piston.v,
			r: $scope.piston.r,
			rop: $scope.piston.rop,
			rn: $scope.piston.rn,
			z: $scope.piston.z,
			n: $scope.meta.name
		}
		dataService.setPiston(piston, $scope.meta.bin).then(function(response) {
			$scope.loading = false;
			if (response && response.data && response.data.build) {
				$scope.meta.active = response.data.active;
				$scope.meta.modified = response.data.modified;
				$scope.meta.build = response.data.build;
				$scope.saveStack(true);
				$scope.mode = 'view';
				$scope.init();
			}
		});;
	}

	$scope.pause = function() {
		$scope.loading = true;
		dataService.pausePiston($scope.pistonId).then(function(data) {
			$scope.loading = false;
			if (data && data.status && (data.status == 'ST_SUCCESS')) {
				$scope.meta.active = data.active;
				$scope.subscriptions = {};
				$scope.updateActivity();
			}
		});
	}

	$scope.resume = function() {
		$scope.loading = true;
		dataService.resumePiston($scope.pistonId).then(function(data) {
			$scope.loading = false;
			if (data && data.status && (data.status == 'ST_SUCCESS')) {
				$scope.meta.active = data.active;
				if (data.subscriptions) $scope.subscriptions = data.subscriptions;
				$scope.updateActivity();
			}
		});
	}

	$scope.del = function() {
		$scope.loading = true;
		dataService.deletePiston($scope.pistonId).then(function(data) {
			$scope.closeDialog();
			$location.path('/');
		});
	}
	$scope.padComment = function(comment, sz) {
		if (!comment) comment = '';
		sz = sz - 6 - comment.length;
		while (sz > 0) {
			comment += ' ';
			sz--;
		}
		return '/* ' + comment + ' */';
		
		
	};

	$scope.range = function(n) {
        return new Array(n);
    };


	$scope.wiki = function(item) {
		$scope.wikiUrl = $sce.trustAsUrl('https://wiki.webcore.co/' + item + '?content-only');
		$window.mydialog = ngDialog.open({
			template: 'dialog-wiki',
			className: 'ngdialog-theme-default ngdialog-large ngdialog-wiki',
			closeByDocument: true,
			disableAnimation: true,
			scope: $scope
		});
	};

	$scope.formatVariableValue = function(variable) {
		if (variable.v == null) return '(not set)';
		switch (variable.t) {
			case 'time':
			case 'date':
			case 'datetime':
				return utcToString(variable.v);
			default:
				return variable.v;
		}
	}

	$scope.deleteDialog = function() {
		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-del-piston',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};

	$scope.getExpressionConfig = function() {
		return {
				autocomplete: [{
					words: []
				},
				{
					words: $scope.listAutoCompleteFunctions(),
					cssClass: 'hl kwd'
				},
				{
					words: $scope.listAutoCompleteVariables(),
					cssClass: 'hl var'
				},
				{
					words: $scope.listAutoCompleteDevices(),
					cssClass: 'hl dev'
				},
				{
					words: [': level]', ': hue]'],
					cssClass: 'hl dev'
				},
				{
					words: [/([0-9]+)(\.[0-9]+)?/g],
					cssClass: 'hl num'
				}
				]
			}
	};

	$scope.removeFromArray = function(array, value) {
		if (!(array instanceof Array)) return;
		var idx = array.indexOf(value);
		if (idx !== -1) {
				array.splice(idx, 1);
			}
		return array;
	}

	$scope.deleteObject = function() {
		if (($scope.designer.parent instanceof Array) && ($scope.designer.$obj)) {
			$scope.autoSave();
			$scope.designer.parent = $scope.removeFromArray($scope.designer.parent, $scope.designer.$obj);
			$scope.closeDialog();
		}
		if ($scope.designer.parent && ($scope.designer.parent.t == 'action') && ($scope.designer.parent.k instanceof Array) && ($scope.designer.$obj)) {
			$scope.autoSave();
			$scope.designer.parent.k = $scope.removeFromArray($scope.designer.parent.k, $scope.designer.$obj);
			$scope.closeDialog();
		}
	}



	$scope.toggleAdvancedOptions = function() {
		$scope.designer.showAdvancedOptions = !$scope.designer.showAdvancedOptions;
	}


















	$scope.editSettings = function() {
		if ($scope.mode != 'edit') return;
		$scope.designer = {
			name: $scope.meta.name,
			description: $scope.piston.z,
			automaticState: $scope.piston.o.mps ? 1 : 0,
			conditionOptimizations: $scope.piston.o.cto ? 1 : 0,
			executionParallelism: $scope.piston.o.pep ? 1 : 0,
			eventSubscriptions: $scope.piston.o.des ? 1 : 0,
			commandDelay: $scope.piston.o.ced ? $scope.piston.o.ced : 0
		};
		window.designer = $scope.designer;
		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-edit-settings',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};

	$scope.updateSettings = function() {
		$scope.meta.name = $scope.designer.name;
		$scope.piston.z = $scope.designer.description;
		$scope.piston.o.mps = $scope.designer.automaticState ? 1 : 0;
		$scope.piston.o.pep = $scope.designer.executionParallelism ? 1 : 0;
		$scope.piston.o.cto = $scope.designer.conditionOptimizations ? 1 : 0;
		$scope.piston.o.des = $scope.designer.eventSubscriptions ? 1 : 0;
		$scope.piston.o.ced = isNaN($scope.designer.commandDelay) ? 0 : parseInt($scope.designer.commandDelay);
		$scope.closeDialog();
	}







	/* statements */

	$scope.addStatement = function(parent) {
		return $scope.editStatement(null, parent);
	};

	$scope.editStatement = function(statement, parent) {
		if ($scope.mode != 'edit') return;
		if (!statement) {
			statement = {};
			statement.t = null; //type
			statement.d = []; //devices
			statement.o = 'and'; //operator
			statement.n = false; //negation
			statement.rop = 'and'; //restriction operator
			statement.rn = false; //restriction negation
			statement.a = '0'; //async
			statement.tcp = 'c'; //tcp - cancel on condition state change
			statement.tep = ''; //tep always
			statement.ctp = 'i';
			//statement.pr = 'none'; //tcpr
			//statement.pv = ''; //tcpv
			statement.s = 'local'; //tos
			statement.z = ''; //desc
		}
		$scope.designer = {
			config: $scope.getExpressionConfig()
		};
		$scope.designer.$obj = statement;
		$scope.designer.$statement = statement;
		$scope.designer.$new = statement.t ? false : true;
		$scope.designer.type = statement.t;
		$scope.designer.page = statement.t ? 1 : 0;
		$scope.designer.operator = statement.o;
		$scope.designer.not = statement.n ? '1' : '0';
		$scope.designer.roperator = statement.rop;
		$scope.designer.rnot = statement.rn;
		$scope.designer.description = statement.z;
		$scope.designer.parent = parent;
		$scope.designer.devices = statement.d;
		$scope.designer.operand = {data: $scope.copy(statement.lo), multiple: false};
		$scope.designer.operand2 = {data: $scope.copy(statement.lo2), multiple: false};
		$scope.designer.operand3 = {data: $scope.copy(statement.lo3), multiple: false};
		$scope.designer.x = statement.x;
		$scope.designer.autoDialogs = true;
		//advanced options
		$scope.designer.tcp = statement.tcp;
		$scope.designer.tep = statement.tep;
		//$scope.designer.tcpr = statement.pr;
		//$scope.designer.tcpv = statement.pv;
		//$scope.designer.tos = statement.os;
		$scope.designer.ctp = statement.ctp || 'i';
		$scope.designer.async = statement.a;
		$scope.designer.ontypechanged = function(designer, type) {
			designer.operand.allowAnyInterval = false;
			designer.operand2.allowAnyInterval = false;
			designer.operand3.allowAnyInterval = false;
			switch (type) {
				case 'for':
					designer.operand.dataType = 'decimal';
					designer.operand2.dataType = 'decimal';
					designer.operand3.dataType = 'decimal';
					designer.operand.onlyAllowConstants = false;
					designer.operand.hideMilliseconds = true;
					if (designer.$new) designer.operand.data = {t: 'c'};
					if (designer.$new) designer.operand2.data = {t: 'c'};
					if (designer.$new) designer.operand3.data = {t: 'c'};
					$scope.validateOperand(designer.operand, true);
					$scope.validateOperand(designer.operand2, true);
					$scope.validateOperand(designer.operand3, true);
					break;
				case 'each':
					designer.operand.dataType = 'devices';
					designer.operand.onlyAllowConstants = false;
					designer.operand.hideMilliseconds = true;
					if (designer.$new) designer.operand.data = {t: 'd'};
					$scope.validateOperand(designer.operand, true);
					break;
				case 'switch':
					designer.operand.dataType = '';
					designer.operand.onlyAllowConstants = false;
					designer.operand.hideMilliseconds = true;
					if (designer.$new) designer.operand.data = {t: 'p'};
					$scope.validateOperand(designer.operand, true);
					break;
				case 'every':
					designer.operand.dataType = 'duration';
					if (designer.$new) designer.operand.data = {t: 'c', c: 1, vt: 'd'};
					designer.operand.onlyAllowConstants = true;
					designer.operand.hideMilliseconds = true;
					designer.operand2.dataType = 'time';
					var d = new Date();
					d.setSeconds(0, 0);
					if (designer.$new) designer.operand2.data = {t: 'c', c: d};
					designer.operand3.dataType = 'duration'; //offset
					designer.operand3.allowAnyInterval = true;
					if (designer.$new) designer.operand3.data = {t: 'c', c: 0, vt: 'm'};
					designer.operand3.onlyAllowConstants = true;
					$scope.validateOperand(designer.operand, true);
					$scope.validateOperand(designer.operand2, true);
					$scope.validateOperand(designer.operand3, true);
					break;
			}
			$scope.refreshSelects();
		}
		window.designer = $scope.designer;
		$scope.designer.items = {
			simple: [
				{ type: 'if', name: 'If Block', icon: 'code-fork', cssClass: 'info', description: 'An if block allows the piston to execute different actions depending on the truth result of a comparison or set of comparisons', button: 'an if' },
				{ type: 'action', name: 'Action', icon: 'code', cssClass: 'success', description: 'An action allows the piston to control devices and execute tasks', button: 'an action' },
				{ type: 'every', name: 'Timer', icon: 'clock-o', cssClass: 'warning', description: 'A timer will trigger execution of the piston at set time intervals', button: 'a timer' }
			],
			advanced: [
				{ type: 'switch', name: 'Switch', icon: 'code-fork', cssClass: 'info', description: 'A switch statement compares an operand against a set of values and executes statements corresponding to those matches', button: 'a switch' },
				{ type: 'for', name: 'For Loop', icon: 'circle-o-notch', cssClass: 'warning', description: 'A for loop executes the same statements for a set number of iteration cycles', button: 'a for loop' },
				{ type: 'each', name: 'For Each Loop', icon: 'circle-o-notch', cssClass: 'warning', description: 'An each loop executes the same statements for each device in a device list', button: 'a for each loop' },
				{ type: 'while', name: 'While Loop', icon: 'circle-o-notch', cssClass: 'warning', description: 'A while loop executes the same statements for as long as a condition is met', button: 'a while loop' },
				{ type: 'repeat', name: 'Repeat Loop', icon: 'circle-o-notch', cssClass: 'warning', description: 'A repeat loop executes the same statements until a condition is met', button: 'a repeat loop' },
				{ type: 'break', name: 'Break', icon: 'ban', cssClass: 'danger', description: 'A break allows the interruption of the inner most switch, for loop, for each loop, while loop, or repeat loop', button: 'a break' },
				{ type: 'exit', name: 'Exit', icon: 'ban', cssClass: 'danger', description: 'An exit interrupts the piston execution and exits immediately', button: 'an exit' }
			]
		};
		$scope.designer.ontypechanged($scope.designer, $scope.designer.type);
		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-edit-statement',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};

	$scope.updateStatement = function(nextDialog, defaultType) {
		$scope.autoSave();
		var statement = $scope.designer.$new ? {t: $scope.designer.type} : $scope.designer.$statement;
		statement.a = $scope.designer.async;
		statement.tcp = $scope.designer.tcp;
		statement.tep = $scope.designer.tep;
		statement.pr = $scope.designer.tcpr;
		statement.pv = $scope.designer.tcpv;
		statement.os = $scope.designer.tos;
		statement.z = $scope.designer.description;
		statement.r = statement.r ? statement.r : [];
		statement.rop = $scope.designer.roperator;
		statement.rn = $scope.designer.rnot;
		switch (statement.t) {
			case 'action':
				statement.d = $scope.designer.devices;
				statement.k = statement.k ? statement.k : [];
				break;
			case 'if':
				statement.o = $scope.designer.operator;
				statement.n = $scope.designer.not == '1';
				statement.c = statement.c ? statement.c : [];
				statement.s = statement.s ? statement.s : [];
				statement.ei = statement.ei ? statement.ei : [];
				statement.e = statement.e ? statement.e : [];
				break;
			case 'switch':
				statement.lo = $scope.designer.operand.data;
				statement.cs = statement.cs || [];
				statement.e = statement.e ? statement.e : [];
				statement.ctp = $scope.designer.ctp;
				break;
			case 'for':
				statement.x = $scope.designer.x;
				statement.lo = $scope.designer.operand.data;
				statement.lo2 = $scope.designer.operand2.data;
				statement.lo3 = $scope.designer.operand3.data;
				statement.s = statement.s ? statement.s : [];
				break;
			case 'each':
				statement.x = $scope.designer.x;
				statement.lo = $scope.designer.operand.data;
				statement.s = statement.s ? statement.s : [];
				break;
			case 'while':
				statement.o = $scope.designer.operator;
				statement.n = $scope.designer.not == '1';
				statement.c = statement.c ? statement.c : [];
				statement.s = statement.s ? statement.s : [];
				break;
			case 'every':
				statement.lo = $scope.designer.operand.data;			
				statement.lo2 = $scope.designer.operand2.data;
				if (statement.lo2.c instanceof Date) statement.lo2.c = statement.lo2.c.getHours() * 60 + statement.lo2.c.getMinutes();
				statement.lo3 = $scope.designer.operand3.data;
				statement.s = statement.s ? statement.s : [];
				break;
			case 'repeat':
				statement.o = $scope.designer.operator;
				statement.n = $scope.designer.not == '1';
				statement.c = statement.c ? statement.c : [];
				statement.s = statement.s ? statement.s : [];
				break;
			case 'break':
				break;
			case 'exit':
				statement.lo = $scope.designer.operand.data;
				break;
			default: statement.t = null;
		}
		if (statement.t) {
			statement.$$html = null;
			if ($scope.designer.$new) {
				//we're adding a new statement
				if ($scope.designer.parent instanceof Array) {
					$scope.designer.parent.push(statement);
				} else if (($scope.designer.parent.s) && ($scope.designer.parent.s instanceof Array)) {
						$scope.designer.parent.s.push(statement);
				} else {
					$scope.designer.parent.s = [statement];
				}
			} else {
				//we're updating an existing statement
				$scope.designer.$statement = statement;
			}
		}
		$scope.closeDialog();
		if (nextDialog) {
			switch (statement.t) {
				case 'action':
					$scope.addTask(statement);
					break;
				case 'if':
					$scope.addCondition(statement.c, false, defaultType);
					break;
				case 'while':
					$scope.addCondition(statement.c);
					break;
				case 'for':
				case 'each':
				case 'repeat':
					$scope.addStatement(statement.s);
					break;
				case 'switch':
					$scope.addCase(statement.cs);
					break;
			}
		}
	}

	$scope.upgradeStatement = function() {
		$scope.updateStatement();
		var statement = $scope.designer.$statement;
		if (statement && statement.c && (statement.c instanceof Array)) {
			statement.c = [{t: 'group', n: false, o: 'and', c:statement.c}];;
		}
	}




















	/* cases */

	$scope.addCase = function(parent) {
		return $scope.editCase(null, parent);
	};

	$scope.editCase = function(_case, parent) {
		if ($scope.mode != 'edit') return;
		var _new = _case ? false : true;
		if (!_case) {
			_case = {};
			_case.t = 's'; //type
			_case.s = [];
			_case.ro = {};
			_case.ro2 = {};
			_case.z = ''; //desc
		}
		$scope.designer = {
			config: $scope.getExpressionConfig()
		};
		$scope.designer.$obj = _case;
		$scope.designer.$case = _case;
		$scope.designer.$new = _new;
		$scope.designer.parent = parent;
		$scope.designer.type = _case.t;
		$scope.designer.operand = {data: _case.ro, multiple: false};
		$scope.designer.operand2 = {data: _case.ro2, multiple: false};
		$scope.designer.autoDialogs = true;
		//advanced options
		window.designer = $scope.designer;
		$scope.validateOperand($scope.designer.operand);
		$scope.validateOperand($scope.designer.operand2);
		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-edit-case',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};

	$scope.updateCase = function(nextDialog) {
		$scope.autoSave();
		var _case = $scope.designer.$case;
		_case.t = $scope.designer.type;
		_case.s = _case.s || [];
		_case.ro = $scope.designer.operand.data;
		_case.ro2 = $scope.designer.operand2.data;
		_case.z = $scope.designer.description;
		if (_case.t) {
			_case.$$html = null;
			_case.$$html2 = null;
			if ($scope.designer.$new) {
				//we're adding a new statement
				if ($scope.designer.parent instanceof Array) {
					$scope.designer.parent.push(_case);
				} else if (($scope.designer.parent.cs) && ($scope.designer.parent.cs instanceof Array)) {
						$scope.designer.parent.cs.push(_case);
				} else {
					$scope.designer.parent.cs = [_case];
				}
			} else {
				//we're updating an existing statement
				$scope.designer.$case = _case;
			}
		}
		$scope.closeDialog();
		if (nextDialog) {
			$scope.addStatement(_case.s);
		}
	}




	/* conditions */

	$scope.addCondition = function(parent, newElseIf, defaultType) {
		return $scope.editCondition(null, parent, newElseIf, defaultType);
	}

	$scope.editCondition = function(condition, parent, newElseIf, defaultType) {
		if ($scope.mode != 'edit') return;

		if (!condition) {
			condition = {};
			condition.t = defaultType;
			condition.d = [];
			condition.n = false;
			condition.o = 'and';
			condition.lo = {t: 'p', d: [], a: null, g:'any', v: null, c: '', x: null, e: ''};
			condition.co = null;
			condition.ro = {t: 'c', d: [], a: null, g:'any', v: null, c: '', x: null, e: ''};
			condition.ro2 = {t: 'c', d: [], a: null, g:'any', v: null, c: '', x: null, e: ''};
			condition.to = {t: 'c', d: [], a: null, g:'any', v: null, c: '', x: null, e: ''};
			condition.z = '';
			condition.sm = 'auto';
			condition.ts = [];
			condition.fs = [];
		}
		$scope.designer = {
			config: $scope.getExpressionConfig()
		};
		$scope.designer.$condition = condition;
		$scope.designer.$obj = condition;
		$scope.designer.type = condition.t;
		$scope.designer.$new = !defaultType && !!condition.t ? false : true;
		$scope.designer.newElseIf = newElseIf;
		$scope.designer.page = $scope.designer.$new && !defaultType ? 0 : 1;
		$scope.designer.parent = parent;
		$scope.designer.devices = condition.d;
		$scope.designer.not = condition.n ? '1' : '0';
		$scope.designer.operator = condition.o;
		$scope.designer.comparison = {
			type: 'condition',
			left: {data: condition.lo ? $scope.copy(condition.lo) : {}, showSubDevices: true},
			operator: condition.co,
			right: {data: condition.ro ? $scope.copy(condition.ro) : {}},
			right2: {data: condition.ro2 ? $scope.copy(condition.ro2) : {}},
			time: {data: condition.to ? $scope.copy(condition.to) : {t:'c'}, dataType: 'duration'}
		}
		$scope.validateComparison($scope.designer.comparison, true);
		$scope.designer.smode = condition.sm;
		$scope.designer.description = condition.z;
		window.designer = $scope.designer;
		$scope.designer.items = [
			{ type: 'condition', name: 'Condition', icon: 'code', cssClass: 'btn-info' },
			{ type: 'group', name: 'Group', icon: 'code-fork', cssClass: 'btn-warning' },
		];


		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-edit-condition',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};
	
	$scope.fixOperand = function(data) {
		switch (data.vt) {
			case 'time':
				data.c = data.c.getHours() * 60 + data.c.getMinutes();
				break;
			case 'date':
				data.c = data.c.getTime() - data.c.getTime().mod(86400000);
				break;
			case 'datetime':
				data.c =data.c.getTime();
				break;
		}
		return data;
	}


	$scope.updateCondition = function(nextDialog) {
		$scope.autoSave();
		var condition = $scope.designer.$new ? {t: $scope.designer.type} : $scope.designer.$condition;
		switch (condition.t) {
			case 'condition':
				condition.lo = $scope.fixOperand($scope.designer.comparison.left.data);
				condition.co = $scope.designer.comparison.operator;
				condition.ro = $scope.fixOperand($scope.designer.comparison.right.data);
				condition.ro2 = $scope.fixOperand($scope.designer.comparison.right2.data);
				condition.to = $scope.designer.comparison.time.data;
				break;
			case 'group':
				condition.c = condition.c ? condition.c : [];
				condition.o = $scope.designer.operator;
				condition.n = $scope.designer.not == '1';
				break;
		}
		condition.sm = $scope.designer.smode;
		condition.ts = condition.ts ? condition.ts : []
		condition.fs = condition.fs ? condition.fs : []
		condition.z = $scope.designer.description;
		if (condition.t) {
			condition.$$html = null;
			if ($scope.designer.$new) {
				if ($scope.designer.newElseIf) {
					var elseIf = {o: 'and', n: false, c: [], s: []};
					elseIf.c.push(condition);		
					$scope.designer.parent.push(elseIf);
				} else if ($scope.designer.parent instanceof Array) {
					$scope.designer.parent.push(condition);
				} else if (($scope.designer.parent.c) && ($scope.designer.parent.c instanceof Array)) {
						$scope.designer.parent.c.push(condition);
				} else {
					$scope.designer.parent.c = [condition];
				}
			} else {
				$scope.designer.$condition = condition;
			}
		}
		$scope.closeDialog();
		if (condition.t && nextDialog) {
			$scope.addCondition(condition.t == 'group' ? condition : $scope.designer.parent);
		}
	}

	$scope.upgradeCondition = function() {
		$scope.updateCondition();
		var parent = $scope.designer.parent;
		if ($scope.designer.$condition && parent && (parent instanceof Array)) {
			var index = parent.indexOf($scope.designer.$condition);
			if (index >= 0) {
				var condition = {}
				condition.t = $scope.designer.$condition.t;
				condition.n = $scope.designer.$condition.n;
				condition.o = $scope.designer.$condition.o;
				condition.c = $scope.designer.$condition.c;
				$scope.designer.$condition = {}
				$scope.designer.$condition.t = 'group';
				$scope.designer.$condition.n = false;
				$scope.designer.$condition.o = 'and';
				$scope.designer.$condition.c = [condition];
				parent[index] = $scope.designer.$condition;
			}
		}
	}





	$scope.editConditionGroup = function(group, parent) {
		if ($scope.mode != 'edit') return;

		$scope.designer = {
			operator: group.o || 'and',
			not: group.n ? '1' : '0',
			description: group.z
		};
		$scope.designer.group = group;
		$scope.designer.$obj = group;
		$scope.designer.parent = parent;
		window.designer = $scope.designer;

		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-edit-condition-group',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};

	$scope.updateConditionGroup = function() {
		$scope.autoSave();
		var group = $scope.designer.group;
		group.n = $scope.designer.not == '1';
		group.o = $scope.designer.operator;
		group.z = $scope.designer.description;
		$scope.closeDialog();
	}

















	/* restrictions */

	$scope.addRestriction = function(parent) {
		return $scope.editRestriction(null, parent);
	}

    $scope.editRestriction = function(restriction, parent) {
        if ($scope.mode != 'edit') return;

        if (!restriction) {
            restriction = {};
            restriction.t = null;
            restriction.d = [];
            restriction.rn = false;
            restriction.rop = 'and';
            restriction.lo = {t: 'p', d: [], a: null, g:'any', v: null, c: '', x: null, e: ''};
            restriction.co = null;
            restriction.ro = {t: 'c', d: [], a: null, g:'any', v: null, c: '', x: null, e: ''};
            restriction.ro2 = {t: 'c', d: [], a: null, g:'any', v: null, c: '', x: null, e: ''};
            restriction.to = {t: 'c', d: [], a: null, g:'any', v: null, c: '', x: null, e: ''};
            restriction.z = '';
        }
        $scope.designer = {
            config: $scope.getExpressionConfig()
        };
        $scope.designer.$restriction = restriction;
        $scope.designer.$obj = restriction;
        $scope.designer.type = restriction.t;
        $scope.designer.$new = restriction.t ? false : true;
        $scope.designer.page = $scope.designer.$new ? 0 : 1;
        $scope.designer.parent = parent;
        $scope.designer.devices = restriction.d;
        $scope.designer.not = restriction.rn ? '1' : '0';
        $scope.designer.operator = restriction.rop;
        $scope.designer.comparison = {
			type: 'restriction',
            left: {data: restriction.lo ? $scope.copy(restriction.lo) : {}},
            operator: restriction.co,
            right: {data: restriction.ro ? $scope.copy(restriction.ro) : {}},
            right2: {data: restriction.ro2 ? $scope.copy(restriction.ro2) : {}},
            time: {data: restriction.to ? $scope.copy(restriction.to) : {t:'c'}, dataType: 'duration'}
        }
        $scope.validateComparison($scope.designer.comparison, true);
        $scope.designer.smode = restriction.sm;
        $scope.designer.description = restriction.z;
        window.designer = $scope.designer;
        $scope.designer.items = [
            { type: 'restriction', name: 'Restriction', icon: 'code', cssClass: 'btn-info' },
            { type: 'group', name: 'Group', icon: 'code-fork', cssClass: 'btn-warning' },
        ];


        $scope.designer.dialog = ngDialog.open({
            template: 'dialog-edit-restriction',
            className: 'ngdialog-theme-default ngdialog-large',
            closeByDocument: false,
            disableAnimation: true,
            scope: $scope
        });
    };

    $scope.updateRestriction = function(nextDialog) {
        $scope.autoSave();
        var restriction = $scope.designer.$new ? {t: $scope.designer.type} : $scope.designer.$restriction;
        switch (restriction.t) {
            case 'restriction':
                restriction.lo = $scope.fixOperand($$scope.designer.comparison.left.data);
                restriction.co = $scope.designer.comparison.operator;
                restriction.ro = $scope.fixOperand($$scope.designer.comparison.right.data);
                restriction.ro2 = $scope.fixOperand($$scope.designer.comparison.right2.data);
                restriction.to = $scope.designer.comparison.time.data;
                break;
            case 'group':
                restriction.r = restriction.r ? restriction.r : [];
                restriction.rop = $scope.designer.operator;
                restriction.rn = $scope.designer.not == '1';
                break;
        }
        restriction.z = $scope.designer.description;
        if (restriction.t) {
            restriction.$$html = null;
            if ($scope.designer.$new) {
		if ($scope.designer.parent instanceof Array) {
                    $scope.designer.parent.push(restriction);
                } else if (($scope.designer.parent.r) && ($scope.designer.parent.r instanceof Array)) {
                        $scope.designer.parent.r.push(restriction);
                } else {
                    $scope.designer.parent.r = [restriction];
                }
            } else {
                $scope.designer.$restriction = restriction;
            }
        }
        $scope.closeDialog();
        if (restriction.t && nextDialog) {
            $scope.addRestriction(restriction.t == 'group' ? restriction : $scope.designer.parent);
        }
    }
		
	$scope.upgradeRestriction = function() {
		$scope.updateRestriction();
		var parent = $scope.designer.parent;
		if ($scope.designer.$restriction && parent && (parent instanceof Array)) {
			var index = parent.indexOf($scope.designer.$restriction);
			if (index >= 0) {
				var restriction = {}
				restriction.t = $scope.designer.$restriction.t;
				restriction.rn = $scope.designer.$restriction.rn;
				restriction.rop = $scope.designer.$restriction.rop;
				restriction.c = $scope.designer.$restriction.c;
				$scope.designer.$restriction = {}
				$scope.designer.$restriction.t = 'group';
				$scope.designer.$restriction.rn = false;
				$scope.designer.$restriction.rop = 'and';
				$scope.designer.$restriction.c = [restriction];
				parent[index] = $scope.designer.$restriction;
			}
		}
	}





	$scope.editRestrictionGroup = function(group, parent) {
		if ($scope.mode != 'edit') return;

		$scope.designer = {
			operator: group.rop || 'and',
			not: group.rn ? '1' : '0',
			description: group.z
		};
		$scope.designer.group = group;
		$scope.designer.$obj = group;
		$scope.designer.parent = parent;
		window.designer = $scope.designer;

		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-edit-restriction-group',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};

	$scope.updateRestrictionGroup = function() {
		$scope.autoSave();
		var group = $scope.designer.group;
		group.rn = $scope.designer.not == '1';
		group.rop = $scope.designer.operator;
		group.z = $scope.designer.description;
		$scope.closeDialog();
	}








	/* tasks */

	$scope.addTask = function(parent) {
		return $scope.editTask(null, parent);
	};

	$scope.editTask = function(task, parent) {
		if ($scope.mode != 'edit') return;
		if (!task) {
			task = {};
			task.c = '';
			task.a = '0';
			task.m = '';
			task.z = '';
		}
		$scope.designer = {};
		var _new = task.c ? false : true;
		var insertIndex = _new ? $scope.insertIndexes[parent.$$hashkey] : parent.k.indexOf(task);
		if (isNaN(insertIndex)) insertIndex = parent.k.length;
		$scope.designer.insertIndex = insertIndex;
		$scope.designer.$task = task;
		$scope.designer.$obj = task;
		$scope.designer.$new = _new;
		$scope.designer.page = 0;
		$scope.designer.parent = parent;
		$scope.designer.command = task.c;
		$scope.designer.mode = task.m;
		$scope.designer.description = task.z;
		$scope.prepareParameters(task);
		window.designer = $scope.designer;
		window.scope = $scope;
		$scope.designer.commands = $scope.listAvailableCommands(parent.d);
		$('a-ckolor-wheel').remove();
		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-edit-task',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};

	$scope.updateTask = function(nextDialog) {
		$scope.autoSave();
		var task = $scope.designer.$new ? {} : $scope.designer.$task;
		task.c = $scope.designer.command;
		task.a = $scope.designer.async;
		task.z = $scope.designer.description;
		task.m = $scope.designer.mode;
		if (task.c) {
			task.$$html = null;
			task.p = [];
			for (parameterIndex in $scope.designer.parameters) {
				var param = $scope.designer.parameters[parameterIndex].data;
				if (param.t == 'c') {
					switch (param.vt) {
						case 'time':
							param.c = param.c.getHours() * 60 + param.c.getMinutes();
							break;
						case 'date':
							param.c = param.c.getTime() - param.c.getTime().mod(86400000);
							break;
						case 'datetime':
							param.c = param.c.getTime();
							break;
					}
				}
				task.p.push(param);
			}
			if ($scope.designer.$new) {
				if (($scope.designer.parent) && ($scope.designer.parent.k instanceof Array)) {
					$scope.designer.parent.k.push(task);
					//save the current insert index
					$scope.insertIndexes[parent.$$hashkey] = $scope.designer.insertIndex + 1;
				}
			} else {
				$scope.designer.$task = task;
			}
		}
		var tasks = $scope.designer.parent.k;
		if (tasks && tasks.length) {
			var currentIndex = tasks.indexOf(task);
			var insertIndex = $scope.designer.insertIndex;
			if (insertIndex > currentIndex) insertIndex--;
			if (insertIndex >= tasks.length) insertIndex = tasks.length - 1;
			if ($scope.designer.insertIndex != currentIndex) {
				//move the task to its needed index
				tasks.splice(insertIndex, 0, tasks.splice(currentIndex, 1)[0]);
			}
		}
		$scope.closeDialog();
		if (nextDialog) {
			$scope.addTask($scope.designer.parent);
		}
	}






	/* variables */

	$scope.addVariable = function() {
		return $scope.editVariable(null);
	};

	$scope.editVariable = function(variable) {
		if ($scope.mode != 'edit') return;
		if (!variable) {
			variable = {};
			variable.t = 'dynamic';
			variable.n = '';
			variable.v = {data:{}};
			variable.a = 'd';
			variable.z = '';
		}
		$scope.designer = {};
		$scope.designer.$variable = variable;
		$scope.designer.$obj = variable;
		$scope.designer.$new = variable.n ? false : true;
		$scope.designer.page = 0;
		$scope.designer.parent = $scope.piston.v;
		$scope.designer.type = variable.t;
		$scope.designer.assignment = variable.a || 'd';
		$scope.designer.name = variable.n;
		$scope.designer.operand = {data: (variable.t == 'device' ? { t: 'd', d: variable.v} : variable.v), multiple: false, dataType: variable.t, optional: true}
		$scope.designer.description = variable.z;
		window.designer = $scope.designer;
		window.scope = $scope;
		$scope.validateOperand($scope.designer.operand);
		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-edit-variable',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};

	$scope.updateVariable = function(nextDialog) {
		$scope.autoSave();
		var variable = $scope.designer.$new ? {} : $scope.designer.$variable;
		variable.t = $scope.designer.operand.dataType;
		variable.n = $scope.designer.name.trim().replace(/[^a-z0-9]|\s+|\r?\n|\r/gmi, '_');
		variable.z = $scope.designer.description;
		variable.a = $scope.designer.assignment;
		var value = $scope.designer.operand.data;
		switch (value.t) {
			case '':
				variable.v = null;
				break;
			case 'd':
				variable.v = value.d;
				break;
			default:
				variable.v = value;
				break;
		}
		if ($scope.designer.$new) {
			$scope.piston.v.push(variable);
		} else {
			$scope.designer.variable = variable;
		}
		$scope.closeDialog();
		if (nextDialog) {
			$scope.addVariable();
		}
	}



	$scope.drag = function(list, index) {
		list.splice(index, 1);
	}


	$scope.setDesignerType = function(type) {
 		$scope.designer.type = type;
		$scope.nextPage();
		if ($scope.designer.ontypechanged) {
			$scope.designer.ontypechanged($scope.designer, type);
		}
	};

	$scope.closeDialog = function() {
		$scope.saveStack();
		if ($scope.designer.dialog) {
			$scope.designer.dialog.close();
			$scope.designer.dialog = null;
		}
	}

	$scope.nextPage = function() {
		$scope.designer.page++;
	}

	$scope.prevPage = function() {
		if ($scope.designer.page) {
			$scope.designer.page--;
		}
	}

	$scope.selectTaskIndex = function(index) {
		if ($scope.designer) $scope.designer.insertIndex = index;
	}

	$scope.initLineNumbers = function() {
		$rootScope.lineNumber = 0;
	}

	$scope.getLineNumber = function() {
		//$rootScope.lineNumber = $rootScope.lineNumber + 1;
		return $rootScope.lineNumber;
	}

	$scope.mergeObjects = function (obj1, obj2) {
		if (Object.assign) return Object.assign(obj1, obj2);
		var result = {};
		for (var attrname in obj1) { result[attrname] = obj1[attrname]; }
		for (var attrname in obj2) { result[attrname] = obj2[attrname]; }
		return result;
	};


	$scope.clearLogs = function() {
		$scope.logs = [];
	}


	$scope.prepareParameters = function(task) {
		$scope.designer.parameters = [];
		var command = $scope.db.commands.physical[$scope.designer.command] || $scope.db.commands.virtual[$scope.designer.command];
		$scope.designer.parameters = [];
		if (command) {
			for (parameterIndex in command.p) {
				var parameter = $scope.copy(command.p[parameterIndex]);
				var p = {
					data: {},
					name: parameter.n,
					dataType: parameter.t.toLowerCase(),
					multiple: false,
					optional: ((parameter.t != 'bool') && (parameter.t != 'boolean')) && !!parameter.d,
					options: parameter.o,			
					strict: !!parameter.s
				}
				var attribute = $scope.getAttributeById(parameter.t);
				if (attribute) {
					p.attribute = attribute;
					p.dataType = attribute.t;
					p.allowMuliple = false;
					p.options = attribute.o;
				}
				if (task && task.p && (task.p.length > parameterIndex)) {
					p.data = $scope.copy(task.p[parameterIndex]);
				}
				$scope.validateOperand(p);
				$scope.designer.parameters.push(p);
			}
		} else {
			//custom command - we add our own parameters
		}
		if ($scope.designer.command == 'setVariable') {
			$scope.designer.parameters[0].linkedOperand = $scope.designer.parameters[1];
			$scope.validateOperand($scope.designer.parameters[0]);
		}
		$scope.refreshSelects();
	}


/*
	$scope.prepareParameters = function(task) {
		$scope.designer.parameters = [];
		var command = $scope.db.commands.physical[$scope.designer.command] || $scope.db.commands.virtual[$scope.designer.command];
		if (command) {
			$scope.designer.parameters = command.p;
			for (parameterIndex in $scope.designer.parameters) {
				var parameter = $scope.designer.parameters[parameterIndex];
				var attribute = $scope.getAttributeById(parameter.t);
				var p = $scope.copy(parameter);
				if (attribute) {
					p.a = attribute;
					p.u = attribute.u;
					p.t = attribute.t;
					p.m = (attribute.r ? attribute.r[0] : null);
					p.M = (attribute.r ? attribute.r[1] : null);
					p.o = attribute.o;
					if (attribute.o && attribute.o.length) {
						p.v = attribute.o[0];
					}
				}
				if (task && task.p && (task.p.length > parameterIndex)) {
					p.v = task.p[parameterIndex].v instanceof Object ? task.p[parameterIndex].v.str : task.p[parameterIndex].v;
					if (!isNaN(p.v)) {
						p.v = p.v.indexOf('.') ? parseFloat(p.v) : parseInt(p.v);
					}
					p.vt = task.p[parameterIndex].vt;
				}
				p.v = p.v ? p.v : '';
				p.vt = (p.vt && p.vt.toString().length) ? p.vt : 's';
				p.i = $scope.getParameterInputType(p);
				$scope.designer.parameters[parameterIndex] = p;
			}
		} else {
			//custom command - we add our own parameters
		}
	}

*/
	$scope.getParameterInputType = function(parameter) {
		switch (parameter.t) {
			case 'color':
			case 'duration':
			case 'enum':
			case 'boolean':
				return parameter.t;
			case 'number':
				return Math.abs(parameter.M - parameter.m) > 360 ? 'number' : 'range';
		}
		return 'text';
	}

	$scope.getParameterMin = function(parameter) {
		switch (parameter.t) {
			case 'level':
			case 'saturation':
			case 'hue':
				return 0;
			case 'colorTemperature':
				return 1500;
		}
		return null;
	}

	$scope.getParameterMax = function(parameter) {
		switch (parameter.t) {
			case 'level':
			case 'saturation':
				return 100;
			case 'hue':
				return 360;
			case 'colorTemperature':
				return 10000;
		}
		return null;
	}

	$scope.getDeviceById = function(deviceId) {
		if (deviceId == $scope.location.id) {
			return {id: deviceId, name: $scope.location.name};
		}
		return $scope.instance.devices[deviceId];
	}

	$scope.getDeviceByName = function(deviceName) {
		for (deviceIndex in $scope.instance.devices) {
			if ($scope.instance.devices[deviceIndex].n == deviceName) {
				return mergeObjects({id: deviceIndex}, $scope.instance.devices[deviceIndex]);
			}
		}
		return null;
	}

	$scope.getVirtualDeviceById = function(deviceId) {
		if (deviceId == $scope.location.id) {
			return {id: deviceId, name: $scope.location.name};
		}
		return $scope.instance.virtualDevices[deviceId];
	}


	$scope.getCapabilityById = function(capabilityId) {
		return $scope.db.capabilities[capabilityId];
	}

	$scope.getCapabilityByName = function(capabilityName) {
		for (capabilityIndex in $scope.db.capabilities) {
			if ($scope.db.capabilities[capabilityIndex].n == capabilityName) {
				return mergeObjects({id: capabilityIndex}, $scope.db.capabilities[capabilityIndex]);
			}
		}
		return null;
	}

	$scope.getCommandById = function(commandId) {
		return $scope.db.commands.physical[commandId] || $scope.db.commands.virtual[commandId];
	}

	$scope.getCommandByName = function(commandName) {
		for (commandIndex in $scope.db.commands.physical) {
			if ($scope.db.commands.physical[commandIndex].n == commandName) {
				return mergeObjects({id: commandIndex}, $scope.db.commands.physical[commandIndex]);
			}
		}
		for (commandIndex in $scope.db.commands.virtual) {
			if ($scope.db.commands.virtual[commandIndex].n == commandName) {
				return mergeObjects({id: commandIndex}, $scope.db.commands.virtual[commandIndex]);
			}
		}
		return null;
	}

	$scope.getAttributeById = function(attributeId) {
		return $scope.db.attributes[attributeId];
	}

	$scope.getAttributeByName = function(attributeName) {
		for (attributeIndex in $scope.db.attributes) {
			if ($scope.db.attributes[attributeIndex].n == attributeName) {
				return $scope.db.attributes[attributeIndex];;
			}
		}
		return null;
	}

	$scope.getDeviceAttributeById = function(device, attributeId) {
		if (!device) return null;
		for(i in device.a) {
			if (device.a[i].n == attributeId) return device.a[i];
		}
		return null;
	}


	$scope.buildName = function(name, noQuotes, pedantic, itemPrefix) {
		if ((name == null) || (name == undefined)) return '';
		if (name instanceof Array) return $scope.buildNameList(name, 'or', '', '', false, noQuotes, pedantic, itemPrefix);
		if (pedantic || (name.length == 34)) {
			for (deviceId in $scope.instance.virtualDevices) {
				var device = $scope.instance.virtualDevices[deviceId];
				if (device.o) {
					for (id in device.o) {
						noQuotes = noQuotes || !pedantic;
						if (name == id) return (!noQuotes ? '\'' : '') + device.o[id] + (!noQuotes ? '\'' : '');
					}
				}
			}
		}
		return (!noQuotes ? '\'' : '') + (itemPrefix ? itemPrefix : '') + name + (!noQuotes ? '\'' : '');
	}


	$scope.buildNameList = function(list, suffix, tag, className, possessive, noQuotes, pedantic, itemPrefix) {
		var cnt = 1;
		var result = '';
		for (i in list) {
			var item = $scope.buildName(list[i], noQuotes, pedantic, itemPrefix);
			result += '<span ' + tag + (className ? ' class="' + className + '"' : '') + '>' + item + '</span>' + (possessive ? '\'' + (item.substr(-1) == 's' ? '' : 's') : '') + (cnt < list.length ? (cnt == list.length - 1 ? (list.length > 2 ? ', ' : ' ') + suffix + ' ' : ', ') : '');
			cnt++;
		}
		return result.trim();
	}

	$scope.buildLocationModeNameList = function(modes) {
		var modeNames = [];
		if (modes instanceof Array) {
			for (modeIndex in modes) {
				modeNames.push($scope.getModeName(modes[modeIndex]));
			}
			if (modeNames.length) {
				return $scope.buildNameList(modeNames, 'or', 'lit', '', false, true);
			}
		}
		return '';
	};


	$scope.buildDeviceNameList = function(devices) {
		var deviceNames = [];
		if (devices instanceof Array) {
			for (deviceIndex in devices) {
				var deviceId = devices[deviceIndex] || '';
				if (deviceId.startsWith(':')) {
					var device = $scope.getDeviceById(deviceId);
					if (device) {
						deviceNames.push(device.n);
					}
				} else {
					deviceNames.push('{<span var>' + deviceId + '</span>}');
				}
			}
			if (deviceNames.length) {
				return $scope.buildNameList(deviceNames, 'and', 'dev', '');
			}
		}
		return 'Location';
	};



	$scope.formatHour = function(hour) {
		return (!location.timeZone || location.timeZone.id.startsWith('America')) ? ((hour % 12 ?hour % 12 : '12') + (hour < 12 ? 'am' : 'pm')) : ('00' + hour).substr(-2);
	};

	$scope.renderDeviceNameList = function(devices) {
		return $sce.trustAsHtml($scope.buildDeviceNameList(devices));
	}

	$scope.hasCommand = function(device, commandName) {
		if (!device || !device.c) return false;
		return $scope.hasName(device.c, commandName);
	}

	$scope.hasName = function(arrayOfObjects, name) {
		if (!arrayOfObjects || !arrayOfObjects.length) return false;
		for (obj in arrayOfObjects) {
			if (arrayOfObjects[obj] && (arrayOfObjects[obj].n === name)) return true;
		}
		return false;
	}

	$scope.hasId = function(arrayOfObjects, id) {
		if (!arrayOfObjects || !arrayOfObjects.length) return false;
		for (obj in arrayOfObjects) {
			if (arrayOfObjects[obj] && (arrayOfObjects[obj].id === id)) return true;
		}
		return false;
	}

	$scope.listAvailableCommands = function(devices) {
		var commands = {}
		var deviceCount = devices.length;
		for (deviceIndex in devices) {
			var deviceId = devices[deviceIndex] || '';
			var cmds = [];
			var all = false;
			if (deviceId.startsWith(':')) {
				var device = $scope.getDeviceById(devices[deviceIndex]);
				cmds = device.c;
			} else {
				all = true;
				cmds = $scope.db.commands.physical;
			}
			//get all the device supported commands
			for (commandIndex in cmds) {
				var commandName = all ? commandIndex : cmds[commandIndex].n;
				if (commands[commandName]) {
					commands[commandName] += 1;
				} else {
					commands[commandName] = 1;
				}
			}
		}
		var result = {
			common: [],
			partial: [],
			virtual: []
		}
		for (commandName in commands) {
			var command = $scope.db.commands.physical[commandName];
			if (!command) command = {n: commandName + '(..)', cm: true};
			if (commands[commandName] == deviceCount) {
				result.common.push(mergeObjects({id: commandName}, command));
			} else {
				result.partial.push(mergeObjects({id: commandName}, command));
			}
		}
		for (commandName in $scope.db.commands.virtual) {
			var command = $scope.db.commands.virtual[commandName];
			if (command.r) {
				var count = 0;
				for (deviceIndex in devices) {
					var deviceId = devices[deviceIndex] || '';
					var ok = false;
					if (deviceId.startsWith(':')) {
						var device = $scope.getDeviceById(devices[deviceIndex]);
						ok = !!device;
						if (ok) for(req in command.r) {
							if (!$scope.hasCommand(device, command.r[req])) {
								ok = false;
								break;
							}
						}
					} else {
						ok = true;
					}
					if (ok) {
						count++;
					}
				}
				if (count > 0 ){
					if (count == deviceCount) {
						if (!$scope.hasId(result.common, commandName))
							result.common.push(mergeObjects({id: commandName, em: true}, command));
					} else {
						if (!$scope.hasId(result.partial, commandName))
							result.partial.push(mergeObjects({id: commandName, em: true}, command));
					}
				}
			} else {
				result.virtual.push(mergeObjects({id: commandName}, command));
			}
		}
		result.common.sort($scope.sortByName);
		result.partial.sort($scope.sortByName);
		//result.custom.sort($scope.sortByName);
		result.virtual.sort($scope.sortByName);
		return result;
	}

	$scope.listAvailableDevices = function() {
		var result = [];
		for(deviceIndex in $scope.instance.devices) {
			var device = $scope.instance.devices[deviceIndex];
			result.push(mergeObjects({id: deviceIndex}, device));
		}
		return result.sort($scope.sortByName);
	}

	$scope.listAvailableVirtualDevices = function() {
		var result = [];
		for(deviceIndex in $scope.instance.virtualDevices) {
			var device = $scope.instance.virtualDevices[deviceIndex];
			result.push(mergeObjects({id: deviceIndex}, device));
		}
		return result.sort($scope.sortByName);
	}

	$scope.escapeRegExp = function(str) {
		return str;//str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
	};

	$scope.listAutoCompleteFunctions = function() {
		var result = [];
		for(functionIndex in $scope.db.functions) {
			result.push(($scope.db.functions[functionIndex].d ? $scope.db.functions[functionIndex].d : functionIndex) + '(');
		}
		return result.sort();
	}

	$scope.listAutoCompleteDevices = function() {
		var result = [];
		for(deviceIndex in $scope.instance.devices) {
			var device = $scope.instance.devices[deviceIndex];
			result.push($scope.escapeRegExp('[' + device.n + ' :'));
		}
		return result.sort();
	}


	$scope.listAutoCompleteVariables = function() {
		var result = [];
		for(varIndex in $scope.piston.v) {
			var v = $scope.piston.v[varIndex];
			result.push($scope.escapeRegExp(v.n));
		}
		if ($scope.systemVars)
			for(varName in $scope.systemVars)
				result.push($scope.escapeRegExp(varName));
		if ($scope.globalVars)
		for(varName in $scope.globalVars)
			result.push($scope.escapeRegExp(varName));
		return result.sort();
	}

	$scope.getVariableByName = function(name) {
		if ($scope.systemVars && $scope.systemVars[name]) return $scope.systemVars[name];
		if ($scope.globalVars && $scope.globalVars[name]) return $scope.globalVars[name];
		for(varIndex in $scope.piston.v) {
			if ($scope.piston.v[varIndex].n == name)
				return $scope.piston.v[varIndex];
		}
		return null;
	}

	$scope.getVariableValue = function(name, dt) {
		if ($scope.localVars) {
			var variable = $scope.localVars[name];
			if (variable != undefined) {
				if (dt == 'datetime') {
					return utcToString(variable);
				}
				return '' + variable;
			}
		}
		return '(not set)';

	}

	$scope.autoAddVariable = function(name) {
		if (!name) return false;
		name = name ? name.trim() : '';
		var v = $scope.getVariableByName(name);
		$scope.piston.v.push({t: 'dynamic', n: name});
	}

	$scope.hasAttribute = function(device, attributeName) {
		for(a in device.a) {
			if (device.a[a].n == attributeName) return true;
		}
		return false;
	}

	$scope.listAvailableAttributes = function(devices, restrictAttribute) {
		var result = [];
		var device = null;
		if (devices && devices.length) {
			var attributes = {}
			var deviceCount = devices.length;
			for (deviceIndex in devices) {
				device = $scope.getDeviceById(devices[deviceIndex]);
				if (device) {
					for (attributeIndex in device.a) {
						var attribute = device.a[attributeIndex];
						if (!restrictAttribute || (attribute.n == restrictAttribute)) {
							if (attributes[attribute.n]) {
								attributes[attribute.n] += 1;
							} else {
								attributes[attribute.n] = 1;
							}
						}
					}
				}
			}
			for (attributeId in attributes) {
				if (attributes[attributeId] == deviceCount) {
					var attribute = $scope.getAttributeById(attributeId);
					if (attribute) {
						result.push(mergeObjects({id: attributeId}, attribute));
					} else {
						//custom attribute? device should contain the last device we've been through
						for (a in device.a) {
							if (device.a[a].n == attributeId) {
								attribute = device.a[a];
								break;
							}
						}
						if (attribute) {
							var obj = mergeObjects({id: attributeId, c:true}, attribute);
							obj.n = '⌂ ' + obj.n;
							obj.t = (obj.t || 'string').toLowerCase().replace('number', 'decimal');
							result.push(obj);
						}
					}
				}
			}
			result.sort($scope.sortByName);
		}
		return result;
	}


	$scope.sortByDisplay = function(a,b) {
		return (a.d > b.d) ? 1 : ((b.d > a.d) ? -1 : 0);
	}

	$scope.sortByName = function(a,b) {
		return (a.n > b.n) ? 1 : ((b.n > a.n) ? -1 : 0);
	}


	$scope.getStackData = function() {
		return {hash: $scope.md5(angular.toJson($scope.piston)), timestamp: (new Date()).getTime(), data: angular.fromJson(angular.toJson($scope.piston))};
	}

	$scope.autoSave = function(stack) {
		var clearRedo = stack ? false : true;
		stack = stack ? stack : $scope.stack.undo;
		pushToStack = true;
		var obj = $scope.getStackData();
		if (stack && stack.length) {
			if (obj.hash == stack[stack.length - 1].hash) {
				pushToStack = false;;
			}
		}
		if (pushToStack) {
			stack.push(obj);
		}
		if (clearRedo) {
			$scope.stack.redo = [];
		}
	}

	$scope.objectToArray = function(object) {
		var result = [];
		for (property in object) {
			result.push({v: property, n: object[property]});
		}
		return result;
	}

	$scope.saveStack = function(justSaved) {
		$scope.stack.current = $scope.getStackData();
		if (justSaved) $scope.stack.current.timestamp = 0;
		$scope.stack.build = $scope.meta.build;
		dataService.saveToStore('stack' + $scope.pistonId, $scope.stack);
	}

	$scope.loadStack = function() {
		$scope.stack = dataService.loadFromStore('stack' + $scope.pistonId);
		$scope.stack = $scope.stack instanceof Object ? $scope.stack : {}
		$scope.stack.undo = ($scope.stack.undo instanceof Array ? $scope.stack.undo : []);
		$scope.stack.redo = ($scope.stack.redo instanceof Array ? $scope.stack.redo : []);
		if ($scope.stack.current instanceof Object && $scope.stack.current.data && ($scope.stack.build == $scope.meta.build) && ($scope.meta.modified < $scope.stack.current.timestamp)) {
			$scope.setStatus();
			$scope.dialogChooseVersion();
		} else {
			$scope.stack.current = $scope.getStackData();
			$scope.stack.undo = [];
			$scope.stack.redo = [];
		}
	}

	$scope.dialogChooseVersion = function() {
        $scope.designer.dialog = ngDialog.open({
            template: 'dialog-choose-version',
            className: 'ngdialog-theme-default ngdialog-large',
            closeByDocument: false,
            disableAnimation: true,
            scope: $scope
        });
	}

	$scope.chooseVersion = function(keepLocal) {
		if (keepLocal) {
			$scope.piston = $scope.stack.current.data;
		} else {
			$scope.autoSave();
		}
		$scope.closeDialog();
	}

	$scope.undo = function() {
		if ($scope.stack && $scope.stack.undo && $scope.stack.undo.length) {
			$scope.autoSave($scope.stack.redo);
			$scope.stack.current = $scope.stack.undo.pop();
			$scope.piston = $scope.stack.current.data;
			$scope.saveStack();
		}
	}

	$scope.redo = function() {
		if ($scope.stack && $scope.stack.redo && $scope.stack.redo.length) {
			$scope.autoSave($scope.stack.undo);
			$scope.stack.current = $scope.stack.redo.pop();
			$scope.piston = $scope.stack.current.data;
			$scope.saveStack();
		}
	}

	$scope.localTimeToDate = function(time) {
		var today = new Date();
		today.setHours(Math.floor(time / 60));
		today.setMinutes(time % 60);
		today.setSeconds(0);
		today.setMilliseconds(0)
		return today;
	};

	$scope.validateOperand = function(operand, reinit, managed) {
		if (!!$scope.designer.comparison && !managed) {
			$scope.validateComparison($scope.designer.comparison, reinit);
			return;
		}

		operand = operand || {};

		if (!operand.initialized || reinit) {
			operand.data = operand.data || {};
			operand.data.a = operand.data.a || '';
			operand.data.c = (operand.data.c == undefined) || (operand.data.c == null) ? '' : operand.data.c;
			operand.data.v = operand.data.v || '';
			operand.data.e = operand.data.e || '';
			operand.data.x = operand.data.x || '';
			operand.data.d = operand.data.d || [];
			operand.data.g = operand.data.g || (operand.multiple ? 'any' : 'avg');
			operand.data.f = operand.data.f || 'l';
			operand.options = operand.options || [];
		}		


		if (true || !operand.initialized || reinit) {
			var dataType = (operand.dataType || 'string').toLowerCase();
			if (dataType == 'variables') {
				operand.multiple = true;
				dataType = 'variable';
			}
			if (dataType == 'devices') {
				operand.multiple = true;
				dataType = 'device';
			}
			if (dataType == 'pistons') {
				operand.multiple = true;
				dataType = 'piston';
			}
			if (dataType == 'routines') {
				operand.multiple = true;
				dataType = 'routine';
			}
			if (dataType == 'modes') {
				operand.multiple = true;
				dataType = 'mode';
			}
			if (dataType == 'alarmsystemstatus') {
				dataType = 'alarmSystemStatus';
			}
			if (dataType == 'alarmsystemstatuses') {
				operand.multiple = true;
				dataType = 'alarmSystemStatus';
			}
			if (dataType == 'modes') {
				operand.multiple = true;
				dataType = 'mode';
			}
			if (dataType == 'enums') {
				operand.multiple = true;
				dataType = 'enum';
			}
			if (dataType == 'number') dataType = 'decimal';
			if (dataType == 'bool') dataType = 'boolean';
			if ((dataType == 'enum') && !operand.options && !operand.options.length) {
				dataType = 'string';
			}
	
			//if (dataType != 'enum') operand.options = null;
	
			if (operand.data.t == null) {
				var t = ''
				if (!operand.optional) {
					t = dataType == 'variable' ? 'x' : 'c';
					if (($scope.designer.$condition) || ($scope.designer.$restriction)) t = 'p';
				}
				operand.data.t = t;
			}

			if ((operand.data.vt == 'time') && !(operand.data.c instanceof Date)) {
				operand.data.c = $scope.localTimeToDate(operand.data.c);
			}
			if ((operand.data.vt == 'date') && !(operand.data.c instanceof Date)) {
				operand.data.c = new Date(operand.data.c);
			}
			if ((operand.data.vt == 'datetime') && !(operand.data.c instanceof Date)) {
				operand.data.c = new Date(operand.data.c);
			}

			operand.onlyAllowConstants = operand.onlyAllowConstants || (dataType == 'piston') || (dataType == 'routine') || (dataType == 'askAlexaMacro')

			var strict = !!operand.strict;
			if (operand.onlyAllowConstants) {
				operand.allowDevices = false;
				operand.allowPhysical = false;
				operand.allowVirtual = false;
				operand.allowConstant = true;
				operand.allowVariable = false;
				operand.allowExpression = false;
			} else {
				operand.allowDevices = dataType == 'device';
				operand.allowPhysical = (dataType != 'datetime') && (dataType != 'date') && (dataType != 'time') && (dataType != 'device') && (dataType != 'variable') && (!strict || (dataType != 'boolean')) && (dataType != 'duration');
				operand.allowPreset = (dataType == 'datetime') || (dataType == 'time') || (dataType == 'color');
				operand.allowVirtual = (dataType != 'datetime') && (dataType != 'date') && (dataType != 'time') && (dataType != 'device') && (dataType != 'variable') && (dataType != 'decimal') && (dataType != 'integer') && (dataType != 'number') && (dataType != 'boolean') && (dataType != 'enum') && (dataType != 'color') && (dataType != 'duration');
				operand.allowVariable = (dataType != 'device' || ((dataType == 'device') && operand.multiple)) && (!strict || (dataType != 'boolean'));
				operand.allowConstant = (dataType != 'device') && (dataType != 'variable');
				operand.allowExpression = (dataType != 'device') && (dataType != 'variable') && (dataType != 'enum') && (!strict || (dataType != 'boolean'));
			}
			if (((operand.data.t == 'p') && (!operand.allowPhysical)) || ((operand.data.t == 'v') && (!operand.allowVirtual))) operand.data.t = 'c';

			if (!operand.config) {
				operand.config = $scope.copy($scope.getExpressionConfig());
				operand.config.autocomplete[5].words = [/([0-9]+)(\.[0-9]+)?/g];
			}



			operand.restrictAttribute = null;
			operand.restrictType = null;
			switch (dataType) {
				case 'color':
					operand.restrictAttribute = 'color';
					break;
				case 'device':
					operand.restrictType = 'device';
					break;
				case 'integer':
					operand.restrictType = 'integer';
					break;
				case 'decimal':
					operand.restrictType = 'integer,decimal';
					break;
				case 'time':
					operand.restrictType = 'datetime,time';
					break;
				case 'date':
				case 'datetime':
					operand.restrictType = 'datetime,date';
					break;
			}
			operand.dataType = dataType;

			operand.durationUnit = operand.durationUnit || operand.data.vt || 's';
			operand.data.vt = dataType == 'duration' ? operand.durationUnit : dataType;


		}

		switch (dataType) {
			case 'bool':
			case 'boolean':
				operand.options = ['false', 'true'];
				break;
			case 'mode':
			case 'alarmSystemStatus':
			case 'routine':
				operand.options = $scope.objectToArray($scope.instance.virtualDevices[dataType].o);
				break;
			case 'piston':
				operand.options = $scope.listAllPistons();
				break;
		}
	

		//default value for options
		if ((!operand.multiple) && (operand.options) && (operand.options.length) && (operand.data.t == 'c')) {
			if (operand.options[0] instanceof Object) {
				var found = false;
				for (i in operand.options) {
					if (operand.options[i].v == operand.data.c) {
						found = true;
						break;
					}
				}
				if (!found) operand.data.c = operand.options[0].v;
			} else {
				if (operand.options.indexOf(operand.data.c) < 0) operand.data.c = operand.options[0];
			}
		}
	

		operand.initialized = true;
		operand.allowAggregation = true;
		operand.allowAll = true;
		operand.attributes = (operand.data.t == 'p') ? $scope.listAvailableAttributes(operand.data.d, operand.restrictAttribute) : [];
		operand.valid = false;
		operand.selectedMultiple = (operand.data.t=='p') && operand.data.d && (operand.data.d.length > 1) && ((operand.data.g == 'all') || (operand.data.g == 'any'));
		operand.error = null;
		operand.momentary = false;

		operand.selectedDataType = 'string';
		operand.selectedOptions = [];

		switch (operand.data.t) {
			case 'p':
				var attribute = $scope.db.attributes[operand.data.a];
				if (!attribute) {
					if (operand.data.d.length) {
						var device = $scope.getDeviceById(operand.data.d[0]);
						if (device) {
							for (a in device.a) {
								if (device.a[a].n == operand.data.a) {
									attribute = device.a[a];
									break;
								}
							}
						}
					}
				}
				if (attribute) {
					operand.momentary = attribute.m;
					operand.count = 0;
					if (operand.momentary && !!attribute.s) {
						//get the number of sub devices
						var countAttributes = attribute.s.split(',');
						for (deviceIndex in operand.data.d) {
							var dev = $scope.getDeviceById(operand.data.d[deviceIndex]);
							if (dev) {
								for (i in countAttributes) {
									var attr = $scope.getDeviceAttributeById(dev, countAttributes[i]);
									if ((attr) && (!isNaN(attr.v))) {
										c = parseInt(attr.v);
										if (c > operand.count) {
											operand.count = c;
										}
									}
								}
							}
						}
					}
					if (operand.count && ((operand.data.i == null) || (operand.data.i == undefined))) {
						//default sub device index
						operand.data.i = ['1'];
					}
					operand.selectedDataType = attribute.t.toLowerCase();
					operand.selectedOptions = attribute.o;
					if (operand.momentary) {
						operand.allowAll = false;
						operand.allowAggregation = false;
					}
				} else {
					operand.selectedDataType = 'string';
				}
				if (operand.data.d && (operand.data.d.length > 1)) {
					if (!operand.data.g) {
						operand.error = 'Invalid aggregation method';
					}
					if (!(['any', 'all', 'least', 'most'].indexOf(operand.data.g) >= 0)) {
						operand.selectedDataType = 'decimal';
					}
				}
				break;
			case 'v':
				var virtualDevice = $scope.instance.virtualDevices[operand.data.v];
				operand.selectedDataType = (!!virtualDevice && !!virtualDevice.t) ? virtualDevice.t : 'string';
				if (virtualDevice) {
					//save the options
					for (o in virtualDevice.o) {
						operand.selectedOptions.push({v: o, n: virtualDevice.o[o]});
					}
				}
				break;
			case 'x':
				if (operand.data.x instanceof Array) {
					if (operand.data.x.length) {
						operand.selectedDataType = 'dynamic';
					} else {
						operand.error = "Invalid list of variables";
					}
				} else {
					var variable = $scope.getVariableByName(operand.data.x);
					if (variable) {
						operand.selectedDataType = variable.t;
					} else {
						operand.error = "Invalid variable";
					}
				}
				break;
			case 'c':
				var expression = $scope.parseString(operand.data.c);
				operand.error = expression.err;
				operand.expressionVar = expression.errVar;
				operand.data.exp = expression;
				if (!operand.options) {
					if (!operand.optional && !operand.data.c) {
						operand.error = 'Empty value';
						operand.expressionVar = '';
					}
				} else {
					if ((operand.data.c == null) || (operand.data.c == undefined)) {
						operand.error = 'Invalid selection';
						operand.expressionVar = '';
					}
				}				
				operand.selectedDataType = operand.dataType;//$scope.detectDataType(operand.data.c);
				break;
			case 'e':
				var expression = $scope.parseExpression(operand.data.e);
				operand.error = expression.err;
				operand.expressionVar = expression.errVar;
				if (expression.err) {
					var loc = (expression.loc ? expression.loc : '0:' + (expression.str.length - 1).toString()).split(':');
					var start = parseInt(loc[0]);
					var end = loc.length == 2 ? parseInt(loc[1]) : start;
					operand.config.autocomplete[0] = {words: [new RegExp('.(?=.{' + (expression.str.length - start - 1) + '}$).{' + (end - start) + '}')], cssClass: 'hl err', title: expression.err};
				} else {
					operand.config.autocomplete[0] = {words: [], cssClass: 'hl err'};
				}
				operand.data.exp = expression;
				operand.selectedDataType = 'dynamic';
				break;
		}

		if ((!operand.error) && (operand.dataType == 'duration') && (!operand.durationUnit)) {
			operand.error = 'Invalid duration unit';
		}

		if ((!operand.error) && operand.count && (!operand.data.i || !operand.data.i.length)) {
			operand.error = 'Invalid sub device selection';
		}

		operand.valid = (!operand.error) && (
			((operand.data.t=='') && (operand.optional)) ||
			((operand.data.t=='d') && !!operand.data.d && !!operand.data.d.length) ||
			((operand.data.t=='p') && !!operand.data.d && !!operand.data.d.length && !!operand.data.a) ||
			((operand.data.t=='v') && !!operand.data.v) ||
			((operand.data.t=='x') && !!operand.data.x && !!operand.data.x.length) ||
			((operand.data.t=='s') && !!operand.data.s) ||
			((operand.data.t=='c') && !((operand.data.c == "Invalid Date") && (operand.data.c instanceof Object)) && !((dataType == 'duration') && (isNaN(operand.data.c) || (!operand.allowAnyInterval && (operand.data.c < 1))))) ||
			((operand.data.t=='e') && !!operand.data.e && !!operand.data.e.length)
		);

		switch (operand.dataType) {
			case 'integer':
				operand.inputType = 'number';
				try {
					operand.data.c = parseInt(operand.data.c);
				} catch(all) {
					operand.data.c = 0;
				}
				break;
			case 'duration':
			case 'decimal':
				operand.inputType = 'number';
				try {
					operand.data.c = parseFloat(operand.data.c);
				} catch(all) {
					operand.data.c = 0;
				}
				break;
			default:
				operand.inputType = operand.dataType;
		}

		if (operand.linkedOperand) {
			operand.linkedOperand.dataType = operand.selectedDataType;
			operand.linkedOperand.options = operand.selectedOptions;
			$scope.validateOperand(operand.linkedOperand, true);
		}
	};

	$scope.refreshSelects = function(type) {
		if (type) {
			$scope.$$postDigest(function() {$('select[' + type + ']').selectpicker('refresh');});
		} else {
			$scope.$$postDigest(function() {$('select').selectpicker('refresh');});
		}
	}

	$scope.getOrdinalSuffix = function(value) {
		if (isNaN(value)) return '';
		value = parseInt(value);
		var value100 = value % 100;
		var value10 = value % 10;
		if (((value100 > 3) && (value100 < 21)) || (value10 == 0) || (value10 > 3)) return 'th';
		switch (value10) {
			case 1: return 'st';
			case 2: return 'nd';
			case 3: return 'rd';
		}
		return 'th';
	}

	$scope.getOrdinal = function(value) {
		if (isNaN(value)) return '';
		value = parseInt(value);
		switch (value) {
			case -3: return 'third-last';
			case -2: return 'second-last';
			case -1: return 'last';
		}
		return value + $scope.getOrdinalSuffix(value);
	}

	$scope.listODM = function() {
		var result = $scope.designer.odm;
		var sz = (!$scope.designer.operand.data.odw || ($scope.designer.operand.data.odw == 'd')) ? 31 : 5;
		if (!result || (result.length != (sz + 3))) {
			result = [];
			for (i = 1; i <= sz; i++) {
				result.push({v: i, n: i + $scope.getOrdinalSuffix(i)});
			}	
			result.push({v: -1, n: 'last'});
			result.push({v: -2, n: 'second-last'});
			result.push({v: -3, n: 'third-last'});
			$scope.designer.odm = result;
		}
		return result;
	}

	$scope.listODW = function() {
		var result = $scope.designer.odw;
		var sz = ($scope.designer.operand.data.odm > 5) ? 0 : 7;
		if (!result || (result.length != (sz + 1))) {
			result = [];
			result.push({v: 'd', n: 'day'});
			if (sz) {
				for(i in $scope.weekDays) {
					result.push({v: i.toString(), n: $scope.weekDays[i]});
				}
			}
			$scope.designer.odw = result;
		}
		return result;
	}

	$scope.validateComparison = function(comparison, reinit) {
		//we run the operand validation, this time managed
		$scope.validateOperand(comparison.left, reinit, true);

		//rebuild the list of comparisons, but only if needed
		if ((comparison.left.selectedDataType != comparison.dataType) || (comparison.left.selectedMultiple != comparison.selectedMultiple) || (comparison.left.momentary != comparison.momentary)) {
			comparison.dataType = comparison.left.selectedDataType;
			comparison.selectedMultiple = comparison.left.selectedMultiple;
			comparison.momentary = comparison.left.momentary;
			var noRestrictions = comparison.type != 'restriction';
			var optionList = [];
			var options = [];
			switch (comparison.dataType) {
				case 'enum':
					dt = '';
					break;
				case 'dynamic':
					dt = '';
					break;
				case 'time':
				case 'date':
				case 'datetime':
					dt = 't';
					break;
				default:
					dt = comparison.comparison.dataType.substr(0, 1);
			}
            dt = (comparison.momentary ? 'm' : ((dt == 'n' ? 'd' : dt)));
			for(conditionId in $scope.db.comparisons.conditions) {
				var condition = $scope.db.comparisons.conditions[conditionId];
				if (((!dt && (condition.g != 'm')) || (condition.g.indexOf(dt) >= 0)) && (noRestrictions || !condition.t))  {
					options.push({ id: conditionId, d: (comparison.selectedMultiple ? (condition.dd ? condition.dd : condition.d) : condition.d), c: 'Conditions' });
				}
			}
			optionList = optionList.concat(options.sort($scope.sortByDisplay));
			if (noRestrictions) {
				options = [];
				for(triggerId in $scope.db.comparisons.triggers) {
					var trigger = $scope.db.comparisons.triggers[triggerId];
					if (trigger.g.indexOf(dt) >= 0) {
						options.push({ id: triggerId, d: (comparison.selectedMultiple ? (trigger.dd ? trigger.dd : trigger.d) : trigger.d), c: 'Triggers' });
					}
				}
				optionList = optionList.concat(options.sort($scope.sortByDisplay));
			}
			comparison.options = optionList;
		}


		var comp = $scope.db.comparisons.conditions[comparison.operator] || $scope.db.comparisons.triggers[comparison.operator];		
		comparison.operatorValid = !!comp;
		comparison.parameterCount = comp && comp.p ? comp.p : 0;
		comparison.multiple = comp && comp.m ? true : false;

		comparison.valid = comparison.left.valid && comparison.operatorValid;

		comparison.timed = comp ? comp.t : 0;

		if (comparison.parameterCount > 0) {
			comparison.right.multiple = comparison.multiple;
			comparison.right.disableAggregation = comparison.multiple;
			comparison.right.dataType = comparison.left.selectedDataType;
			if (angular.toJson(comparison.right.options) != angular.toJson(comparison.left.selectedOptions)) {
				//avoid angular circus
				comparison.right.options = comparison.left.selectedOptions;
			}
			$scope.validateOperand(comparison.right, reinit, true);
			comparison.valid = comparison.valid && comparison.right.valid;
		}

		if (comparison.parameterCount > 1) {
			comparison.right2.multiple = comparison.multiple;
			comparison.right2.disableAggregation = comparison.multiple;
			comparison.right2.dataType = comparison.left.selectedDataType;
			if (angular.toJson(comparison.right2.options) != angular.toJson(comparison.left.selectedOptions)) {
				//avoid angular circus
				comparison.right2.options = comparison.left.selectedOptions;
			}
			$scope.validateOperand(comparison.right2, reinit, true);
			comparison.valid = comparison.valid && comparison.right2.valid;
		}

		if (comparison.timed) $scope.validateOperand(comparison.time, reinit, true);
		//$scope.refreshSelects();

	}


	$scope.detectDataType = function(value) {
		switch (typeof value) {
			case 'string':
				if (!isNaN(parseFloat(value))) return 'number';
				return 'string';
			case 'number':
				return 'number';
			default:
				return 'string';
		}
	};

	$scope.renderOperand = function(operand, noQuotes, pedantic) {
		var result = '';
		if (operand) {
			if (operand instanceof Array) {
				result = $scope.renderDeviceList(operand, null, 'and', true);
			} else {
				switch (operand.t) {
					case 'd': //physical devices
						if (operand.d)
							result = $scope.buildDeviceNameList(operand.d);
						break;
					case 'p': //physical devices
						if (operand.d && operand.a)
							result = $scope.renderDeviceList(operand.d, operand.a, operand.g, true) + ' <span attr>' + operand.a + '</span>';
						break;
					case 'v': //physical devices
						var device = $scope.getVirtualDeviceById(operand.v);
						result = '<span vdev>' + (device ? device.n : '(invalid virtual device)') + '</span>';
						break;
					case 's': //preset
						if (operand.s)
							result = '<span num>' + operand.s + '</span>';
						break;
					case 'x': //variable
						if (operand.x)
							result = '<span var>{' + operand.x + '}</span>';
						break;
					case 'c': //constant
						var m = 'num';
						noQuotes = noQuotes || !isNaN(operand.c);
						switch (operand.vt) {
							case 'time':
								var date = $scope.localTimeToDate(operand.c);
								result = '<span num>' + date.toLocaleTimeString({hour: '2-digit', minute:'2-digit'}) + '</span>';
								break;
							case 'date':
								result = '<span num>' + utcToString(operand.c) + '</span>';
								break;
							case 'datetime':
								result = '<span num>' + utcToString(operand.c) + '</span>';
								break;
							case 'piston':
								result = '<span lit>' + $scope.getPistonName(operand.c) + '</span>';
								break;
							default:
								//if we still think we need quotes, let's make sure booleans don't have any
								if (!noQuotes) {
									if ((operand.vt == 'boolean') || (operand.vt == 'enum')) noQuotes = true;
									m = 'lit';
								}
								result = '<span ' + m + '>' + scope.buildName(operand.c, noQuotes, pedantic) + '</span>';
						}
						break;
					case 'e': //expression
						if (operand.e)
							result = '<span exp>{' + operand.e + '}</span>';
						break;
				}
			}
		}
		result = result ? result : '(invalid operand)';
		return (result instanceof String) ? $sce.trustAsHtml(result) : result;
	}


	$scope.renderForOperands = function(statement) {
		var result;
		result = '<span var>' + (statement.x ? statement.x : '$index') + '</span> <span pun>=</span> <span num>' + $scope.renderOperand(statement.lo) + ' <span kwd>to</span> ' + $scope.renderOperand(statement.lo2) + ' <span kwd>step</span> ' + $scope.renderOperand(statement.lo3);
		return $sce.trustAsHtml(result ? result : '(invalid operands)');
	}

	$scope.renderForEachOperands = function(statement) {
		var result;
		result = '<span var>' + (statement.x ? statement.x : '$device') + '</span> <span pun>in</span> <span num>' + $scope.renderOperand(statement.lo);
		return $sce.trustAsHtml(result ? result : '(invalid operands)');
	}

	$scope.renderComparison = function(l, o, r, r2) {
		var comparison = $scope.db.comparisons.conditions[o] || $scope.db.comparisons.triggers[o];
		if (!comparison) return '[ERROR: Invalid comparison]';
		var pedantic = l.t == 'v';
		var plural = l && (l.t == 'p') && l.d && (l.d.length > 1) && (l.g == 'all');
		var noQuotes = false;
		if (l.t == 'v') {
			switch (l.v) {
				case 'locationMode':
				case 'shmState':
					noQuotes = true;
					break;
			}
		}
		var indexes = '';
		if ((comparison.g == 'm') && l.i && l.i.length) {
			indexes = ' <span num>' + $scope.buildNameList(l.i, 'or', null, null, false, true, false, '#') + '</span>';
		}
		var result = $scope.renderOperand(l) + indexes + ' <span kwd>' + (plural ? (comparison.dd ? comparison.dd : comparison.d) : comparison.d) + '</span>' + (comparison.p > 0 ? ' ' + $scope.renderOperand(r, noQuotes, pedantic) : '') + (comparison.p > 1 ? ' <span pun>' + (comparison.d.indexOf('between') ? 'and' : 'through') + '</span> ' + $scope.renderOperand(r2, noQuotes, pedantic) : '')
		return $sce.trustAsHtml(result);
	}


	$scope.getWeekDayName = function(day) {
		if (isNaN(day)) return 'day';
		return $scope.weekDays[parseInt(day)];
	}

	$scope.getMonthDayName = function(day) {
		switch (day) {
			case -1: return 'last';
			case -2: return 'second-last';
			case -3: return 'third-last';
		}
		return day + $scope.getOrdinalSuffix(day)
	}

	$scope.getMonthName = function(month) {
		return $scope.yearMonths[month];
	}

	$scope.getDurationUnitName = function(unit, plural) {
		var suffix = plural ? 's' : '';
		switch(unit) {
			case 'ms': return 'millisecond' + suffix;
			case 's':  return 'second' + suffix;
			case 'm':  return 'minute' + suffix;
			case 'h':  return 'hour' + suffix;
			case 'd':  return 'day' + suffix;
			case 'w':  return 'week' + suffix;
			case 'n':  return 'month' + suffix;
			case 'y':  return 'year' + suffix;
		}
		return '';
	};

	$scope.renderTimer = function(timer) {
		var result = '';
		var interval = timer.lo;
		var unit = $scope.getDurationUnitName(interval.vt);
		var unit2 = unit;
		var level = 0;		
		switch(interval.vt) {
			case 'ms': level = 1; break;
			case 's':  level = 2; break;
			case 'm':  level = 3; break;
			case 'h':  level = 4; break;
			case 'd':  level = 5; break;
			case 'w':  level = 6; unit = $scope.getWeekDayName(interval.odw); break;
			case 'n':  level = 7; unit = $scope.getMonthDayName(interval.odm) + ' ' + $scope.getWeekDayName(interval.odw) + ' of the month'; break;
			case 'y':  level = 8; unit = $scope.getMonthDayName(interval.odm) + ' ' + $scope.getWeekDayName(interval.odw) + ' of ' + $scope.getMonthName(interval.omy); break;
		}
		switch (interval.t) {
			case 'c':
				if (!isNaN(interval.c)) {
					var c = parseInt(interval.c);
					switch (c) {
						case 1:
							result = unit;
							break;
						case 2:
							result = '<span num>other</span> ' + unit;
							break;
						default:
							result = '<span num>' + c + '</span> ' + unit2 + 's';
							switch (interval.vt) {
								case 'n': result += ', on the ' + $scope.getMonthDayName(interval.odm) + ' ' + $scope.getWeekDayName(interval.odw) + ' of the month'; break;
								case 'y': result += ', on the ' + $scope.getMonthDayName(interval.odm) + ' ' + $scope.getWeekDayName(interval.odw) + ' of ' + $scope.getMonthName(interval.omy); break;
							}
					}
					break;
				}
			default:
				result = $scope.renderOperand(interval) + ' ' + unit + 's';			
		}
		if (level == 4) {
			var m = ('00' + timer.lo.om).substr(-2);
			result += ', <span lit>at <span num>:' + m + '</span> <span lit>past the hour</span>';
		}
		if (level >= 5) {
			//higher levels require a time of day
			result += ', <span lit>at</span> ';
			if (timer.lo2.t != 'c') {
				//anything other than constants may have an offset
				switch (timer.lo3.t) {
					case 'c':
						var offset = isNaN(timer.lo3.c) ? 0 : parseInt(timer.lo3.c);
						if (offset == 0) {
							result += $scope.renderOperand(timer.lo2);
						} else if (offset < 0) {
							result += '<span num>' + (-offset).toString() + '</span> <span lit>' + $scope.getDurationUnitName(timer.lo3.vt, (offset < -1)) + ' before</span> ' + $scope.renderOperand(timer.lo2);
						} else {
							result += '<span num>' + offset.toString() + '</span> <span lit>' + $scope.getDurationUnitName(timer.lo3.vt, (offset > 1)) + ' after</span> ' + $scope.renderOperand(timer.lo2);
						}
						break;
					default:
						result += $scope.renderOperand(timer.lo2) + ' <span num>±</span> ' + $scope.renderOperand(timer.lo3);
				}
			} else {
				result += $scope.renderOperand(timer.lo2);
			}
		}
		

		//render restrictions
		var om = (level <= 2) && (interval.om instanceof Array) && interval.om.length ? interval.om : null;
		var oh = (level <= 3) && (interval.oh instanceof Array) && interval.oh.length ? interval.oh : null;
		var odw = (level <= 5) && (interval.odw instanceof Array) && interval.odw.length ? interval.odw : null;
		var odm = (level <= 6) && (interval.odm instanceof Array) && interval.odm.length ? interval.odm : null;
		var owm = (level <= 6) && !odm && (interval.owm instanceof Array) && interval.owm.length ? interval.owm : null;
		var omy = (level <= 7) && (interval.omy instanceof Array) && interval.omy.length ? interval.omy : null;
		
		if (!!om || !!oh || !!odw || !!odm || !!owm || !!omy) {
			//we have restrictions
			var rCount = 0;
			result += '<span lit>,</span> <span lit>but only</span>';
			if (om) {
				result += ' <span lit>at</span> ';
				for(i in om) {
					if ((i > 0) && (om.length > 2)) result += '<span lit>,</span> ';
					if ((i > 0) && (i == om.length - 1)) result += ' <span lit>or</span> ';
					result += '<span num>:' + ('00' + om[i]).substr(-2) + '</span>';
				}
				result += ' <span lit>minutes past the hour</span>';
				rCount++;
			}
			if (oh) {
				result += (rCount ? '<span lit>,</span>' : '') + ' <span lit>during the</span> ';
				for(i in oh) {
					if ((i > 0) && (oh.length > 2)) result += '<span lit>,</span> ';
					if ((i > 0) && (i == oh.length - 1)) result += ' <span lit>or</span> ';
					result += '<span num>' + $scope.formatHour(oh[i]) + '</span>';
				}
				result += ' <span lit>hour' + (oh.length > 1 ? 's' : '') + '</span>';
				rCount++;
			}
			var odwString = '';
			if (odw) {
				for(i in odw) {
					if ((i > 0) && (odw.length > 2)) odwString += '<span lit>,</span> ';
					if ((i > 0) && (i == odw.length - 1)) odwString += ' <span lit>or</span> ';
					odwString += '<span lit>' + $scope.weekDays[odw[i]] + 's</span>';
				}
				rCount++;
			}
			if (owm) {
				result += (rCount ? '<span lit>,</span>' : '') + ' <span lit>on the</span> ';
				for(i in owm) {
					if ((i > 0) && (owm.length > 2)) result += '<span lit>,</span> ';
					if ((i > 0) && (i == owm.length - 1)) result += '<span lit>or</span> ';
					result += '<span num>' + $scope.getOrdinal(owm[i]) + '</span>';
				}
				result += ' ' + (odwString ? odwString : '<span lit>week' + (owm.length > 1 ? 's' : '') + '</span>') + (omy ? '' : ' <span lit>of the month</span>');
				rCount++;
			} else {
				if (odwString) {
					result += (rCount > 1 ? '<span lit>,</span>' : '') + ' <span lit>on</span> ' + odwString;
				}
			}
			if (odm) {
				result += (rCount ? '<span lit>,</span>' : '') + ' <span lit>on the</span> ';
				for(i in odm) {
					if ((i > 0) && (odm.length > 2)) result += '<span lit>,</span> ';
					if ((i > 0) && (i == odm.length - 1)) result += '<span lit>or</span> ';
					result += '<span num>' + $scope.getOrdinal(odm[i]) + '</span>';
				}
				result += ' day' + (odm.length > 1 ? 's' : '') + (omy ? '' : ' <span lit>of the month</span>');
				rCount++;
			}
			if (omy) {
				result += ' <span lit>' + (owm || odm ? 'of' : 'in') + '</span> ';
				for(i in omy) {
					if ((i > 0) && (omy.length > 2)) result += '<span lit>,</span> ';
					if ((i > 0) && (i == omy.length - 1)) result += ' <span lit>or</span> ';
					result += '<span lit>' + $scope.yearMonths[omy[i] - 1] + '</span>';
				}
				rCount++;
			}
		}

		return $sce.trustAsHtml(result);
	};

	$scope.renderTask = function(task) {
		var command = $scope.getCommandById(task.c);
		var display;
		if (!command) {
			display = task.c + '(';
			var i = 0;
			for (p in task.p) {
				display += (i ? ', ' : '') + $scope.renderOperand(p);
				i++;
			}
			display += ')';
		} else {
			display = !command.d ? command.n : command.d.replace(/\{(\d)\}/g, function(match, text) {
				var idx = parseInt(text);
				if ((idx < 0) || (!task.p) || (idx > task.p.length))
					return '?';
				if ((idx < 0) || (!task.p) || (idx > task.p.length))
					return '?';
				var value = '';
				if (command.p[idx].t == 'duration') {
					var unit = $scope.getDurationUnitName(task.p[idx].vt, true);
					value = $scope.renderOperand(task.p[idx], true) + ' ' + unit;
				} else {
					value = $scope.renderOperand(task.p[idx], true);
				}
				if (!value) value = '';
				if (!!value && !!command.p[idx].d) {
					value = (!!task.p[idx] && !!task.p[idx].t) ? command.p[idx].d.replace('{v}', value) : '';
				}
				return value;
			}).replace(/(\{T\})/g, '°' + $scope.location.temperatureScale);
			var icon = command.i;
			if (icon) display = '<span pun><i class="fa fa-' + icon + '" aria-hidden="true"></i></span> ' + display;
		}
		if (task.m) {
			display += ' <span pun><i>(only while ' + $scope.buildLocationModeNameList(task.m) + ')</i></span>';
		}
		display += '<span pun>;</span>';
		return $sce.trustAsHtml(display);
	}


	$scope.renderDeviceList = function(devices, attribute, aggregation, trailing) {
		var result = '';
		var deviceNames = [];
		suffix = (aggregation == 'any' ? 'or' : 'and');
		var prefix = '';
		if (devices instanceof Array) {
			if (devices.length > 1) {
				switch (aggregation) {
					case 'any':
						prefix = 'Any of ';
						break;
					case 'all':
						prefix = 'All of ';
						break;
					case 'count':
						prefix = 'Count of ';
						break;
					case 'avg':
						prefix = 'Average of ';
						break;
					case 'median':
						prefix = 'Median of ';
						break;
					case 'least':
						prefix = 'Least occurring value of ';
						break;
					case 'most':
						prefix = 'Most occurring value of ';
						break;
					case 'stdev':
						prefix = 'Standard deviation of ';
						break;
					case 'min':
						prefix = 'Minimum of ';
						break;
					case 'max':
						prefix = 'Maximum of ';
						break;
					case 'variance':
						prefix = 'Variance of ';
						break;
				}
				if (!trailing) prefix = prefix.toLowerCase();
			}
			for (deviceIndex in devices) {
				var device = $scope.getDeviceById(devices[deviceIndex]);
				if (device) {
					deviceNames.push(device.n);
				}
			}
			if (deviceNames.length) {
				result = prefix + $scope.buildNameList(deviceNames, suffix, 'dev', '', !!attribute, true);
			}
		}
		return $sce.trustAsHtml(result);
	};









































	$scope.breakList = function(list) {
		return list.replace(/,/g , '<br/>')
	};
	
	var formatDate = function(date) {
		var year = date.getFullYear();
		var month = (1 + date.getMonth()).toString();
		month = month.length > 1 ? month : '0' + month;
		var day = date.getDate().toString();
		day = day.length > 1 ? day : '0' + day;
		return month + '/' + day + '/' + year;
	}
	
	$scope.getMonth = function(date) {
		if (date) {
			return ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC'][date.getMonth()];
		}
	};

	$scope.getDay = function (date) {
		if (date) {
			return ('0' + date.getDate()).substr(-2);
		}
	};	

	$scope.timeSince = timeSince;
	$scope.timeCounter = timeCounter;
	$scope.timeLeft = timeLeft;
	$scope.currentTime = currentTime;

	$scope.tap = function(tapId) {
		dataService.tap(tapId).then(function (response) {
		});
	};

	$scope.togglePiston = function(piston, $event) {
		if ((!piston) && (!$scope.viewerPiston || !$scope.viewerPiston.app)) return;
		var pistonId = piston ? piston.i : $scope.pistonId;
		if (pistonId) {
			$timeout.cancel(tmrRefresh);
			var enabled = !(piston ? piston.e : $scope.viewerPiston.app.enabled);
			if (piston) {
				piston.e = enabled;
			} else {
				$scope.viewerPiston.app.enabled = enabled;
			}
			if (enabled) {
				dataService.resumePiston(pistonId).then(function (response) {
					$scope.onRefresh(response);
				});

			} else {
				dataService.pausePiston(pistonId).then(function (response) {
					$scope.onRefresh(response);
				});
			}
		}
		if ($event && e.preventDefault) $event.preventDefault();
		if ($event && $event.stopPropagation) $event.stopPropagation();
	}

	$scope.configurePiston = function(piston) {
		$scope.configuredPistonId = $scope.configuredPistonId == piston.i ? null : piston.i;
	}

	$scope.showPiston = function(piston) {
		document.body.scrollTop = 0;
		$scope.viewerPiston = null;
		$scope.pistonId = piston.i;
		$scope.refresh();
		window.onSwipeRight = $scope.hidePiston;
	}

	$scope.hidePiston = function() {
		document.body.scrollTop = 0;
		$scope.pistonId = null;
		window.onSwipeRight = null;
	}


	$scope.prepareActions = function(condition) {
		var actions = [];
		var trueActions = [];
		var falseActions = [];
		var mainGroup = (condition.id <= 0);
		var tasks = $scope.viewerPiston.tasks;
		var acts = $scope.viewerPiston.app.actions
		for (action in acts) {
			if (acts[action].pid == condition.id) {
				if (acts[action].t) {
					var actionTasks = acts[action].t;
					for (t in actionTasks) {
						var time = 0;
						for(task in tasks) {
							if ((tasks[task].type == 'cmd') && (tasks[task].ownerId == acts[action].id) && (tasks[task].taskId == actionTasks[t].i)) {
								if ((time == 0) || (time > tasks[task].time)) {
									time = tasks[task].time;
								}
							}
						}
						actionTasks[t].time = time;
					}
				}
				if (mainGroup) {
					actions.push(acts[action]);
				} else {
					if (acts[action].rs == false) {
						falseActions.push(acts[action]);
					} else {
						trueActions.push(acts[action]);
					}
				}
			}
		}
		var time = 0;
		for(task in tasks) {
			if ((tasks[task].type == 'evt') && (tasks[task].ownerId == condition.id)) {
				if ((time == 0) || (time > tasks[task].time)) {
					time = tasks[task].time;
				}
			}
		}
		condition.time = time;
		condition.actions = actions;
		condition.trueActions = trueActions;
		condition.falseActions = falseActions;
		condition.$scope = $scope;
		if (condition.children) {
			for (child in condition.children) {
				$scope.prepareActions(condition.children[child]);
			}
		}
	}

	$scope.hadRecentActivity = function(piston) {
		return piston && piston.le && piston.le.event && piston.le.event.date && (timeLeft((new Date(piston.le.event.date)).getTime()) > -120);
	};

	$scope.toggleViewerOptions = function() {
		$scope.viewerPiston.showOptions = !$scope.viewerPiston.showOptions;
	};

	$scope.getSecondaryStatementName = function() {
		var mode = $scope.viewerPiston.app.mode;
		switch (mode) {
			case 'Latching': return 'BUT IF';
			case 'Then-If': return 'THEN IF';
			case 'Else-If': return 'ELSE IF';
			case 'Or-If': return 'OR IF';
			case 'And-If': return 'AND IF';
		}
		return 'IF';
	}

	$scope.serializeObject = function(object) {
		return angular.toJson(object);
	}

	$scope.anonymizeObject = function(object, returnAsString) {
		var data = $scope.serializeObject(object);
		var matches = data.match(/(:[a-f0-9]{32}:)/g);
		if (matches) matches = matches.unique();
		for(i in matches) {
			data = data.replace(new RegExp(matches[i], 'g'), ('xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx' + i).substr(-32));
		}
		return (returnAsString ? data : angular.fromJson(data));
	}

	$scope.objectToBlob = function(object, contentType) {
		contentType = contentType || '';
		var sliceSize = 1024;
		var data = btoa($scope.serializeObject(object));
		data += '|' + data.length.toString();
		var bytesLength = data.length;
		var slicesCount = Math.ceil(bytesLength / sliceSize);
		var byteArrays = new Array(slicesCount);
	
		for (var sliceIndex = 0; sliceIndex < slicesCount; ++sliceIndex) {
			var begin = sliceIndex * sliceSize;
			var end = Math.min(begin + sliceSize, bytesLength);
	
			var bytes = new Array(end - begin);
			for (var offset = begin, i = 0 ; offset < end; ++i, ++offset) {
				bytes[i] = data[offset].charCodeAt(0);
			}
			byteArrays[sliceIndex] = new Uint8Array(bytes);
		}
		return new Blob(byteArrays, { type: contentType });
	}


	$scope.dataURItoBlob = function(dataURI) {
	// convert base64 to raw binary data held in a string
	// doesn't handle URLEncoded DataURIs - see SO answer #6850276 for code that does this
	var byteString = atob(dataURI.split(',')[1]);
	
	// separate out the mime component
	var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0]
	
	// write the bytes of the string to an ArrayBuffer
	var ab = new ArrayBuffer(byteString.length);
	var ia = new Uint8Array(ab);
	for (var i = 0; i < byteString.length; i++) {
		ia[i] = byteString.charCodeAt(i);
	}
	
	// write the ArrayBuffer to a blob, and you're done
	var blob = new Blob([ab], {type: mimeString});
	return blob;
	
	// Old code
	// var bb = new BlobBuilder();
	// bb.append(ab);
	// return bb.getBlob(mimeString);
	}



	$scope.snapshot = function(anonymize) {
		var cnt = 0;
		var counter = function() {
			cnt++;
			return cnt;
		}
		var data = (anonymize ? $scope.anonymizeObject($scope.piston) : $scope.piston);
		$scope.loading = true;
		dataService.generateBackupBin(data, anonymize).then(function(response) {
			var bin = response.data;
			var piston = document.getElementById('piston');
			$scope.view.exportBin = bin;
			$timeout(function() {
				var width = piston.clientWidth;
				var height = piston.clientHeight + (anonymize ? 0 : 64);
				piston.setAttribute('printing', '');
				if (anonymize) piston.setAttribute('anonymized', '');
				html2canvas(piston, {width: width, height: height, counter: counter}).then(function(canvas) {
					$scope.loading = false;
					var reader = new window.FileReader();
					console.log('Embedding data into PNG: ' + data);
					reader.readAsDataURL(new Blob([$scope.dataURItoBlob(canvas.toDataURL('image/png')), $scope.objectToBlob(data, 'image/png')], {type: 'image/png'})); 
					reader.onloadend = function() {
						$scope.capturedImage = reader.result;
						$scope.dialogCapture = ngDialog.open({
							template: 'dialog-captured-image',
							className: 'ngdialog-theme-default ngdialog-auto-size',
							disableAnimation: true,
							closeByDocument: false,
							scope: $scope,
							showClose: true
						});
					}
				});
				piston.removeAttribute('printing');
				piston.removeAttribute('anonymized');
				delete($scope.view.exportBin);
			}, 1);
		});
	}


	$scope.textSnapshot = function() {
		copyToClipboard('piston');
	}



	$scope.parseString = function(string) {
		return $scope.parseExpression(string, true);
	}

	$scope.parseExpression = function(str, parseAsString) {
		str = str ? str.toString() : "";
		//remove \r \n
		//str = str.replace(/[\r\n]*/g, "");
		var i = 0;
		var initExp = !!parseAsString ? 0 : 1;
		var exp = initExp;
		var sq = false;
		var dq = false;
		var dv = false;
		var func = 0;
		var parenthesis = 0;
		function location(start, end) {
			return start == end ? start.toString() : start.toString() + ':' + end.toString();
		}
		function main() {
			var arr = [];
			var startIndex = i;
			function addOperand() {
				if (i-1 > startIndex) {
					var value = str.slice(startIndex, i-1);
					var parsedValue = parseFloat(value.trim());
					if (!isNaN(parsedValue)) {
						if (Number.isInteger(parsedValue)) {
							arr.push({t: 'integer', v: parseInt(parsedValue), l: location(startIndex, i - 2)});
							return true;
						}
						arr.push({t: 'decimal', v: parsedValue, l: location(startIndex, i - 2)});
						return true;
					}
					if (typeof value == 'string') {
						arr.push({t: 'variable', x: value, l: location(startIndex, i - 2)});
						return true;
					}
					arr.push({t: 'operand', v:str.slice(startIndex, i-1), l: location(startIndex, i - 2)});
					return true;
				}
				return false;
			}
			function addConstant() {
				if (i-1 > startIndex) {
					arr.push({t: 'string', v: str.slice(startIndex, i-1), l: location(startIndex, i - 2)});
				}
			}
			function addDevice() {
				if (i-1 > startIndex) {
					var value = str.slice(startIndex, i-1);
					var pos = value.lastIndexOf(':');
					var deviceName = value;
					var attribute = '';
					if (pos) {
						var deviceName = value.substr(0, pos).trim();
						attribute = value.substr(pos + 1).trim();
					}
					var device = $scope.getDeviceByName(deviceName);
					if (device && device.id) {
						//a device was found
						var a = attribute.toLowerCase();
						attribute = '';
						for (attributeIndex in device.a) {
							var attr = device.a[attributeIndex];
							if (a == attr.n.toLowerCase()) {
								attribute = attr.n;
							}
						}
						arr.push({t: 'device', id: device.id, a: attribute, l: location(startIndex - 1, i - 1)});
					} else {
						//the device name is probably a variable?!
						arr.push({t: 'device', x: deviceName, a: attribute, l: location(startIndex - 1, i - 1)});
					}
				}
			}
			function addFunction() {
				var value = str.slice(startIndex, i-1).toLowerCase();
				if ($scope.db.functions[value]) {
					func++;
					var params = main();
					var items = [];
					var item = null;
					for(p in params) {
						if (!item) item = {t: 'expression', i: []};
						if ((params[p].t == 'operator') && (params[p].o == ',')) {
							items.push(item);
							item = {t: 'expression', i: []};
						} else {
							item.i.push(params[p]);
						}
					}
					if (item) items.push(item);
					arr.push({t: 'function', n: value, i: items, l: location(startIndex, i - 1)});
					func--;
				} else {
					addOperand();
					arr.push({t: 'expression', i: main(), l: location(startIndex, i - 1)});
					startIndex = i;
				}
			}
			while (i < str.length) {
				var c = str[i++];
				switch(c) {
					case ' ':
					case '\t':
					case '\r':
					case '\n':
						if (exp && !dv && !dq && !sq) {
							addOperand();
							startIndex = i;
						}
						continue;
					case '+':
					case '-':
					case '/':
					case '*':
					case '^':
					case '&':
					case '|':
					case ',':
						if (exp && !dv && !sq && !dq) {
							addOperand();
							arr.push({t: 'operator', o: c, l: location(i - 1, i - 1)});
							startIndex = i;
						}
						continue;
					case '"':
						if (exp && !dv && !sq) {
							dq = !dq;
							(dq ? addOperand() : addConstant());
							startIndex = i;
						}
						continue;
					case '\'':
						if (exp && !dq) {
							sq = !sq;
							(sq ? addOperand() : addConstant());
							startIndex = i;
						}
						continue;
					case '(':
						if (exp && !dv && !dq && !sq) {
							parenthesis++;
							addFunction();
							startIndex = i;
						}
						continue;
					case ')':
						if (exp && !dv && !dq && !sq) {
							parenthesis--;
							addOperand();
							startIndex = i;
							return arr;
						}
						continue;
					case '[':
						if (exp && !dq && !sq && !dv) {
							dv = true;;
							addOperand();
							startIndex = i;
						}
						continue;
					case ']':
						if (exp && dv && !dq && !sq) {
							addDevice();
							dv = false;
							startIndex = i;
						}
						continue;
					case '{':
							if (exp == initExp) {
								exp++;
								addConstant();
								startIndex = i;
								arr.push({t: 'expression', i: main(), l: location(startIndex - 1, i - 1)});
								startIndex = i;
							} else {
								exp++;
								startIndex = i;
								arr.push({t: 'expression', i: main(), l: location(startIndex - 1, i - 1)});
								startIndex = i;
							}
						continue;
					case '}':
							addOperand();
							exp--;
							return arr;
						continue;
				}
			}
			i++;
			exp ? addOperand() : addConstant();
			return arr;

		}
		var items = main();
		var result = {t: 'expression', i: items, str: str};
		if (exp != initExp) {
			result.err = 'Invalid expression closure termination';
		} else if (sq) {
			result.err = 'Invalid single quote termination';
		} else if (dq) {
			result.err = 'Invalid double quote termination';
		} else if (parenthesis) {
			result.err = 'Invalid parenthesis closure termination';
//		} else if (!items || !items.length) {
//			result.err = 'Empty expression';
		}
		result.ok = !result.err;
		if (result.ok) {
			//first phase passed, further examine the expression
			result.ok = $scope.validateExpression(result);
		}
		return result;
	};

	$scope.validateExpression = function(expression) {
		var error = '';
		var errVar = '';
		var errorLoc = '';
		function getSubstring(location, separator, partNo) {
			if (!location) return '';
			location = location.toString().split(':');
			var start = parseInt(location[0]);
			var end = (location.length == 2) ? parseInt(location[1]) : start;
			var s = expression.str.substr(start, end - start + 1);
			//remove [ ] from devices
			if ((s.substr(0, 1) == '[') && (s.substr(-1, 1) == ']')) s = s.substr(1, s.length - 2);
			if (separator) {
				s = s.split(separator);
				if (partNo >= s.length) return '';
				return s[partNo].trim();
			}
			return s.trim();
		}
		function validateItem(item) {
			var ok = true;
			var err = '';
			var loc = '';
			if (item.i) {
				for (subitem in item.i) ok = ok && validateItem(item.i[subitem]);
			} else {			
				switch (item.t) {
					case 'device':
							if (!item.x && !item.id) {
								ok = false;
								err = 'Invalid device ' + getSubstring(item.l, ':', 0);
								loc = item.l;
								break;
							}
							if (!item.id && item.x && !(($scope.systemVars && scope.systemVars[item.x]) || ($scope.globalVars && $scope.systemVars[item.x]) || $scope.getVariableByName(item.x))) {
								ok = false;
								err = 'Invalid device variable ' + getSubstring(item.l, ':', 0);
								loc = item.l;
								break;
							}
							if (!item.a) {
								ok = false;
								err = 'Invalid attribute ' + getSubstring(item.l, ':', 1);
								loc = item.l;
								break;
							}
							break;
					case 'variable':
							if ($scope.systemVars && $scope.systemVars[item.x]) break;
							if ($scope.globalVars && $scope.globalVars[item.x]) break;
							if (!$scope.getVariableByName(item.x)) {
								ok = false;
								errVar = getSubstring(item.l);
								err = 'Variable ' + errVar + ' not found';
								loc = item.l;
								break;
							}
							 break;
				}
			}
			item.ok = ok;
			if (err) {
				item.err = err;
				if (!error) {
					error = err;
					errorLoc = loc;
				}
			}
			return ok;
		}
		validateItem(expression);
		if (error) {
			expression.err = error;
			expression.errVar = errVar;
			expression.loc = errorLoc;
		}
		return expression.ok;
	}



	$scope.hexToHsl = function(hex){
		var rgb = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
		if (!rgb) return {h: 0, s: 0, l: 0};
	    var r = 0.0 + parseInt(rgb[1], 16) / 255;
		var g = 0.0 + parseInt(rgb[2], 16) / 255;
		var b = 0.0 + parseInt(rgb[3], 16) / 255;
	    var max = Math.max(r, g, b), min = Math.min(r, g, b);
	    var h, s, l = (max + min) / 2.0;
	    if(max == min){
	        h = s = 0; // achromatic
	    }else{
	        var d = max - min;
	        s = (l > 0.5) ? d / (2 - max - min) : d / (max + min);
	        switch(max){
	            case r: h = (g - b) / d + (g < b ? 6 : 0); break;
	            case g: h = (b - r) / d + 2; break;
	            case b: h = (r - g) / d + 4; break;
	        }
	        h = h / 6;
	    }
	    return {
			h: Math.round(360 * h),
			s: Math.round(100 * s),
			l: Math.round(100 * l)
		};
	};

	$scope.evaluateExpression = function(expression, dataType) {
		dataService.evaluateExpression($scope.pistonId, expression, dataType).then(function (response) {
			console.log(response);
		});
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
	$scope.formatTime = window.formatTime
	$scope.utcToString = utcToString;
	$scope.formatLogTime = function(timestamp, offset) { return utcToString(timestamp) + '+' + offset; };
	$scope.md5 = window.md5;
}]);

function test(value, parseAsString) {
	scope.evaluateExpression(scope.parseExpression(value, parseAsString));
}
