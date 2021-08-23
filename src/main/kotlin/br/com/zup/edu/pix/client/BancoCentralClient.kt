package br.com.zup.edu.pix.client

import br.com.zup.edu.pix.banco.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${bcb.pix.url}")
interface BancoCentralClient {

    @Post("/api/v1/pix/keys", produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun create(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>

    @Delete("/api/v1/pix/keys/{key}", produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun delete(
        @PathVariable key: String,
        @Body request: DeletePixKeyRequest
    ): HttpResponse<DeletePixKeyResponse>

    @Get("/api/v1/pix/keys/{key}", consumes = [MediaType.APPLICATION_XML])
    fun buscarPorChave(@PathVariable key: String): HttpResponse<PixKeyDetailsResponse>
}