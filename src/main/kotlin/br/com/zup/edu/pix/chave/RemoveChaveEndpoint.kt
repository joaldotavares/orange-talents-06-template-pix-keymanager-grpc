package br.com.zup.edu.pix.chave

import br.com.zup.edu.RemoveChavePixRequest
import br.com.zup.edu.RemoveChavePixResponse
import br.com.zup.edu.RemoveChaveServiceGrpc
import br.com.zup.edu.pix.exception.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemoveChaveEndpoint(@Inject private val service: RemoveChaveService) :
    RemoveChaveServiceGrpc.RemoveChaveServiceImplBase() {

    override fun remover(
        request: RemoveChavePixRequest,
        responseObserver: StreamObserver<RemoveChavePixResponse>
    ) {
        service.remover(request.clienteId, request.pixId)

        responseObserver.onNext(
            RemoveChavePixResponse.newBuilder()
                .setClientId(request.clienteId)
                .setPixId(request.pixId)
                .setMessage("Chave removida com sucesso")
                .build()
        )
        responseObserver.onCompleted()
    }
}