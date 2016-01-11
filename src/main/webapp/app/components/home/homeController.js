/**
 * Created by sanjukta on 02-07-2015.
 */
//home
define(['byApp', 'byUtil', 'homePromoController',
        'userTypeConfig',
        'byEditor', 'menuConfig'],
    function (byApp, byUtil, homePromoController, userTypeConfig, byEditor, menuConfig) {
        function BYHomeController($scope, $rootScope, $routeParams, $location) {
            $scope.homeSectionConfig    = BY.config.menu.home;
            $scope.homeimageConfig      = BY.config.menu.homeIcon;
            $scope.moduleConfig         = BY.config.menu.moduleConfig;
            $scope.menuMapConfig        = $rootScope.menuCategoryMap;
            $scope.menuConfig           = BY.config.menu;
            $scope.removeSpecialChars   = BY.byUtil.removeSpecialChars;
            $scope.telNo                = BY.config.constants.byContactNumber;

            var cntAnimDuration         = 1000,
                init                    = initialize();


            function updateMetaTags(){
                var metaTagParams = BY.config.seo.home;
                BY.byUtil.updateMetaTags(metaTagParams);
            }

            function initialize(){
                if($rootScope.totalServiceCount){
                    animateCounter($rootScope.totalServiceCount, $(".HomeSevicesCnt"));
                }

                if($rootScope.totalHousingCount){
                    animateCounter($rootScope.totalHousingCount, $(".HomeHousingCnt"));
                }

                if($rootScope.totalProductCount){
                    animateCounter($rootScope.totalProductCount, $(".HomeProductCnt"));
                }

                updateMetaTags();
            }


            function animateCounter(count, target) {
                $({someValue: 0}).animate({someValue: count}, {
                    duration: cntAnimDuration,
                    easing: 'swing',
                    step: function () {
                        target.text(Math.round(this.someValue));
                    }
                });
            };

            $scope.$on('directoryCountAvailable', function (event, args) {
                animateCounter($rootScope.totalServiceCount, $(".HomeSevicesCnt"));
                animateCounter($rootScope.totalHousingCount, $(".HomeHousingCnt"));
            });

            $scope.$on('productCountAvailable', function (event, args) {
                animateCounter($rootScope.totalProductCount, $(".HomeProductCnt"));
            });


            $(".by_ourExpertTop .by_ourExpertThumb").click(function(){
                var index = $(this).index();
                $(".by_ourExpertDesc").hide();
                $(".by_ourExpertThumbArrow").css('visibility', 'hidden');
                $(".by_ourExpertThumbImg").removeClass('by_ourExpertThumbImgActive');
                $(".by_ourExpertThumb").removeClass('by_ourExpertThumbColor');
                $(this).find(".by_ourExpertThumbArrow").css('visibility','visible');
                $(this).find(".by_ourExpertThumbImg").addClass('by_ourExpertThumbImgActive');
                $(this).addClass('by_ourExpertThumbColor');
                $(".by_ourExpertTop .by_ourExpertDesc").eq(index).show();
            });

             $(".by_ourExpertTop .by_ourExpertThumb").hover(function(){
                var index = $(this).index();
                $(".by_ourExpertDesc").hide();
                $(".by_ourExpertThumbArrow").css('visibility', 'hidden');
                $(".by_ourExpertThumbImg").removeClass('by_ourExpertThumbImgActive');
                $(".by_ourExpertThumb").removeClass('by_ourExpertThumbColor');
                $(this).find(".by_ourExpertThumbArrow").css('visibility','visible');
                $(this).find(".by_ourExpertThumbImg").addClass('by_ourExpertThumbImgActive');
                $(this).addClass('by_ourExpertThumbColor');
                $(".by_ourExpertTop .by_ourExpertDesc").eq(index).show();
            });

            $(".by_ourExpertTop2 .by_ourExpertThumb").click(function(){
                var index = $(this).index();
                $(".by_ourExpertDesc").hide();
                $(".by_ourExpertThumbArrow").css('visibility', 'hidden');
                $(".by_ourExpertThumbImg").removeClass('by_ourExpertThumbImgActive');
                $(".by_ourExpertThumb").removeClass('by_ourExpertThumbColor');
                $(this).find(".by_ourExpertThumbArrow").css('visibility','visible');
                $(this).find(".by_ourExpertThumbImg").addClass('by_ourExpertThumbImgActive');
                $(this).addClass('by_ourExpertThumbColor');
                $(".by_ourExpertTop2 .by_ourExpertDesc").eq(index).show();
            });

            $(".by_homeSectionInside").mouseleave(function(){
                 $(".by_ourExpertDesc").hide();
                $(".by_ourExpertThumbArrow").css('visibility', 'hidden');
                $(".by_ourExpertThumbImg").removeClass('by_ourExpertThumbImgActive');
                $(".by_ourExpertThumb").removeClass('by_ourExpertThumbColor');
            });

            $(".by_homeTextareaShow").click(function(){
                var tinyEditor = BY.byEditor.addEditor({"editorTextArea": "question_textArea"});
                $(".by_homeEditor").animate({width: '100%', height: '350px', marginBottom: '20px'}, "500");
                $(".by_homeEditorShow").show();
                $(".by_homeTextareaShow").hide();
                $(".by_homeTalk").animate({width: '100%'}, "500");
            });
        }

        BYHomeController.$inject = ['$scope', '$rootScope', '$routeParams', '$location'];
        byApp.registerController('BYHomeController', BYHomeController);

        return BYHomeController;
    });


