package br.com.zup.edu.pix.banco

import br.com.zup.edu.pix.chave.ChavePix
import br.com.zup.edu.pix.chave.TipoDeChave
import br.com.zup.edu.pix.conta.ContaAssociada

data class CriarChavePixRequest(
    val tipoDeChave: TipoChave,
    val chave: String,
    val contaBancaria: ContaBancaria,
    val titular: Titular
){

    companion object{
        fun of(chave: ChavePix): CriarChavePixRequest{
            return CriarChavePixRequest(
                tipoDeChave = TipoChave.by(chave.tipoDeChave),
                chave = chave.chave,
                contaBancaria = ContaBancaria(
                    instituicao = "ITAU_UNIBANCO_ISPB",
                    filiacao = chave.conta.agencia,
                    numero = chave.conta.numeroDaConta,
                    tipoConta = ContaBancaria.TipoConta.by(chave.tipoDeConta),
                ),
                titular = Titular(
                    tipo = Titular.TipoDeTitular.NATURAL_PERSON,
                    nome = chave.conta.nomeDoTitular,
                    numeroImposto = chave.conta.cpfDoTitular
                )
            )
        }
    }
}
