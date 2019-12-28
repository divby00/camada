package org.wildcat.camada.common.validator.impl;

import lombok.Getter;
import lombok.Setter;
import org.wildcat.camada.common.validator.TriPredicate;
import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.ValidatorPredicates;
import org.wildcat.camada.persistence.entity.CamadaUser;
import org.wildcat.camada.service.CamadaUserService;

import java.util.Optional;

@Getter
@Setter
public class NewUserValidatorImpl implements Validator {

    private Integer min = 2;
    private Integer max = 20;
    private static TriPredicate<String, Integer> triPredicate = ValidatorPredicates.isValidTextField;

    private final CamadaUserService camadaUserService;

    public NewUserValidatorImpl(CamadaUserService camadaUserService) {
        this.camadaUserService = camadaUserService;
    }

    @Override
    public Boolean validate(String text) {
        Optional<CamadaUser> user = camadaUserService.findByName(text);
        return !user.isPresent() && triPredicate.test(text, min, max);
    }

    @Override
    public String getErrorMessage() {
        return "El valor debe ser una cadena de caracteres con una longitud mayor que " + min + " caracteres y menor que " + max + " caracteres.";
    }
}
