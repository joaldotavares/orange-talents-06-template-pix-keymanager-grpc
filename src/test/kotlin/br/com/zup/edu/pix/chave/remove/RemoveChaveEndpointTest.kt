package br.com.zup.edu.pix.chave.remove

import br.com.zup.edu.RemoveChavePixRequest
import br.com.zup.edu.RemoveChaveServiceGrpc
import br.com.zup.edu.pix.banco.DeletePixKeyRequest
import br.com.zup.edu.pix.banco.DeletePixKeyResponse
import br.com.zup.edu.pix.chave.ChavePixRepository
import br.com.zup.edu.pix.chave.TipoDeChave
import br.com.zup.edu.pix.chave.cria.ChavePix
import br.com.zup.edu.pix.client.BancoCentralClient
import br.com.zup.edu.pix.client.ContasItauClient
import br.com.zup.edu.pix.conta.ContaAssociada
import br.com.zup.edu.pix.conta.TipoDeConta
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest(
    private val repository: ChavePixRepository,
    private val grpcClient: RemoveChaveServiceGrpc.RemoveChaveServiceBlockingStub
) {

    private val logger = LoggerFactory.getLogger(RemoveChaveEndpointTest::class.java)

    @Inject
    lateinit var itauClient: ContasItauClient

    @Inject
    lateinit var bcbClient: BancoCentralClient

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setUp() {
        CHAVE_EXISTENTE = repository.save(chave())
    }

    @AfterEach
    fun cleanUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve remover a chave`() {

        Mockito.`when`(bcbClient.delete(CHAVE_EXISTENTE.chave, deletePixRequest(CHAVE_EXISTENTE.chave)))
            .thenReturn(HttpResponse.ok(deletePixResponse(CHAVE_EXISTENTE.chave)))

        val response = grpcClient.remover(
            RemoveChavePixRequest.newBuilder()
                .setClienteId(CHAVE_EXISTENTE.clienteId.toString())
                .setPixId(CHAVE_EXISTENTE.id.toString())
                .build()
        )

        with(response) {
            assertEquals(CHAVE_EXISTENTE.id.toString(), pixId)
            assertEquals(CHAVE_EXISTENTE.clienteId.toString(), clientId)
            assertEquals(0, repository.count())
        }
    }

    @Test
    fun `nao deve remover chave quando pertencer a outro cliente`() {

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.remover(
                RemoveChavePixRequest.newBuilder()
                    .setClienteId(CHAVE_EXISTENTE.clienteId.toString())
                    .setPixId(UUID.randomUUID().toString())
                    .build()
            )
        }

        with(erro) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave pix não encontrada ou não pertence ao usuario", status.description)
        }

    }

    @Test
    fun `nao deve remover chave quando ocorrer algum erro no banco central`(){
        Mockito.`when`(bcbClient.delete(CHAVE_EXISTENTE.chave, deletePixRequest(CHAVE_EXISTENTE.chave)))
            .thenReturn(HttpResponse.unprocessableEntity())

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.remover(RemoveChavePixRequest.newBuilder()
                .setClienteId(CHAVE_EXISTENTE.clienteId.toString())
                .setPixId(CHAVE_EXISTENTE.id.toString())
                .build()
            )
        }

        with(erro){
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Erro ao remover chave do banco central", status.description)
        }
    }

    @Factory
    class RemoveChaveClient {

        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                RemoveChaveServiceGrpc.RemoveChaveServiceBlockingStub {
            return RemoveChaveServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun chave(): ChavePix {
        return ChavePix(
            clienteId = UUID.randomUUID(),
            tipoDeChave = TipoDeChave.CELULAR,
            chave = "+5571996583398",
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                "BANCO ITAU",
                "Joaldo Tavares",
                "61176001515",
                "0001",
                "291900"
            )
        )
    }


    @MockBean(BancoCentralClient::class)
    fun brbClient(): BancoCentralClient? {
        return Mockito.mock(BancoCentralClient::class.java)
    }

    private fun deletePixRequest(key: String): DeletePixKeyRequest {
        return DeletePixKeyRequest(
            key = key,
            participant = ContaAssociada.ITAU_UNIBANCO_ISPB
        )
    }

    private fun deletePixResponse(key: String): DeletePixKeyResponse {
        return DeletePixKeyResponse(
            key = key,
            participant = ContaAssociada.ITAU_UNIBANCO_ISPB,
            deletedAt = LocalDateTime.now()
        )
    }
}