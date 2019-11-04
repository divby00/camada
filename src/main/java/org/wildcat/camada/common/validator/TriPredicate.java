package org.wildcat.camada.common.validator;

public interface TriPredicate<S, U extends Number> {
    Boolean test(S text, U min, U max);
}
