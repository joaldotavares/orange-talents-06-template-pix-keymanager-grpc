package br.com.zup.edu.pix.chave.consulta

import br.com.zup.edu.pix.chave.ChavePixRepository
import br.com.zup.edu.pix.client.BancoCentralClient
import br.com.zup.edu.pix.exception.ChavePixException
import br.com.zup.edu.pix.validacao.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class Filtro {

    abstract fun filtra(chaveRepository: ChavePixRepository, bcbClient: BancoCentralClient): ChavePixInfo

    @Introspected
    data class PorPixId(
        @field:NotBlank @ValidUUID val clienteId: String,
        @field:NotBlank @ValidUUID val pixId: String
    ) : Filtro() {
        fun clienteIdUuid() = UUID.fromString(clienteId)
        fun pixIdUuid() = UUID.fromString(pixId)

        override fun filtra(chaveRepository: ChavePixRepository, bcbClient: BancoCentralClient): ChavePixInfo {
            return chaveRepository.findById(pixIdUuid())
                .filter { it.pertenceAo(clienteIdUuid()) }
                .map(ChavePixInfo::of)
                .orElseThrow { ChavePixException("Chave não encontrada") }
        }
    }

    @Introspected
    data class PorChave(
        @field:NotBlank @Size(max = 77) val chave: String
    ) : Filtro() {
        override fun filtra(chaveRepository: ChavePixRepository, bcbClient: BancoCentralClient): ChavePixInfo {
            return chaveRepository.findByChave(chave)
                .map(ChavePixInfo::of)
                .orElseGet {

                    val response = bcbClient.buscarPorChave(chave)
                    when (response.status) {
                        HttpStatus.OK -> response.body().toModel()
                        else -> throw ChavePixException("Chave não encontrada")
                    }
                }
        }
    }

    @Introspected
    class Invalido() : Filtro() {
        override fun filtra(chaveRepository: ChavePixRepository, bcbClient: BancoCentralClient): ChavePixInfo {
            throw IllegalArgumentException("Chave inválida ou não informada")
        }
    }
}