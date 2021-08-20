package br.com.zup.edu.pix.chave

import br.com.zup.edu.RegistraChaveGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import javax.inject.Singleton

@Factory
class RegistraClient {

    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RegistraChaveGrpcServiceGrpc.RegistraChaveGrpcServiceBlockingStub {
        return RegistraChaveGrpcServiceGrpc.newBlockingStub(channel)
    }
}