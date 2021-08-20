package br.com.zup.edu.pix.banco

import br.com.zup.edu.pix.chave.TipoDeChave
import java.lang.IllegalArgumentException

enum class TipoChave(val tipo : TipoDeChave?) {
    CPF(TipoDeChave.CPF),
    CNPJ(null),
    PHONE(TipoDeChave.CELULAR),
    EMAIL(TipoDeChave.EMAIL),
    RANDOM(TipoDeChave.ALEATORIA);

    companion object{
        private val mapping = TipoChave.values().associateBy(TipoChave::tipo)

        fun by(tipo: TipoDeChave): TipoChave{
            return mapping[tipo] ?: throw IllegalArgumentException("Tipo de chave inválida")
        }
    }
}