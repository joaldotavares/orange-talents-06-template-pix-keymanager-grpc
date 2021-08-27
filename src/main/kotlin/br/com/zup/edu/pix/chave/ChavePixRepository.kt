package br.com.zup.edu.pix.chave

import br.com.zup.edu.pix.chave.cria.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface ChavePixRepository : CrudRepository<ChavePix, UUID> {
    fun existsByChave(chave: String?): Boolean
    fun findByIdAndClienteId(uuidPix: UUID, uuidCliente: UUID): Optional<ChavePix>

    fun findByChave(chave: String): Optional<ChavePix>

    fun findAllByClienteId(clienteId: UUID?): List<ChavePix>
}