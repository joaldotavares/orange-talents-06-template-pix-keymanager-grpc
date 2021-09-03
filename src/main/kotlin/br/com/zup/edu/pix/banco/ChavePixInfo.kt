package br.com.zup.edu.pix.banco

import br.com.zup.edu.pix.chave.TipoDeChave
import br.com.zup.edu.pix.conta.ContaAssociada
import br.com.zup.edu.pix.conta.TipoDeConta

class ChavePixInfo(
    val tipo: TipoDeChave,
    val chave: String,
    val tipoDeConta: TipoDeConta,
    val conta: ContaAssociada
) {

}
