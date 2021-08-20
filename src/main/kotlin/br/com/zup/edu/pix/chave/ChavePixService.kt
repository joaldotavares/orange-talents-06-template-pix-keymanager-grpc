package br.com.zup.edu.pix.chave

import br.com.zup.edu.pix.client.ContasItauClient
import br.com.zup.edu.pix.exception.ChavePixException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import java.lang.RuntimeException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class ChavePixService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val itauClient: ContasItauClient
) {

    private val logger = LoggerFactory.getLogger(ChavePixService::class.java)

    @Transactional
    fun registra(@Valid novaChavePix: NovaChavePix): ChavePix {

        logger.info(novaChavePix.chave)
        if (chavePixRepository.existsByChave(novaChavePix.chave)) {
            throw ChavePixException("Chave Pix '${novaChavePix.chave}' existente")
        }

        val response = itauClient.buscarContaPorTipo(novaChavePix.clienteId!!, novaChavePix.tipoDeConta!!.name)
        val conta = response.body()?.toModel() ?: throw IllegalStateException("Cliente não encontrado")

        val chave = novaChavePix.toModel(conta)

        chavePixRepository.save(chave)

        return chave
    }
}