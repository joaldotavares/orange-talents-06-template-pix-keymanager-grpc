package br.com.zup.edu.pix.client

import br.com.zup.edu.RemoveChavePixRequest
import br.com.zup.edu.pix.banco.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${bcb.pix.url}")
interface BancoCentralClient {

    @Post( "/api/v1/pix/keys", produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun criarPix(@Body request: CriarChavePixRequest): HttpResponse<CriarChavePixResponse>

    @Delete( "/api/v1/pix/keys/{keys}", produces = [MediaType.APPLICATION_XML], consumes = [MediaType.APPLICATION_XML])
    fun deletarPix(@PathVariable chave: String, @Body request: ExcluiChavePixRequest): HttpResponse<ExcluiChavePixResponse>

    @Get( "/api/v1/pix/keys", consumes = [MediaType.APPLICATION_XML])
    fun buscarPorChave(@PathVariable chave: String): HttpResponse<DetalhesDaChaveResponse>
}