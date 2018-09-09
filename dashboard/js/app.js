var app = angular.module('webCoRE', ['ng', 'ngRoute', 'ngSanitize', 'ngResource', 'ngDialog', 'ngAnimate', 'angular-svg-round-progressbar', 'angular-bootstrap-select', 'swipe', 'dndLists', 'ui.toggle', 'chart.js', 'smartArea', 'ui.bootstrap.contextMenu', 'ngFitText', 'googlechart', 'ngMap', 'monospaced.elastic']);
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


app.directive('ngWheel', ['$parse', function($parse) {
		return function(scope, element, attr) {
			var fn = $parse(attr.ngWheel);
			element.bind('wheel', function(event) {
				scope.$apply(function() {
					fn(scope, {
						$event: event
					});
				});
			});
		};
	}]);


app.directive('refresh',['$interval', function($interval){
		var refreshTime_=0;
		var onRefresh_=null;
		var iv_=null;
		return {
			restrict:'A',
			link:function(scope,elem,attrs){
				elem.on('$destroy', function(){
    	            if (iv_!=null) $interval.cancel(iv_);
				});
				if(angular.isDefined(attrs.refresh) && !isNaN(parseInt(attrs.refresh)))
					refreshTime_=attrs.refresh;
				if(angular.isDefined(attrs.onRefresh) && angular.isFunction(scope[attrs.onRefresh])){
					onRefresh_=scope[attrs.onRefresh];
					iv_=$interval(function() { onRefresh_(elem[0]) },refreshTime_ * 1000);
					attrs.$observe('refresh',function(new_iv){
						if(!angular.equals(new_iv,refreshTime_)){
							if(iv_!=null) $interval.cancel(iv_);
							refreshTime_=new_iv;
							if(refreshTime_>0)
								iv_=$interval(function() { onRefresh_(elem[0]) },refreshTime_ * 1000);
						}
					});
				}
			}
		};
	}]);

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

app.directive('masonry', ['$parse', function ($parse) {
    return {
        restrict: 'AC',
        link: function (scope, elem, attrs) {
            scope.items = [];
            var container = elem[0];
            var options = angular.extend({
                itemSelector: 'tile'
            }, JSON.parse(attrs.masonry));

            var masonry = scope.masonry = new Masonry(container, options);

            var debounceTimeout = 0;
            scope.update = function () {
                if (debounceTimeout) {
                    window.clearTimeout(debounceTimeout);
                }
                debounceTimeout = window.setTimeout(function () {
                    debounceTimeout = 0;

                    masonry.reloadItems();
                    masonry.layout();

                    elem.children(options.itemSelector).css('visibility', 'visible');
                }, 120);
            };
			scope.update();
        }
    };
}]).directive('masonryTile', function () {
    return {
        restrict: 'AC',
        link: function (scope, elem) {
            elem.css('visibility', 'hidden');
            var master = elem.parent('*[masonry]:first').scope(),
                update = master.update;

            imagesLoaded(elem.get(0), update);
            elem.ready(update);
        }
    };
});



app.directive('tileHeight', function(){
    var directive = {
        restrict: 'A',
        link: function (scope, instanceElement, instanceAttributes, controller, transclude) {
            var heightFactor = 1;

            if (instanceAttributes['tileHeight']) {
                heightFactor = instanceAttributes['tileHeight'];
            }

            var updateHeight = function () {
				var h = instanceElement[0].parentElement.offsetWidth / Math.round(instanceElement[0].parentElement.offsetWidth / instanceElement[0].offsetWidth) * heightFactor;
//                instanceElement.outerHeight(instanceElement[0].parentElement.offsetHeight / Math.floor(instanceElement[0].parentElement.offsetHeight / h));
                instanceElement.outerHeight(h);
            };

            scope.$watch(instanceAttributes['tileHeight'], function (value) {
                heightFactor = 1.00 * value;
                updateHeight();
            });

            $(window).resize(updateHeight);
            updateHeight();

            scope.$on('$destroy', function () {
                $(window).unbind('resize', updateHeight);
            });
        }
    };

    return directive;
});


app.directive('tileMeta', ['$parse', '$sce', function($parse, $sce) {
  var directive = {
      restrict: 'A',
      scope: false,
      link: function (scope, elem, attrs) {
          function updateTileMeta() {
              var pistonMeta = $parse(attrs.tileMeta)(scope);
              var index = $parse(attrs.tileIndex)(scope) + 1;
              var meta = renderString($sce, pistonMeta['t' + index]).meta;
              if (!meta || !meta.type) {
                meta = renderString($sce, pistonMeta['f' + index]).meta;
              }
              if (!meta || !meta.type) {
                meta = renderString($sce, pistonMeta['i' + index]).meta;
              }
              scope.$parent.meta = meta;
          }
          
          scope.$watchCollection(attrs.tileMeta, updateTileMeta);
          scope.$watch(attrs.tileIndex, updateTileMeta);
      }
  };

  return directive;
}]);


app.directive('help', ['$compile', function($compile) {
	var directive = {
		restrict: 'A',
		link: function(scope, element, attrs) {
			var data = attrs['help'] ? attrs['help'] : element.text();		
			var el = angular.element('<wiki ng-click="wiki(\'' + data + '\')" class="fa fa-info-circle"></wiki>');
			element.append($compile(el)(scope));
		}
	};
	return directive;
}]);


app.directive('script', function() {
    return {
      restrict: 'E',
      scope: false,
      link: function(scope, elem, attr) {
        if (attr.type === 'text/javascript') {
          var code = elem.text();
          var f = new Function(code);
          f();
        }
      }
    };
  });

app.directive('devData', function ($parse) {
    return function (scope, element, attrs) {
		var func = function(scope, element, attrs) {
	        var data = $parse(attrs.devData)(scope);
			if (data) {
				for(attr in data) {
					element.attr('data-' + attr, data[attr]);
				}
			}
		}
		scope.$watch(attrs['devData'], function() { func(scope, element, attrs); });
    };
});


app.directive('onSizeChanged', ['$window', function ($window) {
    return {
        restrict: 'A',
        scope: {
            onSizeChanged: '&'
        },
        link: function (scope, $element, attr) {
            var element = $element[0];

            cacheElementSize(scope, element);
            $window.addEventListener('resize', onWindowResize);

            function cacheElementSize(scope, element) {
                scope.cachedElementWidth = element.offsetWidth;
                scope.cachedElementHeight = element.offsetHeight;
            }

            function onWindowResize() {
                var isSizeChanged = scope.cachedElementWidth != element.offsetWidth || scope.cachedElementHeight != element.offsetHeight;
                if (isSizeChanged) {
                    var expression = scope.onSizeChanged();
                    expression();
                }
            };
        }
    }
}]);


app.directive('title', function(){
    return {
        restrict: 'A',
        link: function(scope, element, attrs){
			if (!mobileCheck()) {
	            $(element).hover(function(){
    	            // on mouseenter
					$(element).tooltip({container: 'body', html:true, placement:'bottom'});
            	    $(element).tooltip('show');
	            }, function(){
    	            // on mouseleave
        	        $(element).tooltip('hide');
            	});
				$(element).on('$destroy', function(){
    	            $(element).tooltip('hide');
				});
			}
        }
    };
});

app.directive('collapseControl', ['dataService', function(dataService) {
	return function(scope, element, attr) {
		var id = (attr.target || attr.ariaControls || '').replace('#', '');
		var wasCollapsed = dataService.isCollapsed(id);
		
		if (wasCollapsed && 'ariaExpanded' in attr) {
			element.attr('aria-expanded', 'false');
		}
		
		element.bind('click', function(event) {
			var collapsed = dataService.isCollapsed(id);
			dataService.setCollapsed(id, !collapsed);
		});
	};
}]);

app.directive('collapseTarget', ['dataService', function(dataService) {
	return function(scope, element, attr) {
		var id = attr.id || '';
		var collapseClass = attr.collapseClass || 'in';
		var collapsed = dataService.isCollapsed(id);
		
		if (collapsed) {
			element.removeClass(collapseClass);
		} else {
			element.addClass(collapseClass);
		}
	};
}]);

app.directive('taskedit', function() {
	return {
		restrict: 'A',
		scope: false,
		link: function(scope, elem, attr) {
			var watchers = [];
			function setupCommand(command) {
				if (watchers.length > 0) {
					for (var i = 0; i < watchers.length; i++) {
						watchers[i]();
					}
					watchers = [];
				}
				// Custom visibility toggling for httpRequest parameters 
				if (command === 'httpRequest') {
					function toggleHttpRequestFields() {
						var parameters = scope.designer.parameters;
						var method = parameters[1].data.c; 
						var useQueryString = method === 'GET' || method === 'DELETE' || method === 'HEAD';
						var requestBodyTypeOperand = parameters[2];
						var sendVariablesOperand = parameters[3];
						var requestBodyOperand = parameters[4];
						var contentTypeOperand = parameters[5];
						var custom = requestBodyTypeOperand.data.c === 'CUSTOM' && !useQueryString;
						
						requestBodyTypeOperand.hidden = useQueryString;
						sendVariablesOperand.hidden = custom;
						contentTypeOperand.hidden = requestBodyOperand.hidden = !custom || useQueryString;
					}
					watchers.push(
						scope.$watch('designer.parameters[1].data.c', toggleHttpRequestFields),
						scope.$watch('designer.parameters[2].data.c', toggleHttpRequestFields)
					);
				}
			}
			scope.$watch('designer.command', setupCommand);
		}
	};
});

app.directive('checkbox', function() {
	return {
		restrict: 'E',
		scope: {
			checked: '=',
			iconClass: '@',
			radio: '=',
		},
		template: '<i ng-if="checked" ng-class="iconClass + \' fa-check-\' + (radio ? \'circle\' : \'square\')" class="far no-ng-animate"></i><i ng-if="!checked" ng-class="iconClass + \' fa-\' + (radio ? \'circle\' : \'square\')" class="far no-ng-animate"></i>'
	};
});

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

app.filter('dashify', function() {
  return function(value) {
    return dashify(value, { condense: true });
  }
});

app.filter('uniqueDashify', function() {
  var keyByUniqueValue = {};
  return function(value, key) {
    value = dashify(value, { condense: true });
    var unique = value;
    var i = 1;
    while (unique in keyByUniqueValue && keyByUniqueValue[unique] !== key) {
      unique = value + '-' + i++;
    }
    keyByUniqueValue[unique] = key;
    return unique;
  }
});



var config = app.config(['$routeProvider', '$locationProvider', '$sceDelegateProvider', '$rootScopeProvider', '$animateProvider',  function ($routeProvider, $locationProvider, $sceDelegateProvider,  $rootScopeProvider, $animateProvider) {
	$rootScopeProvider.digestTtl(10000); 
	//$cfpLoadingBarProvider.includeSpinner = false;
    var ext = '.module.css';
    $sceDelegateProvider.resourceUrlWhitelist([
        'self',
        cdn + '**'
    ]);
    // Allow ng-animate to be disabled on certain elements
    $animateProvider.classNameFilter(/^(?:(?!no-ng-animate).)*$/);
    $routeProvider.
    when('/', {
        templateUrl: cdn + theme + 'html/modules/dashboard.module.html?v=' + version(),
        controller: 'dashboard',
        css: cdn + theme + 'css/modules/dashboard' + ext + '?v=' + version()
    }).
    when('/register', {
        templateUrl: cdn + theme + 'html/modules/register.module.html?v=' + version(),
        controller: 'register',
        css: cdn + theme + 'css/modules/register' + ext + '?v=' + version()
    }).
    when('/init/:init', {
        redirectTo: function(params) {
			app.initialInstanceUri = atou(params.init);
			return '/';
		}
    }).
    when('/piston/:pistonId', {
        templateUrl: cdn + theme + 'html/modules/piston.module.html?v=' + version(),
        controller: 'piston',
        css: cdn + theme + 'css/modules/piston' + ext + '?v=' + version(),
		reloadOnSearch: false
    }).
    when('/fuel', {
        templateUrl: cdn + theme + 'html/modules/fuel.module.html?v=' + version(),
        controller: 'fuel',
        css: cdn + theme + 'css/modules/fuel' + ext + '?v=' + version()
    }).
    when('/visors', {
        templateUrl: cdn + theme + 'html/modules/visors.module.html?v=' + version(),
        controller: 'visors',
        css: cdn + theme + 'css/modules/visors' + ext + '?v=' + version()
    }).
    when('/init/:instId1/:instId2', {
        redirectTo: function(params) {
			app.initialInstanceUri = atou(params.instId1 + '/' +  params.instId2);
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
	var ws = null;
	var wsCallback = null;
	var nagged = false;

	var storage = {};
	var pendingStorage = 1;
	var initialized = false;

	if (localforage) {
		localforage.config({name:'webCoRE'});
		localforage.keys().then( function(keys) {
			pendingStorage = keys.length;
			if (pendingStorage) {
				localforage.iterate(function(value, key, iterationNumber) {
					storage[key] = decryptObject(value);
					pendingStorage--;
					if (!pendingStorage && !initialized) {
						initialize();
					}
				});
			} else {
				initialize();
			}
		});
	}

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

	dataService.encryptBackup = function(obj, password) {
		return encryptObject(obj, _ek + (password ? password : ''));
	}

	dataService.decryptBackup = function(obj, password) {
		return decryptObject(obj, _ek + (password ? password : ''));
	}

    var decryptObject = function(data, ek) {
        try {
            return angular.fromJson($window.sjcl.decrypt(ek ? ek : _ek, atou(data)));
        } catch (e) {
            return null;
        }

    };

    var writeObject = function(key, obj, ek) {
		localforage.setItem('core:' + key, encryptObject(obj, ek));
		storage['core:' + key] = obj;
		return;
    }

    var readObject = function(key, ek) {
		return storage['core:' + key];
	}

	var setLocation = function(loc) {
		location = loc;
		locations[location.id] = location;
		writeObject('locations', locations);
		return location;
	};

	var fixSI = function(si) {
		if (!si || !si.uri) return null;
		if (si.uri.indexOf('?access_token=')) {
			var parts = si.uri.split('?access_token=');
			si.uri = parts[0];
			si.accessToken = parts[1];
		}
		return si;
	}

	var setInstance = function(inst) {
		var initial = (!instance);
		if (!instance || (instance.id != inst.id)) instance = inst;
		//preserve the token, unless a new one is given
		var si = store[instance.id];
		if (!si) si = {};
		si.token = inst.token ? inst.token : si.token;
		si.uri = inst.uri ? inst.uri.replace(':443', '') : si.uri;
		store[instance.id] = fixSI(si);
		delete(instance.token);
		delete(instance.uri);
		if (inst.contacts) {
			//rewrite contacts
			instance.contacts = inst.contacts;
		}
		instance.contacts = instance.contacts ? instance.contacts : (instances[instance.id] && instances[instance.id].contacts ? instances[instance.id].contacts : []);
		if (inst.devices) {
			//rewrite devices
			instance.devices = inst.devices;
			initial = true;
		}
		instance.devices = instance.devices ? instance.devices : (instances[instance.id] && instances[instance.id].devices ? instances[instance.id].devices : []);
		if (!!instance.pistons) {
			for (i = 0; i < inst.pistons.length; i++) {
				var newPiston = inst.pistons[i];
				for (j = 0; j < instance.pistons.length; j++) {
					if (instance.pistons[j].id == newPiston.id) {
						var oldPiston = instance.pistons[j];
						oldPiston.name = newPiston.name;
						oldPiston.meta = newPiston.meta;
						inst.pistons[i] = oldPiston;
						break;
					}
				}
			}
		}
		instance.pistons = inst.pistons;
		instance.globalVars = inst.globalVars;
		instance.coreVersion = inst.coreVersion;
		instance.name = inst.name;
		instance.settings = inst.settings ? inst.settings : {};
		instance.lifx = inst.lifx ? inst.lifx : {};
		if (initial && instance.devices) {
			for (d in instance.devices) {
				instance.devices[d].t = dataService.determineDeviceType(instance.devices[d]);
			}
		}
		instance.virtualDevices = instance.virtualDevices || {};
		instances[instance.id] = instance;
		writeObject('instances', instances);
		writeObject('store', store);
		writeObject('instance', instance.id, _dk);
		if ((instance.coreVersion) && (version() != instance.coreVersion) && !nagged) {
			nagged = true;
			if (version() > instance.coreVersion) {
				status('A newer SmartApp version (' + version() + ') is available, please update and publish all the webCoRE SmartApps in the SmartThings IDE.', true);
			} else {
				status('A newer UI version (' + instance.coreVersion + ') is available, please hard reload this web page to get the newest version.', true);
			}
		}
		return instance;
	};

	var status = function(status, permanent) {
		if (cbkStatus) cbkStatus(status, permanent);
	}

   	var encodeEmoji = function(value) {
        if (!value) return '';
        return value.replace(/([\uD83C-\uDBFF][\uDC00-\uDFFF])/g, function(match) {
            return ':' + encodeURIComponent(match) + ':';
        });
    };

   	var decodeEmoji = function(value) {
        if (!value) return '';
        return value.replace(/(\:%[0-9A-F]{2}%[0-9A-F]{2}%[0-9A-F]{2}%[0-9A-F]{2}\:)/g, function(match) {
            return decodeURIComponent(match.substr(1,12));
        });
    };

	var getAccessToken = function(si) {
		return (si && si.accessToken ? 'access_token=' + si.accessToken + '&' : '');
	};

	dataService.openWebSocket = function(callback) {
		if (callback && instance) {
			wsCallback = callback;
			if (ws) return ws;
			var iid = instance.id;
			var si = store[instance.id];
			if (!si) si = {};
			var region = (si && si.uri && si.uri.startsWith('https://graph-eu')) ? 'eu' : 'us';
			ws = new WebSocket('wss://api-' + region + '-' + iid[32] + '.webcore.co:9297');
			ws.onopen = function(evt) {
				ws.send(instance.id)
			};
		    ws.onclose = function(evt) {
				ws = null;
				if (wsCallback) {
					setTimeout(function(){dataService.openWebSocket(wsCallback)}, 5000);
				}
			};
			ws.onmessage = function(evt) {
				if(wsCallback)
				try {
					wsCallback(evt);
				} catch(e) {};
			};
		    ws.onerror = function(evt) {
				ws = null;
				if (wsCallback) {
					setTimeout(function(){dataService.openWebSocket(wsCallback)}, 5000);
				}
			};
			return ws;
		} else {
			wsCallback = null;
			ws.close();
			ws = null;
		}
	};

	dataService.closeWebSocket = function() {
		dataService.openWebSocket(null);
	}

	dataService.ready = function() {
		return !!initialized;
	}

	dataService.logout = function() {
		locations = {};
		instances = {};
		storage = {};
		return localforage.clear();
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

	dataService.deleteFromStore = function(key) {
		return localforage.removeItem('core:' + key);
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

	dataService.getInstanceCount = function (locationId) {
		var result = 0;
		for(iid in instances) {
			if (!locationId || (instances[iid].locationId == locationId)) {
				result++;
			}
		}
		return result;
	};

	dataService.getInstance = function (instanceId, getAny) {
		if (instance && !instanceId) return instance;
		if (instance && (instance.id == instanceId)) return instance;		
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
		if (!!getAny && !!instances) {
		    for (iid in instances) return JSON.parse(JSON.stringify(instances[iid]));
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

	dataService.loadInstance = function(inst, uri, pin, dashboard) {
		//inst = dataService.getInstance(inst && inst.id ? inst.id : null);
		var si = inst ? store[inst.id] : null;
		var deviceVersion = !inst || !(inst.devices instanceof Object ) || !(Object.keys(inst.devices).length) ? 0 : (inst.deviceVersion ? inst.deviceVersion : 0);
		if (!si || !si.token) {
			if ((app.initialInstanceUri && app.initialInstanceUri.length) || (uri && uri.length)) {
				uri = app.initialInstanceUri ? app.initialInstanceUri : uri;
				if (!uri.startsWith('https://')) {
					if (uri && (uri.indexOf('tat.comapi') > 0)) {
						var parts = uri.split('api');
						if (parts[1].length >= 33) {
							var uid = parts[1].substr(0, 32);
							var appid = parts[1].substr(32);
							uri = 'https://' + parts[0] + '/api/' + uid.substr(0, 8) + '-' + uid.substr(8, 4) + '-' + uid.substr(12, 4) + '-' + uid.substr(16, 4) + '-' + uid.substr(20, 12) + '/apps/' + appid;
						}
					} else {
						if (uri && !(uri instanceof Object) && (uri.length >= 69)) {
							var host = uri.substr(0, uri.length - 64);
							if (!host.endsWith('.com')) host += '.api.smartthings.com';
							uri = uri.substr(0, 8) == 'https://' ? uri : 'https://' + host + '/api/token/' + uri.substr(-64, 8) + '-' + uri.substr(-56, 4) + '-' + uri.substr(-52, 4) + '-' + uri.substr(-48, 4) + '-' + uri.substr(-44, 12) +  '/smartapps/installations/' + uri.substr(-32, 8) + '-' + uri.substr(-24, 4) + '-' + uri.substr(-20, 4) + '-' + uri.substr(-16, 4) + '-' + uri.substr(-12) + '/';
						}
					}
				}
				si = fixSI({uri: uri});
				for(id in store) {
					if (store[id].uri == uri) {
						si = fixSI(store[id]);
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
			$location.path('/register');
		} else {
			var error = document.getElementById('error');
			if (error) error.parentNode.removeChild(error);
		}

    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/load?' + getAccessToken(si) + 'token=' + (si && si.token ? si.token : '') + (pin ? '&pin=' + pin : '') + '&dashboard='+ (dashboard ? 1 : 0) + '&dev=' + deviceVersion, {jsonpCallbackParam: 'callback'}).then(function(response) {
				var data = response.data;
				if (data.now) {
					adjustTimeOffset(data.now);
				}
				if (data.error && si) {
					data.uri = si.uri ;
					data.accessToken = si.accessToken;
				}
				if (data.location) {
					setLocation(data.location);
				}
				if (data.instance) {
					data.instance = setInstance(data.instance);
				}
				data.endpoint = si.uri;
				data.accessToken = si.accessToken;
				return data;	
			}, function(error) {
				status('There was a problem loading the dashboard data. The data shown below may be outdated; please log out if this problem persists.');
				return error;
			});
    };

    dataService.tap = function (tapId) {
        return $http({
            method: 'GET',
            url: 'tap/' + tapId
        });
    }

	dataService.getApiUri = function() {
		var inst = dataService.getInstance();
		si = store ? store[inst.id] : null;
		return si ? si.uri : null;		
	}

    dataService.refreshDashboard = function () {
		var inst = dataService.getInstance();
		si = store && inst ? store[inst.id] : null;
		var deviceVersion = !inst || !(inst.devices instanceof Object ) || !(Object.keys(inst.devices).length) ? 0 : (inst.deviceVersion ? inst.deviceVersion : 0);
		status('Loading dashboard...');
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/refresh?' + getAccessToken(si) + 'token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				data = response.data;
				return data;
			}, function(error) {
				return null;
			});
    }

    dataService.getPiston = function (pistonId) {
		var inst = dataService.getPistonInstance(pistonId);
		if (!inst) { inst = dataService.getInstance() };
		si = store && inst ? store[inst.id] : null;
		var deviceVersion = !inst || !(inst.devices instanceof Object ) || !(Object.keys(inst.devices).length) ? 0 : (inst.deviceVersion ? inst.deviceVersion : 0);
        var dbVersion = readObject('db.version', _dk);
		status('Loading piston...');
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/get?' + getAccessToken(si) + 'id=' + pistonId + '&db=' + dbVersion + '&token=' + (si && si.token ? si.token : '') + '&dev=' + deviceVersion, {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				data = response.data;
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
					data.instance = setInstance(data.instance);
				}
				data.endpoint = si.uri;
				return data;
			}, function(error) {
				return null;
			});
    }


    dataService.backupPistons = function (instanceId, pistonIds) {
		var inst = dataService.getInstance(instanceId);
		if (!inst) { inst = dataService.getInstance() };
		si = store && inst ? store[inst.id] : null;
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/backup?' + getAccessToken(si) + 'ids=' + pistonIds + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				data = response.data;
				if (data.now) {
					adjustTimeOffset(data.now);
				}
				return data;
			}, function(error) {
				return null;
			});
    }

    dataService.getActivity = function (pistonId, lastLogTimestamp) {
		var inst = dataService.getPistonInstance(pistonId);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/activity?' + getAccessToken(si) + 'id=' + pistonId + '&log=' + (lastLogTimestamp ? lastLogTimestamp : 0) + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				return response.data;
			});
    }

    dataService.generateBackupBin = function (data, publicBin) {
		var inst = dataService.getInstance();
        return $http({
            method: 'POST',
          //  url: 'https://api.myjson.com/bins',
            url: 'https://api.webcore.co/bins/' + (publicBin ? '' : md5(inst.account.id)),
            data: data ? (publicBin ? {d: encryptObject(data, _dk)} : {e: encryptObject(data, _dk + inst.account.id)}) : {},
            transformResponse: function(data) {
				try {
					data = JSON.parse(data);
					if (data && data.bin) {
						return data.bin;
					}
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
            //url: 'https://api.myjson.com/bins/' + binId,
            url: 'https://api.webcore.co/bins/' + md5(inst.account.id) + '/' + binId,
            data: data,
            transformResponse: function(data) {
				status('Backup bin updated');
				return true;
			}
        });
    }

    dataService.loadFromBin = function (binId, privateBin) {
		status('Loading piston from backup bin...');
		var inst = dataService.getInstance();
		if (!(inst && inst.account && inst.account.id)) {
			binId = null;
		}
        return $http({
            method: 'GET',
            url: 'https://api.webcore.co/bins/' + md5(inst.account.id) + '/' + binId,
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
    
    dataService.getImportedData = function() {
      return localforage.getItem('import');
    }
    
    dataService.setImportedData = function(importedData) {
      return localforage.setItem('import', importedData);
    }
    
    dataService.clearImportedData = function() {
      return localforage.removeItem('import');
    }
    
    dataService.loadFromImport = function (pistonId) {
      status('Loading piston from import...');
      return $q.resolve(localforage.getItem('import')).then(function(pistons) {
        status();
        if (pistons && pistons.length > 0) {
          return pistons.find(function(data) {
            return data.meta.id === pistonId;
          }) || null;
        }
      });
    }



    dataService.generateNewPistonName = function () {
		var inst = dataService.getInstance();
		si = store ? store[inst.id] : null;
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/new?' + getAccessToken(si) + 'token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				return response.data;
			});
    }

    dataService.createPiston = function (name, author, backupBin) {
		var inst = dataService.getInstance();
		si = store ? store[inst.id] : null;
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/create?' + getAccessToken(si) + 'author=' + encodeURIComponent(author) + '&name=' + encodeURIComponent(name) + '&bin=' + encodeURIComponent(backupBin ? backupBin : '')  +'&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				return response.data;
			});
    }



	var setPistonChunk = function(si, chunks, chunk, binId) {
		if (chunk < chunks.length) {
			status('Saving piston chunk ' + (chunk + 1).toString() + ' of ' + chunks.length.toString() + '...');
	    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/set.chunk?' + getAccessToken(si) + 'chunk=' + chunk.toString() + '&data=' + encodeURIComponent(chunks[chunk]) + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
				.then(function(response) {
					return setPistonChunk(si, chunks, chunk + 1, binId);
				});
		} else {
			status('Finishing up...');
	    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/set.end?' + getAccessToken(si) + 'bin=' + encodeURIComponent(binId) + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
				.then(function(response) {
					status();
					return response;
				});
		}
	}
 
    dataService.setPiston = function (piston, binId, saveToBinOnly) {
		var inst = dataService.getPistonInstance(piston.id);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		var data = utoa(encodeEmoji(angular.toJson(piston)));
		var maxChunkSize = 3076;
		if (piston && binId) {
			dataService.saveToBin(binId, piston);
		}
		if (saveToBinOnly) return;
		if (data.length > maxChunkSize) {
			var chunks = [];
			for (var i = 0; i < data.length; i += maxChunkSize) {
				chunks.push(data.slice(i, i + maxChunkSize))
			}
			status('Preparing to save chunked piston...');
	    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/set.start?' + getAccessToken(si) + 'id=' + piston.id + '&chunks=' + chunks.length.toString() + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
				.then(function(response) {
					if (response && (response.status == 200) && response.data && (response.data.status == 'ST_READY')) {
						return setPistonChunk(si, chunks, 0, binId);
					}
				});
		} else {
			status('Saving piston...');
	    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/set?' + getAccessToken(si) + 'id=' + piston.id + '&data=' + encodeURIComponent(data) + '&bin=' + encodeURIComponent(binId) + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
				.then(function(response) {
					status();
					return response;
				});
		}
    }

    dataService.setPistonBin = function (pid, bin) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		status('Setting piston bin to ' + bin + '...');
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/set.bin?' + getAccessToken(si) + 'id=' + pid + '&bin=' + bin+ '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				status();
				return response.data;
			});
    }

    dataService.clickPistonTile = function (pid, tile) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/tile?' + getAccessToken(si) + 'id=' + pid + '&tile=' + tile + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				status();
				return response.data;
			});
    }

    dataService.setPistonCategory = function (pid, category) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		status('Setting piston category...');
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/set.category?' + getAccessToken(si) + 'id=' + pid + '&category=' + category + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				status();
				return response.data;
			});
    }

    dataService.setPistonLogging = function (pid, level) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		status('Setting piston logging level to ' + level + '...');
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/logging?' + getAccessToken(si) + 'id=' + pid + '&level=' + level + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				status();
				return response.data;
			});
    }


    dataService.clearPistonLogs = function (pid) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		status('Clearing piston logs...');
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/clear.logs?' + getAccessToken(si) + 'id=' + pid + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				status();
				return response.data;
			});
    }

    dataService.pausePiston = function (pid) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		status('Pausing piston...');
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/pause?' + getAccessToken(si) + 'id=' + pid + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				status();
				return response.data;
			});
    }

    dataService.resumePiston = function (pid) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		status('Resuming piston...');
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/resume?' + getAccessToken(si) + 'id=' + pid + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				status();
				return response.data;
			});
    }


    dataService.testPiston = function (pid) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		status('Testing piston...');
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/test?' + getAccessToken(si) + 'id=' + pid + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				status();
				return response.data;
			});
    }


    dataService.createPresenceSensor = function (name, dni) {
	var inst = dataService.getPistonInstance();
	if (!inst) { inst = dataService.getInstance() };
	si = store ? store[inst.id] : null;
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/presence/create?' + getAccessToken(si) + 'name=' + encodeURIComponent(name) + '&dni=' + encodeURIComponent(dni ? dni : '') + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
	    .then(function(response) {
		return response.data;
	    });
    }

    dataService.deletePiston = function (pid) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/delete?' + getAccessToken(si) + 'id=' + pid + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				return response.data;
			});
    }


    dataService.setVariable = function (name, value, pid) {
		var inst = pid ? dataService.getPistonInstance(pid) : dataService.getInstance();
		si = store ? store[inst.id] : null;
		if (value && value.t) {
			switch (value.t) {
				case 'time':
					var d = new Date(value.v);
					value.v = d.getTime() - d.getTimezoneOffset() * 60000;
					break;
				case 'date':
				case 'datetime':
					value.v = (new Date(value.v)).getTime();
					break;
			}
		}
		var data = value ? utoa(angular.toJson(value)) : '';
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/variable/set?' + getAccessToken(si) + 'name=' + name + '&value=' + encodeURIComponent(data) + (pid ? '&id=' + pid : '') + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				return response.data;
			});
    }

    dataService.setSettings = function (settings) {
		var inst = dataService.getInstance();
		si = store ? store[inst.id] : null;
		var data = settings ? utoa(angular.toJson(settings)) : '';
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/settings/set?' + getAccessToken(si) + 'settings=' + encodeURIComponent(data) + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				return response.data;
			});
    }

    dataService.evaluateExpression = function (pid, expression, dataType) {
		var inst = dataService.getPistonInstance(pid);
		if (!inst) { inst = dataService.getInstance() };
		si = store ? store[inst.id] : null;
		var data = utoa(angular.toJson(expression));
    	return $http.jsonp((si ? si.uri : 'about:blank/') + 'intf/dashboard/piston/evaluate?' + getAccessToken(si) + 'id=' + pid + '&expression=' + encodeURIComponent(data) + '&dataType=' + (dataType ? encodeURIComponent(dataType) : '') + '&token=' + (si && si.token ? si.token : ''), {jsonpCallbackParam: 'callback'})
			.then(function(response) {
				return response.data;
			});
    }


    dataService.registerDashboard = function (code) {
    	return $http.post('https://api.webcore.co/dashboard/register/' + code)
			.then(function(response) {
				return response.data;
			});
    }


	dataService.listFuelStreams = function() {
		var instance = dataService.getInstance();
		if (instance) {
			var iid = instance.id;
			var si = store[instance.id];
			if (!si) si = {};
			var region = (si && si.uri && si.uri.startsWith('https://graph-eu')) ? 'eu' : 'us';
			var req = {
				method: 'POST',
				url: 'https://api-' + region + '-' + iid[32] + '.webcore.co:9287/fuelStreams/list',
				headers: {
				'Auth-Token': '|'+ iid
				},
				data: { i: iid }
			}
			return $http(req).then(function(response) {
					return response.data;
				});
		}
	}

	dataService.login = function(username, password) {
		var inst = instance || dataService.getInstance(null, true);
		if (!inst) inst = {id: 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx' + [0,1,2,3,4,5,6,7,8,9,'a','b','c','d','e','f'][Math.floor(Math.random()*16)]};
		if (inst) {
			var iid = inst.id;
			var si = store[inst.id];
			if (!si) si = {};
			var region = (si && si.uri && si.uri.startsWith('https://graph-eu')) ? 'eu' : 'us';
			var req = {
				method: 'POST',
				url: 'https://api-' + region + '-' + iid[32] + '.webcore.co:9287/user/login',
				headers: {
				'Auth-Token': '|'+ iid
				},
				data: { u: username, p: password }
			}
			return $http(req).then(function(response) {
				var data = response.data;
				store = JSON.parse('{}');//":e1d1f849bf093663be65a380fc6343b3:":{"uri":"https://graph.api.smartthings.com/api/token/7652e3a4-0b1d-4149-8db1-904926b5c1ea/smartapps/installations/3d488355-c7d4-47e5-839c-9f5d5530881f/"},":8d5d2f31e563841c1c95b6b73a6c8b25:":{"uri":"https://graph.api.smartthings.com/api/token/a6f2dfd4-b91b-4bbe-b900-757ad5e6c7a9/smartapps/installations/e035fa0e-0671-406a-9483-d89b15ac3c1b/"}}');
				for (x in store) {
				    if (!instances[x] || !instances[x].account) {
					instances[x] = {id: x, name: 'Unknown', locationId: '?'};
					locations['?'] = {id: '?', name: 'Unknown'};
				    }
				}
				instance = dataService.getInstance(null, true)
				writeObject('store', store);
				writeObject('instances', instances);
				writeObject('locations', locations);
				if (instance) writeObject('instance', instance.id);
				$location.path('/');
				//$route.reload();
				if (data && data.result) {
				    //rewrite all stored keys
				    store = data.store;
				    return true;
				}
				return false;
			});
		}
	}

	dataService.listFuelStreamData = function(fuelStreamId) {
		var instance = dataService.getInstance();
		if (instance) {
			var iid = instance.id;
			var si = store[instance.id];
			if (!si) si = {};
			var region = (si && si.uri && si.uri.startsWith('https://graph-eu')) ? 'eu' : 'us';
			var req = {
				method: 'POST',
				url: 'https://api-' + region + '-' + iid[32] + '.webcore.co:9287/fuelStreams/get',
				headers: {
				'Auth-Token': '|'+iid
				},
				data: { i: iid, f: fuelStreamId }
			}
			return $http(req).then(function(response) {
					return response.data;
				});
		}
	}

	dataService.registerHandler = function() {
		navigator.registerProtocolHandler('web+core','https://' + window.location.hostname + '/handler/%s', 'webCoRE');
	};


	dataService.determineDeviceType = function(device) {
		if (device && device.cn) {
	        if (device.cn.indexOf('Water Sensor') >= 0) return 'waterSensor';
	        if (device.cn.indexOf('Contact Sensor') >= 0) return 'contactSensor';
	        if (device.cn.indexOf('Thermostat') >= 0) return 'thermostat';
	        if (device.cn.indexOf('Garage Door Control') >= 0) return 'garageDoor';
	        if (device.cn.indexOf('Music Player') >= 0) return 'musicPlayer';
	        if (device.cn.indexOf('Door Control') >= 0) return 'door';
	        if (device.cn.indexOf('Presence Sensor') >= 0) return 'presenceSensor';
	        if (device.cn.indexOf('Motion Sensor') >= 0) return 'motionSensor';
	        if (device.cn.indexOf('Color Control') >= 0) return 'rgbBulb';
	        if (device.cn.indexOf('Color Temperature') >= 0) return 'whiteBulb';
	        if (device.cn.indexOf('Switch Level') >= 0) {
				var n = device.n.toLowerCase();
				if (n.indexOf('light') >= 0) return 'whiteBulb';
				if (n.indexOf('keen') >= 0) return 'vent';
				if (n.indexOf('vent') >= 0) return 'vent';
				return 'dimmer';
			}
	        if (device.cn.indexOf('Lock') >= 0) return 'lock';
	        if ((device.cn.indexOf('Button') >= 0) && (device.cn.indexOf('Button') >= 0)) return 'keypad';
	        if (device.cn.indexOf('Button') >= 0) return 'button';
	        if (device.cn.indexOf('Temperature Measurement') > 0) return 'temperatureSensor';
	        if ((device.cn.indexOf('Switch') >= 0) && (device.cn.indexOf('Power Meter') >= 0)) return 'outlet';
	        if (device.cn.indexOf('Switch') >= 0) return 'switch';
	        if (device.cn.indexOf('Power Meter') >= 0) return 'powerMeter';
		}
		return 'unknownDevice';
	}
  
  dataService.getAllCollapsed = function() {
		return dataService.loadFromStore('collapsed') || [];
	}
	
	dataService.isCollapsed = function(id) {
		return dataService.getAllCollapsed().indexOf(id) >= 0;
	}
	
	dataService.setCollapsed = function(id, collapsed) {
		var allCollapsed = dataService.getAllCollapsed();
		var index = allCollapsed.indexOf(id);
		
		if (collapsed && index < 0) {
			allCollapsed.push(id);
		} else if (!collapsed && index >= 0) {
			allCollapsed.splice(index, 1);
		}
		
		dataService.saveToStore('collapsed', allCollapsed);
	}


	var initialize = function() {
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

		userId = 0;

		initialized = true;
		window.ds = dataService;

		if (!!store.user) {
		    dataService.login(store.user.name, store.user.token).then(function(response) {
			console.log(response);
		    });
		}
	}

    return dataService;
}]);






app.run(['$rootScope', '$window', '$location', function($rootScope, $window, $location) {
    $rootScope.getTime = function (date) {
        if (date) {
            return date.format('h:mmtt');                
        }
    };

	$rootScope.$on('$viewContentLoaded', function(event) {
		var path = $location.path();
		if (!path.startsWith('/')) path = '/' + path;
		if (path.startsWith('/init/')) {
			path = '/init';
		}
		if (path.startsWith('/piston/')) {
			path = '/piston';
		}
	    $window.ga('send', 'pageview', { page: path });
		/*
		var units = null;
		if (!mobileCheck()) {
			switch (path) {
				case '/':
			        units = [{"calltype":"async[2]","publisher":"ady624","width":160,"height":600,"sid":"Chitika Default"}];
					break;
				case '/register':
			        units = [{"calltype":"async[2]","publisher":"ady624","width":728,"height":90,"sid":"Chitika Default"}];
					break;
			}
		}
		if (units) {
			$window.CHITIKA = {units: units};
 			if ($window.CHITIKA_ADS) $window.CHITIKA_ADS.make_it_so();
		} else {
			delete(window.CHITIKA);
		}
		*/
	});

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

function fixTime(timestamp) {
	if (timestamp < 86400000) {
		var d = new Date();
		var x = d.getTime();
		timestamp += x - (x % 86400000) + d.getTimezoneOffset() * 60000;
	}
	return timestamp;
}

function utcToString(timestamp) {
	return (new Date(fixTime(timestamp))).toLocaleString();
}

function utcToTimeString(timestamp) {
	return (new Date(fixTime(timestamp))).toLocaleTimeString();
}

function utcToDateString(timestamp) {
	return (new Date(fixTime(timestamp))).toLocaleDateString();
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



function renderString($sce, value) {
        var i = 0;
        if (!value) return '';
		var meta = {type: null, options: {}};

        var process = function(classList) {
            var result = '';
            while (i < value.length) {
                var c = value[i];
                switch (c) {
                    case '<':
                        result += '&lt;';
                        break;
                    case '>':
                        result += '&gt;';
                        break;
                    case '[':
                        var p = value.indexOf('|', i);
                        if (p > i) {
                            var cl = value.substring(i + 1, p);
                            i = p + 1;
                            result += process(cl);
                        } else {
                            i++;
                            result += process()
                        }
                        break;
                    case ']':
                        if (classList == undefined) {
                            return '[' + result + ']';
                        }
                        var cls = classList.trim();
                        // Ensure that unencoded commas in URLs do not get split
                        // into separate commands
                        while (/(\bsrc=\S+),/.test(cls)) {
                          cls = cls.replace(/(\bsrc=\S+),/, '$1:webCoRE-comma:');
                        }
                        cls = cls.replace(/'.*? .*?'|".*? .*?"/g, function(match) {
                            return match.replace(/\s+/g, ':webCoRE-space:');
                        })
                        cls = cls.split(/,|\s+/);
                        var className = '';
                        var color = '';
						var attributes = '';
						var backColor='';
						var fontSize = '';
                        for (x in cls) {
							if (!cls[x]) continue;
                            cls[x] = cls[x].replace(/:webCoRE-comma:/g, ',').replace(/:webCoRE-space:/g, ' ');
                            switch (cls[x]) {
                                case 'b': 
                                case 'u':
                                case 'i':
                                case 's':
                                case 'pre':
                                case 'mono':
                                case 'blink':
                                case 'flash':
                                case 'left':
                                case 'center':
                                case 'condensed':
                                case 'right':
								case 'full':
									className += 's-' + cls[x] + ' ';
									break;
                                case 'chart-gauge':
									meta.type = cls[x].replace('chart-', '');
									break;
                                case 'img':
                                case 'image':
									meta.type = 'image';
									break;
                                case 'vid':
                                case 'video':
									meta.type = 'video';
									break;
                                default:
									if (/^\d+(\.\d+)?(x|em)/.test(cls[x])) {
										fontSize = cls[x].replace('x', 'em');
									} else if (cls[x].startsWith('fa-')) {
										className += cls[x] + ' ';
									} else if (cls[x].startsWith('data-fa-')) {
										attributes += ' ' + cls[x];
									} else if (cls[x].startsWith('color-')) {
										color = cls[x].substr(6);
									} else if (/^(b|bg|bk|back)-/.test(cls[x])) {
										backColor = cls[x].replace(/^(b|bg|bk|back)-/, '');
									} else if (cls[x].indexOf('=') > 0) {
										//options
										var p = cls[x].indexOf('=');
										meta.options[cls[x].substr(0, p)] = cls[x].substr(p + 1);
									} else {
										color = cls[x].replace(/[^#0-9a-z]/gi, '');
									}
                            }
                        }
						meta.attributes = attributes;
						meta.className = className;
						meta.color = color;
						meta.backColor = backColor;
                        return '<span ' + (className ? 'class="' + className + '" ' : '') + (!!color || !!backColor || !!fontSize ? 'style="' + (color ? 'color: ' + color + ' !important;' : '') + ' ' + (backColor ? 'background-color: ' + backColor + ' !important;' : '') + ' ' + (fontSize ? 'font-size: ' + fontSize + ' !important;' : '') + '"' : '') + attributes + '>' + result + '</span>';
                    default:
                        result += c;
                }
                i++;
            }
            return result;
        }

		meta.html = process(value).replace(/\:(fa[blrs5]?)([ -])([a-z0-9\-\s.="']*)\:/gi, function(match, prefix, union, classes) {
            var attributes = '';
            // Default deprecated fa5 prefix to solid weight
            prefix = prefix.toLowerCase();
            prefix = prefix === 'fa5' ? 'fas' : prefix;
            // Support shorthand fas-stroopwafel for fas fa-stroopwafel
            classes = classes.toLowerCase();
            classes = union === '-' ? 'fa-' + classes : classes;
            classes = classes.replace(/(data-fa.*?=(?:'.*?'|".*?"))\s*/gi, function(match) {
                attributes += ' ' + match;
                return '';
            });
            return '<i class="' + prefix.toLowerCase() + ' ' + classes.toLowerCase() + '"' + attributes + '></i>';
      }).replace(/\:wu-([a-k]|v[1-4])-([a-z0-9_\-]+)\:/gi, function(match) {
			var iconSet = match[4];
			if (iconSet == 'v') {
				iconSet += match[5];
				var icon = match.substr(7, match.length - 8);
	            return '<img class="wu" src="https://icons.wxug.com/i/c/' + iconSet + '/' + icon + '.svg" />';
			} else {
				var icon = match.substr(6, match.length - 7);
	            return '<img class="wu" src="https://icons.wxug.com/i/c/' + iconSet + '/' + icon + '.gif" />';
			}
        }).replace(/(?![^<]*[>])#[a-z0-9]{6}/gi, function(match) {
			return '<span class="swatch" style="background-color:' + match + '">&nbsp;&nbsp;&nbsp;&nbsp;</span>' + match;
		}).replace(/\\[rn]/gi, '<br/>');
		var tmp = document.createElement("DIV");
		tmp.innerHTML = meta.html;
		meta.text = tmp.textContent || tmp.innerText || "";
        var result = $sce.trustAsHtml(meta.html);
		result.meta = meta;
		return result;
    };



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

// ios-drag-drop mishandles touches on mobile nav menus; stop propagation of the
// touchstart event in nav menus before it bubbles up to the document level
$(document.documentElement).on('touchstart', '.navbar-collapse *', function (e) { 
  e.stopPropagation();
});


window.onerror = function myErrorHandler(errorMsg, url, lineNumber) {
    //alert("Error occured: " + errorMsg + ' at ' + url + ' line ' + lineNumber);//or any message
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

//document.documentElement.addEventListener('touchstart', function (event) {
//    if (event.touches.length > 1) {
//        event.preventDefault();
//    }
//}, false);


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

function loadFontAwesomeFallback() {
  fontAwesomePro = false;
  $('head script[src*="pro.fontawesome"]').each(function() {
    $(this).remove().clone()
      .attr({
        src: this.src.replace('pro', 'use'),
      }).removeAttr('onerror')
      .appendTo('head');
  });
}

// Handle Pro load failure before app loads
if (!window.fontAwesomePro) {
  loadFontAwesomeFallback();
}


// Map .far to .fas free icons when Pro is not available
app.directive('far', function() {
	var directive = {
		restrict: 'C',
		link: function(scope, element) {
			if (!fontAwesomePro) {
				element.toggleClass('far fas');
			}
		}
	};
	return directive;
});

// Map .fal to .fas free icons when Pro is not available
app.directive('fal', function() {
	var directive = {
		restrict: 'C',
		link: function(scope, element) {
			if (!fontAwesomePro) {
				element.toggleClass('fal fas');
			}
		}
	};
	return directive;
});

// For use with data-fa-symbol, older versions of Firefox require the full 
// pathname in the href
app.directive('spriteIcon', ['$sce', function($sce) {
	return {
		restrict: 'C',
		scope: {
			symbol: '@'
		},
		link: function(scope) {
			scope.href = $sce.trustAsUrl(window.location.pathname + '#' + scope.symbol);
		},
		template: '<use xlink:href="{{href}}"></use>',
	};
}]);

// Polyfills
if (!String.prototype.endsWith) {
	String.prototype.endsWith = function(search, this_len) {
		if (this_len === undefined || this_len > this.length) {
			this_len = this.length;
		}
		return this.substring(this_len - search.length, this_len) === search;
	};
}

version = function() { return 'v0.3.108.20180906'; };
