/**
 * Created by sanjukta on 02-07-2015.
 */
//home
byControllers.controller('BYHomeController', ['$scope', '$rootScope', '$routeParams', '$timeout', '$location', 'DiscussPage', '$sce',
    function ($scope, $rootScope, $routeParams, $timeout, $location, DiscussPage, $sce) {
        $scope.editor = {};
        $scope.error = "";
        $scope.editor.subject = "";
        $scope.editor.articlePhotoFilename = "";
        $scope.currentAcceleratorSelected = "";
        $scope.$watch("articles", function (value) {
            $timeout(
                function () {
                    $scope.scrollToId($scope.currentAcceleratorSelected)
                }, 100);
        });

        $scope.homeViews = {};


        $scope.add = function (type) {
            //BY.removeEditor();
            $scope.error = "";
            $scope.currentView = "editor";
            $scope.homeViews.contentPanel = "app/shared/editor/" + type + "EditorPanel.html?versionTimeStamp=%PROJECT_VERSION%";
            window.scrollTo(0, 0);
        }

        $scope.postSuccess = function () {
            $scope.switchToContentView();
        };


        $scope.switchToContentView = function (scrollTo) {
            $scope.currentAcceleratorSelected = scrollTo || $scope.currentAcceleratorSelected;
            if ($scope.currentView != "content") {
                $scope.currentView = "content";
                $scope.homeViews.leftPanel = "app/components/home/homeLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%";
                $scope.homeViews.contentPanel = "app/components/home/homeContentPanel.html?versionTimeStamp=%PROJECT_VERSION%";
                DiscussPage.get({discussType: 'A',isFeatured:false,p:0,s:3,sort:"lastModifiedAt"},
                		function(value){
                				$scope.articles = value.data.content;
                		},
                		function(error){
        			       	console.log("DiscussPage");
//        			       	alert("error");
                		});
                DiscussPage.get({discussType: 'P',isFeatured:false,p:0,s:3,sort:"lastModifiedAt"},
                		function(value){
                				$scope.posts = value.data.content;
                		},
                		function(error){
        			       	console.log("DiscussPage");
//        			       	alert("error");
                		});
                DiscussPage.get({discussType: 'Q',isFeatured:false,p:0,s:3,sort:"lastModifiedAt"},
                		function(value){
                				$scope.questions = value.data.content;
                		},
                		function(error){
        			       	console.log("DiscussPage");
//        			       	alert("error");
                		});
            } else {
                $scope.scrollToId(scrollTo);
            }
        }

        //$scope.switchToContentView();

        $scope.scrollToId = function (id) {
            if (id) {
                var tag = $("#" + id + ":visible");
                if (tag.length > 0) {
                    $('html,body').animate({scrollTop: tag.offset().top - $(".breadcrumbs").height() - $(".header").height()}, 'slow');
                }
            } else {
                window.scrollTo(0, 0);
            }

        }
        $scope.trustForcefully = function (html) {
            return $sce.trustAsHtml(html);
        };

        if ($routeParams.type === "aboutUs") {
            $scope.currentView = "aboutUs";
            //$scope.homeViews.contentPanel = "'app/components/aboutUs/aboutUs.html?nitin=jain'";
            //$scope.homeViews.leftPanel = "'app/components/aboutUs/aboutUsContentPanel.html?nitin=jain'";
        } else {
            $scope.currentView = "";
            $scope.switchToContentView();

        }

        $scope.go = function ($event, type, id, discussType) {
            $event.stopPropagation();
            if (type === "id") {
                $location.path('/discuss/' + id);
            } else if (type === "name" && $rootScope.discussCategoryListMap) {
                var parentCategoryId = $rootScope.discussCategoryListMap[id].parentId;
                var parentCategoryName = parentCategoryId ? $rootScope.discussCategoryListMap[parentCategoryId].name : null;

                if (parentCategoryName) {
                    $location.path('/discuss/All/' + parentCategoryName + '/' + $rootScope.discussCategoryListMap[id].name);
                } else {
                    $location.path('/discuss/All/' + $rootScope.discussCategoryListMap[id].name + '/all');
                }
            } else if (type = "accordian") {
                $($event.target).find('a').click();
            }

        }

        $scope.$on('ngRepeatFinished', function (ngRepeatFinishedEvent) {

            $('p').each(function () {
                var $this = $(this);
                if ($this.html().replace(/\s|&nbsp;/g, '').length == 0)
                    $this.remove();
            });

            $('.by_story').dotdotdot();
        });

    }]);

