package br.com.zup.edu.pix.chave.lista

import br.com.zup.edu.ListaChavePixRequest
import br.com.zup.edu.ListaChaveServiceGrpc
import br.com.zup.edu.pix.chave.ChavePixRepository
import br.com.zup.edu.pix.chave.TipoDeChave
import br.com.zup.edu.pix.chave.cria.ChavePix
import br.com.zup.edu.pix.conta.ContaAssociada
import br.com.zup.edu.pix.conta.TipoDeConta
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest(transactional = false)
internal class ListaChavePixEndpointTest(
    val chaveRepository: ChavePixRepository,
    val grpcClient: ListaChaveServiceGrpc.ListaChaveServiceBlockingStub
) {

    private val CLIENTE_ID = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        chaveRepository.save(
            chave(
                clienteId = CLIENTE_ID,
                TipoDeChave.CELULAR,
                "+5571996583398"
            )
        )
        chaveRepository.save(
            chave(
                clienteId = CLIENTE_ID,
                TipoDeChave.ALEATORIA,
                "notblank!"
            )
        )
        chaveRepository.save(
            chave(
                clienteId = CLIENTE_ID,
                TipoDeChave.EMAIL,
                "joaldo@email.com"
            )
        )
    }

    @AfterEach
    fun clearAll() {
        chaveRepository.deleteAll()
    }

    @Test
    fun `deve listar as chaves do cliente`() {

        val response = grpcClient.listar(
            ListaChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID.toString())
                .build()
        )


        with(response.chavesList) {
            val toList = this.map { Pair(it.tipoDeChave, it.chave) }
            val tudo = chaveRepository.findAll()
            assertThat(this, hasSize(3))
        }
    }

    @Test
    fun `nao deve listar as chaves quando um cliente nao tiver chaves registradas`() {

    }

    @Test
    fun `nao deve listar as chaves quando o clienteId for invalido`() {

    }

    @Factory
    class ListaChaveClient {

        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                ListaChaveServiceGrpc.ListaChaveServiceBlockingStub {
            return ListaChaveServiceGrpc.newBlockingStub(channel)
        }
    }


    private fun chave(clienteId: UUID, tipoDeChave: TipoDeChave, chave: String): ChavePix {
        return ChavePix(
            clienteId = clienteId,
            tipoDeChave = tipoDeChave,
            chave = chave,
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
}