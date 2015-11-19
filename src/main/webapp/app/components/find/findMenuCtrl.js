define(['byApp', 'serviceOverviewCtrl'], function (byApp, serviceOverviewCtrl) {
    'use strict';

    function FindMenuCtrl($scope, $rootScope, $window, $location, $route, $routeParams) {
        $scope.selectedMenuId       = $routeParams.menuId;
        var selectedNode            = $rootScope.menuCategoryMap[$scope.selectedMenuId];
        $scope.selectedParent       = $rootScope.menuCategoryMap[selectedNode.ancestorIds[selectedNode.ancestorIds.length - 1]];
        $scope.menuConfig           = BY.config.menu;

        $scope.expandParent = function (menuId) {
            if (menuId && menuId.toString() == $scope.selectedMenuId) {
                var selectedNode = $rootScope.menuCategoryMap[$scope.selectedMenuId];
                $scope.selectedParent = $rootScope.menuCategoryMap[selectedNode.ancestorIds[selectedNode.ancestorIds.length - 1]];
                var target = $("#" + $scope.selectedParent.id).children('ul.tree');
                target.toggle(200, function () {
                    if (target.is(':visible')) {
                        $("#" + $scope.selectedParent.id).children('.by_treeMenuIcon').addClass('by_treeMenuIconMinus');
                    } else {
                        $("#" + $scope.selectedParent.id).children('.by_treeMenuIcon').removeClass('by_treeMenuIconMinus');
                    }
                });
            }
        };

        $scope.smartScroll = function () {
            var clientHeight = $( window ).height();
            $(".by_subMenuPlus").css('min-height', (clientHeight - 57)+"px");
            angular.element($window).bind("scroll", function () {
                var winTop = $(this).scrollTop(),
                    winBottom = winTop + $(this).height(),
                    left = $('.by_subMenuPlus'),
                    leftBottom = left.height() + 261;

                //when the user reached the bottom of '#leftShort' set its position to fixed to prevent it from moving on scroll
                if (winBottom >= leftBottom) {

                    left.css({
                        'position': 'fixed',
                        'bottom': '0px'
                    });
                } else {
                    //when the user scrolls back up revert its position to relative
                    left.css({
                        'position': 'relative',
                        'bottom': 'auto'
                    });
                }
            });
        };

        $scope.toggleMenu = function ($event) {
            //console.log($($event.target).parent().children('ul.tree'));
            var target = $($event.target).parent().children('ul.tree');
            target.toggle(200, function () {
                if (target.is(':visible')) {
                    $($event.target).parent().children('.by_treeMenuIcon').addClass('by_treeMenuIconMinus');
                } else {
                    $($event.target).parent().children('.by_treeMenuIcon').removeClass('by_treeMenuIconMinus');
                }
            });
        }

        $scope.subMenuTabMobileShow = function () {
            if ($(".by_mobile_leftPanel_hide").css('left') == '0px') {
                $(".by_mobile_leftPanel_image").animate({left: "0%"}, {duration: 400});
                $(".by_mobile_leftPanel_image").css('background', "url('assets/img/community/mobile/humburger.png?versionTimeStamp=%PROJECT_VERSION%')");
                $(".by_mobile_leftPanel_hide").animate({left: "-90%"}, 400, function () {
                    $scope.expandParent();
                });
            } else {
                $(".by_mobile_leftPanel_image").animate({left: "90%"}, {duration: 400});
                $(".by_mobile_leftPanel_image").css('background', "url('assets/img/community/mobile/humburger-min.png?versionTimeStamp=%PROJECT_VERSION%')");
                $(".by_mobile_leftPanel_hide").animate({left: "0%"}, 400, function () {
                    $scope.expandParent();
                });
            }
        };

        if($scope.showEditor == false){
            $scope.showEditorPage = function(){
                var serviceReviewTag = $rootScope.menuCategoryMap[$scope.menuConfig.reveiwsMenuConfig['service_review'].id].tags[0];
                $location.search('showEditor', 'true');
                $location.search('postCategoryTag', JSON.stringify(serviceReviewTag));
                BY.byEditor.removeEditor();
                var menuId = $scope.menuConfig.reveiwsMenuConfig['service_review'].id;
                $location.path("/services/overview/list/service_review/"+menuId+"/all");
            }
        }

    }


    FindMenuCtrl.$inject = ['$scope', '$rootScope', '$window', '$location', '$route', '$routeParams'];
    byApp.registerController('FindMenuCtrl', FindMenuCtrl);
    return FindMenuCtrl;

});
