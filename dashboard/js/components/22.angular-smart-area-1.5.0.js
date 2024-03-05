/**
 * AngularJS Directive to allow autocomplete and dropdown suggestions
 * on textareas.
 *
 * Homepage: https://github.com/aurbano/smart-area
 *
 * @author Alejandro U. Alvarez (http://urbanoalvarez.es)
 * @license AGPLv3 (See LICENSE)
 */

angular.module('smartArea', [])
    .directive('smartArea', ['$compile', function($compile) {
    return {
        restrict: 'A',
        scope: {
            areaConfig: '=smartArea',
            areaData: '=ngModel'
        },
        replace: true,
        link: function(scope, textArea){
            if(textArea[0].tagName.toLowerCase() !== 'textarea'){
                console.warn("smartArea can only be used on textareas");
                return false;
            }

            // Caret tracking inspired by
            // https://github.com/component/textarea-caret-position
            // Properties to be copied over from the textarea
            var properties = [
                'direction',  // RTL support
                'boxSizing',
                'width',  // on Chrome and IE, exclude the scrollbar, so the mirror div wraps exactly as the textarea does
                'overflowX',
                'overflowY',  // copy the scrollbar for IE
                'color',
                'height',

                'borderTopWidth',
                'borderRightWidth',
                'borderBottomWidth',
                'borderLeftWidth',

                'borderTopColor',
                'borderRightColor',
                'borderBottomColor',
                'borderLeftColor',

                'borderTopStyle',
                'borderRightStyle',
                'borderBottomStyle',
                'borderLeftStyle',
                'borderRadius',

                'backgroundColor',

                'paddingTop',
                'paddingRight',
                'paddingBottom',
                'paddingLeft',

                // https://developer.mozilla.org/en-US/docs/Web/CSS/font
                'fontStyle',
                'fontVariant',
                'fontWeight',
                'fontStretch',
                'fontSize',
                'fontSizeAdjust',
                'lineHeight',
                'fontFamily',

                'textAlign',
                'textTransform',
                'textIndent',
                'textDecoration',  // might not make a difference, but better be safe

                'letterSpacing',
                'wordSpacing',
                'whiteSpace',
                'wordBreak',
                'wordWrap'
            ];

            // Build the HTML structure
            var mainWrap = angular.element('<div class="sa-wrapper"></div>'),
                isFirefox = !(window.mozInnerScreenX === null);

            scope.fakeAreaElement = angular.element($compile('<div class="sa-fakeArea" ng-trim="false" ng-bind-html="fakeArea"></div>')(scope))
                .appendTo(mainWrap);

            scope.dropdown.element = angular.element($compile('<div class="sa-dropdown" ng-show="dropdown.content.length > 0"><input type="text" class="form-control" ng-show="dropdown.showFilter"/><ul class="dropdown-menu" role="menu" style="position:static"><li ng-repeat="element in dropdown.content" role="presentation"><a href="" role="menuitem" ng-click="dropdown.selected(element)" ng-class="{active: $index == dropdown.current}" ng-bind-html="element.display"></a></li></ul></div>')(scope))
                .appendTo(mainWrap);

            scope.dropdown.filterElement = scope.dropdown.element.find('input');
            scope.dropdown.filterElement.bind('keydown', scope.keyboardEvents);

            // Default textarea css for the div
            scope.fakeAreaElement.css('whiteSpace', 'pre-wrap');
            scope.fakeAreaElement.css('wordWrap', 'break-word');

            // Transfer the element's properties to the div
            properties.forEach(function (prop) {
                scope.fakeAreaElement.css(prop, textArea.css(prop));
            });

            scope.fakeAreaElement.css('width',(parseInt(textArea.outerWidth()) + 1) + 'px');

            // Special considerations for Firefox
//            if (isFirefox) {
//                scope.fakeAreaElement.css('width',parseInt(textArea.width()) - 2 + 'px');  // Firefox adds 2 pixels to the padding - https://bugzilla.mozilla.org/show_bug.cgi?id=753662
//                // Firefox lies about the overflow property for textareas: https://bugzilla.mozilla.org/show_bug.cgi?id=984275
//                if (textArea.scrollHeight > parseInt(textArea.height)){
//                    scope.fakeAreaElement.css('overflowY', 'scroll');
//                }
//            }

            // Insert the HTML elements
            mainWrap.insertBefore(textArea);
            textArea.appendTo(mainWrap).addClass('sa-realArea').attr('ng-trim',false);
            $compile(textArea);

            // Dirty hack to maintain the height
            textArea.on('keyup', function(){
                scope.fakeAreaElement.height(textArea.height());
            });

            return mainWrap;
        },
        controller: ['$scope', '$element', '$timeout', '$sce', function($scope, $element, $timeout, $sce){
          var ngModel = $element.controller('ngModel');
            /* +----------------------------------------------------+
             * +                     Scope Data                     +
             * +----------------------------------------------------+ */

            $scope.fakeArea = htmlEncode($scope.areaData);
            $scope.dropdownContent = 'Dropdown';
            $scope.dropdown = {
                content: [],
                element: null,
                current: 0,
                select: null,
                customSelect: null,
                filter: '',
                match: '',
                mode: 'append',
                showFilter: false,
                filterElement: null
            };

            /* +----------------------------------------------------+
             * +                   Scope Watches                    +
             * +----------------------------------------------------+ */
            $scope.$watch('areaData', function(){
                $scope.trackCaret();

                // TODO Track caret on another fake area, so I don't have to recalculate autocomplete triggers every time the cursor moves.
                checkTriggers();
            });

            /* +----------------------------------------------------+
             * +                  Scope Functions                   +
             * +----------------------------------------------------+ */

            /**
             * Update the Dropdown position according to the current caret position
             * on the textarea
             */
            $scope.trackCaret = function(){
                var text = $scope.areaData,
                    position = getCharacterPosition();

                $scope.fakeArea = $sce.trustAsHtml(htmlEncode(text.substring(0, position)) + '<span class="sa-tracking"></span>' + htmlEncode(text.substring(position)));

                // Tracking span
                $timeout(function(){
                    var span = $scope.fakeAreaElement.find('span.sa-tracking');
                    if(span.length > 0){
                        var spanOffset = span.position();
                        // Move the dropdown
                        $scope.dropdown.element.css({
                            top: (spanOffset.top + parseInt($element.css('fontSize')) + 2)+'px',
                            left: (spanOffset.left)+'px'
                        });
                    }
                    highlightText();
                }, 0);
            };

            /**
             * Keyboard event reacting. This function is triggered by
             * keydown events in the dropdown filter and the main textarea
             *
             * @param event JavaScript event
             */
            $scope.keyboardEvents = function(event){
                if($scope.dropdown.content.length > 0) {
                    var code = event.keyCode || event.which;
                    if (code === 13) { // Enter
                        event.preventDefault();
                        event.stopPropagation();
                        // Add the selected word from the Dropdown
                        // to the areaData in the current position
                        $timeout(function(){
                            $scope.dropdown.selected($scope.dropdown.content[$scope.dropdown.current]);
                        },0);
                    }else if(code === 38){ // Up
                        event.preventDefault();
                        event.stopPropagation();
                        $timeout(function(){
                            $scope.dropdown.current--;
                            if($scope.dropdown.current < 0){
                                $scope.dropdown.current = $scope.dropdown.content.length - 1; // Wrap around
                            }
                        },0);
                    }else if(code === 40){ // Down
                        event.preventDefault();
                        event.stopPropagation();
                        $timeout(function(){
                            $scope.dropdown.current++;
                            if($scope.dropdown.current >= $scope.dropdown.content.length){
                                $scope.dropdown.current = 0; // Wrap around
                            }
                        },0);
                    }else if(code === 27){ // Esc
                        event.preventDefault();
                        event.stopPropagation();
                        $timeout(function(){
                            $scope.dropdown.content = [];
                            $element[0].focus();
                        },0);
                    }else{
                        $scope.dropdown.filterElement.focus();
			checkTriggers();
                    }
                }
            };

            /**
             * Add an item to the textarea, this is called
             * when selecting an element from the dropdown.
             * @param item Selected object
             */
            $scope.dropdown.selected = function(item){
                if($scope.dropdown.customSelect !== null){
                    var append = $scope.dropdown.mode === 'append';
                    addSelectedDropdownText($scope.dropdown.customSelect(item), append);
                }else{
                    addSelectedDropdownText(item.display);
                }
                $scope.dropdown.content = [];
            };

            /* +----------------------------------------------------+
             * +                Internal Functions                  +
             * +----------------------------------------------------+ */

            /**
             * Add text to the textarea, this handles positioning the text
             * at the caret position, and also either replacing the last word
             * or appending as new content.
             *
             * @param selectedWord Word to add to the textarea
             * @param append Whether it should be appended or replace the last word
             */
            function addSelectedDropdownText(selectedWord, append){

                $scope.dropdown.showFilter = false;

                var text = $scope.areaData,
                    position = getCharacterPosition(),
                    lastWord = text.substr(0, position).split(/[\s\b{}()]|\w\[/),
                    remove = lastWord[lastWord.length - 1].length;

                if(!append && $scope.dropdown.match){
                  remove = $scope.dropdown.match.length;
                }

                if(append || remove < 0){
                    remove = 0;
                }

                // Now remove the last word, and replace with the dropped down one
                var areaData = text.substr(0, position - remove) +
                    selectedWord +
                    text.substr(position);

                // Now reset the caret position
                if($element[0].selectionStart) {
                    var caretPosition = position - remove + selectedWord.toString().length;
                    $element[0].value = areaData;
                    $element[0].focus();
                    $element[0].setSelectionRange(caretPosition, caretPosition);
                }
                
                ngModel.$setViewValue(areaData, 'default');
                $timeout(checkTriggers);
            }

            /**
             * Perform the "syntax" highlighting of autocomplete words that have
             * a cssClass specified.
             */
            function highlightText(){
                var text = $scope.areaData;
                var cssClasses = [];

                if(typeof($scope.areaConfig.autocomplete) === 'undefined' || $scope.areaConfig.autocomplete.length === 0){
                    return;
                }

                $scope.areaConfig.autocomplete.forEach(function(autoList){
                    for(var i=0; i<autoList.words.length; i++){
                        var cssId = cssClasses.length;
                        cssClasses.push(autoList.cssClass);
                        if(typeof(autoList.words[i]) === "string"){
                            text = text.replace(new RegExp("([^\\w]|\\b)("+autoList.words[i].replace(/[(:]$|^\[/g, '').replace(/[/\-\\^$*+?.()|[\]{}]/g, '\\$&')+")([^\\w]|\\b)", 'g'), '$1§x'+cssId+'x§$2§/x' + cssId + 'x§$3');
                        }else{
                            text = text.replace(autoList.words[i], function(match){
                                return '§x' + cssId + 'x§'+match+'§/x' + cssId + 'x§';
                            });
                        }
                    }
                });
                var html = htmlEncode(text);
                let changed;
                do {
                    changed = false;
                    html = html.replace(/§x(\d+)x§([\s\S]*?)§\/x\1x§/g, function(match, cssId, content) {
                        changed = true;
                        return '<span class="'+cssClasses[cssId]+'">'+content+'</span>';
                    });
                } while (changed)
                // Add to the fakeArea
                $scope.fakeArea = $sce.trustAsHtml(html);
            }

            /**
             * Check all the triggers
             */
            function checkTriggers(){
                triggerDropdownAutocomplete();
                triggerDropdownAdvanced();
            }

            /**
             * Trigger the advanced dropdown system, this will check
             * all the specified triggers in the configuration object under dropdown,
             * and if any of them match it will call it's list() function and add the
             * elements returned from it to the dropdown.
             */
            function triggerDropdownAdvanced(){
                $scope.dropdown.showFilter = false;
                $scope.dropdown.match = false;

                if(typeof($scope.areaConfig.dropdown) === 'undefined' || $scope.areaConfig.dropdown.length === 0){
                    return;
                }

                $scope.areaConfig.dropdown.forEach(function(element){
                    // Check if the trigger is under the cursor
                    var text = $scope.areaData,
                        position = getCharacterPosition();
                    if(typeof(element.trigger) === 'string' && element.trigger === text.substr(position - element.trigger.length, element.trigger.length)){
                        // The cursor is exactly at the end of the trigger

                        element.list(function(data){
                            $scope.dropdown.content = data.map(function(el){
                              el.display = $sce.trustAsHtml(el.display);
                              return el;
                            });

                            $scope.dropdown.customSelect = element.onSelect;
                            $scope.dropdown.mode = element.mode || 'append';
                            $scope.dropdown.match = '';
                            $scope.dropdown.showFilter = element.filter || false;

                            $timeout(function(){
                                $scope.dropdown.filterElement.focus();
                            }, 10);
                        });
                    }else if(typeof(element.trigger) === 'object'){
                        // I need to get the index of the last match
                        var searchable = text.substr(0, position),
                            match, found = false, lastPosition = -1;
                        element.trigger.lastIndex = 0;
                        while ((match = element.trigger.exec(searchable)) !== null){
                            if(match.index === lastPosition){
                                break;
                            }
                            lastPosition = match.index;
                            if(match.index + match[0].length === position){
                                found = true;
                                break;
                            }
                        }
                        if(found){
                            element.list(match, function(data){
                                $scope.dropdown.content = data.map(function(el){
                                  el.display = $sce.trustAsHtml(el.display);
                                  return el;
                                });

                                $scope.dropdown.customSelect = element.onSelect;
                                $scope.dropdown.mode = element.mode || 'append';
                                $scope.dropdown.match = match[0];
                                $scope.dropdown.showFilter = element.filter || false;
                            });

                        }
                    }
                });
            }

            /**
             * Set the scroll on the fake area
             */
            function resetScroll(){
              $timeout(function(){
                $scope.fakeAreaElement.scrollTop($element.scrollTop());
              }, 5);
            }

            /**
             * Trigger a simple autocomplete, this checks the last word and determines
             * whether any word on the autocomplete lists matches it
             */
            function triggerDropdownAutocomplete(){
                // First check with the autocomplete words (the ones that are not objects
                var autocomplete = [],
                    suggestions = [],
                    text = $scope.areaData,
                    position = getCharacterPosition(),
                    lastWord = text.substr(0, position).split(/[\s\b{}()]|\w\[/);

                // Get the last typed word
                lastWord = lastWord[lastWord.length-1];

                $scope.areaConfig.autocomplete.forEach(function(autoList){
                    autoList.words.forEach(function(word){
                        if(typeof(word) === 'string' && autocomplete.indexOf(word) < 0){
                            if(lastWord.length > 0 || lastWord.length < 1 && autoList.autocompleteOnSpace){
                                autocomplete.push(word);
                            }
                        }
                    });
                });

                if ($scope.areaConfig.dropdown !== undefined){
                    $scope.areaConfig.dropdown.forEach(function(element){
                        if(typeof(element.trigger) === 'string' && autocomplete.indexOf(element.trigger) < 0){
                            autocomplete.push(element.trigger);
                        }
                    });
                }

                // Now with the list, filter and return
                autocomplete.forEach(function(word){
                    if(lastWord.length < word.length && word.toLowerCase().substr(0, lastWord.length) === lastWord.toLowerCase()){
                        suggestions.push({
                            display: $sce.trustAsHtml(word),
                            data: null
                        });
                    }
                });

                $scope.dropdown.customSelect = null;
                $scope.dropdown.current = 0;
                $scope.dropdown.content = suggestions;
            }

            /**
             * Get Character count on an editable field
             * http://stackoverflow.com/questions/4767848/get-caret-cursor-position-in-contenteditable-area-containing-html-content
             */
            function getCharacterPosition() {
              var el = $element[0];
              if (typeof(el.selectionEnd) == "number") {
                return el.selectionEnd;
              }
          }

            function htmlEncode(str) {
                var div = document.createElement("div");
                div[("textContent" in div) ? "textContent" : "innerText"] = str;
                return div.innerHTML;
            }

            /* +----------------------------------------------------+
             * +                   Event Binding                    +
             * +----------------------------------------------------+ */

            $element.bind('keyup click focus', function () {
                $timeout(function(){
                    $scope.trackCaret();
                }, 0);
            });

            $element.bind('keydown', function(event){
              resetScroll();
              $scope.keyboardEvents(event);
            });
        }]
    };
}]);