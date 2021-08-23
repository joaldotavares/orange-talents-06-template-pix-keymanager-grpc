package br.com.zup.edu.pix.chave

import br.com.zup.edu.pix.banco.DeletePixKeyRequest
import br.com.zup.edu.pix.client.BancoCentralClient
import br.com.zup.edu.pix.exception.ChavePixException
import br.com.zup.edu.pix.validacao.ValidUUID
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChaveService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val bcbClient: BancoCentralClient
) {

    @Transactional
    fun remover(
        @NotBlank @ValidUUID(message = "Formato de id cliente inválido") clienteId: String?,
        @NotBlank @ValidUUID(message = "Formato de id pix inválido") pixId: String?
    ) {

        val uuidCliente = UUID.fromString(clienteId)
        val uuidPix = UUID.fromString(pixId)

        val chave = chavePixRepository.findByIdAndClienteId(uuidPix, uuidCliente)
            .orElseThrow { ChavePixException("Chave pix não encontrada ou não pertence ao usuario") }

        chavePixRepository.deleteById(uuidPix)

        val request = DeletePixKeyRequest(chave.chave)

        val bcbResponse = bcbClient.delete(chave.chave, request)
        if (bcbResponse.status != HttpStatus.OK) {
            throw IllegalStateException("Erro ao remover chave do banco central")
        }
    }
}