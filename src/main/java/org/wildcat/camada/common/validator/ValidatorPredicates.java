package org.wildcat.camada.common.validator;

import org.apache.commons.lang3.StringUtils;
import org.wildcat.camada.persistence.PaymentFrequency;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface ValidatorPredicates {

    Pattern passwordPattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})");
    Pattern emailPattern = Pattern.compile("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$");
    String DATE_PATTERN = "dd/MM/yyyy HH:mm";

    TriPredicate<String, Integer> isValidTextField = (text, min, max) -> StringUtils.length(text) > min && StringUtils.length(text) < max && StringUtils
            .isAlphanumeric(text);
    Predicate<String> isValidPassword = (text) -> passwordPattern.matcher(text).matches();
    Predicate<String> isValidEmail = (text) -> emailPattern.matcher(text).matches();
    BiPredicate<String, String> passwordsAreTheSame = StringUtils::equals;
    Predicate<String> isValidBoolean = (text) -> StringUtils.equalsAnyIgnoreCase(text, "true", "false");
    BiPredicate<String, Double[]> isValidAmount = (text, amounts) -> Stream.of(amounts).anyMatch(amount -> Double.toString(amount).equals(text));
    Predicate<PaymentFrequency> isValidPaymentFrequency = (text) -> Stream.of(PaymentFrequency.values()).anyMatch(freq -> text.equals(freq.name()));
    Predicate<String> isValidDate = (text) -> {
        boolean result = false;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
            LocalDateTime.parse(text, formatter);
            result = true;
        } catch (DateTimeParseException ignored) {
        }
        return result;
    };
    Predicate<String> isValidDateOrEmpty = (text) -> {
        if (StringUtils.isBlank(text))
            return true;
        boolean result = false;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
            LocalDateTime.parse(text, formatter);
            result = true;
        } catch (DateTimeParseException ignored) {
        }
        return result;
    };
}
