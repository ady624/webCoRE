var app = angular.module('CoRE', ['ng', 'ngRoute', 'ngAnimate', 'ngSanitize', 'ngResource', 'ngDialog', 'angular-loading-bar', 'angular-svg-round-progressbar', 'angular-bootstrap-select', 'aCKolor', 'dndLists', 'ui.toggle', 'chart.js', 'smartArea']);
//var cdn = 'https://core.homecloudhub.com/dashboard/';
var cdn = '';
var theme = '';


app.directive('head', ['$rootScope', '$compile',
    function ($rootScope, $compile) {
        return {
            restrict: 'E',
            link: function (scope, elem) {
                var html = '<link rel="stylesheet" ng-repeat="(routeCtrl, cssUrl) in routeStyles" ng-href="{{cssUrl}}" />';
                elem.append($compile(html)(scope));
                scope.routeStyles = {};
                $rootScope.$on('$routeChangeStart', function (e, next, current) {
                    if (current && current.$$route && current.$$route.css) {
                        if (!angular.isArray(current.$$route.css)) {
                            current.$$route.css = [current.$$route.css];
                        }
                        angular.forEach(current.$$route.css, function (sheet) {
                            delete scope.routeStyles[sheet];
                        });
                    }
                    if (next && next.$$route && next.$$route.css) {
                        if (!angular.isArray(next.$$route.css)) {
                            next.$$route.css = [next.$$route.css];
                        }
                        angular.forEach(next.$$route.css, function (sheet) {
                            scope.routeStyles[sheet] = sheet;
                        });
                    }
                });
            }
        };
    }
]);



app.directive('textcomplete', ['Textcomplete', function(Textcomplete) {
    return {
        restrict: 'EA',
        scope: {
            members: '=',
            message: '=',
			callback: '&'
        },
        template: '<textarea ng-model="message" type="text"></textarea>',
        link: function(scope, iElement, iAttrs) {

            var mentions = scope.members;
            var ta = iElement.find('textarea');
            var textcomplete = new Textcomplete(ta, [
              {
//                match: /(^|\s)@(\w*)$/,
				match: /(\b)(\w{2,})$/,
                search: function(term, callback) {
                    callback($.map(mentions, function(mention) {
                        return mention.toLowerCase().indexOf(term.toLowerCase()) === 0 ? mention : null;
                    }));
                },
                index: 2,
                replace: function(mention) {					
                    return '$1' + mention + ' ';
                }
              }
            ]);

			if (scope.callback) {
				scope.$watch('message', function(newValue, oldValue) {
					scope.callback();
				});
			}
            $(textcomplete).on({
              'textComplete:select': function (e, value) {
                scope.$apply(function() {
                  scope.message = value;
                });
              },
              'textComplete:show': function (e) {
                $(this).data('autocompleting', true);
              },
              'textComplete:hide': function (e) {
                $(this).data('autocompleting', false);
              }
            });
        }
    }
}]);


app.filter('orderObjectBy', function() {
  return function(items, field, reverse) {
    var filtered = [];
    angular.forEach(items, function(item, key) {
      filtered.push(Object.assign({id: key}, item));
    });
    filtered.sort(function (a, b) {
      return (a[field] > b[field] ? 1 : -1);
    });
    if(reverse) filtered.reverse();
    return filtered;
  };
});



var config = app.config(['$routeProvider', '$locationProvider', '$sceDelegateProvider', 'cfpLoadingBarProvider', '$rootScopeProvider',  function ($routeProvider, $locationProvider, $sceDelegateProvider, $cfpLoadingBarProvider, $rootScopeProvider) {
	$rootScopeProvider.digestTtl(100); 
	$cfpLoadingBarProvider.includeSpinner = false;
    var ext = '.module.css';
    $sceDelegateProvider.resourceUrlWhitelist([
        'self',
        cdn + '**'
    ]);
    $routeProvider.
    when('/', {
        templateUrl: cdn + theme + 'html/modules/dashboard.module.html',
        controller: 'dashboard',
        css: cdn + theme + 'css/modules/dashboard' + ext
    }).
    when('/init/:init', {
        redirectTo: function(params) {
			app.initialInstanceUri = atou(params.init);
			return '/';
		}
    }).
    when('/piston/:pistonId', {
        templateUrl: cdn + theme + 'html/modules/piston.module.html',
        controller: 'piston',
        css: cdn + theme + 'css/modules/piston' + ext,
		reloadOnSearch: false
    }).
    when('/init/:instId1/:instId2', {
        redirectTo: function(params) {
			app.initialInstanceUri = params.instId1 + params.instId2;
			return '/';
		}
    }).
    otherwise({
        redirectTo: '/'
    });
    $locationProvider.html5Mode(true);
}]);



/*
config.factory('$exceptionHandler',
  function() {
    return function(exception, cause) {
      exception.message += 'Angular Exception: "' + cause + '"';
      alert(exception);
    };
  }
);
*/


config.factory('dataService', ['$http', '$location', '$rootScope', '$window', '$q', function ($http, $location, $rootScope, $window, $q) {
    var dataService = {};
	var initialInstanceUri = '';
	var location = null;
	var locations = {};
	var instance = null;
	var instances = {};
	var store = {};
	var _dk = 'N7zqL6a8Texs4wY5y&y2YPLzus+_dZ%s';
	var _ek = _dk;
	var cbkStatus = null;

    var dejsonify = function (data) {
        //eval('data = ' + data);
        //return data;
		return JSON.parse(data);
    };

    var padLeft = function (nr, n, str) {
        return Array(n - String(nr).length + 1).join(str || '0') + nr;
    }

    var formatDate = function (date) {
        return padLeft(date.getFullYear(), 4) + '-' +
            padLeft(1 + date.getMonth(), 2) + '-' +
            padLeft(date.getDate(), 2) + ' ' +
            padLeft(date.getHours(), 2) + ':' +
            padLeft(date.getMinutes(), 2) + ':' +
            padLeft(date.getSeconds(), 2);
    }

    var serialize = function (obj) {
		return JSON.stringify(obj);
    };


   	var encryptObject = function(obj, ek) {
        try {
            return utoa($window.sjcl.encrypt(ek ? ek : _ek, angular.toJson(obj), {ks: 256}));
        } catch (e) {
            return null;
        }
    };

    var decryptObject = function(data, ek) {
        try {
            return angular.fromJson($window.sjcl.decrypt(ek ? ek : _ek, atou(data)));
        } catch (e) {
            return null;
        }
    };

    var writeObject = function(key, obj, ek) {
        $window.localStorage.setItem('core.' + key, encryptObject(obj, ek));
    }

    var readObject = function(key, ek) {
        return decryptObject($window.localStorage.getItem('core.' + key), ek);
	}

	var setLocation = function(loc) {
		location = loc;
		locations[location.id] = location;
		writeObject('locations', locations);
	};

	var setInstance = function(inst) {
		instance = inst;
		//preserve the token, unless a new one is given
		var si = store[instance.id];
		if (!si) si = {};
		si.token = instance.token ? instance.token : si.token;
		si.uri = instance.uri ? instance.uri.replace(':443', '') : si.uri;
		store[instance.id] = si;
		delete(instance.token);
		delete(instance.uri);
		instance.devices = instance.devices ? instance.devices : (instances[instance.id] && instances[instance.id].devices ? instances[instance.id].devices : []);
		instance.virtualDevices = instance.virtualDevices || {};
		instances[instance.id] = instance;
		writeObject('instances', instances);
		writeObject('store', store);
		writeObject('instance', instance.id, _dk);
		if ((instance.coreVersion) && (version() > instance.coreVersion)) {
			status('A newer SmartApp version (' + version() + ') is available, please update and publish both the parent and the child SmartApp in the SmartThings IDE.');
		}
	};

	var status = function(status) {
		if (cbkStatus) cbkStatus(status);
	}

	dataService.setStatusCallback = function(cbk) {
		cbkStatus = cbk;
	}

	dataService.saveToStore = function(key, value) {
		return writeObject(key, value);
	};

	dataService.loadFromStore = function(key) {
		return readObject(key);
	};

	dataService.deleteInstance = function(inst) {
		if (inst) {
			if (inst == instance) {
				instance = null;
				writeObject('instance', null, _dk);
			}
			delete(store[inst.id]);
			delete(instances[inst.id]);
			writeObject('instances', instances);
			writeObject('store', store);
		}
	};

	dataService.listLocations = function () {
		var result = [];
		for(lid in locations) {
			result.push(JSON.parse(JSON.stringify(locations[lid])));
		}
		return result;
	};

	dataService.getLocation = function (locationId) {
		if (locationId) {
			for(lid in locations) {
				if (lid == locationId) {
					return JSON.parse(JSON.stringify(locations[lid]));
				}
			}
		} else {
			return JSON.parse(JSON.stringify(location));
		}
		return null;
	};

	dataService.listInstances = function (locationId) {
		var result = [];
		for(iid in instances) {
			if (!locationId || (instances[iid].locationId == locationId)) {
				result.push(JSON.parse(JSON.stringify(instances[iid])));
			}
		}
		return result;
	};

	dataService.getInstance = function (instanceId) {
		if (instanceId) {
			for(iid in instances) {
				if (iid == instanceId) {
					return JSON.parse(JSON.stringify(instances[iid]));
				}
			}
		} else {
			try {
				return JSON.parse(JSON.stringify(instance ? instance : (instances? instances[readObject('instance')] : null)));
			} catch(e) {}
		}
		return null;
	};

	dataService.getPistonInstance = function (pistonId) {
		for(iid in instances) {
			for (i in instances[iid].pistons) {
				if (instances[iid].pistons[i].id == pistonId) {
					return JSON.parse(JSON.stringify(instances[iid]));
				}
			}
		}
		return null;
	}

	dataService.loadInstance = function(inst, uri, pin) {
		var si = inst ? store[inst.id] : null;
		var deviceVersion = !inst || !(inst.devices instanceof Object ) || !(Object.keys(inst.devices).length) ? 0 : (inst.deviceVersion ? inst.deviceVersion : 0);
		if (!si) {
			if ((app.initialInstanceUri && app.initialInstanceUri.length) || (uri && uri.length)) {
				uri = app.initialInstanceUri ? app.initialInstanceUri : uri;
				if (uri && !(uri instanceof Object) && (uri.length >= 69)) {
					uri = uri.substr(0, 8) == 'https://' ? uri : 'https://' + uri.substr(0, uri.length - 64) + '.api.smartthings.com/api/token/' + uri.substr(-64, 8) + '-' + uri.substr(-56, 4) + '-' + uri.substr(-52, 4) + '-' + uri.substr(-48, 4) + '-' + uri.substr(-44, 12) +  '/smartapps/installations/' + uri.substr(-32, 8) + '-' + uri.substr(-24, 4) + '-' + uri.substr(-20, 4) + '-' + uri.substr(-16, 4) + '-' + uri.substr(-12) + '/';
				}
				si = {uri: uri};
				for(id in store) {
					if (store[id].uri == uri) {
						si = store[id];
						if (instances && instances[id] && instances[id].devices instanceof Object && Object.keys(instances[id].devices).length && instances[id].deviceVersion) deviceVersion = instances[id].deviceVersion;
						break;
					}
				}
			}
		}
		delete(app.initialInstanceUri);
		if (!si) {
			var iid = readObject('instance');
			if (iid) {
				si = store[iid];
				if (instances && instances[iid] && instances[iid].devices instanceof Object && Object.keys(instances[iid].devices).length && instances[iid].deviceVersion) deviceVersion = instances[iid].deviceVersion;
			}
		}
		if (!si) {
			if (mobileCheck()) {
				msg = '<p>Oops, you are not logged in!</p>Please open the SmartThings CoRE app and open the dashboard by tapping on the "CoRE Dashboard" link at the top.';
			} else {
				msg = '<p>Oops, you are not logged in!</p>Please open the SmartThings IDE, go to Live Logging, then open the SmartThings CoRE app, go to Settings and enable Logging, then tap Done and look for the dashboard URL in the Live Logging page. You will need to open that link for the first time.';
			}
			var error = document.createElement('error');
			error.id = 'error';
			error.innerHTML = msg;
			document.body.appendChild(error);
			inst = 'about:blank#';
		} else {
			var error = document.getElementById('error');
			if (error) error.parentNode.removeChild(error);
		}
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/load?callback=JSON_CALLBACK&token=' + (si && si.token ? si.token : '') + (pin ? '&pin=' + pin : '') + '&dev=' + deviceVersion)
			.success(function(data) {
				if (data.now) {
					adjustTimeOffset(data.now);
				}
				if (data.error && si) {
					data.uri = si.uri ;
				}
				if (data.location) {
					setLocation(data.location);
				}
				if (data.instance) {
					setInstance(data.instance);
				}				
				return data;
			});
    };

    dataService.tap = function (tapId) {
        return $http({
            method: 'GET',
            url: 'tap/' + tapId
        });
    }


    dataService.getPiston = function (pistonId) {
		var inst = dataService.getPistonInstance(pistonId);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		var deviceVersion = !inst || !(inst.devices instanceof Object ) || !(Object.keys(inst.devices).length) ? 0 : (inst.deviceVersion ? inst.deviceVersion : 0);
        var dbVersion = readObject('db.version', _dk);
		status('Loading piston...');
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/get?callback=JSON_CALLBACK&id=' + pistonId + '&db=' + dbVersion + '&token=' + (si && si.token ? si.token : '') + '&dev=' + deviceVersion)
			.success(function(data) {
				if (data.now) {
					adjustTimeOffset(data.now);
				}
				if (data.dbVersion) {
					writeObject('db.version', data.dbVersion, _dk);
					writeObject('db', data.db);
					status('Database updated to version ' + data.dbVersion);
				} else {
					data.db = readObject('db');
					status();
				}
				if (data.location) {
					setLocation(data.location);
				}
				if (data.instance) {
					setInstance(data.instance);
				}
				return data;
			});
    }

    dataService.getActivity = function (pistonId, lastLogTimestamp) {
		var inst = dataService.getPistonInstance(pistonId);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/activity?callback=JSON_CALLBACK&id=' + pistonId + '&log=' + (lastLogTimestamp ? lastLogTimestamp : 0) + '&token=' + (si && si.token ? si.token : ''))
			.success(function(data) {
				return data;
			});
    }

    dataService.generateBackupBin = function (data, public) {
		var inst = dataService.getInstance();
        return $http({
            method: 'POST',
            url: 'https://api.myjson.com/bins',
            data: data ? (public ? {d: encryptObject(data, _dk)} : {e: encryptObject(data, _dk + inst.account.id)}) : {},
            transformResponse: function(data) {
				try {
					data = JSON.parse(data);
					if (data && data.uri) {
						data = data.uri.split('/');
						if (data && data.length) {
							return data[data.length-1];
						}
					}
				} catch(e) {}
				return null;
			}
        });
    }

    dataService.saveToBin = function (binId, data) {
		status('Saving piston to backup bin...');
		var inst = dataService.getInstance();
		if (inst && inst.account && inst.account.id) {
			data = {e: encryptObject(data, _dk + inst.account.id)};
		} else {
			data = {};
			binId = null;
		}
        return $http({
            method: 'PUT',
            url: 'https://api.myjson.com/bins/' + binId,
            data: data,
            transformResponse: function(data) {
				status('Backup bin updated');
				return true;
			}
        });
    }

    dataService.loadFromBin = function (binId) {
		status('Loading piston from backup bin...');
		var inst = dataService.getInstance();
		if (!(inst && inst.account && inst.account.id)) {
			binId = null;
		}
        return $http({
            method: 'GET',
            url: 'https://api.myjson.com/bins/' + binId,
            transformResponse: function(data) {
				if (binId) {
					try {
						data = JSON.parse(data);
						if (data && data.e) {
							return decryptObject(data.e, _dk + inst.account.id);
						}
						if (data && data.d) {
							return decryptObject(data.d, _dk);
						}
						status();
					} catch(e) {
						status('Sorry, an error occurred while importing the backup bin');
					};
				}
				return null;
			}
        });
    }



    dataService.generateNewPistonName = function () {
		var inst = dataService.getInstance();
		si = store ? store[inst.id] : null;
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/new?callback=JSON_CALLBACK&token=' + (si && si.token ? si.token : ''))
			.success(function(data) {
				return data;
			});
    }

    dataService.createPiston = function (name, author, backupBin) {
		var inst = dataService.getInstance();
		si = store ? store[inst.id] : null;
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/create?callback=JSON_CALLBACK&author=' + encodeURIComponent(author) + '&name=' + encodeURIComponent(name) + '&bin=' + encodeURIComponent(backupBin ? backupBin : '')  +'&token=' + (si && si.token ? si.token : ''))
			.success(function(data) {
				return data;
			});
    }



	var setPistonChunk = function(si, chunks, chunk, binId) {
		if (chunk < chunks.length) {
			status('Saving piston chunk ' + (chunk + 1).toString() + ' of ' + chunks.length.toString() + '...');
	    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/set.chunk?callback=JSON_CALLBACK&chunk=' + chunk.toString() + '&data=' + encodeURIComponent(chunks[chunk]) + '&token=' + (si && si.token ? si.token : ''))
				.then(function(response) {
					return setPistonChunk(si, chunks, chunk + 1, binId);
				});
		} else {
			status('Finishing up...');
	    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/set.end?callback=JSON_CALLBACK' + '&bin=' + encodeURIComponent(binId) + '&token=' + (si && si.token ? si.token : ''))
				.then(function(response) {
					status();
					return response;
				});
		}
	}
 
    dataService.setPiston = function (piston, binId) {
		var inst = dataService.getPistonInstance(piston.id);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		var data = utoa(angular.toJson(piston));
		var maxChunkSize = 1800;
		if (piston && binId) {
			dataService.saveToBin(binId, piston);
		}
		if (data.length > maxChunkSize) {
			//var chunks = data.match(/.{1,maxChunkSize}/g);
			var chunks = [].concat.apply([],data.split('').map(function(x,i){ return i%maxChunkSize ? [] : data.slice(i,i+maxChunkSize) }, data));
			status('Preparing to save chunked piston...');
	    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/set.start?callback=JSON_CALLBACK&id=' + piston.id + '&chunks=' + chunks.length.toString() + '&token=' + (si && si.token ? si.token : ''))
				.then(function(response) {
					if (response && (response.status == 200) && response.data && (response.data.status == 'ST_READY')) {
						return setPistonChunk(si, chunks, 0, binId);
					}
				});
		} else {
			status('Saving piston...');
	    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/set?callback=JSON_CALLBACK&id=' + piston.id + '&data=' + data + '&bin=' + encodeURIComponent(binId) + '&token=' + (si && si.token ? si.token : ''))
				.then(function(response) {
					status();
					return response;
				});
		}
    }


    dataService.pausePiston = function (pid) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		status('Pausing piston...');
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/pause?callback=JSON_CALLBACK&id=' + pid + '&token=' + (si && si.token ? si.token : ''))
			.success(function(data) {
				status();
				return data;
			});
    }

    dataService.resumePiston = function (pid) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		status('Resuming piston...');
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/resume?callback=JSON_CALLBACK&id=' + pid + '&token=' + (si && si.token ? si.token : ''))
			.success(function(data) {
				status();
				return data;
			});
    }

    dataService.deletePiston = function (pid) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/delete?callback=JSON_CALLBACK&id=' + pid + '&token=' + (si && si.token ? si.token : ''))
			.success(function(data) {
				return data;
			});
    }

    dataService.evaluateExpression = function (pid, expression, dataType) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		var data = utoa(angular.toJson(expression));
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/evaluate?callback=JSON_CALLBACK&id=' + pid + '&expression=' + data + '&dataType=' + (dataType ? encodeURIComponent(dataType) : '') + '&token=' + (si && si.token ? si.token : ''))
			.success(function(data) {
				return data;
			});
    }
	//initialize store
	store = readObject('store');
	if (!store) {
		store = {};
	}

	//initialize locations
	locations = readObject('locations');
	if (!locations) {
		locations = {};
	}

	//initialize instances
	instances = readObject('instances');
	if (!instances) {
		instances = {};
	}

	window.ds = dataService;
    return dataService;
}]);






app.run(['$rootScope', '$window', function($rootScope, $window) {
    $rootScope.getTime = function (date) {
        if (date) {
            return date.format('h:mmtt');                
        }
    };

	$rootScope.hasLocalStorage = false;
	try {
		$window.localStorage.setItem('__initialization__test__', true);
		$rootScope.hasLocalStorage = true;
		$window.localStorage.removeItem('__initialization__test__');
	} catch(e) {
		console.log("ERROR: " + e);
	}

    $rootScope.bytesToSize = function(bytes) {
        var sizes = ['bytes', 'kB', 'MB', 'GB', 'TB'];
        if (bytes == 0) return '0 Byte';
        var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
        return (bytes / Math.pow(1024, i)).toFixed(i == 0 ? 0 : 2) + ' ' + sizes[i];
    };
}]);


//utils

Date.prototype.format = function(format, utc) {
    var MMMM = ["\x00", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    var MMM = ["\x01", "Jan.", "Feb.", "Mar.", "Apr.", "May", "June", "July", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."];
    var dddd = ["\x02", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    var ddd = ["\x03", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

    function ii(i, len) {
        var s = i + "";
        len = len || 2;
        while (s.length < len) s = "0" + s;
        return s;
    }

    var y = utc ? this.getUTCFullYear() : this.getFullYear();
    format = format.replace(/(^|[^\\])yyyy+/g, "$1" + y);
    format = format.replace(/(^|[^\\])yy/g, "$1" + y.toString().substr(2, 2));
    format = format.replace(/(^|[^\\])y/g, "$1" + y);

    var M = (utc ? this.getUTCMonth() : this.getMonth()) + 1;
    format = format.replace(/(^|[^\\])MMMM+/g, "$1" + MMMM[0]);
    format = format.replace(/(^|[^\\])MMM/g, "$1" + MMM[0]);
    format = format.replace(/(^|[^\\])MM/g, "$1" + ii(M));
    format = format.replace(/(^|[^\\])M/g, "$1" + M);

    var d = utc ? this.getUTCDate() : this.getDate();
    format = format.replace(/(^|[^\\])dddd+/g, "$1" + dddd[0]);
    format = format.replace(/(^|[^\\])ddd/g, "$1" + ddd[0]);
    format = format.replace(/(^|[^\\])dd/g, "$1" + ii(d));
    format = format.replace(/(^|[^\\])d/g, "$1" + d);

    var H = utc ? this.getUTCHours() : this.getHours();
    format = format.replace(/(^|[^\\])HH+/g, "$1" + ii(H));
    format = format.replace(/(^|[^\\])H/g, "$1" + H);

    var h = H > 12 ? H - 12 : H == 0 ? 12 : H;
    format = format.replace(/(^|[^\\])hh+/g, "$1" + ii(h));
    format = format.replace(/(^|[^\\])h/g, "$1" + h);

    var m = utc ? this.getUTCMinutes() : this.getMinutes();
    format = format.replace(/(^|[^\\])mm+/g, "$1" + ii(m));
    format = format.replace(/(^|[^\\])m/g, "$1" + m);

    var s = utc ? this.getUTCSeconds() : this.getSeconds();
    format = format.replace(/(^|[^\\])ss+/g, "$1" + ii(s));
    format = format.replace(/(^|[^\\])s/g, "$1" + s);

    var f = utc ? this.getUTCMilliseconds() : this.getMilliseconds();
    format = format.replace(/(^|[^\\])fff+/g, "$1" + ii(f, 3));
    f = Math.round(f / 10);
    format = format.replace(/(^|[^\\])ff/g, "$1" + ii(f));
    f = Math.round(f / 10);
    format = format.replace(/(^|[^\\])f/g, "$1" + f);

    var T = H < 12 ? "AM" : "PM";
    format = format.replace(/(^|[^\\])TT+/g, "$1" + T);
    format = format.replace(/(^|[^\\])T/g, "$1" + T.charAt(0));

    var t = T.toLowerCase();
    format = format.replace(/(^|[^\\])tt+/g, "$1" + t);
    format = format.replace(/(^|[^\\])t/g, "$1" + t.charAt(0));

    var tz = -this.getTimezoneOffset();
    var K = utc || !tz ? "Z" : tz > 0 ? "+" : "-";
    if (!utc) {
        tz = Math.abs(tz);
        var tzHrs = Math.floor(tz / 60);
        var tzMin = tz % 60;
        K += ii(tzHrs) + ":" + ii(tzMin);
    }
    format = format.replace(/(^|[^\\])K/g, "$1" + K);

    var day = (utc ? this.getUTCDay() : this.getDay()) + 1;
    format = format.replace(new RegExp(dddd[0], "g"), dddd[day]);
    format = format.replace(new RegExp(ddd[0], "g"), ddd[day]);

    format = format.replace(new RegExp(MMMM[0], "g"), MMMM[M]);
    format = format.replace(new RegExp(MMM[0], "g"), MMM[M]);

    format = format.replace(/\\(.)/g, "$1");

    return format;
};


function formatTime(time) {
	try {
		var timestamp = (new Date(time)).getTime() + (window.timeOffset ? window.timeOffset: 0);
		var d = new Date(timestamp);
		return d.format("h:mm TT")
	} catch(e) {
	}
}


function currentTime() {
	return (new Date()).getTime() + (window.timeOffset ? window.timeOffset: 0);
}



function utcToString(timestamp) {
	return (new Date(timestamp)).toLocaleString();
}

function timeSince(time){
	if (!time) return "never";
	switch (typeof time) {
	    case 'number': break;
	    case 'string': time = +new Date(time); break;
	    case 'object': if (time.constructor === Date) time = time.getTime(); break;
	    default: time = +new Date();
	}
	var time_formats = [
	    [60, 'seconds', 1], // 60
	    [120, '1 minute ago', '1 minute from now'], // 60*2
	    [3600, 'minutes', 60], // 60*60, 60
	    [7200, '1 hour ago', '1 hour from now'], // 60*60*2
	    [86400, 'hours', 3600], // 60*60*24, 60*60
	    [172800, 'yesterday', 'tomorrow'], // 60*60*24*2
	    [604800, 'days', 86400], // 60*60*24*7, 60*60*24
	    [1209600, 'last week', 'next week'], // 60*60*24*7*4*2
	    [2419200, 'weeks', 604800], // 60*60*24*7*4, 60*60*24*7
	    [4838400, 'last month', 'next month'], // 60*60*24*7*4*2
	    [29030400, 'months', 2419200], // 60*60*24*7*4*12, 60*60*24*7*4
	    [58060800, 'last year', 'next year'], // 60*60*24*7*4*12*2
	    [2903040000, 'years', 29030400], // 60*60*24*7*4*12*100, 60*60*24*7*4*12
	    [5806080000, 'last century', 'next century'], // 60*60*24*7*4*12*100*2
	    [58060800000, 'centuries', 2903040000] // 60*60*24*7*4*12*100*20, 60*60*24*7*4*12*100
	];
	var seconds = (+new Date() + (window.timeOffset ? window.timeOffset : 0) - time) / 1000,
	    token = 'ago', list_choice = 1;
	
	if (seconds == 0) {
	    return 'Just now'
	}
	if (seconds < 0) {
	    seconds = Math.abs(seconds);
	    token = 'from now';
	    list_choice = 2;
	}
	var i = 0, format;
	while (format = time_formats[i++])
	    if (seconds < format[0]) {
	        if (typeof format[2] == 'string')
	            return format[list_choice];
	        else
	            return Math.floor(seconds / format[2]) + ' ' + format[1] + ' ' + token;
	    }
	return time;
}

function timeCounter(time) {
	time += window.timeOffset ? window.timeOffset: 0;
	var diff = Math.ceil((0.0 + time - (new Date().getTime())) / 1000.0);
	var pastDue = false
	if (diff <= 0) {
		if (diff > -20) {
			return "pending";
		}
		pastDue = true
		diff = -diff;
	}
	var result = "";
	if (diff > 86400) {
		result = Math.floor(diff / 86400).toString() + "d ";
		diff = diff % 86400;
	}
	var h = Math.floor(diff / 3600);
	var m = Math.floor((diff - h * 3600) / 60);
	var s = diff % 60;
	result += (h > 0 ? (h < 10 ? "0" : "") + h.toString() + ":" : "") + (m < 10 ? "0" : "") + m.toString() + ":" + (s < 10 ? "0" : "") + s.toString();// + (pastDue ? " past due" : "");
	return result;
}

function timeLeft(time, unit) {
	if (!time) return 0;
	time += window.timeOffset ? window.timeOffset: 0;
	var diff = Math.round((time - (new Date().getTime())) / 1000);
	switch (unit) {
		case 'h': return Math.floor(diff / 3600); break;
		case 'm': return diff >= 3600 ? 60 : Math.floor(diff / 60); break;
		case 's': return diff >= 60 ? 60 : Math.floor(diff % 60); break;
	}
	return diff;
}

function adjustTimeOffset(time) {
	var offset = time - (new Date()).getTime();
	if (isNaN(window.timeOffset) || (Math.abs(offset) < Math.abs(window.timeOffset))) {
		window.timeOffset = offset;
	}
}
//document.addEventListener('touchstart', handleTouchStart, false);        
//document.addEventListener('touchmove', handleTouchMove, false);

//var xDown = null;                                                        
//var yDown = null;                                                        

//function handleTouchStart(evt) {                                         
//    xDown = evt.touches[0].clientX;                                      
//    yDown = evt.touches[0].clientY;                                      
//};                                                

//function handleTouchMove(evt) {
//    if ( ! xDown || ! yDown ) {
//        return;
//    }
//
//    var xUp = evt.touches[0].clientX;                                    
//    var yUp = evt.touches[0].clientY;
//
//    var xDiff = xDown - xUp;
//    var yDiff = yDown - yUp;
//
//    if ( Math.abs( xDiff ) > Math.abs( yDiff ) ) {/*most significant*/
//        if ( xDiff > 0 ) {
//            /* left swipe */
//            if (window.onSwipeLeft) window.onSwipeLeft();
//        } else {
//            if (window.onSwipeRight) window.onSwipeRight();
//            /* right swipe */
//        }                       
//    } else {
//        if ( yDiff > 0 ) {
//            /* up swipe */ 
//            if (window.onSwipeUp) window.onSwipeUp();
//        } else { 
//            /* down swipe */
//            if (window.onSwipeDown) window.onSwipeDown();
//        }                                                                 
//    }
//    /* reset values */
//    xDown = null;
//    yDown = null;                                             
//};




//$(document).ready(function () {
//	$('.selectpicker').selectpicker('refresh');
//});


/* prototypes */
Object.defineProperty(Array.prototype, "unique", {
	enumerable: false,
	value: function() {
		if (!this) return [];
		var u = {}, a = [];
		for(var i = 0, l = this.length; i < l; ++i){
			if(u.hasOwnProperty(this[i])) continue;
			a.push(this[i]);
			u[this[i]] = 1;
		}
		return a;
	}
});


function mobileCheck() {
  var check = false;
  (function(a){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4))) check = true;})(navigator.userAgent||navigator.vendor||window.opera);
  return check;
};

function mobileOrTabletCheck() {
  var check = false;
  (function(a){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino|android|ipad|playbook|silk/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4))) check = true;})(navigator.userAgent||navigator.vendor||window.opera);
  return check;
};

function initBootstrapSelect() {
	$('select').selectpicker();
}


window.onerror = function myErrorHandler(errorMsg, url, lineNumber) {
    alert("Error occured: " + errorMsg + ' at ' + url + ' line ' + lineNumber);//or any message
    return false;
}

function mergeObjects(obj1, obj2) {
	if (Object.assign) return Object.assign(obj1, obj2);
	var result = {};
	if (obj1 instanceof Object) for (var attrname in obj1) { result[attrname] = obj1[attrname]; }
	if (obj2 instanceof Object) for (var attrname in obj2) { result[attrname] = obj2[attrname]; }
	return result;
};

function utoa(str) {
    return window.btoa(unescape(encodeURIComponent(str)));
}
// base64 encoded ascii to ucs-2 string
function atou(str) {
    return decodeURIComponent(escape(window.atob(str)));
}
//window.navigator.registerProtocolHandler('web+core','https://graph.api.smartthings.com/api/token//smartapps/installations//dashboard#/handler/%s', 'CoRE');

document.documentElement.addEventListener('touchstart', function (event) {
    if (event.touches.length > 1) {
        event.preventDefault();
    }
}, false);


function copyToClipboard(containerId) {
if (document.selection) { 
    var range = document.body.createTextRange();
    range.moveToElementText(document.getElementById(containerId));
    range.select().createTextRange();
    document.execCommand("Copy"); 

} else if (window.getSelection) {
    var range = document.createRange();
     range.selectNode(document.getElementById(containerId));
     window.getSelection().addRange(range);
     document.execCommand("Copy");
}}

version = function() { return 'v0.0.043.20170317'; }