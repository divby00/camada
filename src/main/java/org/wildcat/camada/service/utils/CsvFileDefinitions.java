package org.wildcat.camada.service.utils;

import org.wildcat.camada.common.validator.Validator;
import org.wildcat.camada.common.validator.impl.BooleanValidatorImpl;
import org.wildcat.camada.common.validator.impl.DateValidatorImpl;
import org.wildcat.camada.common.validator.impl.EmailValidatorImpl;
import org.wildcat.camada.common.validator.impl.TextValidatorImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CsvFileDefinitions {
    USERS("Nombre de usuario,Nombre,Apellido,Email,Acceso administrador,Acceso socios,Acceso padrinos presenciales,Acceso padrinos virtuales,Acceso voluntarios,Fecha de alta,Última conexión") {
        @Override
        public List<Validator> getValidators() {
            Validator[] validators = {
                    new TextValidatorImpl(3, 20),
                    new TextValidatorImpl(3, 20),
                    new TextValidatorImpl(3, 20),
                    new EmailValidatorImpl(),
                    new BooleanValidatorImpl(),
                    new BooleanValidatorImpl(),
                    new BooleanValidatorImpl(),
                    new BooleanValidatorImpl(),
                    new BooleanValidatorImpl(),
                    new DateValidatorImpl(),
                    new DateValidatorImpl()
            };
            return Arrays.asList(validators);
        }
    };

    private String fields;

    CsvFileDefinitions(String fields) {
        this.fields = fields;
    }

    public List<String> getFields() {
        return Stream.of(this.fields.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public abstract List<Validator> getValidators();
}
