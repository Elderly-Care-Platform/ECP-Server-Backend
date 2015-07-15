byControllers.controller('regUserTypeController', ['$scope', '$rootScope', '$http', '$location', '$routeParams','UserProfile',
    function ($scope, $rootScope, $http, $location, $routeParams, UserProfile) {
        $scope.userCategory = "";
        $scope.individualUserType = [
            {key:'0', value:"I take care of a senior person", category:"indv"},
            {key:'1', value:"I am not that young, but I am young at heart", category:"indv"},
            {key:'2', value:"I volunteer with senior people", category:"indv"},
            {key:'7', value:"I am an elder care professional", category:"indv"
            }];

        $scope.institutionUserType = [
            {key:'3', value:"Senior living facilities", category:"inst"},
            {key:'4', value:"Services for seniors & elder care", category:"inst"}];

        $scope.otherUserType = [
            {key:'-1', value:"None of the above!", category:"other"}];

        $scope.selectedUserType = {};

        $scope.selectUserType = function(element){
            if(element.type.selected){
                $scope.userCategory = element.type.category;
                $scope.selectedUserType[element.type.key] = element.type.category;

                if($scope.userCategory === $scope.individualUserType[0].category){
                    $scope.unSelectUserType($scope.institutionUserType);
                    $scope.unSelectUserType($scope.otherUserType);
                }

                if($scope.userCategory === $scope.institutionUserType[0].category){
                    $scope.unSelectUserType($scope.individualUserType);
                    $scope.unSelectUserType($scope.otherUserType);
                }

                if($scope.userCategory === $scope.otherUserType[0].category){
                    $scope.unSelectUserType($scope.individualUserType);
                    $scope.unSelectUserType($scope.institutionUserType);
                }
            } else {
                $scope.userCategory = "";
                delete $scope.selectedUserType[element.type.key];
            }

        }

        $scope.unSelectUserType = function(userArr){
            angular.forEach(userArr, function (type) {
                type.selected = false;
                delete $scope.selectedUserType[type.key];
            });
        }

        $scope.submit = function(){
            $scope.userProfile = new UserProfile();
            $scope.userProfile.userId = localStorage.getItem("USER_ID");
            $scope.userProfile.userTypes = $.map($scope.selectedUserType, function(value, key){
                return parseInt(key);
            })
            $scope.userProfile.$post(function(profile, headers){
                console.log("success");
                $scope.$parent.updateRegistration();
            }, function(error){
                console.log("error");
                $scope.$parent.exit();
            });
        }

        $scope.cancel = function(){
            console.log("return");
            $scope.$parent.exit();
        }


    }]);