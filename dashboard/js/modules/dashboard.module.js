config.controller('dashboard', ['$scope', '$rootScope', 'dataService', '$timeout', '$interval', '$location', '$sce', '$routeParams', 'ngDialog', '$window', '$q', function($scope, $rootScope, dataService, $timeout, $interval, $location, $sce, $routeParams, ngDialog, $window, $q) {
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
	$scope.dropDownMenu = false;
	$scope.endpoint = '';
	$scope.rawEndpoint = '';
	$scope.categories = [];
	$scope.pausedPistons = [];
	$scope.view = 'piston';
	$scope.isAppHosted = !!window.BridgeCommander;
	$scope.hostDeviceId = '';
	$scope.sidebarCollapsed = dataService.isCollapsed('dashboardSidebar');
	$scope.completedInitialRender = false;
	dataService.getImportedData().then(function(data) {
		$scope.canResumeImport = data && data.length;
	});

	$scope.init = function(instance, uri, pin) {
		//if (!instance) instance = dataService.getInstance();
		if ($scope.$$destroyed) return;
        if (tmrActivity) $timeout.cancel(tmrActivity);
		tmrActivity = null;
		$scope.requestId++;
		var currentRequestId = 0 + $scope.requestId;
		$scope.loading = !$scope.initialized || !$scope.instance;
        dataService.setStatusCallback($scope.setStatus);
		dataService.loadInstance(instance, uri, pin, ($scope.view == 'dashboard')).then(function(data) {
				if ($scope.$$destroyed) return;
				if (currentRequestId != $scope.requestId) { return };
				if (data) {
					$scope.endpoint=data.endpoint + 'execute/:pistonId:';
					$scope.rawEndpoint=data.endpoint;
					$scope.rawAccessToken=data.accessToken;
					if (data.error) {
						switch (data.error) {
							case 'ERR_INVALID_TOKEN':
								$scope.dialogLogIn(data.name, data.uri, data.accessToken);
								break;
						}
					} else {
						$scope.initialized = true;
						$scope.location = dataService.getLocation();
						$scope.instance = dataService.getInstance();
						$scope.currentInstanceId = $scope.instance.id;
						$scope.instanceCount = dataService.getInstanceCount();
						$scope.sidebarCollapsed = dataService.isCollapsed('dashboardSidebar');
    		            if (!$scope.devices) $scope.devices = $scope.listAvailableDevices();
	    	            if (!$scope.virtualDevices) $scope.virtualDevices = $scope.listAvailableVirtualDevices();
						window.scope = $scope;
						window.dataService = dataService;
						$scope.loading = false;
						var categories = $scope.getCategories();
						while($scope.categories.length > categories.length) $scope.categories.pop();
						while($scope.categories.length < categories.length) $scope.categories.push({});
						for (var i = 0; i < categories.length; i++) {
							$scope.categories[i].i = categories[i].i;
							$scope.categories[i].n = categories[i].n;
							$scope.categories[i].t = categories[i].t;
							$scope.categories[i].p = [];
						}
						$scope.pausedPistons = [];
						for(pistonIndex in $scope.instance.pistons) {
							var piston = $scope.instance.pistons[pistonIndex];
							if (piston.meta && piston.meta.a) {
								if (piston.meta.s && (!!piston.meta.s.i || !!piston.meta.s.t)) {
									//temporary fix for piston tiles before moving to multiple tiles
									piston.meta.s.i1 = piston.meta.s.i;
									piston.meta.s.t1 = piston.meta.s.t;
									piston.meta.s.b1 = piston.meta.s.b;
									piston.meta.s.c1 = piston.meta.s.c;
									piston.meta.s.f1 = piston.meta.s.f;
								}
								var cat = $scope.getCategory(piston.meta.c);
								cat.p = (cat.p instanceof Array) ? cat.p : [];
								cat.p.push(piston);
							} else {
								$scope.pausedPistons.push(piston);
							}
						}
						$scope.clock();
						$scope.render();
						$timeout(function() {
							$scope.completedInitialRender = true;
						});
					}
				} else {
					$scope.dialogDeleteInstance(instance);
				}
			}, function(data) {
				$scope.initialized = true;
				$scope.render();
				$timeout(function() {
					$scope.completedInitialRender = true;
				});
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
		if (!tmrActivity) tmrActivity = $timeout($scope.init, 10000);
    }

	$scope.getTileMeta = function(piston, $index) {
		var meta = $scope.renderString(piston.meta.s['t' + ($index + 1)]).meta;
		if (!!meta && !!meta.type) return meta;
		meta = $scope.renderString(piston.meta.s['f' + ($index + 1)]).meta;
		if (!!meta && !!meta.type) return meta;
		meta = $scope.renderString(piston.meta.s['i' + ($index + 1)]).meta;
		return meta;
	}

	$scope.refreshImage = function(img) {
		var found = false;
		var parent = img.parentElement; while(parent) { if (parent.tagName.toLowerCase() == 'viewer') { found = true; break; };  parent = parent.parentElement; }
		if (!found) return;
		var src = img.osrc;
		var tmr = '_img_refresh_token_'
		if (!src || img.src.indexOf(tmr) < 0) {
			src = img.src;
			img.osrc = src;
		}
		if (!src || src.startsWith('data:') || src.startsWith('{') || src.startsWith('%7B')) return;
		var p = src.indexOf(tmr);
		if (p > 0) {
			src = src.substr(0, p) + tmr + '=' + (new Date()).getTime();
		} else {
			src += (src.indexOf('?') > 0 ? '&' : '?') + tmr + '=' + (new Date()).getTime();
		}
		var i = new Image();
		i.onload = function() {
			img.src = src;
		}
		i.src = src;
		img.src = src;
	}

	$scope.getGaugeChart = function(piston, $index, meta) {
		return {
			type:'Gauge',
			options: meta.options,
			data: {
				cols: [{
					id:'gauge',
					label:meta.text,
					type:'number'
				}],
				rows: [{
					c: [{
						v: $scope.renderString(piston.meta.s['t' + ($index + 1)]).meta.text,
						f: piston.meta.s['o' + ($index + 1)] ? $scope.renderString(piston.meta.s['o' + ($index + 1)]).meta.text : null
					}]
				}]
			}
		}
	};

	$scope.clock = function() {
		if ($scope.instance) {
			for(pistonIndex in $scope.instance.pistons) {
				var piston = $scope.instance.pistons[pistonIndex];
				piston.opacity = piston.meta ? $scope.getOpacity(piston.meta.t) : 0;
			}
		}
	};

    $scope.setStatus = function(status, permanent) {
		if (permanent) {
			$scope.permanentStatus = status
			return;
		}
        if (tmrStatus) $timeout.cancel(tmrStatus);
        tmrStatus = null;
        $scope.status = status;
        if ($scope.status) {
            tmrStatus = $timeout(function() { $scope.setStatus(); }, 10000);
        }
    }

    $scope.clickPistonTile = function($event, piston, tile) {
		if ($event.originalEvent.ctrlKey || $event.originalEvent.shiftKey) {
			$scope.openPiston(piston.id);
		} else {
	        dataService.clickPistonTile(piston.id, tile).then(function(data) {
				if (data && (data.status == 'ST_SUCCESS') && !!(data['new']) && !!piston && !!(piston.meta)) {
					piston.meta.s = data;
				}
			});
		}
    }

    $scope.copy = function(object) {
        return angular.fromJson(angular.toJson(object));
    };


	$scope.getPlaces = function() {
		var places = (!!$scope.instance && !!$scope.instance.settings && ($scope.instance.settings.places instanceof Array)) ? $scope.copy($scope.instance.settings.places) : [];
		return places;
	}

	$scope.getCategories = function() {
		var categories = (!!$scope.instance && !!$scope.instance.settings && ($scope.instance.settings.categories instanceof Array)) ? $scope.copy($scope.instance.settings.categories) : [];
		if (!categories.length) categories = [{n: 'Uncategorized', t: 'd', i: 0}];
		return categories;
	}

	$scope.getCategory = function(id) {
		id = parseInt(id);
		if (isNaN(id)) id = 0;
		for (var i in $scope.categories) {
			if ($scope.categories[i].i == id) return $scope.categories[i];
		}
		for (var i in $scope.categories) {
			if ($scope.categories[i].i == 0) return $scope.categories[i];
		}
		$scope.categories.push({n: 'Uncategorized', t: 'd', i: 0});
		return $scope.categories[$scope.categories.length - 1];
	}


	$scope.updateLocation = function(position) {
	    $scope.coords = position.coords;
	}

	$scope.showSettings = function() {
		ga('send', 'event', 'settings', 'show');
		if (navigator.geolocation) {
		    navigator.geolocation.getCurrentPosition($scope.updateLocation);
		}
		$scope.checkPresenceSensor();
		$scope.closeNavBar();
		$scope.settings = $scope.copy($scope.instance.settings);
		$scope.settings.categories = $scope.getCategories();
		$scope.settings.places = $scope.getPlaces();
		$scope.view = 'settings';
	};

	$scope.addCategory = function() {
		var i = 0;
		for (x in $scope.settings.categories) {
			if ($scope.settings.categories[x].i >= i) i = $scope.settings.categories[x].i + 1;
		}
		$scope.settings.categories.push({n: 'New Category ' + i, t: 'd', i: i});
	}



	$scope.randomHash = function(nChar) {
	    var chars = '0123456789abcdef'.split('');
	    var hex = '';
	    for (var i = 0; i < nChar; i++) {
	        hex += chars[Math.floor(Math.random() * 16)];
	    }
	    return ':' + hex + ':';
	 }

/*
	$scope.addPlace = function() {
	    coords = $scope.coords ? $scope.coords : {longituted: 0, latitude: 0};
	    var place = {n: "Unnamed place", p:[coords.latitude, coords.longitude], i: 100, o: 400, id: $scope.randomHash(32)};
	    $scope.settings.places.push(place);
	}
*/




        $scope.addPlace = function() {
                return $scope.editPlace(null);
        };

        $scope.editPlace = function(_place) {
                var _new = !!_place ? false : true;
                if (!_place) {
		    coords = $scope.coords ? $scope.coords : {longituted: 0, latitude: 0};
		    _place = {n: "", p:[coords.latitude, coords.longitude], i: 100, o: 500, id: $scope.randomHash(32)};
                }
		$scope.designer = {};
                $scope.designer.$place = _place;
                $scope.designer.$new = _new;
		$scope.designer.name = _place.n;
		$scope.designer.position = _place.p + [];
		$scope.designer.inner = _place.i;
		$scope.designer.outer = _place.o;
		$scope.designer.home = !!_place.h;
                //advanced options
                window.designer = $scope.designer;
                $scope.designer.dialog = ngDialog.open({
                        template: 'dialog-edit-place',
                        className: 'ngdialog-theme-default ngdialog-large',
                        closeByDocument: false,
                        disableAnimation: true,
                        scope: $scope
                });
		//we need this trick to get the map to show properly
		$scope.designer.showMap = true;
        };


        $scope.updatePlace = function() {
                var _place = $scope.designer.$place;
                _place.n = $scope.designer.name;
                _place.p = $scope.designer.position;
                _place.i = $scope.designer.inner;
                _place.o = $scope.designer.outer;
		_place.h = !!$scope.designer.home;
		if (_place.i > _place.o) {
		    var x = _place.i;
		    _place.i = _place.o;
		    _place.o = x;
		}
		if (_place.i + 100 >= _place.o) {
		    _place.o = _place.i + 100;
		}
		if ($scope.designer.$new) $scope.settings.places.push(_place);
		if (_place.h) {
		    for (i in $scope.settings.places) {
			$scope.settings.places[i].h = ($scope.settings.places[i] == _place);
		    }
		}
                $scope.closeDialog();
        }



	$scope.movePlace = function($event, circle) {
	    var center = $event ? $event.latLng : this.center;
	    $scope.designer.position = [center.lat(), center.lng()];
	    switch (circle) {
		case 'i': $scope.designer.inner = this.radius; break;
		case 'o': $scope.designer.outer = this.radius; break;
	    }
	    if ($scope.designer.inner > $scope.designer.outer) {
		var x = $scope.designer.inner;
		$scope.designer.inner = $scope.designer.outer;
		$scope.designer.outer = x;
	    }
	    if ($scope.designer.inner < 50) {
		$scope.designer.inner = 50;
	    }
	    if ($scope.designer.inner + 200 >= $scope.designer.outer) {
	        $scope.designer.outer = $scope.designer.inner + 200;
	    }
	}

	$scope.deletePlace = function() {
	    for(var i=0; i<$scope.settings.places.length; i++) {
		if ($scope.settings.places[i] == $scope.designer.$place) {
		    $scope.settings.places.splice(i, 1);
                    $scope.closeDialog();
		    return;
		}
	    }
            $scope.closeDialog();
	};

	$scope.moveCategoryUp = function(index) {
		if ((index < 1) || (index >= $scope.settings.categories)) return;
		var x = $scope.settings.categories[index];
		$scope.settings.categories[index] = $scope.settings.categories[index - 1];
		$scope.settings.categories[index - 1] = x;
	}

	$scope.moveCategoryDown = function(index) {
		if ((index < 0) || (index >= $scope.settings.categories - 1)) return;
		var x = $scope.settings.categories[index];
		$scope.settings.categories[index] = $scope.settings.categories[index + 1];
		$scope.settings.categories[index + 1] = x;
	}

	$scope.deleteCategory = function(index) {
	    $scope.settings.categories.splice(index, 1);
	};

	$scope.hideSettings = function() {
		ga('send', 'event', 'settings', 'hide');
		$scope.view = 'piston';
	}


	$scope.messageHost = function(message, args, cbk) {
	    if (!window.BridgeCommander) return;
	    var platform = window.BridgeCommander.getPlatformName ? window.BridgeCommander.getPlatformName() : 'unknown';
	    switch (platform) {
		case "iOS":
		    window.BridgeCommander.call(message, JSON.stringify(args)).then(function(result) { if (cbk) cbk(result ? JSON.parse(result) : null); });
		    break;
    		case "Android":
		    if (window.BridgeCommander.hasOwnProperty(message)) {
			var result = window.BridgeCommander[message](JSON.stringify(args));
			if (cbk) cbk(result ? JSON.parse(result) : null);
		    }
		    break;
		default:
		    //support for legacy iOS app
		    window.BridgeCommander.subscribe($scope.onAppRequest);
		    window.BridgeCommander.call(message, JSON.stringify(args)).then(function(result) { if (cbk) cbk(result); });
		    break;
	    }
	}

	$scope.checkPresenceSensor = function() {
	    $scope.messageHost('getStatus', {i: $scope.instance.id}, function(result) {	
		$scope.hostDeviceId = result instanceof Object ? (result.dni ? result.dni : '') : '';
		$scope.presenceSensorId = result instanceof Object ? !!result.s : !!result;
	    });
	}


        $scope.registerPresenceSensor = function() {
                $scope.designer.name = '';
                //advanced options
                window.designer = $scope.designer;
                $scope.designer.dialog = ngDialog.open({
                        template: 'dialog-register-presence-sensor',
                        className: 'ngdialog-theme-default ngdialog-large',
                        closeByDocument: false,
                        disableAnimation: true,
                        scope: $scope
                });
        };

	$scope.doRegisterPresenceSensor = function() {
	    var name = $scope.designer.name;
	    $scope.closeDialog();
	    if (!name) return;
	    dataService.createPresenceSensor(name, $scope.hostDeviceId).then(function(data) {
		var deviceId = data.deviceId;
		if (deviceId)
		    $scope.messageHost('register', {e: $scope.rawEndpoint, a: $scope.rawAccessToken, i: $scope.instance.id, d: deviceId}, function(result) { $scope.presenceSensorId = deviceId });
	    });
	}

	$scope.unregisterPresenceSensor = function() {
	    if (!$scope.presenceSensorId) return;
	    dataService.destroyPresenceSensor($scope.presenceSensorId).then(function(data) {
		$scope.messageHost('unregister', {i: $scope.instance.id});
	    });
	}

	$scope.updatePlaces = function() {
	    $scope.messageHost('update', {i: $scope.instance.id, p: $scope.instance.settings.places});
	}

	$scope.saveSettings = function() {
		ga('send', 'event', 'settings', 'save');
		$scope.instance.settings = $scope.settings;
		dataService.setSettings($scope.settings).then(function(data) {
			$scope.instance.settings = $scope.settings;
			$scope.updatePlaces();
			$scope.hideSettings();
		});
	}

	$scope.showFuelStreams = function() {
		ga('send', 'event', 'fuel', 'show');
		$scope.initialized = false;
		$scope.loading = true;
      	$location.path('fuel');
	}

	$scope.showDashboard = function() {
		$scope.view = 'dashboard';
		ga('send', 'event', 'dashboard', 'show');
		dataService.openWebSocket($scope.onWSUpdate);
		$scope.dropDownMenu = false;
		$scope.refreshing = true;
		dataService.refreshDashboard().then(function(data) {
			for (deviceId in data) {
				if (deviceId.startsWith(':')) {
					var device = $scope.instance.devices[deviceId];
					if (device) {
						var attributes = data[deviceId];
						for (attr in attributes) {
							for (i in device.a) {
								if (device.a[i].n == attr) {
									device.a[i].v = attributes[attr];
									break;
								}
							}
						}
						device.data = $scope.getDeviceData(device);
					}
				}
			}
			$scope.refreshing = false;
			$scope.setStatus();
		});
	}

	$scope.hideDashboard = function() {
		ga('send', 'event', 'dashboard', 'hide');
		dataService.closeWebSocket();
		$scope.view = 'piston';
		$scope.dropDownMenu = false;
	}

	$scope.onWheel = function(event) {
		$scope.dropDownMenu = event && event.originalEvent && (event.currentTarget.scrollTop == 0) && (event.originalEvent.deltaY < 0);
		return true;
	}

	$scope.onSwipe = function(event, direction) {
		$scope.dropDownMenu = (event.currentTarget.scrollTop == 0) && (direction == 'down');
		return true;
	}

    $scope.range = function(n) {
        return new Array(n);
    };


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

    $scope.listAvailableDevices = function() {
        var result = [];
        for(deviceIndex in $scope.instance.devices) {
            $scope.instance.devices[deviceIndex].id = deviceIndex;
//			device.id = deviceIndex;
			result.push($scope.instance.devices[deviceIndex]);
//            result.push(mergeObjects({id: deviceIndex, dev: device}, device));
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

    $scope.sortByDisplay = function(a,b) {
        return (a.d > b.d) ? 1 : ((b.d > a.d) ? -1 : 0);
    }

    $scope.sortByName = function(a,b) {
        return (a.n > b.n) ? 1 : ((b.n > a.n) ? -1 : 0);
    }



	$scope.switchInstance = function(instanceId) {
		if (instanceId != $scope.instance.id) {
			var instance = dataService.getInstance(instanceId);
			if (instance) {
				$scope.instance = null;
		        if (tmrActivity) $timeout.cancel(tmrActivity);
				tmrActivity = null;
				$scope.devices = null;
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

	$scope.getDeviceData = function(device) {
		var data = {};
		for(a in device.a) {
			data[device.a[a].n] = device.a[a].v;
		}
		return data;
	}

	$scope.getBatteryLevel = function(level) {
		if (isNaN(level)) return 0;
		level = Math.floor(level / 20);
		if (level <= 0) return 0;
		if (level >= 4) return 4;
		return level;
	}

    $scope.renderString = function(value) {
        return renderString($sce, value);
    };

	$scope.onWSUpdate = function(evt) {
		if (evt.isTrusted && evt.data) {
			try {
				var data = JSON.parse(evt.data);
				if (data.d && data.n) {
					var device = $scope.instance.devices[data.d];
					if (device) {
						for (a in device.a) {
							if (device.a[a].n == data.n) {
								device.a[a].v = data.v;
								device.data = $scope.getDeviceData(device);
								break;
							}
						}
					}
				}
				$scope.$apply();
			} catch (e) {};
		}
	}


	$scope.getDeviceAttribute = function(device, attribute) {
		for (a in device.a) {
			var attr = device.a[a];
			if (attribute == attr.n) return attr.v;
		}
		return '';
	}

    $scope.openPiston = function(id) {
		ga('send', 'event', 'piston', 'view', id);
        $scope.loading = true;
        $scope.initialized = false;
      	$location.path('piston/' + id);
    }

	$scope.newPiston = function() {
		$scope.loading = true;
		return $q.all([
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
					// { type: 'template', name: 'Create a piston from a template', icon: 'code', cssClass: 'wide btn-success' },
					{ type: 'restore', name: 'Restore a piston using a backup code', icon: 'code', cssClass: 'wide btn-success' },
					{ type: 'import', name: 'Import a piston from a backup file', icon: 'code', cssClass: 'wide btn-warning' },
				];
				$scope.designer.dialog = ngDialog.open({
					template: 'dialog-add-piston',
					className: 'ngdialog-theme-default ngdialog-large',
					closeByDocument: false,
					disableAnimation: true,
					scope: $scope
				});
			}),
			dataService.getImportedData().then(function(importedPistons) {
				$scope.importedPistons = importedPistons;
				if (importedPistons) $scope.sortImportedPistons();
			})
		]);
	};
	
	$scope.resumeImport = function() {
		$scope.newPiston().then(function() {
			$scope.designer.page = 1;
			$scope.designer.type = 'import';
		})
	};
	
	if ($rootScope.dashboardResumeImport) {
		delete $rootScope.dashboardResumeImport;
		$scope.resumeImport();
	}
		
	$scope.restoreImportedPiston = function(data) {
		$scope.designer.name = data.meta.name;
		$scope.designer.author = data.meta.author;
		$scope.designer.piston = data.meta.id;
		return $scope.createPiston()
			.then(function(piston) {
				// Mark piston as imported and cycle it to the end of the array
				data.imported = piston.id;
				data.importedAt = Date.now();
				dataService.setImportedData($scope.importedPistons);
			});
	}
	
	$scope.removeImportedPiston = function(pistonId) {
		for (var i = 0; i < $scope.importedPistons.length; i++) {
			if ($scope.importedPistons[i].meta.id === pistonId) {
				$scope.importedPistons.splice(i, 1);
				break;
			}
		}
		return dataService.setImportedData($scope.importedPistons);
	}
	
	$scope.sortImportedPistons = function(order) {
		$scope.importedPistonsSortOrder = $scope.importedPistonsSortOrder || 'safest';
		$scope.importedPistons.sort(function(a, b) { 
			var xorImported = !!a.imported - !!b.imported;
			if (xorImported) return xorImported;
			
			switch (order) {
				case 'category':
					return (a.meta.category || Infinity) - (b.meta.category || Infinity);
				case 'name':
					return a.meta.name.localeCompare(b.meta.name);
				case 'created':
					return b.meta.created - a.meta.created;
				case 'modified':
					return (b.meta.modified || b.meta.created) - (a.meta.modified || a.meta.created);
				default:
					return a.imported ? (a.importedAt - b.importedAt) : (a.warningLevel - b.warningLevel || a.meta.name.localeCompare(b.meta.name))
					break;
			}
		});
	}


	$scope.backup = function() {
		$scope.designer = {
			page: 0,
			pistons: []
		}
   	    $scope.designer.dialog = ngDialog.open({
       	    template: 'dialog-backup-piston',
           	className: 'ngdialog-theme-default ngdialog-large',
            closeByDocument: false,
   	        disableAnimation: true,
       	    scope: $scope,
			onOpenCallback: function() {$scope.$$postDigest(function() {$('select').selectpicker('selectAll');})}
        });
	};

	$scope.backupPistons = function() {
		$scope.designer.progress = 0;
		$scope.designer.page = 1;
		$scope.designer.instances = {};
		for (i in $scope.designer.pistons) {
			var iid = $scope.designer.pistons[i].substr(0, 34);
			var pid = $scope.designer.pistons[i].substr(34);
			$scope.designer.instances[iid] = $scope.designer.instances[iid] ? $scope.designer.instances[iid] : [];
			$scope.designer.instances[iid].push({
				pid: pid,
				requested: false
			});
		}
		$scope.designer.results = [];
		$scope.backupBatch();
	}

	$scope.backupBatch = function() {
		if (!$scope.designer || !$scope.designer.instances) return;
		var iid = '';
		var pids = [];
		for(i in $scope.designer.instances) {
			if (iid != '') break;
			var instance = $scope.designer.instances[i];
			for(p in instance) {
				if (((iid == '') || (iid == i)) && (!instance[p].requested)) {
					iid = i;
					instance[p].requested = true;
					pids.push(instance[p].pid);
					if (pids.length >= 10) break;
				}
			}
		}
		if (pids.length) {
			dataService.backupPistons(iid, pids).then(function(data) {
				if (data && (data.pistons instanceof Array)) {
					$scope.designer.results = $scope.designer.results.concat(data.pistons);
					$scope.designer.progress = $scope.designer.results.length;
				}
				$scope.backupBatch();
			});
		} else {
			//we're done
			if ($scope.designer.pistons.length == $scope.designer.results.length) {
				//success
				$scope.designer.page = 2;
			} else {
				$scope.designer.page = 3;
			}
		}
	}


	$scope.saveBackup = function() {
		var blob = new Blob([dataService.encryptBackup($scope.designer.results, $scope.designer.password)], {type: 'text/plain'});
		var link = document.createElement('a');
		var url = window.URL.createObjectURL(blob);
		link.href = url;
		link.download = 'webCoRE.' + (new Date()).toJSON() + '.backup';
		document.body.appendChild(link);
		link.click();
		setTimeout(function(){
			document.body.removeChild(link);
			window.URL.revokeObjectURL(link.href);
		}, 100);  
		$scope.closeDialog();
	}

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
			setTimeout(function() {
				$location.path("piston/" + data.id).search({description: $scope.designer.description, type: $scope.designer.type, piston: $scope.designer.piston, bin: $scope.designer.bin});
			}, 100);
			return data;
		};
		$scope.loading = true;
		dataService.saveToStore('backup.auto', !!$scope.designer.backup);
		dataService.saveToStore('author.handle', $scope.designer.author);
		if ($scope.designer.backup) {
			return dataService.generateBackupBin().then(function(response) {
				var binId = response.data;
				dataService.createPiston($scope.designer.name, $scope.designer.author, binId).then(success);
			});
		} else {
			return dataService.createPiston($scope.designer.name, $scope.designer.author).then(success);
		}
    };

	$scope.dialogLogIn = function(sender, uri, accessToken) {
		if (tmrActivity) $timeout.cancel(tmrActivity);
		tmrActivity = null;
		$scope.loading = false;
		$scope.initialized = false;
		$scope.designer = {};
		$scope.designer.sender = sender;
		$scope.designer.uri = uri;
		$scope.designer.accessToken = accessToken;
        $scope.designer.dialog = ngDialog.open({
            template: 'dialog-auth',
            className: 'ngdialog-theme-default ngdialog-large',
            closeByDocument: false,
            disableAnimation: true,
            scope: $scope
        });
	}


	$scope.logOut = function() {
		dataService.logout().then(function() {
			$scope.loading = true;
			$scope.initialized = false;
			$location.path('register');
		});
	}


	$scope.onAppRequest = function(payload) {$scope.setStatus(payload)};

	$scope.initAds = function() {
	    if ($scope.isAppHosted) return;
	    window.adsbygoogle = (window.adsbygoogle || []);
	    window.adsbygoogle.push({google_ad_client: "ca-pub-4643048739403893", enable_page_level_ads: true});
	}

	$scope.authenticate = function() {
		$scope.closeDialog();
		$scope.init(null, $scope.designer.uri + ($scope.designer.accessToken ? '?access_token=' + $scope.designer.accessToken : ''), window.md5('pin:' + $scope.designer.password));
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
		
	$scope.loadBackupFile = function() {
		var uploadField = angular.element('#backupFile')[0];
		var file = uploadField.files[0];
		var reader = new window.FileReader();
		var warningConditions = {
			'executes other pistons': { p: /"c":"executePiston"/, l: 20 },
			'may require global variables': { p: /[^\w]@\w/, l: 10 },
			'has tiles': { p: /"c":"setTile/, l: 5 },
			'requires IFTTT': { p: /"c":"iftttMaker"/, l: 2 },
			'expects arguments': { p: /"u":"[^\[]|\$args\./, l: 1 },
			'has devices in expressions': { p: /\[[^\]\{]+:/, l: 1 }
		};
		reader.onload = function(e) {
			var encrypted = e.target.result;
			if (encrypted) {
				var data;
				var password;
				do {
					password = prompt('Backup file password');
					data = dataService.decryptBackup(encrypted, password);
				} while (password !== null && !data);
				
				if (data) {
					var idByName = {};
					for (var i = 0; i < $scope.instance.pistons.length; i++) {
						idByName[$scope.instance.pistons[i].name] = $scope.instance.pistons[i].id;
					}
					for (var i = 0; i < data.length; i++) {
						var json = JSON.stringify(data[i].piston);
						data[i].imported = idByName[data[i].meta.name];
						data[i].warnings = [];
						data[i].warningLevel = 0;
						for (var warning in warningConditions) {
							if (warningConditions[warning].p && warningConditions[warning].p.test(json)) {
								data[i].warnings.push(warning);
								data[i].warningLevel += warningConditions[warning].l;
							}
						}
					}
					
					dataService.setImportedData(data);
					$scope.canResumeImport = true;
					$scope.importedPistons = data;
					$scope.sortImportedPistons();
				}
			}
		};
		reader.readAsText(file);
	};
	
	$scope.restartPistonImport = function() {
		dataService.clearImportedData();
		$scope.importedPistons = null;
		$scope.canResumeImport = false;
	};


	$scope.getOpacity = function(time) {
		if (!time) return 0;
		time = currentTime() - time;
		if ((time < 0) || (time > 60000)) return 0;
		return 1.0 - time / 60000.0;
	}

	$scope.getLocationMode = function() {
		var mode = $scope.location.mode;
		for(var i=0; i < $scope.location.modes.length; i++) {
			if ($scope.location.modes[i].id == mode) return $scope.location.modes[i].name;			
		}
		return '(unknown)';
	}

	$scope.getSHMModeName = function() {
		switch ($scope.location.shm) {
			case 'away': return 'ARMED (AWAY)';
			case 'stay': return 'ARMED (HOME)';
			case 'off': return 'DISARMED';
		}
		return '(unknown)';
	}


	$scope.renderIncident = function(incident) {
		result = '<span date>' + $scope.utcToString(incident.date) + '</span> - <span title>';
		result += incident.message.replace(/\{\{(.*)\}\}/gi, function(match) {
            return incident.args[match.substr(2, match.length - 4).trim()];
        });
		result += '</span>';
		return $sce.trustAsHtml(result);
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


    $scope.pausePiston = function(pistonId) {
        $scope.loading = true;
        dataService.pausePiston(pistonId).then(function(data) {
            $scope.init();
        });
    }

    $scope.resumePiston = function(pistonId) {
        $scope.loading = true;
        dataService.resumePiston(pistonId).then(function(data) {
            $scope.init();
        });
    }

    $scope.testPiston = function(pistonId) {
        dataService.testPiston(pistonId);
    }

	$scope.determineDeviceType = function(device) {
		return dataService.determineDeviceType(device);
	};

	$scope.initSocialMedia = function() {
		$window.FB.XFBML.parse();
	};
	
	$scope.toggleSidebar = function() {
		$scope.sidebarCollapsed = !$scope.sidebarCollapsed;
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
	var tmrInit = setInterval(function() {
		if (dataService.ready()) {
			clearInterval(tmrInit);
			$scope.init();
		}
	}, 1);

	if (navigator.geolocation) {
	    navigator.geolocation.getCurrentPosition($scope.updateLocation);
	}

}]);
