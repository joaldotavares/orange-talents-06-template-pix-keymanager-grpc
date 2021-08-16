package br.com.zup.edu.pix.chave

import br.com.zup.edu.pix.conta.ContaAssociada
import br.com.zup.edu.pix.conta.TipoDeConta
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
@Table(uniqueConstraints = [UniqueConstraint(
    name = "uk_chave_pix",
    columnNames = ["chave"]
)])
class ChavePix(

    @field:NotNull
    @Column(nullable = false)
    val clienteId: UUID,
    @field:NotNull
    @Enumerated(EnumType.STRING)
    val tipoDeChave: TipoDeChave,
    @field:NotBlank
    @Column(unique = true, nullable = false)
    val chave: String,
    @field:Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoDeConta: TipoDeConta,
    @field:Valid @NotNull
    @Embedded
    val conta: ContaAssociada
) {

    @Id
    @GeneratedValue
    val id: UUID? = null

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()

}
