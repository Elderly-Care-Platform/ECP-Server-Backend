package com.beautifulyears.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlTagValidator {

    private Pattern pattern;
    private Matcher matcher;
    private static final String HTML_TAG_FORMAT_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";

    public HtmlTagValidator() {
        pattern = Pattern.compile(HTML_TAG_FORMAT_PATTERN);
    }

    public boolean validate(final String tag) {
        matcher = pattern.matcher(tag);
        if(matcher.find()){
            return true;
        }
        
        return false;
    }
}