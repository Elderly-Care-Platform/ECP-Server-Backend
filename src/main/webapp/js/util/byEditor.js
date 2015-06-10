
var BY = BY || {};

BY.initEditor = function initEditor(param){
    //tinymce.remove();
    console.log(document.getElementById("article_textArea"));
    var tinyEditor = tinymce.get(param.editorTextArea);
    if(tinyEditor){
        tinyEditor.remove();
    }

    tinymce.init({
        selector: "#"+param.editorTextArea,
        theme: "modern",
        skin: 'light',
        statusbar: false,
        menubar: false,
        plugins: [
            "advlist autolink autosave link image lists charmap print preview hr anchor pagebreak spellchecker",
            "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
            " emoticons textcolor paste "
        ],
        toolbar: "styleselect | bold italic | bullist numlist hr  | undo redo | link unlink emoticons image media   ",
        setup : function(ed) {
            var placeholder = $('#' + ed.id).attr('placeholder');
            if (typeof placeholder !== 'undefined' && placeholder !== false) {
                var is_default = false;
                ed.on('init', function () {
                    // get the current content
                    var cont = ed.getContent();

                    // If its empty and we have a placeholder set the value
                    if (cont.length === 0) {
                        ed.setContent(placeholder);
                        // Get updated content
                        cont = placeholder;
                    }
                    // convert to plain text and compare strings
                    is_default = (cont == placeholder);

                    // nothing to do
                    if (!is_default) {
                        return;
                    }
                }).on('keydown', function () {
                    // replace the default content on focus if the same as original placeholder
                    if (is_default) {
                        ed.setContent('');
                        is_default = false;
                    }
                }).on('blur', function () {
                    if (ed.getContent().length === 0) {
                        ed.setContent(placeholder);
                    }
                });
            }
            ed.on('init', function (evt) {
                var toolbar = $(evt.target.editorContainer)
                    .find('>.mce-container-body >.mce-toolbar-grp');
                var editor = $(evt.target.editorContainer)
                    .find('>.mce-container-body >.mce-edit-area');

                // switch the order of the elements
                toolbar.detach().insertAfter(editor);
            });
            ed.on("keyup", function () {
                var id = ed.id;
                if ($.trim(ed.getContent({format: 'text'})).length) {
                    $("#" + id).parents(".textarea-label").find(".btn").removeClass("disabled");
                } else {
                    $("#" + id).parents(".textarea-label").find(".btn").addClass("disabled");
                }
            });

            ed.on('blur', function(e) {
                console.log('reset event', e);
            });

            ed.on('remove', function(e) {
                console.log('remove event', e);
            });
        }
    });
}

BY.selectCategory = function(selectedInput){
    var $body = angular.element(document.body);   // 1
    var $rootScope = $body.scope().$root;
    var categorySelected = selectedInput.value;
    for(var i=0; i<$rootScope.discussCategoryList.length; i++) {
        if($rootScope.discussCategoryList[i].id == categorySelected){
            var childElements = document.getElementById(categorySelected).children;
            for(var j=0; j<childElements.length; j++) {
                childElements[j].children[0].checked = selectedInput.checked;
            }
        }
    }
}