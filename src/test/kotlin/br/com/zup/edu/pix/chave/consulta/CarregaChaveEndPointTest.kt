package br.com.zup.edu.pix.chave.consulta

import br.com.zup.edu.CarregaChavePixRequest
import br.com.zup.edu.CarregaChaveServiceGrpc
import br.com.zup.edu.pix.chave.ChavePix
import br.com.zup.edu.pix.chave.ChavePixEndPointTest
import br.com.zup.edu.pix.chave.ChavePixRepository
import br.com.zup.edu.pix.chave.TipoDeChave
import br.com.zup.edu.pix.client.BancoCentralClient
import br.com.zup.edu.pix.conta.ContaAssociada
import br.com.zup.edu.pix.conta.TipoDeConta
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class CarregaChaveEndPointTest(
    private val chaveRepository: ChavePixRepository,
    private val grpcClient: CarregaChaveServiceGrpc.CarregaChaveServiceBlockingStub
) {

    private val logger = LoggerFactory.getLogger(ChavePixEndPointTest::class.java)

    @Inject
    lateinit var bcbClient: BancoCentralClient

    private val CLIENTE_ID = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        chaveRepository.save(chave(clienteId = CLIENTE_ID, TipoDeChave.CELULAR, "+5571996583398"))
        chaveRepository.save(chave(clienteId = CLIENTE_ID, TipoDeChave.ALEATORIA, "notblank!"))
        chaveRepository.save(chave(clienteId = CLIENTE_ID, TipoDeChave.EMAIL, "joaldo@email.com"))
    }

    @AfterEach
    fun clearAll() {
        chaveRepository.deleteAll()
    }


    @Test
    fun `deve carregar a chave pelo id do pix e do cliente`() {
        val chave = chaveRepository.findByChave("+5571996583398").get()

        val response = grpcClient.carregar(
            CarregaChavePixRequest.newBuilder()
                .setPixId(
                    CarregaChavePixRequest.FiltroPorPixId.newBuilder()
                        .setClienteId(chave.clienteId.toString())
                        .setPixId(chave.id.toString())
                        .build()
                )
                .build()
        )

        with(response) {
            assertEquals(chave.clienteId.toString(), this.clienteId)
            assertEquals(chave.tipoDeChave.name, this.chave.tipoDeChave.name)
            assertEquals(chave.chave.toString(), this.chave.chave)
            assertEquals(chave.conta.instituicao, this.chave.conta.instituicao)
            assertEquals(chave.conta.nomeDoTitular, this.chave.conta.nomeDoTitular)
            assertEquals(chave.conta.cpfDoTitular, this.chave.conta.cpfDoTitular)
            assertEquals(chave.conta.agencia, this.chave.conta.agencia)
            assertEquals(chave.conta.numeroDaConta, this.chave.conta.numeroDaConta)
            assertEquals(chave.id.toString(), this.pixId)
        }
    }

    @Factory
    class CarregaChaveClient {

        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                CarregaChaveServiceGrpc.CarregaChaveServiceBlockingStub {
            return CarregaChaveServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(BancoCentralClient::class)
    fun brbClient(): BancoCentralClient? {
        return Mockito.mock(BancoCentralClient::class.java)
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