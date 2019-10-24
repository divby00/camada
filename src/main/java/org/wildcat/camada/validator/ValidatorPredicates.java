package org.wildcat.camada.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface ValidatorPredicates {

    Pattern passwordPattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})");
    Pattern emailPattern = Pattern.compile("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$");

    TriPredicate<String, Integer> isValidTextField = (text, min, max) -> {
        return StringUtils.isNotBlank(text) && StringUtils.length(text) > min && StringUtils.length(text) < max;
    };
    Predicate<String> isValidPassword = (text) -> passwordPattern.matcher(text).matches();
    Predicate<String> isValidEmail = (text) -> emailPattern.matcher(text).matches();
    BiPredicate<String, String> passwordsAreTheSame = (check, source) -> {
        return StringUtils.isNotBlank(check) && StringUtils.isNotBlank(source) && StringUtils.equals(check, source);
    };

}