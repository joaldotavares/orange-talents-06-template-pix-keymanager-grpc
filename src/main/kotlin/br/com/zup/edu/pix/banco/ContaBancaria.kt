package br.com.zup.edu.pix.banco

import br.com.zup.edu.pix.conta.TipoDeConta

data class ContaBancaria(
    val instituicao : String,
    val filiacao: String,
    val numero: String,
    val tipoConta: TipoConta
) {
    enum class TipoConta {
        CAAC,
        SVGS;

        companion object {
            fun by(tipo: TipoDeConta): TipoConta {
                return when (tipo) {
                    TipoDeConta.CONTA_CORRENTE -> CAAC
                    TipoDeConta.CONTA_POUPANCA -> SVGS
                }
            }
        }
    }
}
