define(['byApp',
    'discussLikeController',
    'shareController',
    'byEditor', 'menuConfig', 'blogMasonary', 'jqueryMasonaryGrid'], function (byApp, discussLikeController, shareController, byEditor, menuConfig, blogMasonary, jqueryMasonaryGrid) {

    'use strict';

    function ProductReviewsCtrl($scope, $rootScope, $location, $route, $routeParams, DiscussPage,
                                 DiscussCount, $sce, $timeout) {

        window.scrollTo(0, 0);
        $scope.discussType = $routeParams.discussType ? $routeParams.discussType : 'all'; //Needed for left side Q/A/P filters
        $scope.selectedMenu = $scope.$parent.selectedMenu;
        $scope.pageSize = 20;
        $scope.isGridInitialized = false;

        var tags = [];
        var queryParams = {p: 0, s: $scope.pageSize, sort: "lastModifiedAt"};

        $scope.initGrid = function (index) {
            if ($rootScope.windowWidth > 800) {
                var gridMasonary = $(".masonry");
                window.setTimeout(function () {
                    masonaryGridInit();
                    $(".masonry").masonry("reload");
                }, 100);

            }
            else if ($rootScope.windowWidth < 800) {
                $(".grid-boxes-in").removeClass('grid-boxes-in');
                $("#preloader").hide();
            }
            window.scrollTo(0, 0);
            //masonaryGridInit();
        };

        function updateMetaTags(){
            var metaTagParams = {
                title: $scope.selectedMenu.displayMenuName,
                imageUrl: "",
                description: "",
                keywords: [$scope.selectedMenu.displayMenuName, $scope.selectedMenu.slug]
            }
            BY.byUtil.updateMetaTags(metaTagParams);
        }

        $scope.initDiscussListing = function () {
            if ($scope.selectedMenu) {
                updateMetaTags();
                //tags = $.map($scope.selectedMenu.tags, function (value, key) {
                //    return value.id;
                //})
                //queryParams.tags = tags.toString();  //to create comma separated tags list

                queryParams.tags = BY.config.menu.reveiwsMenuConfig['product_review'].tag;
                if ($scope.discussType && $scope.discussType.toLowerCase().trim() !== "all") {
                    queryParams.discussType = $routeParams.discussType;
                }

                DiscussCount.get({tags: queryParams.tags, contentTypes: "f,total,p,q"}, function (counts) {
                        $scope.discuss_counts = counts.data;
                    },
                    function (error) {
                        console.log(error);
                    }
                );

                if (queryParams.discussType === 'f') {
                    queryParams.isFeatured = true;
                    delete queryParams.discussType;
                }

                $scope.getDiscussData = function (page, size) {
                    $("#preloader").show();
                    queryParams.p = page;
                    queryParams.s = size;
                    DiscussPage.get(queryParams,
                        function (value) {
                            $scope.discussList = value.data.content;
                            $scope.pageInfo = BY.byUtil.getPageInfo(value.data);
                            $scope.pageInfo.isQueryInProgress = false;
                            $scope.discussPagination = {};
                            $scope.discussPagination.totalPosts = value.data.total;
                            $scope.discussPagination.noOfPages = Math.ceil(value.data.total / value.data.size);
                            $scope.discussPagination.currentPage = value.data.number;
                            $scope.discussPagination.pageSize = $scope.pageSize;

                            /*if ($scope.discussList.length === 0) {
                                $("#preloader").hide();
                            }*/

                            $("#preloader").hide();
                        },
                        function (error) {
                            $("#preloader").hide();
                            console.log(error);
                        });
                }

                $scope.getDiscussData(0, $scope.pageSize);

            };
        }


        $scope.trustForcefully = function (html) {
            return $sce.trustAsHtml(html);
        };

        $scope.trustAsResourceUrl = function (url) {
            return $sce.trustAsResourceUrl(url);
        };

        $scope.nextLocation = function($event, discuss, queryParams){
            $event.stopPropagation();
            var url = getDiscussDetailUrl(discuss, queryParams, true);
            $location.path(url);
        };
        
        $scope.getHref = function(discuss, queryParams){
        	var newHref = getDiscussDetailUrl(discuss, queryParams, false);
            newHref = "#!" + newHref;
            return newHref;
        };

        function getDiscussDetailUrl(discuss, queryParams, isAngularLocation){
            var disTitle = "others";
            if(discuss.title && discuss.title.trim().length > 0){
                disTitle = discuss.title;
            } else if(discuss.text && discuss.text.trim().length > 0){
                disTitle = discuss.text;
            } else if(discuss.linkInfo && discuss.linkInfo.title && discuss.linkInfo.title.trim().length > 0){
                disTitle = discuss.linkInfo.title;
            } else{
                disTitle = "others";
            }

            disTitle = BY.byUtil.getSlug(disTitle);
            var newHref = "/communities/"+disTitle;


            if(queryParams && Object.keys(queryParams).length > 0){
                //Set query params through angular location search method
                if(isAngularLocation){
                    angular.forEach($location.search(), function (value, key) {
                        $location.search(key, null);
                    });
                    angular.forEach(queryParams, function (value, key) {
                        $location.search(key, value);
                    });
                } else{ //Set query params manually
                    newHref = newHref + "?"
                    angular.forEach(queryParams, function (value, key) {
                        newHref = newHref + key + "=" + value + "&";
                    });

                    //remove the last  '&' symbol from the url, otherwise browser back does not work
                    newHref = newHref.substr(0, newHref.length - 1);
                }
            }

            return newHref;
        };


        $scope.getHrefProfile = function(profile, urlQueryParams){
        	var newHref = getProfileDetailUrl(profile, urlQueryParams, false);
            newHref = "#!" + newHref;
            return newHref;
        };

        function getProfileDetailUrl(profile, urlQueryParams, isAngularLocation){
        	var proTitle = "others";
        	 if(profile && profile.userProfile && profile.userProfile.basicProfileInfo.firstName && profile.userProfile.basicProfileInfo.firstName.length > 0){
        		 proTitle = profile.userProfile.basicProfileInfo.firstName;
        		 if(profile.userProfile.individualInfo.lastName && profile.userProfile.individualInfo.lastName != null && profile.userProfile.individualInfo.lastName.length > 0){
        			 proTitle = proTitle + " " + profile.userProfile.individualInfo.lastName;
        		 }
        	 } else if(profile && profile.username && profile.username.length > 0){
        		 proTitle = BY.byUtil.validateUserName(profile.username);
        	 }else{
        		 proTitle = "others";
        	 }

        	proTitle = BY.byUtil.getSlug(proTitle);
            var newHref = "/users/"+proTitle;


            if(urlQueryParams && Object.keys(urlQueryParams).length > 0){
                //Set query params through angular location search method
                if(isAngularLocation){
                    angular.forEach($location.search(), function (value, key) {
                        $location.search(key, null);
                    });
                    angular.forEach(urlQueryParams, function (value, key) {
                        $location.search(key, value);
                    });
                } else{ //Set query params manually
                    newHref = newHref + "?"

                    angular.forEach(urlQueryParams, function (value, key) {
                        newHref = newHref + key + "=" + value + "&";
                    });

                    //remove the last  '&' symbol from the url, otherwise browser back does not work
                    newHref = newHref.substr(0, newHref.length - 1);
                }
            }

            return newHref;
        };


    }


    ProductReviewsCtrl.$inject = ['$scope', '$rootScope', '$location', '$route', '$routeParams',
        'DiscussPage', 'DiscussCount', '$sce', '$timeout'];

    byApp.registerController('ProductReviewsCtrl', ProductReviewsCtrl);
    return ProductReviewsCtrl;

});
