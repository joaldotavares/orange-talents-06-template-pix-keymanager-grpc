package br.com.zup.edu.pix.chave.cria

import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.RegistraChavePixResponse
import br.com.zup.edu.RegistraChaveServiceGrpc
import br.com.zup.edu.pix.chave.toModel
import br.com.zup.edu.pix.exception.ErrorHandler
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ChavePixEndPoint(@Inject private val service: ChavePixService) :
    RegistraChaveServiceGrpc.RegistraChaveServiceImplBase() {

    private val logger = LoggerFactory.getLogger(ChavePixEndPoint::class.java)

    override fun registrar(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {
        val chave = request.toModel();
        val resposta = service.registra(chave)

        logger.info(chave.toString())
        responseObserver.onNext(
            RegistraChavePixResponse.newBuilder()
                .setClienteId(resposta.clienteId.toString())
                .setPixId(resposta.id.toString())
                .build()
        )

        responseObserver.onCompleted()
    }
}