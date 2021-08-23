package br.com.zup.edu.pix.banco

import br.com.zup.edu.pix.chave.ChavePix
import br.com.zup.edu.pix.conta.ContaAssociada

data class CreatePixKeyRequest(
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner
) {

    companion object {
        fun of(chave: ChavePix): CreatePixKeyRequest {
            return CreatePixKeyRequest(
                keyType = PixKeyType.by(chave.tipoDeChave),
                key = chave.chave,
                bankAccount = BankAccount(
                    participant = ContaAssociada.ITAU_UNIBANCO_ISPB,
                    branch = chave.conta.agencia,
                    accountNumber = chave.conta.numeroDaConta,
                    accountType = BankAccount.AccountType.by(chave.tipoDeConta),
                ),
                owner = Owner(
                    type = Owner.OwnerType.NATURAL_PERSON,
                    name = chave.conta.nomeDoTitular,
                    taxIdNumber = chave.conta.cpfDoTitular
                )
            )
        }
    }
}
