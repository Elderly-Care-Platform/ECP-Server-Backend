define([], function () {

    /* @ngInject */
    function SelectAddressFactory($rootScope, $location, $http, UserProfile) {
        var addressFormat = {
            "firstName": "",
            "lastName": "",
            "phoneNumber": "",
            "email": "",
            "address": {
                "city": "", "country": "", "locality": "", "streetAddress": "", "zip": ""
            },
            "userId": ""
        }

        return {
            //getCustomerProfile: getCustomerProfile,
            getAddress: getAddress,
            updateAddress: updateAddress,
            addNewAddress: addNewAddress,
            getAddressFormat: getAddressFormat
            //updateProfile:updateProfile
        };


        function getAddressFormat() {
            var addressFormat = {
                "firstName": "",
                "lastName": "",
                "phoneNumber": "",
                "email": "",
                "address": {
                    "city": "", "country": "", "locality": "", "streetAddress": "", "zip": ""
                },
                "userId": ""
            }
            return addressFormat;
        }

        function getAddress(addressIdx) {
            var userId = localStorage.getItem("USER_ID");
            if (userId) {
                if (addressIdx) {
                    return $http.get('api/v1/userAddress/' + userId + '?addressId=' + addressIdx);
                } else {
                    return $http.get('api/v1/userAddress/' + userId);
                }
            } else {
                $rootScope.nextLocation = "/selectAddress"
                $location.path('/users/login');
            }
        }

        function updateAddress(params) {
            var userId = localStorage.getItem("USER_ID");
            if (userId) {
                return $http.put('api/v1/userAddress/' + userId, params.address);
            } else {
                $rootScope.nextLocation = "/selectAddress"
                $location.path('/users/login');
            }
        }

        function addNewAddress(params) {
            var userId = localStorage.getItem("USER_ID");
            if (userId) {
                return $http.post('api/v1/userAddress/' + userId, params.address);
            } else {
                $rootScope.nextLocation = "/selectAddress"
                $location.path('/users/login');
            }
        }


        //function getCustomerProfile() {
        //    var userId = localStorage.getItem("USER_ID");
        //    if(userId){
        //        return $http.get('api/v1/userProfile/'+userId);
        //    }else{
        //        $rootScope.nextLocation = "/selectAddress"
        //        $location.path('/users/login');
        //    }
        //}

        //function getProfile(){
        //    var userId = localStorage.getItem("USER_ID");
        //    var userProfile = UserProfile.get({userId: userId});
        //    return userProfile;
        //}


        //function updateProfile(params, data){
        //    var userId = localStorage.getItem("USER_ID");
        //    if(userId) {
        //        var userProfile = new UserProfile();
        //        angular.extend(userProfile, data.profile);
        //        if(userProfile.userId){
        //            return $http.put('api/v1/userProfile/'+userId, userProfile);
        //        }else{
        //            return $http.post('api/v1/userProfile', userProfile);
        //        }
        //
        //    }
        //
        //
        //    //userProfile.$update({userId: $scope.userId}, function (profileOld) {
        //    //    console.log("success");
        //    //    $scope.submitted = false;
        //    //    $scope.$parent.exit();
        //    //}, function (err) {
        //    //    console.log(err);
        //    //    $scope.$parent.exit();
        //    //});
        //}

    }

    return SelectAddressFactory;
});