config.controller('visor', ['$scope', '$rootScope', 'dataService', '$timeout', '$interval', '$location', '$sce', '$routeParams', 'ngDialog', '$window', function($scope, $rootScope, dataService, $timeout, $interval, $location, $sce, $routeParams, ngDialog, $window) {
	var w = 128;
	var h = 128;
	var cols = 15;
	var rows = 8;
	$scope.tiles = [];
	$scope.placeholders = [];

	$scope.tileTypes = [
		{ name: 'Temperature',		type: 'temperature',	icon: 'thermometer',	description: 'Provides information about temperature'					},
		{ name: 'Contact', 			type: 'contact',		icon: 'circle-o-notch',	description: 'Provides information about a generic contact sensor'		},
		{ name: 'Contact (door)', 	type: 'doorContact',	icon: 'circle-o-notch',	description: 'Provides information about a door contact sensor'			},
		{ name: 'Contact (window)', type: 'windowContact',	icon: 'circle-o-notch',	description: 'Provides information about a window contact sensor'		}
	];


	$scope.init = function() {
		$scope.tiles.push({z: 'Piston state', t: 'contact', i: 0, sz: {w:8, h:1}});
		$scope.tiles.push({z: 'xx', i: 8, t: 'contact', sz: {w:1, h:1}});
		$scope.tiles.push({z: 'yy', i: 9, t: 'contact', sz: {w:1, h:1}});
		$scope.tiles.push({z: 'zz', i: 71, t: 'contact', sz: {w:4, h:4}});
		$scope.prepare();
		window.scope = $scope;
	};


	$scope.prepare = function() {
		for(var i = 0; i < $scope.tiles.length; i++) {
			var tile = $scope.tiles[i];
			var tw = (tile.sz ? (tile.sz.w ? tile.sz.w : 1) : 1);
			var th = (tile.sz ? (tile.sz.h ? tile.sz.h : 1) : 1);
			tile.style = {
				left: ((tile.i % cols) * w) + 'px',
				top: (Math.floor(tile.i / cols) * h) + 'px',
				width: w * tw + 'px',
				height: h * th + 'px',
				zIndex: 10000 - tw * th
			}
		}
		if (!$scope.placeholders || !$scope.placeholders.length) {
			$scope.placeholders = [];
			for(var i=0; i < rows; i++) {
				for(var j=0; j < cols; j++) {
					$scope.placeholders.push({
						style: {
							left: (j * w) + 'px',
							top: (i * h) + 'px',
							width: w + 'px',
							height: h + 'px'
						}
					});
				}
			}
		}
	};

	$scope.getPageStyle = function() {
		var pw = w * cols;
		var ph = h * rows;
		var sx = (document.documentElement.clientWidth - 16) / pw;
		var sy = (document.documentElement.clientHeight - 64) / ph;
		var scale = sx > sy ? sy : sx;		
		return {
			width: (w * cols) + 'px',
			height: (h * rows) + 'px',
			transform: 'scale(' + scale + ')',
			left: ((document.documentElement.clientWidth - pw * scale) / 2) + 'px',
			top: ((document.documentElement.clientHeight - 48 - ph * scale) / 2) + 'px'
		};
	};

	$scope.onSizeChanged = function() {
		$scope.$apply();
	};

    $scope.setDesignerType = function(type) {
        $scope.designer.type = type;
        $scope.nextPage();
        if ($scope.designer.ontypechanged) {
            $scope.designer.ontypechanged($scope.designer, type);
        }
    };

    $scope.closeDialog = function() {
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

    $scope.refreshSelects = function(type) {
        if (type) {
            $scope.$$postDigest(function() {
                $('select[' + type + ']').selectpicker('refresh');
                $timeout(function() {$('select[' + type + ']').selectpicker('refresh');}, 0, false);
            });
        } else {
            $scope.$$postDigest(function() {
                $('select[selectpicker]').selectpicker('refresh');
                $timeout(function() {$('select[selectpicker]').selectpicker('refresh');}, 0, false);
            });
        }
    }


	$scope.clickTile = function(tile) {
		$scope.selectedTile = tile;
	};

	$scope.addTile = function(index) {
		if ($scope.selectedTile) {
			if (!!$scope.selectedTile && ($scope.selectedTile.i == index)) {
				return $scope.editTile($scope.selectedTile);
			} else {
				$scope.selectedTile.i = index;
				$scope.prepare();
				return;
			}
		}
	    return $scope.editTile(null, index);
    };

    $scope.editTile = function(tile, index) {
		$scope.selectedTile = null;
        if (!tile) {
            tile = {};
			tile.i = index;
            tile.t = null; //type
            tile.d = []; //devices
            tile.z = ''; //desc
        }
        $scope.designer = {};
        $scope.designer.$obj = tile;
        $scope.designer.$tile = tile;
        $scope.designer.$new = tile.t ? false : true;
        $scope.designer.type = tile.t;
		$scope.designer.index = index;
		$scope.designer.width = (tile.sz ? (tile.sz.w ? tile.sz.w : 1) : 1);
		$scope.designer.height = (tile.sz ? (tile.sz.h ? tile.sz.h : 1) : 1);
        $scope.designer.page = tile.t ? 1 : 0;
        $scope.designer.description = tile.z;
        $scope.designer.devices = tile.d;
        $scope.designer.dialog = ngDialog.open({
            template: 'dialog-edit-tile',
            className: 'ngdialog-theme-default ngdialog-large',
            closeByDocument: false,
            disableAnimation: true,
            scope: $scope
        });
	}


	$scope.updateTile = function() {
        var tile = $scope.designer.$new ? {t: $scope.designer.type, i: $scope.designer.index} : $scope.designer.$tile;
        tile.z = $scope.designer.description;
		if (($scope.designer.width > 1) || ($scope.designer.height > 1)) {
			tile.sz = {w: $scope.designer.width, h: $scope.designer.height};
		} else {
			delete(tile.sz);
		}
		if ($scope.designer.$new) {
			$scope.tiles.push(tile);
		}
		$scope.prepare();
        $scope.closeDialog();
    }

	$scope.init();
}]);
