package br.com.zup.edu.pix.exception

import io.grpc.Status
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExceptionHandlerResolver(@Inject val handlers: List<ExceptionHandler<Exception>>) {

    private var defaultHandler: ExceptionHandler<Exception> = DefaultExceptionHandler()

    constructor(handlers: List<ExceptionHandler<Exception>>, defaultHandler: ExceptionHandler<Exception>) : this(
        handlers
    ) {
        this.defaultHandler = defaultHandler
    }

    fun resolve(exception: Exception): ExceptionHandler<Exception> {
        val foundHandler = handlers.filter { h -> h.supports(exception) }
        if (foundHandler.size > 1) {
            throw IllegalStateException("Too many handlers supporting ${exception.javaClass.name}: $foundHandler")
        }
        return foundHandler.firstOrNull() ?: defaultHandler
    }
}


class DefaultExceptionHandler : ExceptionHandler<Exception> {
    override fun handle(exception: Exception): ExceptionHandler.StatusWithDetails {
        val status = when (exception) {
            is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(exception.message)
            is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(exception.message)
            else -> Status.UNKNOWN
        }
        return ExceptionHandler.StatusWithDetails(status.withCause(exception))
    }

    override fun supports(exception: Exception): Boolean {
        return true
    }

}