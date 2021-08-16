package br.com.zup.edu.pix.exception

import io.grpc.BindableService
import io.grpc.stub.StreamObserver
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExceptionHandlerInterceptor(@Inject private val resolver: ExceptionHandlerResolver) :
    MethodInterceptor<BindableService, Any> {
    override fun intercept(context: MethodInvocationContext<BindableService, Any>): Any? {
        try {
            return context.proceed()
        } catch (e: Exception) {
            val handler = resolver.resolve(e)
            val status = handler.handle(e)

            GrpcEndpointArguments(context).response().onError(status.asRuntimeException())

            return null
        }
    }

    private class GrpcEndpointArguments(val context: MethodInvocationContext<BindableService, Any>) {

        fun response(): StreamObserver<*> {
            return context.parameterValues[1] as StreamObserver<*>
        }
    }
}