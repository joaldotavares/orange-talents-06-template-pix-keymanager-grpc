package br.com.zup.edu.pix.chave.lista

import br.com.zup.edu.*
import br.com.zup.edu.pix.chave.ChavePixRepository
import br.com.zup.edu.pix.exception.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ListaChavePixEndpoint(@Inject private val chaveRepository: ChavePixRepository) :
    ListaChaveServiceGrpc.ListaChaveServiceImplBase() {

    override fun listar(
        request: ListaChavePixRequest,
        responseObserver: StreamObserver<ListaChavePixResponse>
    ) {
        if (request.clienteId.isNullOrBlank()) {
            throw IllegalArgumentException("Id do cliente não pode ser nulo ou vazio")
        }

        val clienteId = UUID.fromString(request.clienteId)

        val listaDeChaves = chaveRepository.findAllByClienteId(clienteId).map {
            ListaChavePixResponse.ChavePix.newBuilder()
                .setPixId(it.id.toString())
                .setTipoDeChave(TipoDeChave.valueOf(it.tipoDeChave.name))
                .setChave(it.chave)
                .setTipoDeConta(TipoDeConta.valueOf(it.tipoDeConta.name))
                .setCriadoEm(it.criadaEm.let {
                    val createAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createAt.epochSecond)
                        .setNanos(createAt.nano)
                        .build()
                })
                .build()
        }

        responseObserver.onNext(
            ListaChavePixResponse.newBuilder()
                .setClienteId(clienteId.toString())
                .addAllChaves(listaDeChaves)
                .build()
        )
        responseObserver.onCompleted()

    }
}