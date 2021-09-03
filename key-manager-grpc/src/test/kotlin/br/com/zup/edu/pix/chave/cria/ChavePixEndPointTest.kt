package br.com.zup.edu.pix.chave.cria

import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.RegistraChaveServiceGrpc
import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta
import br.com.zup.edu.pix.banco.*
import br.com.zup.edu.pix.chave.ChavePixRepository
import br.com.zup.edu.pix.client.BancoCentralClient
import br.com.zup.edu.pix.client.ContasItauClient
import br.com.zup.edu.pix.conta.ContaAssociada
import br.com.zup.edu.pix.conta.DetalhesDaConta
import br.com.zup.edu.pix.conta.InstituicaoResponse
import br.com.zup.edu.pix.conta.TitularResponse
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class ChavePixEndPointTest(
    private val repository: ChavePixRepository,
    private val grpcClient: RegistraChaveServiceGrpc.RegistraChaveServiceBlockingStub
) {

    private val logger = LoggerFactory.getLogger(ChavePixEndPointTest::class.java)

    @Inject
    lateinit var itauClient: ContasItauClient

    @Inject
    lateinit var bcbClient: BancoCentralClient

    private val CLIENTE_ID = UUID.randomUUID().toString()
    private val CONTA_CORRENTE = TipoDeConta.CONTA_CORRENTE.toString()

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun `deve registrar uma nova chave do tipo cpf`() {
        Mockito.`when`(itauClient.buscarContaPorTipo(clienteId = CLIENTE_ID, TipoDeConta.CONTA_CORRENTE.toString()))
            .thenReturn(HttpResponse.ok(detalhesDaConta()))

        Mockito.`when`(bcbClient.create(createPixRequest()))
            .thenReturn(HttpResponse.created(createPixResponse()))

        val response = grpcClient.registrar(
            RegistraChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID)
                .setTipoDeChave(TipoDeChave.CPF)
                .setChave("61176001515")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )
        logger.info(response.clienteId)
        with(response) {
            assertEquals(CLIENTE_ID.toString(), clienteId)
            assertNotNull(pixId)
        }
    }

    @Test
    fun `deve registrar uma nova chave do tipo telefone`() {
        Mockito.`when`(itauClient.buscarContaPorTipo(CLIENTE_ID, CONTA_CORRENTE))
            .thenReturn(HttpResponse.ok(detalhesDaConta()))

        Mockito.`when`(bcbClient.create(CreatePixKeyRequest(
            keyType = PixKeyType.PHONE,
            key = "+5571996583398",
            bankAccount = BankAccount(
                "60701190",
                "0001",
                "291900",
                accountType = BankAccount.AccountType.CACC
            ), owner = Owner(
                type = Owner.OwnerType.NATURAL_PERSON,
                "Joaldo Tavares",
                "61176001515"
            ))))
            .thenReturn(HttpResponse.created(createPixResponse()))

        val response = grpcClient.registrar(
            RegistraChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID)
                .setTipoDeChave(TipoDeChave.CELULAR)
                .setChave("+5571996583398")
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

        Mockito.`when`(bcbClient.create(CreatePixKeyRequest(
            keyType = PixKeyType.EMAIL,
            key = "tetste@zup.com.br",
            bankAccount = BankAccount(
                "60701190",
                "0001",
                "291900",
                accountType = BankAccount.AccountType.CACC
            ), owner = Owner(
                type = Owner.OwnerType.NATURAL_PERSON,
                "Joaldo Tavares",
                "61176001515"
            ))))
            .thenReturn(HttpResponse.created(createPixResponse()))

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
    fun `nao deve registrar nova chave quando ela ja existir`() {

        repository.save(
            ChavePix(
                clienteId = UUID.fromString(CLIENTE_ID),
                tipoDeChave = br.com.zup.edu.pix.chave.TipoDeChave.EMAIL,
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
    fun `nao deve registrar chave com parametros invalidos`() {

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                RegistraChavePixRequest.newBuilder().build()
            )
        }

        with(erro) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }

    }

    @Test
    fun `nao deve registrar uma nova chave sem registrar no BCB`() {
        Mockito.`when`(itauClient.buscarContaPorTipo(clienteId = CLIENTE_ID, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(detalhesDaConta()))

        Mockito.`when`(bcbClient.create(createPixRequest()))
            .thenReturn(HttpResponse.badRequest())

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.registrar(
                RegistraChavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID)
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setChave("61176001515")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(erro) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Erro ao registrar chave no banco central", status.description)
        }
    }

    @Factory
    class Client {

        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RegistraChaveServiceGrpc.RegistraChaveServiceBlockingStub {
            return RegistraChaveServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ContasItauClient::class)
    fun itauClient(): ContasItauClient? {
        return Mockito.mock(ContasItauClient::class.java)
    }

    @MockBean(BancoCentralClient::class)
    fun brbClient(): BancoCentralClient? {
        return Mockito.mock(BancoCentralClient::class.java)
    }

    private fun detalhesDaConta(): DetalhesDaConta {
        return DetalhesDaConta(
            "CONTA_CORRENTE",
            InstituicaoResponse("BANCO ITAU", "60701190"),
            "0001",
            "291900",
            TitularResponse("Joaldo Tavares", "61176001515")
        )
    }

    private fun createPixRequest(): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            keyType = PixKeyType.CPF,
            key = "61176001515",
            bankAccount = BankAccount(
                "60701190",
                "0001",
                "291900",
                accountType = BankAccount.AccountType.CACC
            ), owner = Owner(
                type = Owner.OwnerType.NATURAL_PERSON,
                "Joaldo Tavares",
                "61176001515"
            )
        )
    }

    private fun createPixResponse(): CreatePixKeyResponse {
        return CreatePixKeyResponse(
            keyType = createPixRequest().keyType,
            key = createPixRequest().key,
            bankAccount = createPixRequest().bankAccount,
            owner = createPixRequest().owner,
            createdAt = LocalDateTime.now()
        )
    }
}