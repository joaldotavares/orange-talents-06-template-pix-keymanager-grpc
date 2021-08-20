package br.com.zup.edu.pix.banco

import br.com.zup.edu.pix.conta.ContaAssociada
import br.com.zup.edu.pix.conta.TipoDeConta
import java.time.LocalDateTime

data class DetalhesDaChaveResponse(
    val tipo: TipoChave,
    val chave: String,
    val contaBancaria: ContaBancaria,
    val titular: Titular,
    val criadoEm: LocalDateTime
) {
    fun toModel(): ChavePixInfo {
        return ChavePixInfo(
            tipo = tipo.tipo!!,
            chave = this.chave,
            tipoDeConta = when(this.contaBancaria.tipoConta){
                ContaBancaria.TipoConta.CAAC -> TipoDeConta.CONTA_CORRENTE
                ContaBancaria.TipoConta.SVGS -> TipoDeConta.CONTA_POUPANCA
            },
            conta = ContaAssociada(
                instituicao = contaBancaria.instituicao,
                nomeDoTitular = titular.nome,
                cpfDoTitular = titular.numeroImposto,
                agencia = contaBancaria.filiacao,
                numeroDaConta = contaBancaria.numero
            )
        )
    }
}
