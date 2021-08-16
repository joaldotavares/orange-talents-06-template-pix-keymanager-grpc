package br.com.zup.edu.pix.client

import br.com.zup.edu.pix.conta.DetalhesDaConta
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.contas.url}")
interface ContasItauClient {

    @Get("/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun buscarContaPorTipo(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<DetalhesDaConta>
}