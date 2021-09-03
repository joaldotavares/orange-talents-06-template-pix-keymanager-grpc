package br.com.zup.edu.pix.chave

import br.com.zup.edu.CarregaChavePixRequest
import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.TipoDeChave.ALEATORIA
import br.com.zup.edu.TipoDeChave.UNKNOWN_TIPO_CHAVE
import br.com.zup.edu.TipoDeConta.UNKNOWN_TIPO_CONTA
import br.com.zup.edu.pix.chave.consulta.Filtro
import br.com.zup.edu.pix.chave.cria.NovaChavePix
import br.com.zup.edu.pix.conta.TipoDeConta
import io.micronaut.validation.validator.Validator
import java.util.*
import javax.validation.ConstraintViolationException

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

fun CarregaChavePixRequest.toModel(validador: Validator): Filtro {


    val filtro = when (filtroCase) {
        CarregaChavePixRequest.FiltroCase.PIXID -> pixId.let {
            Filtro.PorPixId(clienteId = it.clienteId, pixId = it.pixId)
        }
        CarregaChavePixRequest.FiltroCase.CHAVE -> Filtro.PorChave(chave)
        CarregaChavePixRequest.FiltroCase.FILTRO_NOT_SET -> Filtro.Invalido()
    }

    val violations = validador.validate(filtro)
    if (violations.isNotEmpty()) {
        throw ConstraintViolationException(violations)
    }

    return filtro
}