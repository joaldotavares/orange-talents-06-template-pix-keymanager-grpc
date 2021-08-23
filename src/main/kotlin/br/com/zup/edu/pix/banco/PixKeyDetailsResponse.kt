package br.com.zup.edu.pix.banco

import br.com.zup.edu.pix.conta.ContaAssociada
import br.com.zup.edu.pix.conta.TipoDeConta
import java.time.LocalDateTime

data class PixKeyDetailsResponse(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
) {
    fun toModel(): ChavePixInfo {
        return ChavePixInfo(
            tipo = keyType.domainType!!,
            chave = this.key,
            tipoDeConta = when (this.bankAccount.accountType) {
                BankAccount.AccountType.CACC -> TipoDeConta.CONTA_CORRENTE
                BankAccount.AccountType.SVGS -> TipoDeConta.CONTA_POUPANCA
            },
            conta = ContaAssociada(
                instituicao = bankAccount.participant,
                nomeDoTitular = owner.name,
                cpfDoTitular = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numeroDaConta = bankAccount.accountNumber
            )
        )
    }
}
