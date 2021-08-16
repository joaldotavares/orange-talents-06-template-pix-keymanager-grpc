package br.com.zup.edu.pix.exception

import io.grpc.Status

class ChavePixExceptionHandler : ExceptionHandler<ChavePixException> {

    override fun handle(exception: ChavePixException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.ALREADY_EXISTS
                .withDescription(exception.message)
                .withCause(exception)
        )
    }

    override fun supports(exception: Exception): Boolean {
        return exception is ChavePixException
    }
}