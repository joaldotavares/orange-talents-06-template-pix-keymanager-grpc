package br.com.zup.edu.pix.chave.consulta

import br.com.zup.edu.pix.chave.cria.ChavePix
import br.com.zup.edu.pix.chave.TipoDeChave
import br.com.zup.edu.pix.conta.ContaAssociada
import br.com.zup.edu.pix.conta.TipoDeConta
import java.time.LocalDateTime
import java.util.*

data class ChavePixInfo(
    val pixId: UUID? = null,
    val clienteId: UUID? = null,
    val tipoDeChave: TipoDeChave,
    val chave: String,
    val tipoDeConta: TipoDeConta,
    val conta: ContaAssociada,
    val criadaEm: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun of(chave: ChavePix): ChavePixInfo {
            return ChavePixInfo(
                pixId = chave.id!!,
                clienteId = chave.clienteId,
                tipoDeChave = chave.tipoDeChave,
                chave = chave.chave,
                tipoDeConta = chave.tipoDeConta,
                conta = chave.conta,
                criadaEm = chave.criadaEm
            )
        }
    }
}
