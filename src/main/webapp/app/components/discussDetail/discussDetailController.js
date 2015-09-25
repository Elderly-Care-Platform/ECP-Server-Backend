define(['byApp', 'byUtil', 'discussLikeController', 'discussDetailLeftController', 'discussReplyController', 'shareController'],
    function(byApp, byUtil, discussLikeController, discussDetailLeftController, discussReplyController, shareController) {
    function DiscussDetailController($scope, $rootScope, $routeParams, $location, DiscussDetail, $sce, broadCastData, $timeout){
        var discussId = $routeParams.discussId;	//discuss Id from url
        var isComment = $routeParams.comment;


        $scope.discussDetailViews = {};
        $scope.discussDetailViews.leftPanel = "app/components/discussDetail/discussDetailLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%";
        $scope.discussDetailViews.contentPanel = "app/components/discussDetail/discussDetailContentPanel.html?versionTimeStamp=%PROJECT_VERSION%";
        $("#preloader").show();


        var scrollToEditor = function(){
            if(isComment){
                $timeout(
                    function () {
                        var tag = $("#replyEditor:visible");
                        if (tag.length > 0) {
                            $('html,body').animate({scrollTop: tag.offset().top - $(".breadcrumbs").height() - $(".header").height()}, 'slow');
                        }
                    }, 100);
            }

        };

        DiscussDetail.get({discussId: discussId}, function (discussDetail, header) {
                //broadcast data to left panel, to avoid another query from left panel of detail page
                $scope.detailResponse = discussDetail.data;
                broadCastData.update(discussDetail.data.discuss);
                $scope.detailResponse.discuss.createdAt = discussDetail.data.discuss.createdAt;
                $("#preloader").hide();

                var metaTagParams = {
                    title:  $scope.detailResponse.discuss.title,
                    imageUrl:   BY.byUtil.getImage($scope.detailResponse.discuss),
                    description:    $scope.detailResponse.discuss.text,
                    keywords:[]
                }
                for(var i=0;i<$scope.detailResponse.discuss.systemTags.length ; i++){
                	metaTagParams.keywords.push($scope.detailResponse.discuss.systemTags[i].name);
                }
                BY.byUtil.updateMetaTags(metaTagParams);
                scrollToEditor();
            },
            function (error) {
                console.log("error");
            });



        $scope.trustForcefully = function (html) {
            return $sce.trustAsHtml(html);
        };
        $scope.trustAsResourceUrl = function(url) {
            return $sce.trustAsResourceUrl(url);
        };

        //update data in view after comments/answers are posted from child controller
        $scope.$on('handleBroadcast', function () {
            if (broadCastData.newData.discuss && discussId === broadCastData.newData.discuss.id) {
                $scope.detailResponse = broadCastData.newData;
            }
        });

        $scope.updateShareCount = function(count){
            $scope.detailResponse.discuss.shareCount = count;
        }
    }

    DiscussDetailController.$inject = ['$scope', '$rootScope', '$routeParams', '$location', 'DiscussDetail', '$sce', 'broadCastData', '$timeout'];
    byApp.registerController('DiscussDetailController', DiscussDetailController);
    return DiscussDetailController;
});