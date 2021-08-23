package br.com.zup.edu.pix.chave

import br.com.zup.edu.RemoveChavePixRequest
import br.com.zup.edu.RemoveChaveServiceGrpc
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
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertThrows
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest(
    private val repository: ChavePixRepository,
    private val grpcClient: RemoveChaveServiceGrpc.RemoveChaveServiceBlockingStub
) {

    @Inject
    lateinit var itauClient: ContasItauClient

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setUp() {
        CHAVE_EXISTENTE = repository.save(chave())
    }

    @AfterEach
    fun cleanUp(){
        repository.deleteAll()
    }

    @Test
    fun `deve remover a chave`() {

        val response = grpcClient.remover(RemoveChavePixRequest.newBuilder()
            .setClienteId(CHAVE_EXISTENTE.clienteId.toString())
            .setPixId(CHAVE_EXISTENTE.id.toString())
            .build())

        assertEquals(CHAVE_EXISTENTE.id.toString(), response.pixId)
        assertEquals(CHAVE_EXISTENTE.clienteId.toString(), response.clientId)
        assertEquals(0, repository.count())
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

        with(erro){
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave pix não encontrada ou não pertence ao usuario", status.description)
        }

    }

    @Singleton
    private fun chave(): ChavePix{
        return ChavePix(
            clienteId = UUID.randomUUID(),
            tipoDeChave = TipoDeChave.EMAIL,
            chave = UUID.randomUUID().toString(),
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                "BANCO ITAU",
                "Testador",
                "45660989800",
                "2309",
                "12345"
            )
        )
    }
}