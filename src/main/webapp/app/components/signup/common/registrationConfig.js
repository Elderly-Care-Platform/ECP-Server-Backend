var BY = BY || {};
BY.config = BY.config || {};
BY.config.regConfig = BY.config.regConfig || {};


BY.config.regConfig.formConfig = {
    "maxSecondaryPhoneNos":3,
    "maxSecondaryEmailId":2,
    "maxUserAddress":20

}

BY.config.regConfig.userTypeConfig = {
    '-1' : {
        'type':'-1',
        'contentPanel':'app/components/signup/regUserType.html?versionTimeStamp=%PROJECT_VERSION%',
        'leftPanel' : "app/components/signup/registrationLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%",
        'category':'NONE',
        'label' : "I am"
    },
    '0' : {
        'type':'0',
        'contentPanel':'app/components/signup/registration/regIndividual.html?versionTimeStamp=%PROJECT_VERSION%',
        'leftPanel' : "app/components/signup/registrationLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%",
        'category':'CAREGIVER',
        'label' : "About Me"
    },
    '1' : {
        'type':'1',
        'contentPanel':'app/components/signup/registration/regIndividual.html?versionTimeStamp=%PROJECT_VERSION%',
        'leftPanel' : "app/components/signup/registrationLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%",
        'category':'ELDER',
        'label' : "About Me"
    },
    '2' : {
        'type':'2',
        'contentPanel':'app/components/signup/registration/regIndividual.html?versionTimeStamp=%PROJECT_VERSION%',
        'leftPanel' : "app/components/signup/registrationLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%",
        'category':'CURIOUS',
        'label' : "About Me"
    },
    '3' : {
        'type':'3',
        'contentPanel':'app/components/signup/registration/regHousing.html?versionTimeStamp=%PROJECT_VERSION%',
        'leftPanel' : "app/components/signup/registrationLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%",
        'category':'HOUSING',
        'label' : "Housing"
    },
    '4' : {
        'type':'4',
        'contentPanel':'app/components/signup/registration/regInstitution.html?versionTimeStamp=%PROJECT_VERSION%',
        'leftPanel' : "app/components/signup/registrationLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%",
        'category':'SERVICES',
        'label' : "Institution info"
    },
    '5' : {
        'type':'5',
        'contentPanel':'',
        'category':'PRODUCTS',
        'label' : ""
    },
    '6' : {
        'type':'6',
        'contentPanel':'',
        'category':'NGO',
        'label' : ""
    },
    '7' : {
        'type':'7',
        'contentPanel':'app/components/signup/registration/regProfessional.html?versionTimeStamp=%PROJECT_VERSION%',
        'leftPanel' : "app/components/signup/registrationLeftPanel.html?versionTimeStamp=%PROJECT_VERSION%",
        'category':'PROFESSIONAL',
        'label' : "Professional Info"
    }
}


BY.config.regConfig.indvUserRegConfig = {
    'salutations' : ["Ms.", "Mr.", "Dr.","Prof."],

    'showGenderOptions' : {
        "Dr." : ['0', '1'],
        "Prof." : ['0', '1'],
        "Ms." : [0],
        "Mr." : [1]
    },

    'gender' : {
        '0' : "Female",
        '1' : "Male"
    },

    'occupation' : ["Working", "Not Working", "Studying", "Retired"],


    'emotional_challenges1' : ['Boredom',
                        'Depression','Stress', 'Anxiety', 'Problematic Relationship'],

    'emotional_challenges2' : ['Caregiver Burn-out',
        'Boredom',
        'Depression','Stress', 'Anxiety', 'Problematic Relationship'],

    'medical_issues' : {
        'userType' : [0, 1],
        'fetchFromMenu' : "Medical Conditions"
    },

    'hobbies' : {
        'fetchFromMenu' : "Activities"
    },

    'countries' : ["India"],
    
    'maritalStatus' : ["married", "single", "divorced", "widowed"],

    'defaultAvatars' : {
        '0' : [{"reg":"assets/img/profile/f-1.png","detail":"assets/img/profile/detail_f-1.png"},
            {"reg":"assets/img/profile/f-2.png", "detail":"assets/img/profile/detail_f-2.png"},
            {"reg":"assets/img/profile/f-3.png", "detail":"assets/img/profile/detail_f-3.png"}],

        '1' : [{"reg":"assets/img/profile/m-1.png","detail":"assets/img/profile/detail_m-1.png"},
            {"reg":"assets/img/profile/m-2.png", "detail":"assets/img/profile/detail_m-2.png"},
            {"reg":"assets/img/profile/m-3.png", "detail":"assets/img/profile/detail_m-3.png"}]
    }


}
BY.config.regConfig.housingFacility = {
		'id' : "",
		
		'userId' : "",
		
		'name' : "",
		
		'systemTags' : [],
		
		'tier' : "",
		
		'primaryAddress': {
			'city': "",
			'country': "",
			'locality': "",
			'streetAddress': "",
			'zip': ""
		},
		
		'primaryPhoneNo' : "",
		
		'secondaryPhoneNos' : [],
		
		'primaryEmail' : "",
		
		'secondaryEmails' : [],
		
		'profileImage': null,
		
		'photoGalleryURLs' : [],
		
		'shortDescription' : "",
		
		'description' : "",
		
		'status' : 0,
		
		'reviewedBy' : [],
		
		'ratedBy' : [],
		
		'aggrRatingPercentage' : 0,		
		
		'isReviewedByUser': false,
		
		 'isRatedByUser' : false
		
};
