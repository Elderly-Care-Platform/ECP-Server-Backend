define(['byApp', 'byUtil', 'LoginController', 'registrationConfig'], function(byApp, byUtil, LoginController, registrationConfig) {
    function RegistrationController($scope, $rootScope, $http, $location, $routeParams, UserProfile){
        $scope.views = {};
        $scope.views.leftPanel = "";
        $scope.profile = null;
        $scope.housingFacilityTabs = [];
        $scope.sectionLabel = null;
        $scope.userType = null;
        $scope.facilityIdx = $routeParams.facilityIndex ? parseInt($routeParams.facilityIndex) : 0;

        $scope.changeUsername = function (elemClassName) {
            $(".list-group-item").removeClass('active');
            $(".username").addClass('active');
            $scope.views.leftPanel = "app/components/signup/registrationLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%";
            $scope.views.contentPanel = "app/components/signup/login/modifyUsername.html?versionTimeStamp=%PROJECT_VERSION%";
        };
        $scope.changePassword = function (elemClassName) {
            $(".list-group-item").removeClass('active');
            $(".password").addClass('active');
            $scope.views.leftPanel = "app/components/signup/registrationLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%";
            $scope.views.contentPanel = "app/components/signup/login/modifyPassword.html?versionTimeStamp=%PROJECT_VERSION%";
        };

        $scope.editUserProfile = function (elemClassName) {
            if ($scope.profile && $scope.profile.userTypes && $scope.profile.userTypes.length) {
                $(".list-group-item").removeClass('active');
                $("."+elemClassName).addClass('active');
                $scope.views.contentPanel = $scope.userTypeConfig.contentPanel;
            } else {
                $scope.getUserProfile();
                $(".list-group-item").removeClass('active');
                $("."+elemClassName).addClass('active');
            }
        };

        $scope.updateLeftPanel = function(){
            $scope.views.leftPanel = $scope.userTypeConfig.leftPanel;
            $scope.userType  = $scope.profile.userTypes[0];
            if($scope.profile.userTypes[0]===3){
                if($scope.profile.facilities && $scope.profile.facilities.length > 0){
                    for(var i=0; i<$scope.profile.facilities.length; i++){
                        if($scope.profile.facilities[i].name && $scope.profile.facilities[i].name.trim().length > 0){
                            $scope.housingFacilityTabs.push($scope.profile.facilities[i].name);
                        } else{
                            $scope.housingFacilityTabs.push("Facility"+(i+1));
                        }
                        if($scope.facilityIdx==i){
                        	$scope.facilityProfileId = $scope.profile.facilities[i].id;
                        }
                        
                    }
                }else{
                    $scope.sectionLabel = $scope.userTypeConfig.label;
                }

                if($routeParams.facilityIndex){
                    if($scope.facilityIdx > $scope.profile.facilities.length){
                        $scope.housingFacilityTabs.push("Facility"+$scope.facilityIdx);
                        $scope.facilityIdx = $scope.facilityIdx - 1;
                    }
                }
            } else {
                $scope.sectionLabel = $scope.userTypeConfig.label;
            }
        };

        $scope.updateContentPanel = function(){
            if($routeParams.changeUserName) {
                require(["modifySignupCtrl"], function(regCtrl) {
                    $scope.changeUsername();
                    $scope.$apply();
                });

            } else if($routeParams.changeUserPwd){
                require(["modifySignupCtrl"], function(regCtrl) {
                    $scope.changePassword();
                    $scope.$apply();
                });
            } else{
                require([$scope.userTypeConfig.controller], function(regCtrl) {
                    $scope.views.contentPanel = $scope.userTypeConfig.contentPanel;
                    $scope.$apply();
                    if (!$scope.views.contentPanel || $scope.views.contentPanel == "") {
                        $scope.exit();
                    }

                });
            }
        };

        $scope.getUserProfile = function (regLevel) {
            $scope.userId = localStorage.getItem("USER_ID");
            $scope.userName = localStorage.getItem("USER_NAME");

            $scope.userProfile = UserProfile.get({userId: $scope.userId}, function (profile) {
                $scope.profile = profile.data;
                if ($scope.profile.userTypes.length > 0) {
                    $scope.userTypeConfig = BY.config.regConfig.userTypeConfig[$scope.profile.userTypes[0]];
                    $scope.updateLeftPanel();
                    $scope.updateContentPanel();
                } else {
                    $scope.views.leftPanel = BY.config.regConfig.userTypeConfig[-1].leftPanel;
                    if($routeParams.changeUserName) {
                        require(["modifySignupCtrl"], function(regCtrl) {
                            $scope.changeUsername();
                            $scope.$apply();
                        });

                    } else if($routeParams.changeUserPwd){
                        require(["modifySignupCtrl"], function(regCtrl) {
                            $scope.changePassword();
                            $scope.$apply();
                        });
                    } else{
	                    require([BY.config.regConfig.userTypeConfig[-1].controller], function(regCtrl) {
	                        $scope.views.contentPanel = BY.config.regConfig.userTypeConfig[-1].contentPanel;
	                        $scope.$apply();
	                    });
                    }

                }
            });
        }


        $scope.exit = function () {
            if ($rootScope.nextLocation) {
                $location.path($rootScope.nextLocation);
            }
            else {
                $location.path("/users/home");
            }
        }

        $scope.showFacility = function(facilityIdx){
            $location.path('/users/housingRegistration/'+ facilityIdx);
        }
        

        $scope.initialize = function(){
            var metaTagParams = {
                title: "Beautiful Years | Registration",
                imageUrl: "",
                description: "",
                keywords:[]
            }
            BY.byUtil.updateMetaTags(metaTagParams);

            if (localStorage.getItem('SessionId') == '' || localStorage.getItem('SessionId') == undefined) {
                $scope.views.leftPanel = "app/components/signup/login/loginLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%";
                $scope.views.contentPanel = "app/components/signup/login/login.html?versionTimeStamp=%PROJECT_VERSION%";
            } else{
                $scope.getUserProfile();
            }
        };
    }

    RegistrationController.$inject = ['$scope', '$rootScope', '$http', '$location', '$routeParams', 'UserProfile'];
    byApp.registerController('RegistrationController', RegistrationController);

    return RegistrationController;
});