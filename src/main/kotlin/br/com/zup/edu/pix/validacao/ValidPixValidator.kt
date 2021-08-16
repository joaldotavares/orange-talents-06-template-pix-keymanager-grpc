package br.com.zup.edu.pix.validacao

import br.com.zup.edu.pix.chave.NovaChavePix
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext

class ValidPixValidator : ConstraintValidator<ValidPix, NovaChavePix> {

    override fun isValid(
        value: NovaChavePix?,
        annotationMetadata: AnnotationValue<ValidPix>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value?.tipoDeChave == null){
            return false
        }
        return value.tipoDeChave.validar(value.chave)
    }
}
