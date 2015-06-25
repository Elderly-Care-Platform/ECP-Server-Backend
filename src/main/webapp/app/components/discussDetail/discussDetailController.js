byControllers.controller('DiscussDetailController', ['$scope', '$rootScope', '$routeParams', '$location', 'DiscussDetail', '$sce','broadCastData',
    function ($scope, $rootScope, $routeParams, $location, DiscussDetail, $sce, broadCastData) {

        var discussId = $routeParams.discussId;	//discuss Id from url
        $scope.discussDetailViews = {};

        $scope.discussDetailViews.leftPanel = "app/components/discussDetail/discussDetailLeftPanel.html";
        $scope.discussDetailViews.contentPanel = "app/components/discussDetail/discussDetailContentPanel.html";

        $scope.detailResponse = DiscussDetail.get({discussId: discussId}, function(discussDetail, header){
            //broadcast data to left panel, to avoid another query from left panel of detail page
            broadCastData.update(discussDetail.discuss);
        });

        $scope.trustForcefully = function (html) {
            return $sce.trustAsHtml(html);
        };

        //update data in view after comments/answers are posted from child controller
        $scope.$on('handleBroadcast', function() {
            if(broadCastData.newData.discuss && discussId === broadCastData.newData.discuss.id){
                $scope.detailResponse = broadCastData.newData;
            }
        });
    }]);


byControllers.controller('DiscussReplyController', ['$scope', '$rootScope', '$routeParams', '$location', 'DiscussDetail', '$sce','broadCastData',
    function ($scope, $rootScope, $routeParams, $location, DiscussDetail, $sce, broadCastData) {
        $scope.showEditor = false;
        $scope.trustForcefully = function (html) {
            return $sce.trustAsHtml(html);
        };

        $scope.createNewComment = function(commentId){
            $scope.showEditor = true;
            BY.addEditor({"editorTextArea":commentId, "commentEditor" : true, "autoFocus":true});
            tinyMCE.execCommand('mceFocus', false, commentId);
        };

        $scope.disposeComment  = function(typeId){
            $scope.showEditor = false;
            tinyMCE.activeEditor.setContent('');
            if(tinyMCE.activeEditor){
                tinyMCE.activeEditor.remove();
            }
        };

        //Post method called from comments or answers of main detail discuss
        $scope.postComment = function(discussId, parentReplyId){
            $scope.discussReply = new DiscussDetail();
            $scope.discussReply.parentReplyId = parentReplyId ?  parentReplyId : "";
            $scope.discussReply.discussId = discussId;
            $scope.discussReply.text = tinyMCE.activeEditor.getContent();

            $scope.discussReply.$postComment(function (discussReply, headers) {
                broadCastData.update(discussReply); //broadcast data for parent controller to update the view with latest comment/answer
                $scope.disposeComment();           //dispose comment editor and remove tinymce after successful post of comment/answer
            });
        };


        //Post method called from main detail discuss
        $scope.postReply = function(discussId, discussType){
            if(discussType==="Q"){
                $scope.discussReply = new DiscussDetail();
                $scope.discussReply.discussId = discussId;
                $scope.discussReply.text = tinyMCE.activeEditor.getContent();

                $scope.discussReply.$postAnswer(function (discussReply, headers) {
                    broadCastData.update(discussReply); //broadcast data for parent controller to update the view with latest comment/answer
                    $scope.disposeComment();           //dispose comment editor and remove tinymce after successful post of comment/answer
                });
            }else{
                $scope.postComment(discussId);
            }
        };

    }]);

byControllers.controller('DiscussLikeController', ['$scope', '$rootScope','DiscussLike',
    function ($scope, $rootScope, DiscussLike) {
        $scope.beforePost = true;

        $scope.likeDiscuss = function(discussId){
            $scope.discussLike = new DiscussLike();
            $scope.discussLike.discussId = discussId;
            $scope.discussLike.$likeDiscuss(function(likeReply, headers){
                $scope.beforePost = false;
                $scope.aggrLikeCount = likeReply.aggrLikeCount;
            });
        }

        $scope.likeComment = function(commentId, replyType){
            $scope.discussLike = new DiscussLike();
            $scope.discussLike.replyId = commentId;

            if(replyType===6){
                $scope.discussLike.$likeAnswer(function(likeReply, headers){
                    $scope.beforePost = false;
                    $scope.aggrLikeCount = likeReply.likeCount;
                });
            }else{
                $scope.discussLike.$likeComment(function(likeReply, headers){
                    $scope.beforePost = false;
                    $scope.aggrLikeCount = likeReply.likeCount;
                });
            }

        }

    }]);