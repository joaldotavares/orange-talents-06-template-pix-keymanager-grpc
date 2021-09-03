package br.com.zup.edu.pix.chave.consulta

import br.com.zup.edu.CarregaChavePixRequest
import br.com.zup.edu.CarregaChavePixResponse
import br.com.zup.edu.CarregaChaveServiceGrpc
import br.com.zup.edu.pix.chave.ChavePixRepository
import br.com.zup.edu.pix.chave.toModel
import br.com.zup.edu.pix.client.BancoCentralClient
import br.com.zup.edu.pix.exception.ErrorHandler
import io.grpc.stub.StreamObserver
import io.micronaut.validation.validator.Validator
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class CarregaChaveEndPoint(
    @Inject private val chaveRepository: ChavePixRepository,
    @Inject private val bcbClient: BancoCentralClient,
    @Inject private val validador: Validator
) : CarregaChaveServiceGrpc.CarregaChaveServiceImplBase() {

    override fun carregar(
        request: CarregaChavePixRequest,
        responseObserver: StreamObserver<CarregaChavePixResponse>
    ) {
        val filtro = request.toModel(validador)

        val chaveInfo = filtro.filtra(chaveRepository, bcbClient)

        responseObserver.onNext(CarregaChavePixConverter().converter(chaveInfo))
        responseObserver.onCompleted()
    }
}