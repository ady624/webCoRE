config.controller('dashboard', ['$scope', '$rootScope', 'dataService', '$timeout', '$interval', '$location', '$sce', '$routeParams', 'ngDialog', '$window', function($scope, $rootScope, dataService, $timeout, $interval, $location, $sce, $routeParams, ngDialog, $window) {
	var tmrStatus = null;
	var tmrClock = null;
	var tmrActivity = null;
	$scope.initialized = false;
    $scope.loading = true;
    $scope.data = null;
    $scope.error = '';
	$scope.designer = {};
	$scope.locations = null;
	$scope.instances = null;
	$scope.requestId = 0;
	$scope.activePistons = 0;
	$scope.pausedPistons = 0;

	$scope.init = function(instance, uri, pin) {
		if ($scope.$$destroyed) return;
        if (tmrActivity) $timeout.cancel(tmrActivity);
		tmrActivity = null;
		$scope.requestId++;
		var currentRequestId = 0 + $scope.requestId;
		$scope.loading = !$scope.initialized || !$scope.instance;
        dataService.setStatusCallback($scope.setStatus);
		dataService.loadInstance(instance, uri, pin).then(function(data) {
				if ($scope.$$destroyed) return;
				if (currentRequestId != $scope.requestId) { return };
				if (data.error) {
					switch (data.error) {
						case 'ERR_INVALID_TOKEN':
							$scope.dialogLogIn(data.name, data.uri);
							break;
					}
				} else {
					$scope.initialized = true;
					$scope.location = dataService.getLocation();
					$scope.instance = dataService.getInstance();
					$scope.currentInstanceId = $scope.instance.id;
					$scope.instanceCount = dataService.getInstanceCount();
					window.scope = $scope;
					$scope.loading = false;
					$scope.activePistons = 0;
					$scope.pausedPistons = 0;
					for(pistonIndex in $scope.instance.pistons) {
						var piston = $scope.instance.pistons[pistonIndex];
						if (piston.meta && piston.meta.a) {
							$scope.activePistons++;
						} else {
							$scope.pausedPistons++;
						}
					}
					$scope.clock();
					$scope.render();
				}
		    }, function(data, status, headers, config) {
				if ($scope.$$destroyed) return;
				if (status == 404) {
					$scope.dialogDeleteInstance(instance);
				}
			});
	};

	$scope.closeNavBar = function() {
		if (!$("#navbar-btn").hasClass('collapsed')) $("#navbar-btn").click();
	};

    $scope.render = function() {
		if ($scope.$$destroyed) return;
		if (!$scope.initialized) return;
        //do nothing, but rerenders
		if (!tmrClock) tmrClock = $interval($scope.clock, 1000);
		if (!tmrActivity) tmrActivity = $timeout($scope.init, 5000);
    }


	$scope.clock = function() {
		for(pistonIndex in $scope.instance.pistons) {
			var piston = $scope.instance.pistons[pistonIndex];
			piston.opacity = piston.meta ? $scope.getOpacity(piston.meta.t) : 0;
		}
	};

    $scope.setStatus = function(status) {
        if (tmrStatus) $timeout.cancel(tmrStatus);
        tmrStatus = null;
        $scope.status = status;
        if ($scope.status) {
            tmrStatus = $timeout(function() { $scope.setStatus(); }, 10000);
        }
    }

	$scope.listLocations = function() {
		return dataService.listLocations();
	};
    
	$scope.listInstances = function(locationId) {
		return dataService.listInstances(locationId);
	};

	$scope.listAllInstances = function() {
		var result = [];
		var locations = $scope.listLocations();
		for (l in locations) {
			var instances = dataService.listInstances(locations[l].id);
			for (i in instances) {
				result.push({ id: instances[i].id, name: locations[l].name + ' \\ ' + instances[i].name, pistons: instances[i].pistons });
			}
		}
		return result;
	};

	$scope.switchInstance = function(instanceId) {
		if (instanceId != $scope.instance.id) {
			var instance = dataService.getInstance(instanceId);
			if (instance) {
				$scope.instance = null;
		        if (tmrActivity) $timeout.cancel(tmrActivity);
				tmrActivity = null;
				$scope.init(instance);
				$scope.closeNavBar();
			}
		}
	};

    $scope.$on('$destroy', function() {
        if (tmrStatus) $timeout.cancel(tmrStatus);
        if (tmrClock) $interval.cancel(tmrClock);
        if (tmrActivity) $timeout.cancel(tmrActivity);
    });

    $scope.openPiston = function(id) {
        $scope.loading = true;
        $scope.initialized = false;
      	$location.path('piston/' + id);
    }

	$scope.newPiston = function() {
		$scope.loading = true;
		dataService.generateNewPistonName().then(function(data) {
			$scope.loading = false;
    	    $scope.designer = {};
			$scope.designer.author = dataService.loadFromStore('author.handle');
			$scope.designer.name = data.name;
			$scope.designer.page = 0;
			$scope.designer.backup = !!dataService.loadFromStore('backup.auto');
			$scope.designer.disclaimer = !$scope.designer.backup;
        	$scope.designer.items = [
	            { type: 'blank', name: 'Create a blank piston', icon: 'code', cssClass: 'wide btn-default' },
	            { type: 'duplicate', name: 'Create a duplicate piston', icon: 'code', cssClass: 'wide btn-info' },
    	        { type: 'template', name: 'Create a piston from a template', icon: 'code', cssClass: 'wide btn-success' },
        	    { type: 'restore', name: 'Restore a piston using a backup code', icon: 'code', cssClass: 'wide btn-warning' },
        	    { type: 'import', name: 'Import a piston from an external source', icon: 'code', cssClass: 'wide btn-danger' },
	        ];
    	    $scope.designer.dialog = ngDialog.open({
        	    template: 'dialog-add-piston',
            	className: 'ngdialog-theme-default ngdialog-large',
	            closeByDocument: false,
    	        disableAnimation: true,
        	    scope: $scope
	        });
		});
    };


	$scope.movePiston = function() {
		$scope.designer = {
			pistons: [],
			instance: ''
		}
   	    $scope.designer.dialog = ngDialog.open({
       	    template: 'dialog-move-piston',
           	className: 'ngdialog-theme-default ngdialog-large',
            closeByDocument: false,
   	        disableAnimation: true,
       	    scope: $scope
        });
	};

	$scope.movePistons = function() {
		alert('Sorry, not ready yet');
	}

	$scope.createPiston = function() {
		var success = function(data) {
			$scope.closeDialog();
			$scope.initialized = false;
			$location.path("piston/" + data.id).search({description: $scope.designer.description, type: $scope.designer.type, piston: $scope.designer.piston, bin: $scope.designer.bin});
		};
		$scope.loading = true;
		dataService.saveToStore('backup.auto', !!$scope.designer.backup);
		dataService.saveToStore('author.handle', $scope.designer.author);
		if ($scope.designer.backup) {
			dataService.generateBackupBin().then(function(binId) {
				dataService.createPiston($scope.designer.name, $scope.designer.author, binId).then(success);
			});
		} else {
			dataService.createPiston($scope.designer.name, $scope.designer.author).then(success);
		}
    };

	$scope.dialogLogIn = function(sender, uri) {
		if (tmrActivity) $timeout.cancel(tmrActivity);
		tmrActivity = null;
		$scope.loading = false;
		$scope.initialized = false;
		$scope.designer = {};
		$scope.designer.sender = sender;
		$scope.designer.uri = uri;
        $scope.designer.dialog = ngDialog.open({
            template: 'dialog-auth',
            className: 'ngdialog-theme-default ngdialog-large',
            closeByDocument: false,
            disableAnimation: true,
            scope: $scope
        });
	}


	$scope.logOut = function() {
		localStorage.clear();
		$scope.loading = true;
		$scope.initialized = false;
		$location.path('register');
	}


	$scope.authenticate = function() {
		$scope.closeDialog();
		$scope.init(null, $scope.designer.uri, window.md5('pin:' + $scope.designer.password));
		$scope.designer = null;
	}


	$scope.dialogDeleteInstance = function(instance) {
		if (instance) {		
			$scope.loading = false;
			$scope.initialized = false;
			$scope.designer = {};
			$scope.designer.sender = instance.locationName + ' \\ ' + instance.name;
			$scope.designer.instance = instance ;
	        $scope.designer.dialog = ngDialog.open({
	            template: 'dialog-del-instance',
	            className: 'ngdialog-theme-default ngdialog-large',
	            closeByDocument: false,
	            disableAnimation: true,
	            scope: $scope
	        });
		}
	}

	$scope.deleteInstance = function() {
		$scope.closeDialog();
		dataService.deleteInstance($scope.designer.instance);
		$scope.designer = null;
		$scope.init();
	}

    $scope.setDesignerType = function(type) {
        $scope.designer.type = type;
        $scope.nextPage();
    };

    $scope.closeDialog = function() {
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


	$scope.getOpacity = function(time) {
		if (!time) return 0;
		time = currentTime() - time;
		if ((time < 0) || (time > 60000)) return 0;
		return 1.0 - time / 60000.0;
	}



































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
		$scope.closeNavBar();
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


	$scope.capturePiston = function() {
		var panel = document.getElementById('viewerPanel');
		document.body.scrollTop = 0;
        html2canvas(panel).then(function(canvas) {
            $scope.capturedImage = canvas.toDataURL('image/png');
			$scope.dialogCapture = ngDialog.open({
            	template: 'dialog-captured-image',
				className: 'ngdialog-theme-default ngdialog-large',
				disableAnimation: true,
	            scope: $scope,
	            showClose: true
	        });
//			var open = window.open(img);
//	        if ((open == null) || (open == undefined))
//	            alert('Sorry, your browser is blocking pop-ups. Please disable the pop-up blocker and try again.');
        });
	}




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
