package br.com.zup.edu.pix.chave

import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.TipoDeChave.ALEATORIA
import br.com.zup.edu.TipoDeChave.UNKNOWN_TIPO_CHAVE
import br.com.zup.edu.pix.conta.TipoDeConta
import br.com.zup.edu.pix.chave.TipoDeChave
import br.com.zup.edu.TipoDeConta.UNKNOWN_TIPO_CONTA
import java.util.*

fun RegistraChavePixRequest.toModel(): NovaChavePix {

    return NovaChavePix(
        clienteId = clienteId,
        tipoDeChave = when (tipoDeChave) {
            UNKNOWN_TIPO_CHAVE -> null
            else -> TipoDeChave.valueOf(tipoDeChave.name)
        },
        chave = if (tipoDeChave == ALEATORIA) UUID.randomUUID().toString() else chave,
        tipoDeConta = when (tipoDeConta) {
            UNKNOWN_TIPO_CONTA -> null
            else -> TipoDeConta.valueOf(tipoDeConta.name)
        }
    )
}