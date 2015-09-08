byControllers.controller('ShareController', ['$scope', '$rootScope', '$location','ValidateUserCredential','ShareDiscuss',
    function ($scope, $rootScope, $location, ValidateUserCredential,ShareDiscuss) {
        $scope.shareComment = function(sharedObj, $event){
        	$event.stopPropagation();
            if(FB && sharedObj){
                var caption = sharedObj.title ? sharedObj.title: "BeautifulYears",
                    picture = sharedObj.articlePhotoFilename ? sharedObj.articlePhotoFilename.thumbnailImage: "",
                    description = sharedObj.text ? $(sharedObj.text).text() : "";

                if(picture && picture!==""){
                    picture = picture.substr(1);
                    picture = window.location.origin + window.location.pathname + picture;

                }else if(sharedObj.linkInfo && sharedObj.linkInfo.mainImage){
                	picture = sharedObj.linkInfo.mainImage;
                }else if(sharedObj.linkInfo && sharedObj.linkInfo.otherImages && sharedObj.linkInfo.otherImages.length > 0){
                	picture = sharedObj.linkInfo.otherImages[0];
                }
                
                if((!description || description =="") && (sharedObj.linkInfo)){
                	description = sharedObj.linkInfo.description || "";
                	caption = caption || sharedObj.linkInfo.title || "BeautifulYears";
                }

                FB.ui({
                    method: 'feed',
                    link: window.location.origin + "/#!/discuss/"+sharedObj.id,
                    picture: picture,
                    caption: "Beautiful Years",
                    description: description,
                    name:caption
                }, function(response){
                    console.log(response);
                    if (response && response.post_id) {
                        var shareDiscuss = new ShareDiscuss();
                        shareDiscuss.id = sharedObj.id;
                        shareDiscuss.$post({},function(res){
                        	$scope.$parent.updateShareCount(res.data.shareCount);
                        },function(err){
                        	console.log("alert posting the share count");
                        });
                    } else {
                        console.log('Post was not published.');
                    }
                });
            }
        }
    }]);