package br.com.zup.edu.pix.chave

import br.com.zup.edu.RegistraChaveGrpcServiceGrpc
import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta
import br.com.zup.edu.pix.client.ContasItauClient
import br.com.zup.edu.pix.conta.ContaAssociada
import br.com.zup.edu.pix.conta.DetalhesDaConta
import br.com.zup.edu.pix.conta.InstituicaoResponse
import br.com.zup.edu.pix.conta.TitularResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class ChavePixEndPointTest(
    private val repository: ChavePixRepository,
    private val grpcClient: RegistraChaveGrpcServiceGrpc.RegistraChaveGrpcServiceBlockingStub
) {

    @Inject
    lateinit var itauClient: ContasItauClient

    private val CLIENTE_ID = UUID.randomUUID().toString()
    private val CONTA_CORRENTE = TipoDeConta.CONTA_CORRENTE.toString()

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve registrar uma nova chave do tipo cpf`() {
        Mockito.`when`(itauClient.buscarContaPorTipo(CLIENTE_ID, CONTA_CORRENTE))
            .thenReturn(HttpResponse.ok(detalhesDaConta()))

        val response = grpcClient.registrar(
            RegistraChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID)
                .setTipoDeChave(TipoDeChave.CPF)
                .setChave("01567920990")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertEquals(CLIENTE_ID, clienteId)
            assertNotNull(pixId)
        }
    }

    @Test
    fun `deve registrar uma nova chave do tipo telefone`() {
        Mockito.`when`(itauClient.buscarContaPorTipo(CLIENTE_ID, CONTA_CORRENTE))
            .thenReturn(HttpResponse.ok(detalhesDaConta()))

        val response = grpcClient.registrar(
            RegistraChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID)
                .setTipoDeChave(TipoDeChave.CELULAR)
                .setChave("71996095632")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertEquals(CLIENTE_ID, clienteId)
            assertNotNull(pixId)
        }
    }

    @Test
    fun `deve registrar uma nova chave do tipo email`() {
        Mockito.`when`(itauClient.buscarContaPorTipo(CLIENTE_ID, CONTA_CORRENTE))
            .thenReturn(HttpResponse.ok(detalhesDaConta()))

        val response = grpcClient.registrar(
            RegistraChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID)
                .setTipoDeChave(TipoDeChave.EMAIL)
                .setChave("tetste@zup.com.br")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertEquals(CLIENTE_ID, clienteId)
            assertNotNull(pixId)
        }
    }

    @Test
    fun `nao deve registrar nova chave quando ela ja existir`() {

        repository.save(
            ChavePix(
                clienteId = UUID.fromString(CLIENTE_ID),
                tipoDeChave = br.com.zup.edu.pix.chave.TipoDeChave.CPF,
                chave = "teste@zup.com.br",
                tipoDeConta = br.com.zup.edu.pix.conta.TipoDeConta.CONTA_CORRENTE,
                conta = ContaAssociada(
                    "BANCO ITAU",
                    "Joaldo Tavares",
                    "97650865432",
                    "0001",
                    "376709"
                )
            )
        )

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                RegistraChavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID)
                    .setTipoDeChave(TipoDeChave.EMAIL)
                    .setChave("teste@zup.com.br")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(erro) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave Pix 'teste@zup.com.br' existente", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave quando nao encontrar dados da conta do cliente`() {
        Mockito.`when`(itauClient.buscarContaPorTipo(CLIENTE_ID, CONTA_CORRENTE))
            .thenReturn(HttpResponse.notFound())

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                RegistraChavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID)
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setChave("01567920997")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(erro) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Cliente não encontrado", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave com parametros invalidos`() {

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                RegistraChavePixRequest.newBuilder().build()
            )
        }

        with(erro) {
            assertEquals(Status.UNKNOWN.code, status.code)
        }

    }

    @MockBean(ContasItauClient::class)
    fun itauClient(): ContasItauClient? {
        return Mockito.mock(ContasItauClient::class.java)
    }

    private fun detalhesDaConta(): DetalhesDaConta {
        return DetalhesDaConta(
            "CONTA_CORRENTE",
            InstituicaoResponse("BANCO ITAU", "60701190"),
            "0001",
            "376709",
            TitularResponse("Joaldo Tavares", "97650865432")
        )
    }
}