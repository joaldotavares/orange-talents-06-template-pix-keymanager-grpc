package br.com.zup.edu.pix.chave

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoDeChave {
    CPF {
        override fun validar(chave: String?): Boolean {
            if (chave.isNullOrBlank()) {
                return false
            }

            if (!chave.matches("[0-9]+".toRegex())) {
                return false

            }
            return CPFValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },
    CELULAR {
        override fun validar(chave: String?): Boolean {
            if (chave.isNullOrBlank()) {
                return false
            }
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL {
        override fun validar(chave: String?): Boolean {
            if (chave.isNullOrBlank()) {
                return false
            }
            if (!chave.matches("[0-9]+".toRegex())) {
                return false

            }
            return EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },
    ALEATORIA {
        override fun validar(chave: String?): Boolean{
            return chave.isNullOrBlank()
        }
    };

    abstract fun validar(chave: String?): Boolean

}