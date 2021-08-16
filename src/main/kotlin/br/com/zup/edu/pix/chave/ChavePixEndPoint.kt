package br.com.zup.edu.pix.chave

import br.com.zup.edu.RegistraChaveGrpcServiceGrpc
import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.RegistraChavePixResponse
import br.com.zup.edu.pix.exception.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ChavePixEndPoint(@Inject private val service: ChavePixService) :
    RegistraChaveGrpcServiceGrpc.RegistraChaveGrpcServiceImplBase() {

    override fun registrar(
        request: RegistraChavePixRequest,
        responseObserver: StreamObserver<RegistraChavePixResponse>
    ) {
        val chave = request.toModel();
        val resposta = service.registra(chave)

        responseObserver.onNext(
            RegistraChavePixResponse.newBuilder()
                .setClienteId(resposta.clienteId.toString())
                .setPixId(resposta.id.toString())
                .build()
        )

        responseObserver.onCompleted()
    }
}