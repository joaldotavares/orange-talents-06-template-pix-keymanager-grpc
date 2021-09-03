package br.com.zup.edu.pix.exception

import io.grpc.Status

class ChavePixNaoEncontradaHandler: ExceptionHandler<ChavePixException> {

    override fun handle(e: ChavePixException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(Status.NOT_FOUND
            .withDescription(e.message).withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixException
    }
}