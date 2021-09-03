package br.com.zup.edu.pix.banco

import br.com.zup.edu.pix.conta.TipoDeConta

data class BankAccount(
    val participant : String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
) {
    enum class AccountType {
        CACC,
        SVGS;

        companion object {
            fun by(domainType: TipoDeConta): AccountType {
                return when (domainType) {
                    TipoDeConta.CONTA_CORRENTE -> CACC
                    TipoDeConta.CONTA_POUPANCA -> SVGS
                }
            }
        }
    }
}
