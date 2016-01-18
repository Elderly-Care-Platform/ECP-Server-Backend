define(['byApp'], function (byApp) {

    function DisService($location) {
        return {
            getDiscussDetailUrl: getDiscussDetailUrl,
            formatData : formatData
        };

        function getDiscussDetailUrl(discuss, urlQueryParams, isAngularLocation){
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
            var newHref = "/communities/"+disTitle ;


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

        function formatData(discussObj) {
                var formattedData = [], title, id, image;
                for (var i = 0; i < discussObj.length; i++) {
                    title = getShortTitle(discussObj[i]);
                    id = discussObj[i].id;
                    if (discussObj[i].articlePhotoFilename != null) {
                        image = discussObj[i].articlePhotoFilename.original;
                    }
                    formattedData.push({
                        title: title,
                        image: image,
                        id: id
                    });
                };
                return formattedData;
            }

            function getShortTitle(discuss) {
                var disTitle = "";
                if (discuss.discussType == 'Q') {
                    disTitle = discuss.text;
                } else if (discuss.discussType == 'P' && discuss.title && discuss.title.trim().length > 0) {
                    disTitle = discuss.title;
                } else if (discuss.discussType == 'P' && discuss.linkInfo && discuss.linkInfo.title && discuss.linkInfo.title.trim().length > 0) {
                    disTitle = discuss.linkInfo.title;
                } else if(discuss.discussType == 'P' && discuss.shortSynopsis){
                    disTitle = discuss.shortSynopsis;
                } else{
                    disTitle = "";
                }

                disTitle = BY.byUtil.getShortTitle(disTitle);
                return disTitle;
            };
    }

    byApp.registerService('DisService', DisService);
    return DisService;
});
