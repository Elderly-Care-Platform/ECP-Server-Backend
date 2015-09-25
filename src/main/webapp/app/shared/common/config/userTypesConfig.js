var BY = BY || {};
BY.config = BY.config || {};
BY.config.profile = BY.config.profile || {};

BY.config.profile.userType = {
    '0' : {
        'category':'0',
        'leftPanel':'app/components/profile/profileLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%',
        'contentPanel':'app/components/profile/indvUserProfile.html?versionTimeStamp=%PROJECT_VERSION%',
        'controller':'indvUserProfileCtrl',
        'type':'CAREGIVER',
        'label':'Personal Story'
    },
    '1' : {
        'category':'0',
        'leftPanel':'app/components/profile/profileLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%',
        'contentPanel':'app/components/profile/indvUserProfile.html?versionTimeStamp=%PROJECT_VERSION%',
        'controller':'indvUserProfileCtrl',
        'type':'ELDER',
        'label':'Personal Story'
    },
    '2' : {
        'category':'0',
        'leftPanel':'app/components/profile/profileLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%',
        'contentPanel':'app/components/profile/indvUserProfile.html?versionTimeStamp=%PROJECT_VERSION%',
        'controller':'indvUserProfileCtrl',
        'type':'VOLUNTEER',
        'label':'Personal Story'
    },
    '3' : {
        'category':'1',
        'leftPanel':'app/components/profile/housingProfileLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%',
        'contentPanel':'app/components/profile/housingProfile.html?versionTimeStamp=%PROJECT_VERSION%',
        'controller':'housingProfileCtrl',
        'leftPanelCtrl':'housingProfileLeftCtrl',
        'reviewContentType':'6',
        'type':'HOUSING',
        'label':'Corporate'
    },
    '4' : {
        'category':'1',
        'leftPanel':'app/components/profile/profileLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%',
        'contentPanel':'app/components/profile/instProfile.html?versionTimeStamp=%PROJECT_VERSION%',
        'controller':'instProfileCtrl',
        'type':'SERVICES',
        'reviewContentType':'4',
        'profileImage':'assets/img/profile/instituation.png?versionTimeStamp=%PROJECT_VERSION%',
        'label':'Institution Info'
    },
    '5' : {
        'category':'1',
        'contentPanel':'',
        'type':'PRODUCTS'
    },
    '6' : {
        'category':'1',
        'contentPanel':'',
        'type':'NGO'
    },
    '7' : {
        'category':'0',
        'leftPanel':'app/components/profile/profileLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%',
        'contentPanel':'app/components/profile/profUserProfile.html?versionTimeStamp=%PROJECT_VERSION%',
        'controller':'profUserProfileCtrl',
        'type':'PROFESSIONAL',
        'reviewContentType':'5',
        'profileImage':'assets/img/profile/individual.png?versionTimeStamp=%PROJECT_VERSION%',
        'label':'Professional Info'
    }

}

BY.config.profile.userCategory = {
    '0':'INDIVIDUAL',
    '1':'INSTITUTION'
}

BY.config.profile.userGender = {
    '0':'Ms.',
    '1':'Mr.'
}


BY.config.profile.rate = {
    'lowerLimit':'0',
    'upperLimit':'5'
}
