package br.com.zup.edu.pix.banco

import br.com.zup.edu.pix.chave.TipoDeChave
import java.lang.IllegalArgumentException

enum class PixKeyType(val domainType : TipoDeChave?) {
    CPF(TipoDeChave.CPF),
    CNPJ(null),
    PHONE(TipoDeChave.CELULAR),
    EMAIL(TipoDeChave.EMAIL),
    RANDOM(TipoDeChave.ALEATORIA);

    companion object{
        private val mapping = PixKeyType.values().associateBy(PixKeyType::domainType)

        fun by(domainType: TipoDeChave): PixKeyType{
            return mapping[domainType] ?: throw IllegalArgumentException("Tipo de chave inválida")
        }
    }
}