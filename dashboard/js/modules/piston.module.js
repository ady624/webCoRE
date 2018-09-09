config.controller('piston', ['$scope', '$rootScope', 'dataService', '$timeout', '$interval', '$location', '$sce', '$routeParams', 'ngDialog', '$window', '$animate', '$q', function($scope, $rootScope, dataService, $timeout, $interval, $location, $sce, $routeParams, ngDialog, $window, $animate, $q) {
	var tmrReveal;
	var tmrStatus;
	var tmrActivity;
	var tmrClock;
	var statusAttribute = '$status';
	$scope.lastLogEntry = 0;
	$scope.error = '';
	$scope.loading = true;
	$scope.initialized = false;
	$scope.mode = 'view';
	$scope.logging = '0';
	$scope.data = null;
	$scope.error = '';
	$scope.pistonId = $routeParams.pistonId;
	$scope.piston = null;
	$scope.designer = {};
	$scope.showAdvancedOptions = false;
	$scope.dk = 'N7zqL6a8Texs4wY5y&y2YPLzus+_dZ%s';
	$scope.params = $location.search();
	$scope.insertIndexes = {};
	$scope.warnings = {};
	$scope.evalType = 'v';
	$scope.evalText = '';
	$scope.evals = [];
	$scope.lastEval = 0;
	$scope.category = '0';
	$scope.categories = [];
	if ($scope.params) $location.search({});
	$scope.stack = {
		undo: [],
		redo: []
	};
	$scope.weekDays = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
	$scope.yearMonths = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
	dataService.getImportedData().then(function(data) {
		$scope.canResumeImport = data && data.length;
	});

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

	$scope.version = function() {
		return $window.version();
	}


	$scope.encodeEmoji = function(value) {
		if (!value) return '';
		//return value.replace(/([\u20a0-\u32ff]|[\u1f000-\u1ffff]|[\ufe4e5-\ufe4ee])/g, function(match) {
		return value.replace(/([\uD83C-\uDBFF][\uDC00-\uDFFF])/g, function(match) {
			return encodeURIComponent(match);
		});
	};

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

	$scope.listAvailableContacts = function() {
		var result = [];
		for(i in $scope.instance.contacts) {
			var contact = $scope.instance.contacts[i];
			result.push({v: i, n: (contact.f + ' ' + contact.l).trim() + (contact.p ? ' (PUSH)' : (contact.t ? ' (' + contact.t + ')' : '')), an: contact.an});
		}
		if (!result.length) {
			result.push({v: 'no one', n: 'No available contacts'});
		}
		return result;
	}

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

    $scope.getLifxSceneName = function(sceneId) {
		if (!$scope.instance.lifx.scenes) return sceneId;
		var sceneName = $scope.instance.lifx.scenes[sceneId];
		if (!sceneName) return sceneId;
		return sceneName;
    };

    $scope.getLifxSelectorName = function(selectorId) {
		if (!$scope.instance.settings) return selectorId;
		var name = $scope.instance.lifx.lights ? $scope.instance.lifx.lights[selectorId] : null;
		if (name) return name;
		name = $scope.instance.lifx.groups ? $scope.instance.lifx.groups[selectorId] : null;
		if (name) return name;
		name = $scope.instance.lifx.locations ? $scope.instance.lifx.locations[selectorId] : null;
		if (name) return name;
		name = $scope.instance.lifx.scenes ? $scope.instance.lifx.scenes[selectorId] : null;
		if (name) return name;
		return selectorId;
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
				if (response.activity.globalVars) {
					$scope.updateGlobalVars(response.activity.globalVars);
				}
			}
			tmrActivity = $timeout($scope.updateActivity, 3000);
		}, function (error) {
			tmrActivity = $timeout($scope.updateActivity, 3000);
		});
	}


	$scope.updateGlobalVars = function(globalVars) {
		$scope.globalVars = $scope.globalVars instanceof Object ? $scope.globalVars : {};
		for (varName in globalVars) {
			var varType = globalVars[varName].t;
			var varValue = globalVars[varName].v;
			var v = $scope.globalVars[varName];
			if (!v) {
				$scope.globalVars[varName] = {t: varType, v: varValue};
			} else {
				if (v.t != varType) v.t = varType;
				if (v.v != varValue) v.v = varValue;
			}
		}
		for (varName in $scope.globalVars) {
			if (!globalVars[varName]) delete($scope.globalVars[varName]);
		}
	}

	$scope.init = function() {
		if ($scope.$$destroyed) return;	
		dataService.setStatusCallback($scope.setStatus);
		$scope.initialized = false;
		$scope.loading = true;
		if ($scope.piston) $scope.loading = true;
		dataService.getPiston($scope.pistonId).then(function (response) {
			if ($scope.$$destroyed) return;
			$scope.endpoint = data.endpoint + 'execute/' + $scope.pistonId;
			try {
				var showOptions = $scope.piston ? !!$scope.showOptions : false;
				if (!response || !response.data || !response.data.piston) {
					$scope.error = $sce.trustAsHtml('Sorry, an error occurred while retrieving the piston data.');
					$scope.loading = false;
					return;
				}	
				$scope.piston = response.data.piston;
				$scope.validatePiston($scope.piston);
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
				$scope.logging = '' + (response.data.logging ? response.data.logging : 0);
				$scope.memory = response.data.memory ? response.data.memory : 0;
				$scope.lastExecuted = response.data.lastExecuted;
				$scope.nextSchedule = response.data.nextSchedule;
				$scope.schedules = response.data.schedules;
				$scope.categories = $scope.getCategories();
				$scope.category = $scope.meta.category ? $scope.meta.category : '0';
				
				$scope.lifx = {
					lights: !!$scope.instance.settings && !!$scope.instance.lifx.lights ? $scope.objectToArray($scope.instance.lifx.lights) : [],
					groups: !!$scope.instance.settings && !!$scope.instance.lifx.groups ? $scope.objectToArray($scope.instance.lifx.groups) : [],
					locations: !!$scope.instance.settings && !!$scope.instance.lifx.locations ? $scope.objectToArray($scope.instance.lifx.locations) : [],
					scenes: !!$scope.instance.settings && !!$scope.instance.lifx.scenes ? $scope.objectToArray($scope.instance.lifx.scenes) : []
				};
				
				$scope.initChart();
				if ($scope.instance && $scope.instance.devices) $scope.anonymizeDevices($scope.instance.devices);
				if ($scope.instance && $scope.instance.contacts) $scope.anonymizeContacts($scope.instance.contacts);
				$scope.devices = $scope.listAvailableDevices();
				$scope.contacts = $scope.listAvailableContacts();
				$scope.virtualDevices = $scope.listAvailableVirtualDevices();
				window.scope = $scope;
				$scope.localVars = response.data.localVars;
				$scope.globalVars = $scope.instance.globalVars;
				$scope.systemVars = response.data.systemVars;
				$scope.systemVarNames = []; //fix for angular ignoring keys that start with $
				for(name in $scope.systemVars) $scope.systemVarNames.push(name);
				$scope.meta.build = $scope.meta.build ? 1 * $scope.meta.build : 0;
				if ($scope.piston && ($scope.meta.build == 0)) {
					$scope.piston.z = $scope.params && $scope.params.description ? $scope.params.description : '';
					$scope.mode = 'edit';
					if ($scope.params && $scope.params.type != 'blank') {
						var getPiston;
						switch ($scope.params.type) {
							case 'duplicate':
								if ($scope.params.piston) {
									$scope.loading = true;
									getPiston = dataService.getPiston($scope.params.piston).then(function (response) {
										// Do not trigger a rebuild
										delete response.data.piston.l;
										return response.data.piston;
									});
								}
								break;
							case 'restore':
								if ($scope.params.bin) {
									$scope.loading = true;
									getPiston = dataService.loadFromBin($scope.params.bin).then(function (response) {
										return response.data;
									});
								}
								break;
							case 'import':
								if ($scope.params.piston) {
									$scope.loading = true;
									getPiston = $q.all([
										dataService.loadFromImport($scope.params.piston),
										dataService.getImportedData()
									]).then(function (results) {
										var pistonData = results[0];
										var importData = results[1];
										var queue = [pistonData.piston];
										var pistonIdQueue = [];
										// Find executePiston commands
										while (queue.length) {
											var obj = queue[0];
											queue.shift();
											if (obj instanceof Array) {
												queue.push.apply(queue, obj);
											} else if (obj instanceof Object) {
												if (obj.c === 'executePiston') {
													pistonIdQueue.push(obj.p[0]);
												} else {
													for (var key in obj) {
														queue.push(obj[key]);
													}
												}
											}
										}
										// If any Execute Piston commands found, fix any IDs that
										// are mapped to an imported piston
										while (pistonIdQueue.length) {
											var obj = pistonIdQueue[0];
											pistonIdQueue.shift();
											if (typeof obj instanceof Array) {
												pistonIdQueue.push.apply(pending, obj);
											} else if (obj instanceof Object) {
												for (var key in obj) {
													if (typeof obj[key] === 'string' && obj[key][0] === ':') {
														for (var i = 0; i < importData.length; i++) {
															if (importData[i].meta.id === obj[key] && importData[i].imported) {
																obj[key] = importData[i].imported;
															}
														}
													} else {
														pistonIdQueue.push(obj[key]);
													}
												}
											}
										}
										return pistonData.piston;
									});
								}
								break;
						}
						
						if (getPiston) {
							getPiston.then(function(piston) {
								if (piston) {
									$scope.piston.o = piston.o ? piston.o : {};
									$scope.piston.r = piston.r ? piston.r : [];
									$scope.piston.rn = !!piston.rn;
									$scope.piston.rop = piston.rop ? piston.rop : 'and';
									$scope.piston.s = piston.s ? piston.s : [];
									$scope.piston.v = piston.v ? piston.v : [];
									$scope.piston.z = piston.z ? piston.z : '';
									
									if ((piston.l instanceof Object) && ($scope.objectToArray(piston.l).length)) {
										$scope.rebuildPiston(piston.l);
									}
								}
								$scope.initialized = true;
								$scope.loading = false;
							});
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

    $scope.getCategories = function() {
        var categories = (!!$scope.instance && !!$scope.instance.settings && ($scope.instance.settings.categories instanceof Array)) ? $scope.copy($scope.instance.settings.categories) : [];
        if (!categories.length) categories = [{n: 'Uncategorized', t: 'd', i: 0}];
        return categories;
    }

	$scope.edit = function() {
		$scope.mode = 'edit';
		$scope.init();
		$('viewer')[0].scrollTop = 0;
	}

	$scope.cancel = function() {
		$scope.mode = 'view';
		$scope.init();
	}

	$scope.enableAutomaticBackup = function() {
		dataService.generateBackupBin().then(function(response) {
        	var binId = response.data;			
            dataService.setPistonBin($scope.pistonId, binId).then(function(response) {
				$scope.meta.bin = binId;
				$scope.save(true);
				$scope.loading = false;
			});
		});
	}

	$scope.save = function(saveToBinOnly) {
		$scope.loading = true;
		var piston = $scope.compilePiston({
			id: $scope.pistonId,
			o: $scope.piston.o,
			s: $scope.piston.s,
			v: $scope.piston.v,
			r: $scope.piston.r,
			rop: $scope.piston.rop,
			rn: $scope.piston.rn,
			z: $scope.piston.z,
			n: $scope.meta.name
		});
		var promise = dataService.setPiston(piston, $scope.meta.bin, saveToBinOnly);
		if (promise) promise.then(function(response) {
			if (saveToBinOnly) return;
			// Always pause imported pistons
			if ($scope.params.type === 'import') {
				return $scope.pause().then(function() {
					return response;
				});
			}
			return response;
		}).then(function(response) {
			$scope.loading = false;
			if (response && response.data && response.data.build) {
				$scope.meta.active = response.data.active;
				$scope.meta.modified = response.data.modified;
				$scope.meta.build = response.data.build;
				$scope.saveStack(true);
				$scope.mode = 'view';
				$scope.init();
			}
		});
	}

	$scope.pause = function() {
		$scope.loading = true;
		return dataService.pausePiston($scope.pistonId).then(function(data) {
			$scope.loading = false;
			if (data && data.status && (data.status == 'ST_SUCCESS')) {
				$scope.meta.active = data.active;
				$scope.subscriptions = {};
				$scope.updateActivity();
			}
		});
	}


	$scope.setLoggingLevel = function(obj) {
		$scope.loading = true;
		dataService.setPistonLogging($scope.pistonId, $scope.logging).then(function(data) {
			$scope.loading = false;
		});
	}

	$scope.setCategory = function() {
		$scope.loading = true;
		dataService.setPistonCategory($scope.pistonId, $scope.category).then(function(data) {
			$scope.loading = false;
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
		dataService.deleteFromStore('stack' + $scope.pistonId);
		dataService.deletePiston($scope.pistonId).then(function(data) {
			$scope.closeDialog();
			$location.path('/');
		});
	}
	
	$scope.resumeImport = function() {
		$rootScope.dashboardResumeImport = true;
		$location.path('/');
	}
	
	$scope.padComment = function(comment, sz) {
		if (!comment) comment = '';
		//replace LEFT-TO-RIGHT marks \u200E - Edge keeps adding them to date/times
		sz = sz - 6 - comment.replace(/\u200E/g, '').trim().length;
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

	$scope.formatVariableValue = function(variable, name) {
		if ((variable.v == null) && !!name && $scope.localVars) {
			variable = $scope.copy(variable);
            variable.v = $scope.localVars[name];
		}
		var t = (name == '$localNow') || (name == '$utc') ? 'long' : variable.t;
		if ((variable.v === '') || (variable.v === null) || ((variable.v instanceof Array) && !variable.v.length)) return '(not set)';
		switch (t) {
			case 'time':
				return utcToTimeString(variable.v);
			case 'datetime':
				return utcToString(variable.v);
			case 'date':
				return utcToDateString(variable.v);
			case 'contact':
				return $scope.renderContactNameList(variable.v);
			case 'device':
				return $scope.renderDeviceNameList(variable.v);
		}
		if (variable.v instanceof Object) {
			return angular.toJson(variable.v);
		}
		return variable.v;
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



	$scope.listDevicesWithAttributes = function(attributes) {
		if (!attributes || !(attributes instanceof Array) || !attributes.length) return $scope.instance.devices;
		// Virtual status attribute is available on all devices
		if (attributes.length === 1 && attributes[0] === statusAttribute) return $scope.instance.devices;
		// Use threeAxis instead of attributes derived from it
		var isThreeAxis;
		attributes = attributes.filter(function(a) {
			switch (a) {
				case 'orientation':
				case 'axisX':
				case 'axisY':
				case 'axisZ':
					isThreeAxis = true;
				case statusAttribute:
					return false;
			}
			return true;
		}).concat(isThreeAxis ? 'threeAxis' : []);
		var result = {};
		for (d in $scope.instance.devices) {
			var device = $scope.instance.devices[d];
			var found = 0;
			for (a in device.a) {
				if (attributes.indexOf(device.a[a].n) >= 0) {
					found++;
					if (found == attributes.length) break;
				}
			}
			if (found == attributes.length) result[d] = device;
		}
		return result;
	}


	$scope.rebuildPiston = function(legend) {
		if (!legend) return;

		for (key in legend) {
			var item = legend[key];
			item.id = '';
			switch (item.t) {
				case 'device':
					item.i = $scope.listDevicesWithAttributes(item.a);
					break;
				case 'contact':
					item.i = $scope.instance.contacts;
					break;
				case 'mode':
					item.i = $scope.instance.virtualDevices['mode'].o;
					for (i in item.i) {
						if (item.i[i] == item.n) {
							item.id = i;
							break;
						}
					}
					break;
				case 'routine':
					item.i = $scope.instance.virtualDevices['routine'].o;
					break;
			}
		}

		$scope.designer = {
			legend: legend
		};
		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-rebuild-piston',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};

	$scope.doRebuildPiston = function() {
		$scope.piston = $scope.compilePiston($scope.piston, false, $scope.designer.legend);
		$scope.closeDialog();
	}

	$scope.doValidatePiston = function() {
		$scope.validatePiston($scope.piston);
	}

	$scope.getExpressionConfig = function() {
		var attributes = [];
		for (attribute in $scope.db.attributes) {
			attributes.push(': ' + attribute + ']');
			if (attribute == 'threeAxis') {
				attributes.push(': axisX]');
				attributes.push(': axisY]');
				attributes.push(': axisZ]');
				attributes.push(': orientation]');
			}
		}
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
					words: attributes,
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

	$scope.deleteObject = function(obj, parent) {
		var dialog = !obj;
		if (dialog) {
			obj = $scope.designer.$obj;
			parent = $scope.designer.parent;
		}
		if (!obj) return;
		if ((parent instanceof Array) && (obj)) {
			$scope.autoSave();
			parent = $scope.removeFromArray(parent, obj);
			if (dialog) $scope.closeDialog();
		}
		if (parent && (parent.t == 'action') && (parent.k instanceof Array) && (obj)) {
			$scope.autoSave();
			parent.k = $scope.removeFromArray(parent.k, obj);
			if (dialog) $scope.closeDialog();
		}
	}

	$scope.getIFTTTUri = function(eventName) {
		var uri = dataService.getApiUri();
		if (!uri) return "An error has occurred retrieving the IFTTT Maker URL";
		return uri + 'ifttt/' + eventName;
	}

	$scope.toggleAdvancedOptions = function() {
		$scope.designer.showAdvancedOptions = !$scope.designer.showAdvancedOptions;
	}


	$scope.getClipboard = function() {
		var clipboard = dataService.loadFromStore('clipboard');
		if (!clipboard) clipboard = [];
		return clipboard;
	}

	$scope.getClipboardItems = function(itemType) {
		var clipboard = $scope.getClipboard();
		var result = [];
		for (i in clipboard) {
			if (clipboard[i].t.startsWith(itemType)) result.push(clipboard[i]);
		}
		return result;
	}

    $scope.saveToClipboard = function(object, objectType) {
		var clipboard = $scope.getClipboard();
		clipboard.push({
			s: (new Date()).getTime(),
			t: objectType,
			o: $scope.copy(object)
		});
		if (clipboard.length > MAX_STACK_SIZE) clipboard = clipboard.slice(-MAX_STACK_SIZE);
        dataService.saveToStore('clipboard', clipboard);
    }

	$scope.deleteClipboardItem = function(item) {
		var clipboard = $scope.getClipboard();
		$scope.removeFromArray($scope.designer.clipboard, item);
		for (i = 0; i < clipboard.length; i++) {
			if ((clipboard[i].s == item.s) && (clipboard[i].t == item.t)) break;
		}
		if (i < clipboard.length) {
			clipboard.splice(i, 1);
			dataService.saveToStore('clipboard', []);
		}		
	}

	$scope.clearClipboard = function() {
        dataService.saveToStore('clipboard', []);
	}



	$scope.copySelection = function() {
		if (!$scope.selection) return;
		$scope.saveToClipboard($scope.selection, $scope.selectionType);
		
	}

	$scope.cutSelection = function() {
		if (!$scope.selection) return;
		$scope.saveToClipboard($scope.selection, $scope.selectionType);
		$scope.deleteObject($scope.selection, $scope.selectionParent);
		$scope.selectionType = null;
		$scope.selection = null;
	}

	$scope.duplicateSelection = function() {
		if (!$scope.selection) return;
		if (!$scope.selectionParent) return;
		if ($scope.selectionParent instanceof Array) {
			$scope.selectionParent.push($scope.copy($scope.selection));
		}
	}

	$scope.deleteSelection = function() {
		if (!$scope.selection) return;
		$scope.deleteObject($scope.selection, $scope.selectionParent);
		$scope.selectionType = null;
		$scope.selection = null;
	}

	$scope.pasteItem = function(clipboardItem) {
		if (!clipboardItem || !clipboardItem.o || !$scope.designer || !$scope.designer.parent) return
		var parent = ($scope.designer.parent instanceof Array) ? $scope.designer.parent : (clipboardItem.t.startsWith('condition') && $scope.designer.parent.c ? $scope.designer.parent.c : (clipboardItem.t.startsWith('restriction') && $scope.designer.parent.r ? $scope.designer.parent.r : (clipboardItem.t.startsWith('task') && $scope.designer.parent.k ? $scope.designer.parent.k : null)));
		if (!parent) return;
		parent.push($scope.copy(clipboardItem.o));
		$scope.closeDialog();
	}

	$scope.contextMenu = function(item) {
		var result = [];
		if ($scope.selection) {
			result.push(['Copy selected ' + $scope.selectionType, $scope.copySelection]);
			if ($scope.mode == 'edit') {
				result.push(['Duplicate selected ' + $scope.selectionType, $scope.duplicateSelection]);
				if ($scope.selectionParent) {
					result.push(null);
					result.push(['Cut selected ' + $scope.selectionType, $scope.cutSelection]);
					result.push(['Delete selected ' + $scope.selectionType, $scope.deleteSelection]);
				}
			}
			result.push(null);
		}
		result.push(['Clear clipboard', $scope.clearClipboard]);
		return result;
	};


	$scope.select = function(object, parent, objectType) {
		if (!objectType) objectType = 'statement';
		$scope.selection = object;
		$scope.selectionParent = parent;
		$scope.selectionType = objectType;
	}


	$scope.editSettings = function() {
		if ($scope.mode != 'edit') return;
		$scope.designer = {
			name: $scope.meta.name,
			description: $scope.piston.z,
			automaticState: $scope.piston.o.mps ? 1 : 0,
			commandOptimizations: $scope.piston.o.dco ? 1 : 0,
			conditionOptimizations: $scope.piston.o.cto ? 1 : 0,
			executionParallelism: $scope.piston.o.pep ? 1 : 0,
			eventSubscriptions: $scope.piston.o.des ? 1 : 0,
			allowPreSchedules: $scope.piston.o.aps ? 1 : 0,
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
		$scope.piston.o.dco = $scope.designer.commandOptimizations ? 1 : 0;
		$scope.piston.o.cto = $scope.designer.conditionOptimizations ? 1 : 0;
		$scope.piston.o.des = $scope.designer.eventSubscriptions ? 1 : 0;
		$scope.piston.o.aps = $scope.designer.allowPreSchedules ? 1 : 0;
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
			statement.di = false; //disabled
			statement.tcp = 'c'; //tcp - cancel on condition state change
			statement.tep = ''; //tep always
			statement.tsp = ''; //tsp override
			statement.ctp = 'i';
			statement.s = 'local'; //tos
			statement.z = ''; //desc			
		}
		$scope.designer = {
			config: $scope.getExpressionConfig(),
			clipboard: statement.t ? [] : $scope.getClipboardItems('statement')
		};
		$scope.designer.$obj = statement;
		$scope.designer.$statement = statement;
		$scope.designer.$new = statement.t ? false : true;
		$scope.designer.type = statement.t;
		$scope.designer.page = statement.t ? 1 : 0;
		$scope.designer.operator = statement.o;
		$scope.designer.not = statement.n ? '1' : '0';
		$scope.designer.disabled = statement.di ? '1' : '0';
		$scope.designer.roperator = statement.rop;
		$scope.designer.rnot = statement.rn ? '1' : '0';
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
		$scope.designer.tsp = statement.tsp;
		//$scope.designer.tcpr = statement.pr;
		//$scope.designer.tcpv = statement.pv;
		//$scope.designer.tos = statement.os;
		$scope.designer.ctp = statement.ctp || 'i';
		$scope.designer.async = statement.a;
		$scope.designer.ontypechanged = function(designer, type) {
			designer.operand.requirePositiveNumber = false;
			designer.operand2.requirePositiveNumber = false;
			designer.operand3.requirePositiveNumber = false;
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
				case 'exit':
					designer.operand.dataType = 'string';
					if (designer.$new) designer.operand.data = {t: 'c'};
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
				{ type: 'if', name: 'If Block', icon: 'code-branch', cssClass: 'info', description: 'An if block allows the piston to execute different actions depending on the truth result of a comparison or set of comparisons', button: 'an if' },
				{ type: 'action', name: 'Action', icon: 'code', cssClass: 'success', description: 'An action allows the piston to control devices and execute tasks', button: 'an action' },
				{ type: 'every', name: 'Timer', icon: 'clock', iconStyle: 'r', cssClass: 'warning', description: 'A timer will trigger execution of the piston at set time intervals', button: 'a timer' }
			],
			advanced: [
				{ type: 'switch', name: 'Switch', icon: 'code-branch', cssClass: 'info', description: 'A switch statement compares an operand against a set of values and executes statements corresponding to those matches', button: 'a switch' },
				{ type: 'do', name: 'Do Block', icon: 'code', cssClass: 'success', description: 'A do block can help organize several statements into a single block', button: 'a do block' },
				{ type: 'on', name: 'On event', icon: 'code-branch', cssClass: 'warning', description: 'An on event executes its statements only when certain events happen', button: 'an on event' },
				{ type: 'for', name: 'For Loop', icon: 'circle-notch', cssClass: 'warning', description: 'A for loop executes the same statements for a set number of iteration cycles', button: 'a for loop' },
				{ type: 'each', name: 'For Each Loop', icon: 'circle-notch', cssClass: 'warning', description: 'An each loop executes the same statements for each device in a device list', button: 'a for each loop' },
				{ type: 'while', name: 'While Loop', icon: 'circle-notch', cssClass: 'warning', description: 'A while loop executes the same statements for as long as a condition is met', button: 'a while loop' },
				{ type: 'repeat', name: 'Repeat Loop', icon: 'circle-notch', cssClass: 'warning', description: 'A repeat loop executes the same statements until a condition is met', button: 'a repeat loop' },
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
		statement.a = ($scope.designer.async == '1') || (['every', 'on'].indexOf($scope.designer.type) >= 0) ? '1' : '0';
		statement.tcp = $scope.designer.tcp;
		statement.tep = $scope.designer.tep;
		statement.tsp = $scope.designer.tsp;
//		statement.pr = $scope.designer.tcpr;
//		statement.pv = $scope.designer.tcpv;
//		statement.os = $scope.designer.tos;
		statement.z = $scope.designer.description;
		statement.r = statement.r ? statement.r : [];
		statement.rop = $scope.designer.roperator;
		statement.rn = $scope.designer.rnot == '1';
		statement.di = $scope.designer.disabled == '1';
		switch (statement.t) {
			case 'action':
				statement.d = $scope.designer.devices;
				statement.k = statement.k ? statement.k : [];
				break;
			case 'do':
				statement.s = statement.s ? statement.s : [];
				break;
			case 'on':
				statement.c = statement.c ? statement.c : [];
				statement.o = 'or';
				statement.n = false;
				statement.s = statement.s ? statement.s : [];
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
		$scope.doValidatePiston();
		$scope.closeDialog();
		if (nextDialog) {
			switch (statement.t) {
				case 'action':
					$scope.addTask(statement);
					return;
				case 'if':
					$scope.addCondition(statement.c, false, defaultType);
					return;
				case 'on':
					$scope.addEvent(statement.c);
					return;
				case 'while':
					$scope.addCondition(statement.c);
					return;
				case 'do':
				case 'for':
				case 'each':
				case 'repeat':
				case 'every':
					$scope.addStatement(statement.s);
					return;
				case 'switch':
					$scope.addCase(statement.cs);
					return;
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
		$scope.designer.description = _case.z;
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
		$scope.doValidatePiston();
		$scope.closeDialog();
		if (nextDialog) {
			$scope.addStatement(_case.s);
			return;
		}
	}













	/* events */

	$scope.addEvent = function(parent) {
		return $scope.editEvent(null, parent);
	}

	$scope.editEvent = function(event, parent) {
		if ($scope.mode != 'edit') return;
		var _new = !event;
		if (!event) {
			event = {};
			event.t = 'event';
			event.lo = {t: 'p', d: [], a: null, g:'any', v: null, c: '', x: null, e: ''};
			event.z = '';
			event.sm = 'auto';
		}
		$scope.designer = {
			config: $scope.getExpressionConfig(),
			clipboard: _new ? $scope.getClipboardItems('event') : []
		};
		$scope.designer.$event = event;
		$scope.designer.$obj = event;
		$scope.designer.type = event.t;
		$scope.designer.$new = _new;
		$scope.designer.parent = parent;
		$scope.designer.comparison = {
			event: true,
			type: 'event',
			left: {data: event.lo ? $scope.copy(event.lo) : {}, event: true},
		}
		$scope.validateComparison($scope.designer.comparison, true);
		$scope.designer.smode = event.sm;
		$scope.designer.description = event.z;
		window.designer = $scope.designer;

		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-edit-event',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};
	
	$scope.updateEvent = function(nextDialog) {
		$scope.autoSave();
		var event = $scope.designer.$new ? {t: $scope.designer.type} : $scope.designer.$event;
		event.lo = $scope.fixOperand($scope.designer.comparison.left.data);
		event.sm = $scope.designer.smode;
		event.z = $scope.designer.description;
		if (event.t) {
			event.$$html = null;
			if ($scope.designer.$new) {
				if ($scope.designer.parent instanceof Array) {
					$scope.designer.parent.push(event);
				} else if (($scope.designer.parent.c) && ($scope.designer.parent.c instanceof Array)) {
						$scope.designer.parent.c.push(event);
				} else {
					$scope.designer.parent.c = [event];
				}
			} else {
				$scope.designer.$event = event;
			}
		}
		$scope.doValidatePiston();
		$scope.closeDialog();
		if (event.t && nextDialog) {
			$scope.addEvent($scope.designer.parent);
			return;
		}
	}

















	/* conditions */

	$scope.addCondition = function(parent, newElseIf, defaultType, groupingMethod) {
		return $scope.editCondition(null, parent, newElseIf, defaultType, groupingMethod ? groupingMethod : (parent ? parent.o : null));
	}

	$scope.editCondition = function(condition, parent, newElseIf, defaultType, groupingMethod) {		
		if ($scope.mode != 'edit') return;
		var _new = !condition;
		var list = parent instanceof Array ? parent : (parent instanceof Object ? parent.c : null);
		var followedBy = (groupingMethod == 'followed by') && (list instanceof Array) && (list.length > 0) && (list[0] != condition);
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
			condition.to2 = {t: 'c', d: [], a: null, g:'any', v: null, c: '', x: null, e: ''};
//			condition.wt = {t: 'c', d: [], a: null, g:'any', v: null, c: 1, vt: 'm', x: null, e: ''};
//			condition.wd = {t: 'c', d: [], a: null, g:'any', v: null, c: 'l', x: null, e: ''};
			condition.z = '';
			condition.sm = 'auto';
			condition.ts = [];
			condition.fs = [];
		}
		$scope.designer = {
			config: $scope.getExpressionConfig(),
			clipboard: _new ? $scope.getClipboardItems('condition') : []
		};
		$scope.designer.$condition = condition;
		$scope.designer.followedBy = followedBy;
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
			followedBy: followedBy,
			left: {data: condition.lo ? $scope.copy(condition.lo) : {}, showSubDevices: true, showInteraction: true},
			operator: condition.co,
			right: {data: condition.ro ? $scope.copy(condition.ro) : {}},
			right2: {data: condition.ro2 ? $scope.copy(condition.ro2) : {}},
			time: {data: condition.to ? $scope.copy(condition.to) : {t:'c', c: 0}, dataType: 'duration'},
			time2: {data: condition.to2 ? $scope.copy(condition.to2) : {t:'c', c: 0}, dataType: 'duration'}
		}
		if (followedBy) {
			$scope.designer.comparison.within = {data: condition.wd ? $scope.copy(condition.wd) : {t:'c', c: 1, vt: 'm'}, style: 'success', dataType: 'duration', hideMilliseconds: true};
			$scope.designer.comparison.withinOpt = (condition.wt ? condition.wt : 'l');
		}
		$scope.validateComparison($scope.designer.comparison, true);
		$scope.designer.smode = condition.sm;
		$scope.designer.description = condition.z;
		window.designer = $scope.designer;
		$scope.designer.items = [
			{ type: 'condition', name: 'Condition', icon: 'code', cssClass: 'btn-info' },
			{ type: 'group', name: 'Group', icon: 'code-branch', cssClass: 'btn-warning' },
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
				data.c = data.c instanceof Date ? data.c.getHours() * 60 + data.c.getMinutes() : data.c;
				break;
			case 'date':
			case 'datetime':
				data.c = data.c instanceof Date ? data.c.getTime() : (new Date(data.c)).getTime();
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
				condition.to2 = $scope.designer.comparison.time2.data;
				if ($scope.designer.followedBy) {
					condition.wd = $scope.designer.comparison.within.data;
					condition.wt = $scope.designer.comparison.withinOpt;
				}
				break;
			case 'group':
				condition.c = condition.c ? condition.c : [];
				condition.o = $scope.designer.operator;
				condition.n = $scope.designer.not == '1';
				if ($scope.designer.followedBy) {
					condition.wd = $scope.designer.comparison.within.data;
					condition.wt = $scope.designer.comparison.withinOpt;
				}
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
		$scope.doValidatePiston();
		$scope.closeDialog();
		if (condition.t && nextDialog) {
			$scope.addCondition(condition.t == 'group' ? condition : $scope.designer.parent);
			return;
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





	$scope.editConditionGroup = function(group, parent, groupingMethod) {
		if ($scope.mode != 'edit') return;
		var followedBy = (groupingMethod == 'followed by') && (parent instanceof Array) && (parent.length > 0) && (parent[0] != group);
		$scope.designer = {
			operator: group.o || 'and',
			not: group.n ? '1' : '0',
			description: (group.t == 'group' ? group.z : group.zc)
		};
		$scope.designer.group = group;
		$scope.designer.followedBy = followedBy;
		$scope.designer.$obj = group;
		$scope.designer.parent = parent;
		if (followedBy) {
			$scope.designer.within = {data: group.wd ? $scope.copy(group.wd) : {t:'c', c: 1, vt: 'm'}, style: 'success', dataType: 'duration', hideMilliseconds: true};
			$scope.designer.withinOpt = (group.wt ? group.wt : 'l');
			$scope.validateOperand($scope.designer.within);
		}
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
		if (group.t == 'group') {
			group.z = $scope.designer.description;
			if ($scope.designer.followedBy) {
				group.wd = $scope.designer.within.data;
				group.wt = $scope.designer.withinOpt;
			}
		} else {
			group.zc = $scope.designer.description;
		}
		$scope.closeDialog();
	}

















	/* restrictions */

	$scope.addRestriction = function(parent) {
		return $scope.editRestriction(null, parent);
	}

    $scope.editRestriction = function(restriction, parent) {
        if ($scope.mode != 'edit') return;
		var _new = !restriction;
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
            restriction.to2 = {t: 'c', d: [], a: null, g:'any', v: null, c: '', x: null, e: ''};
            restriction.z = '';
        }
        $scope.designer = {
            config: $scope.getExpressionConfig(),
			clipboard: _new ? $scope.getClipboardItems('restriction') : []
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
            time: {data: restriction.to ? $scope.copy(restriction.to) : {t:'c', c: 0}, dataType: 'duration'},
            time2: {data: restriction.to2 ? $scope.copy(restriction.to2) : {t:'c', c: 0}, dataType: 'duration'}
        }
        $scope.validateComparison($scope.designer.comparison, true);
        $scope.designer.smode = restriction.sm;
        $scope.designer.description = restriction.z;
        window.designer = $scope.designer;
        $scope.designer.items = [
            { type: 'restriction', name: 'Restriction', icon: 'code', cssClass: 'btn-info' },
            { type: 'group', name: 'Group', icon: 'code-branch', cssClass: 'btn-warning' },
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
                restriction.lo = $scope.fixOperand($scope.designer.comparison.left.data);
                restriction.co = $scope.designer.comparison.operator;
                restriction.ro = $scope.fixOperand($scope.designer.comparison.right.data);
                restriction.ro2 = $scope.fixOperand($scope.designer.comparison.right2.data);
                restriction.to = $scope.designer.comparison.time.data;
                restriction.to2 = $scope.designer.comparison.time2.data;
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
		$scope.doValidatePiston();
        $scope.closeDialog();
        if (restriction.t && nextDialog) {
            $scope.addRestriction(restriction.t == 'group' ? restriction : $scope.designer.parent);
			return;
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
			description: (group.t == 'group' ? group.z : group.zr)
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
		if (group.t == 'group') {
			group.z = $scope.designer.description;
		} else {
			group.zr = $scope.designer.description;
		}
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
		var _new = task.c ? false : true;
		$scope.designer = {
			clipboard: _new ? $scope.getClipboardItems('task') : []
		};
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
							param.c = param.c instanceof Date ? param.c.getHours() * 60 + param.c.getMinutes() : param.c;
							break;
						case 'date':
						case 'datetime':
							param.c = param.c instanceof Date ? param.c.getTime() : (new Date(param.c)).getTime();
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
		$scope.doValidatePiston();
		$scope.closeDialog();
		if (nextDialog) {
			$scope.addTask($scope.designer.parent);
			return;
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
		//temporary fix
		if (variable.v instanceof Array) variable.v = {data:{}};
		$scope.designer = {};
		$scope.designer.$variable = variable;
		$scope.designer.$obj = variable;
		$scope.designer.$new = variable.n ? false : true;
		$scope.designer.page = 0;
		$scope.designer.parent = $scope.piston.v;
		$scope.designer.type = variable.t;
		$scope.designer.assignment = variable.a || 'd';
		$scope.designer.name = variable.n;
		$scope.designer.operand = {data: variable.v, multiple: false, dataType: variable.t, optional: true}
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
		$scope.refreshSelects();
	};

	$scope.updateVariable = function(nextDialog) {
		$scope.autoSave();
		var variable = $scope.designer.$new ? {} : $scope.designer.$variable;
		variable.t = $scope.designer.operand.dataType;
		variable.n = $scope.designer.name.trim().replace(/[^a-z0-9]|\s+|\r?\n|\r/gmi, '_');
		variable.z = $scope.designer.description;
		variable.a = $scope.designer.assignment;
		var value = $scope.fixOperand($scope.designer.operand.data);
		switch (value.t) {
			case '':
				variable.v = null;
				break;
			default:
				variable.v = value;
				break;
		}
		variable.$$html = null;
		if ($scope.designer.$new) {
			$scope.piston.v.push(variable);
		} else {
			$scope.designer.variable = variable;
		}
		$scope.doValidatePiston();
		$scope.closeDialog();
		if (nextDialog) {
			$scope.addVariable();
			return;
		}
	}





	/* quick edit local variables */

	$scope.editLocalVariable = function(variable) {
		//we cannot edit variables that are statically defined in a piston
		if (!variable || !variable.n || !variable.t || !!variable.v) return;
		var value = $scope.localVars[variable.n];
		if ((value instanceof Array) && (value.length == 0)) value = null
		if (!variable) return;
		$scope.designer = {};
		$scope.designer.$variableName = variable.n;
		$scope.designer.$variable = variable;
		$scope.designer.$obj = variable;
		$scope.designer.name = variable.n;
		$scope.designer.type = variable.t;
		$scope.designer.operand = {data: {t: !value ? '' : (variable.t == 'device' ? 'd' : 'c'), c:value, d: value}, multiple: false, dataType: variable.t, optional: true, onlyAllowConstants: true, disableExpressions: true}
		window.designer = $scope.designer;
		window.scope = $scope;
		$scope.validateOperand($scope.designer.operand);
		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-edit-local-variable',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};

	$scope.updateLocalVariable = function(nextDialog) {
		var variable = $scope.designer.$variable;
		var value = variable.t == 'device' ? $scope.designer.operand.data.d : ($scope.designer.operand.data.t == 'c' ? $scope.designer.operand.data.c : null);
		dataService.setVariable($scope.designer.$variableName, {t: variable.t, v: value}, $scope.pistonId).then(function(data) {
			if (data && data.localVars && data.id && (data.id == $scope.pistonId)) {
				$scope.localVars = data.localVars;
			}
		});
		$scope.closeDialog();
	}





	/* global variables */

	$scope.addGlobalVariable = function() {
		return $scope.editGlobalVariable(null);
	};

	$scope.editGlobalVariable = function(variableName) {
		if ($scope.mode != 'edit') return;
		var variable = $scope.globalVars[variableName];
		if (!variable) {
			variable = {t:'dynamic', v:''};
		}
		$scope.designer = {};
		$scope.designer.$variableName = variableName;
		$scope.designer.$variable = variable;
		$scope.designer.$obj = variable;
		$scope.designer.$new = variableName ? false : true;
		$scope.designer.name = variableName ? '' + variableName : '@';
		$scope.designer.type = variable.t;
		$scope.designer.operand = {data: {t: (variable.v == null || variable.v == undefined) ? '' : ( variable.t == 'device' ? 'd' : 'c'), c: variable.v, d: variable.v, vt: variable.t}, multiple: false, dataType: variable.t, optional: true, onlyAllowConstants: true}
		window.designer = $scope.designer;
		window.scope = $scope;
		$scope.validateOperand($scope.designer.operand);
		$scope.designer.dialog = ngDialog.open({
			template: 'dialog-edit-global-variable',
			className: 'ngdialog-theme-default ngdialog-large',
			closeByDocument: false,
			disableAnimation: true,
			scope: $scope
		});
	};

	$scope.updateGlobalVariable = function(nextDialog) {
		$scope.autoSave();
		var variable = $scope.designer.$new ? {} : $scope.designer.$variable;
		variable.t = $scope.designer.operand.dataType;
		variable.n = $scope.designer.name.trim().replace(/[^@a-z0-9]|\s+|\r?\n|\r/gmi, '_');
		var value = $scope.designer.operand.data;
		switch (value.t) {
			case '':
				variable.v = variable.t == 'device' ? [] : null;
				break;
			case 'c':
				variable.v = value.c ? value.c : '';
				break;
			case 'd':
				variable.v = value.d ? value.d : [];
				break;
			default:
				variable.v = null;
				break;
		}
		delete(variable.$$html);
		//save global var
		dataService.setVariable($scope.designer.$variableName, variable).then(function(data) {
			if (data && data.globalVars) {
				$scope.updateGlobalVars(data.globalVars);
			}
		});
		$scope.doValidatePiston();
		$scope.closeDialog();
		if (nextDialog) {
			$scope.addGlobalVariable();
			return;
		}
	}

	$scope.deleteGlobalVariable = function() {
		if ((!$scope.designer) || (!$scope.designer.$variableName)) return;
		dataService.setVariable($scope.designer.$variableName, null).then(function(data) {
			if (data && data.globalVars) {
				$scope.updateGlobalVars(data.globalVars);
			}
		});
		$scope.closeDialog();
		
	}

	$scope.validateGlobalVariableName = function() {
		if (!$scope.designer) return false;
		var name = $scope.designer.name;
		if (!name) return false;
		if (!name.startsWith('@')) name = '@' + name;
		//one or two @ at the beginning of the name only
		while (name.startsWith('@@@')) name = name.substr(1);
		if ($scope.designer.name != name) {
			$scope.designer.name = name;
		}
		return name && (name != '@') && (name != '@@') && (($scope.designer.$variableName == name) || !($scope.globalVars[name])
);
	}


	$scope.getDeviceAttributeValue = function(device, attributeName) {
		for(i in device.a) {
			if (device.a[i].n == attributeName) {
				var result = {v: device.a[i].v, t: device.a[i].v};
				if (result.v == undefined) result.v = '';
				if ((attributeName == 'battery') && (!isNaN(result.v))) {
					result.t = result.t + '%';
					result.v = Math.floor(parseInt(result.v) / 20);
					if (result.v > 4) result.v = 4;
				}
				if ((attributeName == 'temperature') && (!isNaN(result.v))) {
					result.v = Math.round(parseFloat(result.v)).toString() + '°';
					result.t = result.v;
				}
				return result;
			}
		}
		return {v:'', t:''};
	}
	
	var attributeIcons = {
		battery: {
			0: 'battery-empty',
			1: 'battery-quarter',
			2: 'battery-half',
			3: 'battery-three-quarters',
			4: 'battery-full',
		},
		motion: 'exchange-alt',
		presence: 'child',
		'switch': {
			'on': 'toggle-on',
			'off': 'toggle-off',
		}
	};

	$scope.renderDevice = function(device) {
//		var result = '<div class="col-sm-7">' + device.n + '</div><div class="col-sm-1">1</div>' + '<div class="col-sm-1">2</div>' + '<div class="col-sm-1">3</div>' + '<div class="col-sm-1">4</div>' + '<div class="col-sm-1">5</div>';
		var sSwitch = $scope.getDeviceAttributeValue(device, 'switch');
		var sSwitch = sSwitch ? 'class="fa fa-toggle-off" switch="' + sSwitch + '"' : '';
		var attributes = ['temperature', 'battery', 'switch', 'motion', 'presence'];
		var result = '<div col>' + device.n + '</div>';
		for (a in attributes) {
			var value = $scope.getDeviceAttributeValue(device, attributes[a]);
			var icon = attributeIcons[attributes[a]];
			result += '<div col ' + attributes[a] + '="' + value.v + '" title="' + value.t + '">'
			if (value.v !== '') {
				if (icon && typeof icon !== 'string') {
					icon = icon[value.v];
				}
				if (icon) {
					result += '<i class="fas fa-' + icon + '"></i>';
				} else {
					result += value.v;
				}
			}
			result += '</div>';
		}
//<div col ' + sSwitch + '> </div>' + '<div col motion="' + $scope.getDeviceAttributeValue(device, 'motion') + '"> </div>' + '<div col>3</div>' + '<div col>4</div>' + '<div col>5</div>';
		return $sce.trustAsHtml(result);
	}

	$scope.drag = function(list, index) {
		list.splice(index, 1);
		$scope.autoSave();
		$scope.doValidatePiston();
	}

	$scope.copyVariable = function(list, index) {
		var variable = list[index];
		for (var i = 0; i < list.length; i++) {
			if (i !== index && list[i].n === variable.n) {
				list[i].n = list[i].n.replace(/(?:_(\d+))?$/, function(m, number) {
					return '_' + ((+number || 0) + 1);
				});
				break;
			}
		}
		$scope.autoSave();
		$scope.doValidatePiston();
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
		$scope.refreshSelects();
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
		dataService.clearPistonLogs($scope.pistonId).then(function(data) {
			$scope.lastLogEntry = 0;
		});
	}


	$scope.prepareParameters = function(task) {
		$scope.designer.parameters = [];
		var command = $scope.db.commands.physical[$scope.designer.command] || $scope.db.commands.virtual[$scope.designer.command];
		$scope.designer.parameters = [];
		$scope.designer.custom = false;
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
					strict: !!parameter.s,
					warn: parameter.w
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
			$scope.designer.custom = !!$scope.designer.command;
			for (i in task.p) {
				var param = {dataType: task.p[i].vt, data: $scope.copy(task.p[i])};
				$scope.validateOperand(param);
				$scope.designer.parameters.push(param);
			}
			//custom command - we add our own parameters
		}
		if ($scope.designer.command == 'setVariable') {
			$scope.designer.parameters[0].linkedOperand = $scope.designer.parameters[1];
			$scope.validateOperand($scope.designer.parameters[0]);
		}
		$scope.refreshSelects();
	}

	$scope.renameParameters = function() {
		if (!$scope.designer.custom) return;
		for (i in $scope.designer.parameters) {
			$scope.designer.parameters[i].name = 'Parameter #' + (parseInt(i) + 1).toString() + ' (' + $scope.designer.parameters[i].dataType + ')';
		}
		$scope.refreshSelects();
	}

	$scope.addParameter = function(dataType) {
		if (!$scope.designer.custom) return;
		var param = {dataType: dataType, name: '', data: {t: 'c'}};
		$scope.validateOperand(param);
		$scope.designer.parameters.push(param);
		$scope.renameParameters();
	}

	$scope.deleteParameter = function(parameter) {
		if (!$scope.designer.custom) return;
		var index = $scope.designer.parameters.indexOf(parameter);
		if (index > -1) {
		    $scope.designer.parameters.splice(index, 1);
		}
		$scope.renameParameters();
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

	$scope.getContactById = function(contactId) {
		return $scope.instance.contacts[contactId];
	}

	$scope.getRoutineById = function(routineId) {
		if ($scope.instance.virtualDevices.routine && $scope.instance.virtualDevices.routine.o) {
			return $scope.instance.virtualDevices.routine.o[routineId];
		}
		return null;
	}

	$scope.getLocationModeById = function(locationModeId) {
		if ($scope.instance.virtualDevices.mode && $scope.instance.virtualDevices.mode.o) {
			return $scope.instance.virtualDevices.mode.o[locationModeId];
		}
		return null;
	}

	$scope.getDeviceById = function(deviceId) {
		if (deviceId == $scope.location.id) {
			return {id: deviceId, n: $scope.location.name, an: 'Location'};
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


	$scope.buildName = function(name, noQuotes, pedantic, itemPrefix, grouping) {
		if ((name == null) || (name == undefined)) return '';
		if (name instanceof Array) return $scope.buildNameList(name, grouping ? grouping : 'or', '', '', false, noQuotes, pedantic, itemPrefix);
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
			var an = '';
			var it = list[i]
			if (it instanceof Object) {
				tag = it.t ? it.t : tag;
				an = it.a ? it.a : '[unknown]';
				it = it.n;
			}
			var item = $scope.buildName(it, noQuotes, pedantic, itemPrefix);
			result += '<span ' + (tag ? tag : '') + (an ? ' an="' + an + '"' : '') + (className ? ' class="' + className + '"' : '') + '>' + item + '</span>' + (possessive ? '\'' + (item.substr(-1) == 's' ? '' : 's') : '') + (cnt < list.length ? '<span pun>' + (cnt == list.length - 1 ? (list.length > 2 ? ', ' : ' ') + suffix + ' ' : ', ') + '</span>' : '');
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
	             var device = $scope.getDeviceById(devices[deviceIndex]);
	             if (device) {
	                 deviceNames.push({n: device.n, a: device.an, t: 'dev'});
	             } else {
	                 deviceNames.push({ n: '{' + devices[deviceIndex] + '}', t: 'var'});
	             }
	         }
			if (deviceNames.length) {
				return $scope.buildNameList(deviceNames, 'and', 'dev', '', false, true);
			}
		}
		return 'Location';
	};

	$scope.buildContactNameList = function(contacts) {
		var contactNames = [];
		if (contacts instanceof Array) {
	        for (contactIndex in contacts) {
	             var contact = $scope.getContactById(contacts[contactIndex]);
	             if (contact) {
	                 contactNames.push({n: (contact.f + ' ' + contact.l).trim() + ' (' + contact.t + '/' + (contact.p ? 'PUSH' : 'SMS') + ')', a: contact.an, t: 'cnt'});
	             } else {
	                 contactNames.push({ n: '{' + contacts[contactIndex] + '}', a: 'Unknown Contact', t: 'var'});
	             }
	         }
			if (contactNames.length) {
				return $scope.buildNameList(contactNames, 'and', 'cnt', '', false, true);
			}
		}
		return '(empty)';
	};


	$scope.formatHour = function(hour) {
		return (!location.timeZone || location.timeZone.id.startsWith('America')) ? ((hour % 12 ?hour % 12 : '12') + (hour < 12 ? 'am' : 'pm')) : ('00' + hour).substr(-2);
	};

	$scope.renderDeviceNameList = function(devices) {
		return $sce.trustAsHtml($scope.buildDeviceNameList(devices));
	}

	$scope.renderContactNameList = function(contacts) {
		return $sce.trustAsHtml($scope.buildContactNameList(contacts));
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
		var deviceCount = devices ? devices.length : 0;
		for (deviceIndex in devices) {
			var deviceId = devices[deviceIndex] || '';
			var cmds = [];
			var all = false;
			if (deviceId.startsWith(':')) {
				var device = $scope.getDeviceById(devices[deviceIndex]);
				if (device) cmds = device.c;
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
			var tokens = "";
			for (i in device.a) tokens += ':' + device.a[i].n + ' ';
			result.push(mergeObjects({id: deviceIndex, tokens: tokens + device.n}, device));
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

	$scope.listAvailableAttributeNames = function(devices, restrictAttribute) {
	    var result = [];
	    var list = $scope.listAvailableAttributes(devices, restrictAttribute);
		for (i in list) {
			if (list[i].n != statusAttribute) result.push({n: list[i].n, v: list[i].id});
	    }
		return result;
	}

	$scope.listAvailableAttributes = function(devices, restrictAttribute) {
		var result = [];
		var device = null;
		if (devices && devices.length) {
			var attributes = {}
			var deviceCount = devices.length;
			var hasThreeAxis = false;
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
				} else {
					//variable
					for (attributeName in $scope.db.attributes) {
						if (!restrictAttribute || (attributeName == restrictAttribute)) {
							if (attributes[attributeName]) {
								attributes[attributeName] += 1;
							} else {
								attributes[attributeName] = 1;
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
						if (attributeId == 'threeAxis') hasThreeAxis = true;
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
			if (hasThreeAxis) {
				result.push({id: 'axisX', n: 'X axis', t:'decimal'});
				result.push({id: 'axisY', n: 'Y axis', t:'decimal'});
				result.push({id: 'axisZ', n: 'Z axis', t:'decimal'});
				result.push({id: 'orientation', n: 'orientation', t:'string'});
			}
			result.push({id: statusAttribute, n: '⌂ ' + statusAttribute, t:'string'});
			result.sort($scope.sortByName);
		}
		return result;
	}


	$scope.sortByDisplay = function(a,b) {
		return (a.d > b.d) ? 1 : ((b.d > a.d) ? -1 : 0);
	}

	$scope.sortByName = function(a,b) {
		a = a.n.toLowerCase();
		b = b.n.toLowerCase();
		return (a > b) ? 1 : ((b > a) ? -1 : 0);
	}


	$scope.getStackData = function() {
		var data = angular.toJson($scope.compilePiston($scope.piston));
		return {hash: $scope.md5(data), timestamp: (new Date()).getTime(), data: angular.fromJson(data)};
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
			if (stack.length > MAX_STACK_SIZE) {
				stack = stack.slice(-MAX_STACK_SIZE);
			}
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
			//$scope.stack.current = $scope.getStackData();
			//$scope.stack.undo = [];
			//$scope.stack.redo = [];
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

	$scope.getLocalVariable = function(name) {
		for(i in $scope.piston.v) {
			if ($scope.piston.v[i].n == name) return $scope.piston.v[i];
		}
		return null;
	}

	$scope.getLocalVariableType = function(name) {
		for(i in $scope.piston.v) {
			if ($scope.piston.v[i].n == name) return $scope.piston.v[i].t;
		}
		return '';
	}

	$scope.chooseVersion = function(keepLocal) {
		if (keepLocal) {
			$scope.piston = $scope.stack.current.data;
			$scope.validatePiston($scope.piston);
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
			$scope.validatePiston($scope.piston);
			$scope.saveStack();
		}
	}

	$scope.redo = function() {
		if ($scope.stack && $scope.stack.redo && $scope.stack.redo.length) {
			$scope.autoSave($scope.stack.undo);
			$scope.stack.current = $scope.stack.redo.pop();
			$scope.piston = $scope.stack.current.data;
			$scope.validatePiston($scope.piston);
			$scope.saveStack();
		}
	}

	$scope.localTimeToDate = function(time) {
		time = time ? time : 0;
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
			if (dataType == 'attributes') {
				operand.multiple = true;
				dataType = 'attribute';
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
			if (dataType == 'lifxScenes') {
				operand.multiple = true;
				dataType = 'lifxScene';
			}
			if (dataType == 'lifxscene') {
				dataType = 'lifxScene';
			}
			if (dataType == 'lifxselector') {
				dataType = 'lifxSelector';
			}
			if (dataType == 'contacts') {
				operand.multiple = true;
				dataType = 'contact';
			}
			if (dataType == 'number') dataType = 'decimal';
			if (dataType == 'bool') dataType = 'boolean';
			if ((dataType == 'enum') && !operand.options && !operand.options.length) {
				dataType = 'string';
			}
	
			//if (dataType != 'enum') operand.options = null;
	

			switch (operand.data.vt) {
				case 'time':
					if (!(operand.data.c instanceof Date)) {
						if (operand.data.c != undefined) { //firefox is weird
							operand.data.c = $scope.localTimeToDate(operand.data.c);
						}
					}
					break;
				case 'date':
				case 'datetime':
					if (!(operand.data.c instanceof Date)) {
						operand.data.c = new Date(operand.data.c);
						if (operand.data.c == 'Invalid Date') operand.data.c = new Date();
					}
					break;
			}

			var disableExpressions = (!!operand.disableExpressions) || (dataType == 'piston') || (dataType == 'routine') || (dataType == 'askAlexaMacro') || (dataType == 'attribute')
			operand.onlyAllowConstants = operand.onlyAllowConstants || disableExpressions

			var strict = !!operand.strict;
			if (operand.onlyAllowConstants || (dataType == 'contact')) {
				operand.allowArgument = false;
				operand.allowDevices = (dataType == 'device');
				operand.allowPhysical = false;
				operand.allowVirtual = false;
				operand.allowConstant = (dataType != 'device');;
				operand.allowVariable = false;
				operand.allowExpression = !disableExpressions;
			} else {
				operand.allowDevices = dataType == 'device';
				operand.allowPhysical = (dataType != 'datetime') && (dataType != 'date') && (dataType != 'time') && (dataType != 'device') && (dataType != 'variable') && (!strict || (dataType != 'boolean')) && (dataType != 'duration');
				operand.allowPreset = (!operand.event) && (dataType == 'datetime') || (dataType == 'time') || (dataType == 'color');
				operand.allowVirtual = (dataType != 'datetime') && (dataType != 'date') && (dataType != 'time') && (dataType != 'device') && (dataType != 'variable') && (dataType != 'decimal') && (dataType != 'integer') && (dataType != 'number') && (dataType != 'boolean') && (dataType != 'enum') && (dataType != 'color') && (dataType != 'duration');
				operand.allowVariable = (dataType != 'device' || ((dataType == 'device') && operand.multiple)) && (!strict || (dataType != 'boolean'));
				operand.allowConstant = (!operand.event) && (dataType != 'device') && (dataType != 'variable');
				operand.allowArgument = (!operand.event) && (dataType != 'device') && (dataType != 'variable');
				operand.allowExpression = (!operand.event) && (dataType != 'variable') && (!strict || (dataType != 'boolean'));
			}

			if (operand.data.t == null) {
				var t = ''
				if (!operand.optional) {
					t = dataType == 'variable' ? 'x' : (!!operand.allowPreset ? 's' : 'c');
					if (($scope.designer.$condition) || ($scope.designer.$restriction)) t = 'p';
				}
				operand.data.t = t;
			}

			if (((operand.data.t == 'p') && (!operand.allowPhysical)) || ((operand.data.t == 'v') && (!operand.allowVirtual))) operand.data.t = (!!operand.allowPreset) ? 's' : 'c';

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
					operand.restrictType = 'integer,integer[]';
					break;
				case 'decimal':
					operand.restrictType = 'integer,integer[],decimal,decimal[]';
					break;
				case 'time':
					operand.restrictType = 'datetime,datetime[],time,time[]';
					break;
				case 'date':
				case 'datetime':
					operand.restrictType = 'datetime,datetime[],date,date[]';
					break;
			}
			operand.dataType = dataType;

			operand.durationUnit = operand.durationUnit || operand.data.vt || 's';
			operand.data.vt = dataType == 'duration' ? operand.durationUnit : dataType;


		}

		switch (dataType) {
			case 'enum':
				break;
			case 'bool':
			case 'boolean':
				// Require string format to match the options
				if ((operand.data.t == 'c') && (typeof operand.data.c == 'boolean')) operand.data.c += '';
				operand.options = ['false', 'true'];
				break;
			case 'mode':
			case 'powerSource':
			case 'alarmSystemStatus':
			case 'routine':
				operand.options = $scope.objectToArray($scope.instance.virtualDevices[dataType].o);
				break;
			case 'attribute':
				operand.attrs = operand.attrs ? operand.attrs : $scope.listAvailableAttributeNames($scope.designer.parent.d);
				operand.options = operand.attrs;
				break;
			case 'piston':
				operand.options = $scope.listAllPistons();
				break;
			case 'contact':
				operand.options = $scope.contacts;
				break;
			case 'lifxScene':
				operand.options = $scope.objectToArray($scope.instance.lifx.scenes).sort($scope.sortByName);
				break;
			case 'lifxSelector': //fake options - we're using a custom select for grouping purposes
				operand.options = [];
				break;
			case 'integer':
			case 'decimal':
			case 'duration':
				if ((operand.data.t == 'c') && (isNaN(operand.data.c) || (operand.data.c == ''))) operand.data.c = 0;
			default:
				operand.options = null;
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
				operand.count = 0;
				if (attribute) {
					operand.momentary = attribute.m || (!!attribute.s && (operand.data.i instanceof Array) && operand.data.i.length);
					operand.interactive = !!attribute.p;
					if (operand.interactive && !operand.data.p) {
						operand.data.p = 'a';
					}
					if (!!attribute.s) {
						operand.subDeviceName = attribute.sd;
						//get the number of sub devices
						//default of 32 buttons, if no description available
						var countAttributes = attribute.s.split(',');
						for (deviceIndex in operand.data.d) {
							var dev = $scope.getDeviceById(operand.data.d[deviceIndex]);
							var c = 0;
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
						if (operand.count == 0) operand.count = 32;
					} else {
						operand.subDeviceName = '';
					}
					if (operand.count) {
						if ((operand.data.i == null) || (operand.data.i == undefined)) {
							//default sub device index
							operand.data.i = [];
						}
						$scope.refreshSelects();
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
					operand.momentary = virtualDevice.m
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
						if (operand.selectedDataType == 'boolean') {
							operand.selectedOptions = ['false', 'true'];
						}
					} else {
						operand.error = "Invalid variable";
					}
				}
				break;
			case 'u':
				operand.selectedDataType = 'dynamic';
				break;
			case 'c':
				var expression = $scope.parseString(operand.data.c, operand.data.vt);
				operand.error = expression.err;
				operand.expressionVar = expression.errVar;
				operand.data.exp = expression;
				if (!operand.options) {
					if (!operand.optional && !operand.data.c && (operand.requirePositiveNumber)) {
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
				var expression = $scope.parseExpression(operand.data.e, false, operand.data.vt);
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
				$scope.delayEvaluation(operand);
				operand.selectedDataType = 'dynamic';
				break;
		}

		if ((!operand.error) && (operand.dataType == 'duration') && (!operand.durationUnit)) {
			operand.error = 'Invalid duration unit';
		}

		//if ((!operand.error) && operand.count && (!operand.data.i || !operand.data.i.length)) {
		//	operand.error = 'Invalid sub device selection';
		//}

		operand.valid = (!operand.error) && (
			((operand.data.t=='') && (operand.optional)) ||
			((operand.data.t=='d') && !!operand.data.d && !!operand.data.d.length) ||
			((operand.data.t=='p') && !!operand.data.d && !!operand.data.d.length && !!operand.data.a) ||
			((operand.data.t=='v') && !!operand.data.v) ||
			((operand.data.t=='x') && !!operand.data.x && !!operand.data.x.length) ||
			((operand.data.t=='s') && !!operand.data.s) ||
			((operand.data.t=='u') && !!operand.data.u) ||
			((operand.data.t=='c') && !((operand.data.c == "Invalid Date") && (operand.data.c instanceof Object)) && !((dataType == 'duration') && (isNaN(operand.data.c) || (operand.requirePositiveNumber && (operand.data.c < 1))))) ||
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
			$scope.refreshSelects();
		}
	};

	$scope.refreshSelects = function(type) {
		type = type || 'selectpicker';
		$scope.$$postDigest(function() {
			$('select[' + type + ']').selectpicker('refresh');
			$timeout(function() {
				$('select[' + type + ']').selectpicker('refresh');
				// Match smart-area height to backing textarea
				$('textarea').trigger('keyup');
			}, 0, false);
		});
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
		if ((comparison.left.selectedDataType != comparison.dataType) || (comparison.left.selectedMultiple != comparison.selectedMultiple) || (comparison.left.momentary != comparison.momentary) || (comparison.left.data.t == 'v') || (comparison.selectedInteractive != comparison.left.data.p)) {
			comparison.dataType = comparison.left.selectedDataType;
			comparison.selectedMultiple = comparison.left.selectedMultiple;
			comparison.selectedInteractive = comparison.left.data.p;
			comparison.momentary = comparison.left.momentary;
			//timed conditions are disabled if not comparing physical devices, or if applying an aggregation function
			var disableTimedConditions = (comparison.left.data.t != 'p') || ((comparison.left.data.g != 'any') && (comparison.left.data.g != 'all'));
			var disableConditions = (comparison.left.interactive && ((comparison.left.data.p == 'p') || (comparison.left.data.p == 's')));
			var disableTimedTriggers = disableConditions;
			var disableTriggers = (comparison.type == 'restriction');
			var optionList = [];
			var options = [];
			if (!comparison.dataType) comparison.dataType = 'dynamic';
			switch (comparison.dataType) {
				// There may be a better way to compare these, but string is better than nothing
				case 'color':
				case 'hexcolor':
				case 'object':
				case 'vector3':
				case 'enum':
					dt = 's';
					break;
				case 'image':
					dt = 'f'; // binary file
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
					dt = comparison.dataType.substr(0, 1);
			}
            dt = (comparison.momentary && (dt != 'e') ? (comparison.left.data.t == 'v' ? 'v' : 'm') : ((dt == 'n' ? 'd' : dt)));
			if (!disableConditions) {
				for(conditionId in $scope.db.comparisons.conditions) {
					var condition = $scope.db.comparisons.conditions[conditionId];
					if (((!dt && (condition.g != 'm')) || (condition.g.indexOf(dt) >= 0)) && (!disableTimedConditions || !condition.t))  {
						options.push({ id: conditionId, d: (comparison.selectedMultiple ? (condition.dd ? condition.dd : condition.d) : condition.d), c: 'Conditions' });
					}
				}
				optionList = optionList.concat(options.sort($scope.sortByDisplay));
			}
			if (!disableTriggers) {
				options = [];
				for(triggerId in $scope.db.comparisons.triggers) {
					var trigger = $scope.db.comparisons.triggers[triggerId];
					if ((trigger.g.indexOf(dt) >= 0) && (!disableTimedTriggers || !trigger.t)) {
						options.push({ id: triggerId, d: (comparison.selectedMultiple ? (trigger.dd ? trigger.dd : trigger.d) : trigger.d), c: 'Triggers' });
					}
				}
				optionList = optionList.concat(options.sort($scope.sortByDisplay));
			}
			comparison.options = optionList;
			if (comparison.options.length == 1) comparison.operator = comparison.options[0].id;
		}


		var comp = $scope.db.comparisons.conditions[comparison.operator] || $scope.db.comparisons.triggers[comparison.operator];		
		comparison.operatorValid = !!comp;
		comparison.parameterCount = comp && comp.p ? comp.p : 0;
		comparison.multiple = comp && comp.m ? true : false;

		comparison.valid = comparison.left.valid && comparison.operatorValid;

		comparison.timed = comp ? comp.t : 0;

		if ((comparison.parameterCount > 0) || (comparison.dataType == 'email')) {
			comparison.right.multiple = comparison.multiple;
			comparison.right.disableAggregation = comparison.multiple;
			comparison.right.dataType = (comparison.dataType == 'email' ? 'string' : comparison.left.selectedDataType);
			if (angular.toJson(comparison.right.options) != angular.toJson(comparison.left.selectedOptions)) {
				//avoid angular circus
				if ((comparison.right.data.t == 'c') && comparison.right.options && comparison.right.options.length && (!comparison.left.selectedOptions || !comparison.left.selectedOptions.left)) {
					//cleanup right operand constant value so we don't display old IDs
					comparison.right.data.c = '';
				}
				comparison.right.options = comparison.left.selectedOptions;
			}
			$scope.validateOperand(comparison.right, reinit, true);
			comparison.valid = comparison.valid && comparison.right.valid;
		}

		if ((comparison.parameterCount > 1) || (comparison.dataType == 'email')) {
			comparison.right2.multiple = comparison.multiple;
			comparison.right2.disableAggregation = comparison.multiple;
			comparison.right2.dataType = (comparison.dataType == 'email' ? 'string' : comparison.left.selectedDataType);
			if (angular.toJson(comparison.right2.options) != angular.toJson(comparison.left.selectedOptions)) {
				//avoid angular circus
				comparison.right2.options = comparison.left.selectedOptions;
			}
			$scope.validateOperand(comparison.right2, reinit, true);
			comparison.valid = comparison.valid && comparison.right2.valid;
		}


		var usingTime = (comparison.timed > 0);
		var usingTime2 = false;

		if (comparison.left.selectedDataType == 'time') {
			usingTime = usingTime || (comparison.right.data.t != 'c');
			usingTime2 = (comparison.right2.data.t != 'c');
		}
		if (usingTime) {
			comparison.time.requirePositiveNumber = !!comparison.timed;
			$scope.validateOperand(comparison.time, reinit, true);
			comparison.valid = comparison.valid && comparison.time.valid;
		}
		if (usingTime2) {
			comparison.time2.requirePositiveNumber = false;
			comparison.time2.dataType = 'duration';
			$scope.validateOperand(comparison.time2, reinit, true);
			comparison.valid = comparison.valid && comparison.time2.valid;
		}

		if (comparison.followedBy) {
			comparison.within.requirePositiveNumber = false;
			comparison.within.dataType = 'duration';
			$scope.validateOperand(comparison.within, reinit, true);
			comparison.valid = comparison.valid && comparison.within.valid;
		}
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

	$scope.renderOperand = function(operand, noQuotes, pedantic, noNegatives, grouping) {
		var result = '';
		if (operand) {
//			if (operand instanceof Array) {
//				result = $scope.renderDeviceList(operand, null, 'and', true);
//			} else {
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
							result = '<span var>{' + operand.x + ($scope.getLocalVariableType(operand.x).endsWith(']') ? '[' + operand.xi + ']' : '') + '}</span>';
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
								result = '<span num>' + utcToDateString(operand.c) + '</span>';
								break;
							case 'datetime':
								result = '<span num>' + utcToString(operand.c) + '</span>';
								break;
							case 'email':
								result = '<span eml>' + operand.c + '</span>';
								break;
							case 'piston':
								result = '<span lit>' + $scope.getPistonName(operand.c) + '</span>';
								break;
							case 'lifxScene':
								result = '<span lit>' + $scope.getLifxSceneName(operand.c) + '</span>';
								break;
							case 'lifxSelector':
								result = '<span lit>' + $scope.getLifxSelectorName(operand.c) + '</span>';
								break;
							case 'phone':
								result = '<span phn>' + operand.c + '</span>';
								break;
							case 'uri':
								result = '<span uri>' + operand.c + '</span>';
								break;
							case 'contact':
								result = $scope.renderContactNameList(operand.c);
								break;
							default:
								//if we still think we need quotes, let's make sure booleans don't have any
								if (!noQuotes) {
									if ((operand.vt == 'boolean') || (operand.vt == 'enum')) noQuotes = true;
									m = 'lit';
								}
								var c = operand.c;
								if (noNegatives && !isNaN(c) && parseInt(c) < 0) c = -parseInt(c);
								result = '<span ' + m + '>' + scope.buildName(c, noQuotes, pedantic, null, grouping) + '</span>';
						}
						break;
					case 'u':
						result = result + '<span var>{$args.' + operand.u + '}</span>';
						break;
					case 'e': //expression
						if (operand.e)
							result = '<span exp>{' + operand.e + '}</span>';
						break;
				}
//			}
		}
		result = result ? result : '<span nul class="nul">(empty)</span>';
		return (result instanceof Object) ? result : $sce.trustAsHtml(result);
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

	$scope.renderTimeOperand = function(to) {
		if (!to) return '';
		var isConstant = (to.t == 'c');
		var constantValue = isConstant && !isNaN(to.c) ? parseInt(to.c) : 0;
		if (isConstant && (constantValue == 0)) return '';
		return $scope.renderOperand(to, false, false, true) + ' <span lit>' + $scope.getDurationUnitName(to.vt, (constantValue != 1)) + '</span> <span pun>' + (constantValue < 0 ? 'to' : 'past') +' </span> ';
	}

	$scope.renderComparison = function(l, o, r, r2, to, to2) {
		var comparison = $scope.db.comparisons.triggers[o];
		var trigger = !!comparison;
		if (!comparison) comparison = $scope.db.comparisons.conditions[o];
		if (!comparison) return '[ERROR: Invalid comparison]';
		var pedantic = l.t == 'v';
		var plural = l && (l.t == 'p') && l.d && (l.d.length > 1) && (l.g == 'all');
		var noQuotes = false;
		var unit = '';
		var a = null;
		switch (l.t) {
			case 'v':
				switch (l.v) {
					case 'locationMode':
					case 'shmState':
						noQuotes = true;
						break;
				}
				break;
			case 'p':
				a = $scope.getAttributeById(l.a);
				if (!!a && !!a.u) unit = a.u;
				if (unit == '°?') unit = '°' + ($scope.location.temperatureScale ? $scope.location.temperatureScale : '');
				break;
		}
		var indexes = '';
		if (!!a && !!a.s && (l.i instanceof Array) && l.i.length) {
			indexes = ' <span num>' + $scope.buildNameList(l.i, 'or', null, null, false, true, false, '#') + '</span>';
		}
		if (!!a && !!a.p) {
			switch (l.p) {
				case 'p': indexes += ' <span lit>physically</span>'; break;
				case 's': indexes += ' <span lit>programmatically</span>'; break;
			}
		}
		var offset1 = '';
		var offset2 = '';
		if ((l.t == 'v') && (l.v == 'time')) {
			//time comparison, offsets?
			if (r && to && (r.t != 'c')) offset1 = $scope.renderTimeOperand(to);
			if (r2 && to2 && (r2.t != 'c')) offset2 = $scope.renderTimeOperand(to2);
		}
		var result = $scope.renderOperand(l) + indexes + ' <span pun>' + (plural ? (comparison.dd ? comparison.dd : comparison.d) : comparison.d) + '</span>' + (comparison.p > 0 ? ' ' + offset1 + $scope.renderOperand(r, noQuotes, pedantic) + (unit ? '<span pun>' + unit + '</span> ' : '') : '') + (comparison.p > 1 ? ' <span pun>' + (comparison.d.indexOf('between') ? 'and' : 'through') + '</span> ' + offset2 + $scope.renderOperand(r2, noQuotes, pedantic) + (unit ? '<span pun>' + unit + '</span> ' : '') : '');

		switch (comparison.t) {
			case 1:
				result += ' <span pun>' + (trigger ? 'for' : 'in the last') + '</span> ' + $scope.renderOperand(to) + ' <span lit>' + $scope.getDurationUnitName(to.vt, !((to.t == 'c') && (!isNaN(to.c)) && (parseInt(to.c) == 1))) + '</span>';
				break;
			case 2:
				result += ' <span pun>for ' + (to.f == 'g' ? 'at least' : 'less than') + '</span> ' + $scope.renderOperand(to) + ' <span lit>' + $scope.getDurationUnitName(to.vt, !((to.t == 'c') && (!isNaN(to.c)) && (parseInt(to.c) == 1))) + '</span>';
				break;
		}


		if ((l.t == 'v') && (['time', 'date', 'datetime'].indexOf(l.v) >= 0)) {
			var odw = (l.odw instanceof Array) && l.odw.length ? l.odw : null;
			var odm = (l.odm instanceof Array) && l.odm.length ? l.odm : null;
			var owm = !odm && (l.owm instanceof Array) && l.owm.length ? l.owm : null;
			var omy = (l.omy instanceof Array) && l.omy.length ? l.omy : null;
		
			if (!!odw || !!odm || !!owm || !!omy) {
				//we have restrictions
				var rCount = 0;
				result += '<span pun>,</span> <span pun>but only</span>';
				var odwString = '';
				if (odw) {
					for(i in odw) {
						if ((i > 0) && (odw.length > 2)) odwString += '<span pun>,</span> ';
						if ((i > 0) && (i == odw.length - 1)) odwString += ' <span pun>or</span> ';
						odwString += '<span lit>' + $scope.weekDays[odw[i]] + 's</span>';
					}
					rCount++;
				}
				if (owm) {
					result += (rCount ? '<span pun>,</span>' : '') + ' <span pun>on the</span> ';
					for(i in owm) {
						if ((i > 0) && (owm.length > 2)) result += '<span pun>,</span> ';
						if ((i > 0) && (i == owm.length - 1)) result += ' <span pun>or</span> ';
						result += '<span num>' + $scope.getOrdinal(owm[i]) + '</span>';
					}
					result += ' ' + (odwString ? odwString : '<span pun>week' + (owm.length > 1 ? 's' : '') + '</span>') + (omy ? '' : ' <span pun>of the month</span>');
					rCount++;
				} else {
					if (odwString) {
						result += (rCount > 1 ? '<span pun>,</span>' : '') + ' <span pun>on</span> ' + odwString;
					}
				}
				if (odm) {
					result += (rCount ? '<span pun>,</span>' : '') + ' <span pun>on the</span> ';
					for(i in odm) {
						if ((i > 0) && (odm.length > 2)) result += '<span pun>,</span> ';
						if ((i > 0) && (i == odm.length - 1)) result += ' <span pun>or</span> ';
						result += '<span num>' + $scope.getOrdinal(odm[i]) + '</span>';
					}
					result += ' day' + (odm.length > 1 ? 's' : '') + (omy ? '' : ' <span pun>of the month</span>');
					rCount++;
				}
				if (omy) {
					result += ' <span pun>' + (owm || odm ? 'of' : 'in') + '</span> ';
					for(i in omy) {
						if ((i > 0) && (omy.length > 2)) result += '<span pun>,</span> ';
						if ((i > 0) && (i == omy.length - 1)) result += ' <span pun>or</span> ';
						result += '<span lit>' + $scope.yearMonths[omy[i] - 1] + '</span>';
					}
					rCount++;
				}
			}
		}
		return $sce.trustAsHtml(result);
	}

	$scope.renderGroupingMethod = function(collection, item) {
		var result = collection.o;
		if ((collection.c instanceof Array) && (result == 'followed by')) {
			var idx = collection.c.indexOf(item) + 1;
			if (idx < collection.c.length) {
				var it = collection.c[idx];
				if (!it.wd) it.wd = {t:'c', c:'1', vt: 'm'};
				if (!it.wt) it.wt = 'l';
				result = (it.wt == 'n' ? 'not ' : '') + 'followed ' + (it.wt == 's' ? 'strictly ' : '') + 'within ' + $scope.renderOperand(it.wd) + ' <span lit>' + $scope.getDurationUnitName(it.wd.vt, !((it.wd.t == 'c') && (!isNaN(it.wd.c)) && (parseInt(it.wd.c) == 1))) + '</span> by';
			}
		}
		return $sce.trustAsHtml(result);
	}
	$scope.renderGroupWithin = function(collection, group) {
		var list = collection instanceof Array ? collection : collection.c;
		var result = '';
		if ((list instanceof Array) && (!!list.length) && (list[0] != group)) {
			result = ' within ' + $scope.renderOperand(group.wd) + ' <span lit>' + $scope.getDurationUnitName(group.wd.vt, !((group.wd.t == 'c') && (!isNaN(group.wd.c)) && (parseInt(group.wd.c) == 1))) + '</span>' + (group.wt == 's' ? ' (strict)' : '');
		}
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
			result += ', <span pun>at <span num>:' + m + '</span> <span pun>past the hour</span>';
		}
		if (level >= 5) {
			//higher levels require a time of day
			result += ', <span pun>at</span> ';
			if (timer.lo2.t != 'c') {
				//anything other than constants may have an offset
				switch (timer.lo3.t) {
					case 'c':
						var offset = isNaN(timer.lo3.c) ? 0 : parseInt(timer.lo3.c);
						if (offset == 0) {
							result += $scope.renderOperand(timer.lo2);
						} else if (offset < 0) {
							result += '<span num>' + (-offset).toString() + '</span> <span lit>' + $scope.getDurationUnitName(timer.lo3.vt, (offset < -1)) + '</span> <span pun>before</span> ' + $scope.renderOperand(timer.lo2);
						} else {
							result += '<span num>' + offset.toString() + '</span> <span lit>' + $scope.getDurationUnitName(timer.lo3.vt, (offset > 1)) + '</span> <span pun>after</span> ' + $scope.renderOperand(timer.lo2);
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
			result += '<span pun>,</span> <span pun>but only</span>';
			if (om) {
				result += ' <span pun>at</span> ';
				for(i in om) {
					if ((i > 0) && (om.length > 2)) result += '<span pun>,</span> ';
					if ((i > 0) && (i == om.length - 1)) result += ' <span pun>or</span> ';
					result += '<span num>:' + ('00' + om[i]).substr(-2) + '</span>';
				}
				result += ' <span pun>minutes past the hour</span>';
				rCount++;
			}
			if (oh) {
				result += (rCount ? '<span pun>,</span>' : '') + ' <span pun>during the</span> ';
				for(i in oh) {
					if ((i > 0) && (oh.length > 2)) result += '<span pun>,</span> ';
					if ((i > 0) && (i == oh.length - 1)) result += ' <span pun>or</span> ';
					result += '<span num>' + $scope.formatHour(oh[i]) + '</span>';
				}
				result += ' <span pun>hour' + (oh.length > 1 ? 's' : '') + '</span>';
				rCount++;
			}
			var odwString = '';
			if (odw) {
				for(i in odw) {
					if ((i > 0) && (odw.length > 2)) odwString += '<span pun>,</span> ';
					if ((i > 0) && (i == odw.length - 1)) odwString += ' <span pun>or</span> ';
					odwString += '<span lit>' + $scope.weekDays[odw[i]] + 's</span>';
				}
				rCount++;
			}
			if (owm) {
				result += (rCount ? '<span pun>,</span>' : '') + ' <span pun>on the</span> ';
				for(i in owm) {
					if ((i > 0) && (owm.length > 2)) result += '<span pun>,</span> ';
					if ((i > 0) && (i == owm.length - 1)) result += ' <span pun>or</span> ';
					result += '<span num>' + $scope.getOrdinal(owm[i]) + '</span>';
				}
				result += ' ' + (odwString ? odwString : '<span pun>week' + (owm.length > 1 ? 's' : '') + '</span>') + (omy ? '' : ' <span pun>of the month</span>');
				rCount++;
			} else {
				if (odwString) {
					result += (rCount > 1 ? '<span pun>,</span>' : '') + ' <span pun>on</span> ' + odwString;
				}
			}
			if (odm) {
				result += (rCount ? '<span pun>,</span>' : '') + ' <span pun>on the</span> ';
				for(i in odm) {
					if ((i > 0) && (odm.length > 2)) result += '<span pun>,</span> ';
					if ((i > 0) && (i == odm.length - 1)) result += ' <span pun>or</span> ';
					result += '<span num>' + $scope.getOrdinal(odm[i]) + '</span>';
				}
				result += ' day' + (odm.length > 1 ? 's' : '') + (omy ? '' : ' <span pun>of the month</span>');
				rCount++;
			}
			if (omy) {
				result += ' <span pun>' + (owm || odm ? 'of' : 'in') + '</span> ';
				for(i in omy) {
					if ((i > 0) && (omy.length > 2)) result += '<span pun>,</span> ';
					if ((i > 0) && (i == omy.length - 1)) result += ' <span pun>or</span> ';
					result += '<span lit>' + $scope.yearMonths[omy[i] - 1] + '</span>';
				}
				rCount++;
			}
		}

		return $sce.trustAsHtml(result);
	};

	$scope.renderString = function(value) {
		return renderString($sce, value);
	};

	$scope.renderTask = function(task) {
		var command = $scope.getCommandById(task.c);
		var display;
		if (!command) {
			display = task.c + '(';
			for (i in task.p) {
				display += (parseInt(i) ? ', ' : '') + $scope.renderOperand(task.p[i], null, null, null, 'and');
			}
			display += ')';
		} else {
			var displayFormat = command.d;
			if (task.c === 'httpRequest') {
				var method = task.p[1].c;
				var useQueryString = method === 'GET' || method === 'DELETE' || method === 'HEAD';
				var requestBodyType = task.p[2].c;
				if (useQueryString) {
					// with query [variables]
					displayFormat += '[? with query {3}]';
				} else if (requestBodyType === 'CUSTOM') {
					// with data [request body] as type [content type]
					displayFormat += '[? with {4}][? as type {5}]';
				} else {
					// with [request body type] encoded data [request body]
					displayFormat += '[? with {2}][? encoded {3}]';
				}
			}
			display = !displayFormat ? command.n : displayFormat.replace(/(?:\[\?(.*?))?\{(\d)\}(?:\s*\])?/g, function(match, prefix, text) {
				var idx = parseInt(text);
				if ((idx < 0) || (!task.p) || (idx >= task.p.length))
					return ' (?) ';
				var value = '';
				if (command.p[idx].t == 'duration') {
					var unit = $scope.getDurationUnitName(task.p[idx].vt, true);
					value = $scope.renderOperand(task.p[idx], true) + ' ' + unit;
				} else {
					if ((task.p[idx].t == 'c') && (!!command.p[idx].d) && (task.p[idx].c == 'false')) {
						//false optional values, we don't show them
						value = '';
					} else {
						value = $scope.renderOperand(task.p[idx], true, null, null, 'and');
					}
				}
				if (!value) value = '';
				if (!!value && !!command.p[idx].d) {
					value = (!!task.p[idx] && !!task.p[idx].t) ? command.p[idx].d.replace('{v}', value) : '';
				}
				return (value ? (prefix || '') : '') + value;
			}).replace(/(\{T\})/g, '°' + $scope.location.temperatureScale);
			var icon = command.i;
			var iconStyle = (window.fontAwesomePro && command.is) || 's';
			if (icon) display = '<span pun><i class="fa' + iconStyle + ' fa-' + icon + '"></i></span> ' + display;
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
					deviceNames.push({n: device.n, a: device.an, t: 'dev'});
				} else {
					deviceNames.push({ n: '{' + devices[deviceIndex] + '}', t: 'var'});
				}
			}
			if (deviceNames.length) {
				result = prefix + $scope.buildNameList(deviceNames, suffix, 'dev', '', !!attribute, true);
			}
		}
		return $sce.trustAsHtml(result);
	};










	$scope.validatePiston = function(piston) {
		var idx = 0;
		var level = 0;
		var warnings = {};
		var addWarning = function(object, warning) {
			if (!object) return;
			object.w = object.w ? object.w : [];
			object.w.push(warning);
		}
		var traverseObject = function(object, parentObject, dataType, parentLevel) {
			var level = parentLevel + 1;
			if (object instanceof Array) {
				for(i in object) {
					object[i] = traverseObject(object[i], parentObject, dataType, level);
				}
				return object;
			}
			if (object instanceof Object) {
				for (property in object) {
					object[property] = traverseObject(object[property], object, object.vt ? object.vt : object.t, level);
				}
				if (!!object.t) {
					delete(object.w);
					switch (object.t) {
						case 'every': if (level > 3) addWarning(object, 'Timers are designed to be top-level statements and should not be used inside other statements. If you need a conditional timer, please look into using a while loop instead.'); break;
						case 'on': if (level > 3) addWarning(object, 'On event statements are designed to be top-level statements and should not be used inside other statements.'); break;
					}
				}
			}
			return object;
		}
		piston = traverseObject(piston, 'piston', null, 0);
		$scope.warnings = warnings;
		return piston;
	}















	$scope.compilePiston = function(piston, anonymize, legend) {
		var legend = legend ? legend : {}
		var idx = 0;
		var warnings = {};
		var anonymizeValue = function(key, data) {
			if (!anonymize) {
				return (!!legend[key] && !!legend[key].id) ? legend[key].id : key;
			}
			if (!key) return '';
			var safeKey;
			if (legend[key]) {
				var item = legend[key];
				safeKey = item.key;
				if (data && data.a && (data.a instanceof Array) && item.value && item.value.a && (item.value.a instanceof Array)) {
					for (a in data.a) {
						if (item.value.a.indexOf(data.a[a]) < 0) item.value.a.push(data.a[a]);
					}
				}
			} else {
				safeKey = ':' + ('xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx' + idx).substr(-32) + ':';
				idx++;
				legend[key] = { key: safeKey, value: data };
			}
			return safeKey;
		}


		var addWarning = function(object, warning) {
			if (!object) return;
			object.w = object.w ? object.w : [];
			object.w.push(warning);
		}
		var traverseObject = function(object, parentObject, dataType) {
			if (object instanceof Array) {
				for(i in object) {
					object[i] = traverseObject(object[i], parentObject, dataType);
				}
				return object;
			}
			if (object instanceof Object) {
				if (object.exp) delete(object.exp);
				if (anonymize) {
					switch (object.vt) {
						case 'phone':
							var phones = object.c ? object.c.split(/,;\*\|/) : [];
							var safePhones = [];
							for (p in phones) {
								safePhones.push(anonymizeValue(phones[p], {t: 'phone'}));
							}
							object.c = safePhones.join(',');
							object.e = '';
							break;
						case 'contact':
						case 'contacts':
							var contacts = object.c ? (object.c instanceof Array ? object.c : object.c.split(/,;\*|/)) : [];
							var safeContacts = [];
							for (c in contacts) {
								safeContacts.push(anonymizeValue(contacts[c], {t: 'contact'}));
							}
							object.c = (object.c instanceof Array) ? safeContacts : safeContacts[0];
							object.e = '';
							break;
						case 'email':
							object.c = anonymizeValue(object.c, {t: 'email'})
							object.e = '';
							break;
						case 'uri':
							object.c = anonymizeValue(object.c, {t: 'uri'})
							object.e = '';
							break;
					}
				}
				delete(object.w);
				for (property in object) {
					var v = object[property];
					if ((v === false) || (v === null) || (v === '')) {
						delete(object[property]);
					} else {
						object[property] = traverseObject(object[property], object, object.vt ? object.vt : object.t);
					}
				}
				if (!anonymize && !!object && !!object.t && !!object.vt && ((object.t == 'c') || (object.t == 'e'))) {
					switch (object.t) {
						case 'c':
							object.exp = $scope.parseString(object.c, object.vt);
							break;
						case 'e':
							object.exp = $scope.parseExpression(object.e, false, object.vt);
							break;
					}
				}
				return object;
			}
			var value = object ? object.toString() : '';
			if (value.startsWith(':') && value.endsWith(':')) {
				if (anonymize) {
					var device = $scope.getDeviceById(object);
					if (device) {
						object = anonymizeValue(object, {t: 'device', n: device.an, a: !!parentObject && !!parentObject.a && (parentObject.a.length > 1) ? [parentObject.a] : []});
						return object;
					}
					var locationMode = $scope.getLocationModeById(object);
					if (locationMode) {
						switch (locationMode) {
							case 'Home':
							case 'Night':
							case 'Sleep':
							case 'Away':
							case 'Vacation':
								break;
							default:
								locationMode = 'Custom Mode';
						}
						object = anonymizeValue(object, {t: 'mode', n: locationMode});
						return object;
					}
					var routine = $scope.getRoutineById(object);
					if (routine) {
						object = anonymizeValue(object, {t: 'routine'});
							return object;
					}
					var contact = $scope.getContactById(object);
					if (contact) {
						object = anonymizeValue(object, {t: 'contact'});
						return object;
					}
				} else {
					object = anonymizeValue(object, {t: 'unknown'});
					return object;
				}
			}
			return object;
		}
		piston = traverseObject($scope.copy(piston), 'piston');
		piston.l = {};
		for (l in legend) {
			piston.l[legend[l].key] = legend[l].value;
		}
		$scope.warnings = warnings;
		return piston;
	}










    $scope.determineDeviceType = function(device) {
        return dataService.determineDeviceType(device);
    };

    $scope.anonymizeDevices = function(devices) {
		var cache = {}
		for (i in devices) {
			var device = devices[i];
	        var name = dataService.determineDeviceType(device).replace(/([A-Z])/g, ' $1').replace(/^./, function(str){ return str.toUpperCase(); }).replace('Rgb ', 'RGB ');
			var idx = cache[name] ? cache[name] + 1 : 1;
			cache[name] = idx;
			devices[i].an = name + ' ' + idx;
		}
		return devices;
    };

    $scope.anonymizeContacts = function(contacts) {
		var cache = {}
		for (i in contacts) {
			var contact = contacts[i];
	        var name = 'John Doe';
			var idx = cache[name] ? cache[name] + 1 : 1;
			cache[name] = idx;
			contacts[i].an = name + ' ' + idx;
		}
		return contacts;
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

	$scope.test = function() {
		dataService.testPiston($scope.pistonId);
	}

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
		var data = utoa($scope.serializeObject(object));
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

		function doSnapshot(bin) {
			$animate.enabled(false);
			$scope.view.exportBin = bin;
			var piston = document.getElementById('piston');
			piston.setAttribute('printing', '');
			if (anonymize) piston.setAttribute('anonymized', '');
			$timeout(function() {
				var width = piston.clientWidth + 10;
				var height = piston.clientHeight + (anonymize ? 0 : 64) + 10;
				// html2canvas cannot render SVGs; rasterize all icons to canvas first
				var $svgs = $(piston).find('svg');
				$svgs.each(function() {
					var $svg = $(this);
					// Size the canvas to match the actual icon rather than the svg
					var iconWidth = $svg.find('path').width();
					var iconHeight = $svg.find('path').height();
					// Allow canvg to read the inherited color from style.color
					$svg.css('color', $svg.css('color'));
					var $canvas = $('<canvas class="snapshotSvgCanvas"></canvas>').attr({
						width: iconWidth * 2,
						height: iconHeight * 2
					});
					xml = (new XMLSerializer()).serializeToString(this);
					// Avoid error in IE 
					xml = xml.replace(/xmlns=\"http:\/\/www\.w3\.org\/2000\/svg\"/, '');
					canvg($canvas[0], xml, { ignoreDimensions: true });
					$svg.css('color', '');
					// Align the canvas within the svg element bounds
					var paddingX = ($svg.width() - iconWidth) / 2;
					var paddingY = ($svg.height() - iconHeight) / 2;
					$canvas.css({
						width: iconWidth,
						height: iconHeight,
						paddingBottom: paddingY,
						paddingLeft: paddingX,
						paddingRight: paddingX,
						paddingTop: paddingY,
					});
					$svg.hide().after($canvas).next();
				});
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
				$svgs.show().next('.snapshotSvgCanvas').remove();
				delete($scope.view.exportBin);
				$animate.enabled(true);
			}, 1, false);
		};


		var cnt = 0;
		var counter = function() {
			cnt++;
			return cnt;
		}
//		var data = (anonymize ? $scope.anonymizePiston($scope.piston) : $scope.piston);
		var data = $scope.compilePiston($scope.piston, anonymize);
		$scope.loading = true;
		if (anonymize) {
			dataService.generateBackupBin(data, anonymize).then(function(response) {
				var bin = response.data;
				doSnapshot(bin);
			}, function() {
				//error
				piston.removeAttribute('printing');
				piston.removeAttribute('anonymized');
				delete($scope.view.exportBin);
				//$animate.enabled(true);
			});
		} else {
			doSnapshot($scope.meta.bin);
		}
	};

	$scope.textSnapshot = function() {
		copyToClipboard('piston');
	}



	$scope.parseString = function(string, dataType) {
		return $scope.parseExpression(string, true, dataType);
	}

	$scope.parseExpression = function(str, parseAsString, dataType) {
		str = (str != null) ? str.toString() : "";
		//remove \r \n
		//str = str.replace(/[\r\n]*/g, "");
		var i = 0;
		var initExp = !!parseAsString ? 0 : 1;
		var exp = initExp;
		//var sq = false;
		//var dq = false;
		//var dv = false;
		var osq = false;
		var odq = false;
		var func = 0;
		var numExp = /^-?(0(\.\d*)?|([1-9]\d*\.?\d*)|(\.\d+))([Ee][+-]?\d+)?$/;
		var parenthesis = 0;
		function location(start, end) {
			return start == end ? start.toString() : start.toString() + ':' + end.toString();
		}
		function main() {
			var arr = [];
			var sq = false;
			var dq = false;
			var dv = false;
			var startIndex = i;
			function isCompositeVariable() {return (str.substr(startIndex, 6) == '$args.') || (str.substr(startIndex, 6) == '$json.') || (str.substr(startIndex, 10) == '$response.') || (str.substr(startIndex, 5) == '$nfl.') || (str.substr(startIndex, 8) == '$places.') || (str.substr(startIndex, 9) == '$weather.') || (str.substr(startIndex, 11) == '$incidents.') ||  (str.substr(startIndex, 6) == '$args[') || (str.substr(startIndex, 6) == '$json[') || (str.substr(startIndex, 8) == '$places[') || (str.substr(startIndex, 10) == '$response[') || (str.substr(startIndex, 11) == '$incidents[');};
			function addOperand() {
				if (i-1 > startIndex) {
					var value = str.slice(startIndex, i-1).trim();
					var parsedValue = parseFloat(value.trim());
					if (!isNaN(parsedValue) && (numExp.test(value.trim()))) {
						arr.push({t: (value.indexOf('.') >= 0 ? 'decimal' : 'integer'), v: parsedValue, l: location(startIndex, i - 2)});
						return true;
					}
					if (typeof value == 'string') {
						if (['true', 'false'].indexOf(value) >= 0) {
							arr.push({t: 'boolean', v: value, l: location(startIndex, i - 2)});
						} else if (['null'].indexOf(value) >= 0) {
							arr.push({t: 'dynamic', v: null, l: location(startIndex, i - 2)});
						} else {
							arr.push({t: 'variable', x: value, l: location(startIndex, i - 2)});
						}
						return true;
					}
					arr.push({t: 'operand', v:str.slice(startIndex, i-1), l: location(startIndex, i - 2)});
					return true;
				}
				return false;
			}
			function addConstant(allowEmpty) {
				if (i - (allowEmpty ? 0 : 1) > startIndex) {
					var value = str.slice(startIndex, i-1).replace(/\\[\[\]\{\}\'\"0-9abcdefghijklmopqsuvwxyz]/gi, function(match) { return match[1] });
					var parsedValue = parseFloat(value.trim());
					if ((dataType != 'phone') && !isNaN(parsedValue) && (numExp.test(value.trim()))) {
						arr.push({t: (value.indexOf('.') >= 0 ? 'decimal' : 'integer'), v: parsedValue, l: location(startIndex, i - 2)});
						return true;
					}					
					arr.push({t: (['true', 'false'].indexOf(value) >= 0 ? 'boolean' : 'string'), v: (value == 'null' ? null : value), l: location(startIndex, i - 2)});
				}
			}
			function addDevice() {
				if (i-1 > startIndex) {
					var value = str.slice(startIndex, i-1);
					var pos = value.lastIndexOf(':');
					var deviceName = value;
					var attribute = '';
					if (pos > 0) {
						var deviceName = value.substr(0, pos).trim().replace(/\\[\[\]\{\}\'\"0-9abcdefghijklmopqsuvwxyz]/gi, function(match) { return match[1] });;
						attribute = value.substr(pos + 1).trim();
					}
					var device = $scope.getDeviceByName(deviceName);
					if (device && device.id) {
						//a device was found
						var a = attribute.toLowerCase();
						attribute = '';
						virtualAttribute = '';
						switch (a) {
							case 'orientation':
							case 'axisx':
							case 'axisy':
							case 'axisz':
								virtualAttribute = a.replace('axisx', 'axisX').replace('axisy', 'axisY').replace('axisz', 'axisZ');
								a = 'threeaxis';
						}
						if (a == statusAttribute) {
							attribute = statusAttribute;
						} else {
							for (attributeIndex in device.a) {
								var attr = device.a[attributeIndex];
								if (a == attr.n.toLowerCase()) {
									attribute = virtualAttribute ? virtualAttribute : attr.n;
								}
							}
						}
						if (!!a && !attribute) attribute = '?';
						arr.push({t: 'device', id: device.id, a: attribute, l: location(startIndex - 1, i - 1)});
					} else {
						//the device name is probably a variable?!
						arr.push({t: 'device', x: deviceName, a: attribute, l: location(startIndex - 1, i - 1)});
					}
				}
			}
			function addFunction() {
				var value = str.slice(startIndex, i-1).toLowerCase().trim();
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
			var compositeVariable = isCompositeVariable();
			while (i < str.length) {
				var c = str[i++];

				if (!compositeVariable) {
					var value = str.slice(startIndex, i-1).trim();				
					if (value.indexOf(' ') < 0) {
						for(var ci in $scope.piston.v) {
							if (($scope.piston.v[ci].n == value) && $scope.piston.v[ci].t.endsWith(']')) {
								compositeVariable = true;
								break;
							}
						}
					}
				}

				switch(c) {
					case ' ':
					case '\t':
					case '\r':
					case '\n':
						if (exp && !dv && !dq && !sq) {
							addOperand();
							startIndex = i;
							compositeVariable = isCompositeVariable();
						}
						continue;
					case '+':
					case '-':
					case '/':
					case '*':
					case '~':
					case '^':
					case '\\':
					case '%':
					case '&':
					case '|':
					case ',':
					case '!':
					case '=':
					case '<':
					case '>':
					case '?':
					case ':':
						var c2 = (i < str.length) ? str[i] : '';
						if (exp && !dv && !sq && !dq) {
							addOperand();
							if (['**', '&&', '||', '^^', '!&', '!|', '!^', '==', '!=', '!!', '>=', '<=', '<>', '<<', '>>'].indexOf(c + c2) >= 0) {
								i++;
								c += c2;
							}
							arr.push({t: 'operator', o: c, l: location(i - 1, i - 1)});
							startIndex = i;
							compositeVariable = isCompositeVariable();
						} else if (c == '\\') {
							i++;
							c = c2;
						}
						continue;
					case '"':
					case '“':
					case '”':
						if (exp && !dv && !sq) {
							dq = !dq;
							odq = !odq;
							(dq ? addOperand() : addConstant(true));
							startIndex = i;
							compositeVariable = isCompositeVariable();
						}
						continue;
					case '\'':
					case '‘':
					case '’':
						if (exp && !dq && !dv) {
							sq = !sq;
							osq = !osq;
							(sq ? addOperand() : addConstant(true));
							startIndex = i;
							compositeVariable = isCompositeVariable();
						}
						continue;
					case '(':
						if (exp && !dv && !dq && !sq) {
							parenthesis++;
							addFunction();
							startIndex = i;
							compositeVariable = isCompositeVariable();
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
						if (!compositeVariable && exp && !dq && !sq && !dv) {
							dv = true;;
							addOperand();
							startIndex = i;
							compositeVariable = isCompositeVariable();
						}
						continue;
					case ']':
						if (!compositeVariable && exp && dv && !dq && !sq) {
							addDevice();
							dv = false;
							startIndex = i;
							compositeVariable = isCompositeVariable();
						}
						continue;
					case '{':
						if (exp == initExp) {
							exp++;
							addConstant();
							startIndex = i;
							arr.push({t: 'expression', i: main(), l: location(startIndex - 1, i - 1)});
							startIndex = i;
							compositeVariable = isCompositeVariable();
						} else {
							exp++;
							startIndex = i;
							arr.push({t: 'expression', i: main(), l: location(startIndex - 1, i - 1)});
							startIndex = i;
							compositeVariable = isCompositeVariable();
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
		} else if (osq) {
			result.err = 'Invalid single quote termination';
		} else if (odq) {
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
							if (item.a == '?') {
								ok = false;
								err = 'Invalid attribute ' + getSubstring(item.l, ':', 1);
								loc = item.l;
								break;
							}
							break;
					case 'variable':
							if (item.x.startsWith('$args.') && (item.x.length > 6)) break;
							if (item.x.startsWith('$args[') && (item.x.length > 6)) break;
							if (item.x.startsWith('$json.') && (item.x.length > 6)) break;
							if (item.x.startsWith('$json[') && (item.x.length > 6)) break;
							if (item.x.startsWith('$places.') && (item.x.length > 8)) break;
							if (item.x.startsWith('$places[') && (item.x.length > 8)) break;
							if (item.x.startsWith('$response.') && (item.x.length > 10)) break;
							if (item.x.startsWith('$response[') && (item.x.length > 10)) break;
							if (item.x.startsWith('$nfl.') && (item.x.length > 5)) break;
							if (item.x.startsWith('$weather.') && (item.x.length > 9)) break;
							if (item.x.startsWith('$incidents.') && (item.x.length > 11)) break;
							if (item.x.startsWith('$incidents[') && (item.x.length > 11)) break;
							if ($scope.systemVars && $scope.systemVars[item.x]) break;
							if ($scope.globalVars && $scope.globalVars[item.x]) break;
							if (!$scope.getVariableByName(item.x)) {
								if (item.x.indexOf('[') >= 0) {
									var v = $scope.getVariableByName(item.x.split('[')[0]);
									if (v && v.t.endsWith(']')) break;
								}
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

	$scope.onEvalKeyDown = function(event) {
		if (($scope.lastEval >= 0) || ($scope.evalText == '')) {
			var delta = (event.originalEvent.keyCode == 38 ? -1 : (event.originalEvent.keyCode == 40 ? 1 : 0));
			if (delta == 0) return;
			var i = $scope.lastEval + delta;
			if (i >= $scope.evals.length) i = 0;
			if (i < 0) i = $scope.evals.length - 1;
			if ((i >= 0) && (i < $scope.evals.length)) {
				$scope.evalText = $scope.evals[i].text;
			}
			$scope.lastEval = i;
		}		
	}

	$scope.onEvalKeyPress = function(event) {
		if (event.originalEvent.keyCode != 13) return;
		$scope.lastEval = -1;
		var text = $scope.evalText;
		switch (text) {
			case '/clear':
				$scope.evalText = '';
				$scope.evals = [];
				return;
		}
		var eval = {type: ($scope.evalType == 'e' ? 'expression' : 'value'), text: text, eval: ''};
		if ($scope.evalType == 'e') {
			eval.eval = $scope.evaluateExpression(text, null, eval, true);
		} else {
			eval.eval = $scope.evaluateValue(text, null, eval, true);
		}
		$scope.evals.push(eval);
		$scope.evalText = '';
		$scope.$$postDigest(function() {
			var d = $("console > content");
			d.scrollTop(d.prop("scrollHeight"));
		});
	}

	$scope.delayEvaluation = function(operand) {
		if (!operand) return;
		operand.eval = '...';
		var expression = operand.data.exp;
		var dataType = operand.data.vt;
		// Display durations relative to the selected unit; otherwise the expression
		// will evaluate and return in terms of milliseconds.
		switch (dataType) {
			case 's':
			case 'm':
			case 'h':
			case 'd':
			case 'w':
			case 'n':
			case 'y':
				dataType = 'ms';
		}
		$timeout.cancel(operand.tmrDelayEvaluation);
		operand.tmrDelayEvaluation = $timeout(function() { if ($scope.designer && $scope.designer.dialog) {operand.eval = '(evaluating)'; evaluateExpression(expression, dataType, operand);}}, 2500);
	}

	$scope.evaluateValue = function(value, dataType, output, showType) {
		return $scope.evaluateExpression($scope.parseExpression(value, true), dataType, output, showType);
	}

	$scope.evaluateExpression = function(expression, dataType, output, showType) {
		var useConsole = !(output instanceof Object);
		if (!(expression instanceof Object)) {
			expression = $scope.parseExpression(expression);
		}
		if (!(expression instanceof Object)) {
			return 'Evaluation error: unknown error.';
		}
		if (expression.err) {
			return 'Evaluation error: ' + expression.err;
		}
		dataService.evaluateExpression($scope.pistonId, expression, dataType).then(function (response) {
			var result = '';
			if (!response || (response.status != 'ST_SUCCESS')) {		
				result = 'Evaluation error: Received a ' + (response ? response.status : '(unknown)') + ' result.';
			} else {
				result = (!!useConsole || !!showType ? '(' + response.value.t + ') ' : '') + response.value.v;
			}
			if (useConsole) {
				console.log(result);
			} else {
				output.eval = $scope.renderString(result);
			}
		});
		return '(evaluating)';
	};
	window.evaluateValue = $scope.evaluateValue;
	window.evaluateExpression = $scope.evaluateExpression;

	var userAgent = navigator.userAgent || navigator.vendor || window.opera;
	if( userAgent.match( /Android/i ) ) {
		$scope.android = true;
	}
	$scope.url = window.location.href;
	$scope.mobile = window.mobileCheck();
	$scope.tablet = (!$scope.mobile) && (window.mobileOrTabletCheck());
	$scope.formatTime = window.formatTime
	$scope.utcToString = utcToString;
	$scope.utcToTimeString = utcToTimeString;
	$scope.utcToDateString = utcToDateString;
	$scope.formatLogTime = function(timestamp, offset) { return utcToString(timestamp) + '+' + offset; };
	$scope.md5 = window.md5;
	//init
    var tmrInit = setInterval(function() {
        if (dataService.ready()) {
            clearInterval(tmrInit);
            $scope.init();
        }
    }, 1);

}]);

function test(value, parseAsString, dataType) {
	scope.evaluateExpression(scope.parseExpression(value, parseAsString, dataType));
}

var MAX_STACK_SIZE = 10;