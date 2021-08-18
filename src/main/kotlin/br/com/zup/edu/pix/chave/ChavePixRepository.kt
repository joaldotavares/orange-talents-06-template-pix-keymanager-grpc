package br.com.zup.edu.pix.chave

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface ChavePixRepository : CrudRepository<ChavePix, UUID> {
    fun existsByChave(chave: String?): Boolean
    fun findByIdAndClienteId(uuidPix: UUID, uuidCliente: UUID): Optional<ChavePix>
}