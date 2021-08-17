package br.com.zup.edu.pix.chave

import br.com.zup.edu.pix.conta.ContaAssociada
import br.com.zup.edu.pix.conta.TipoDeConta
import br.com.zup.edu.pix.validacao.ValidPix
import br.com.zup.edu.pix.validacao.ValidUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPix
@Introspected
class NovaChavePix(

    @ValidUUID
    @field:NotBlank
    val clienteId: String?,

    @field:NotNull
    val tipoDeChave: TipoDeChave?,

    @field:NotBlank @Size(max = 77)
    val chave: String?,

    @field:NotNull
    val tipoDeConta: TipoDeConta?
) {

    fun toModel(conta: ContaAssociada): ChavePix {
        return ChavePix(
            clienteId = UUID.fromString(this.clienteId),
            tipoDeChave = TipoDeChave.valueOf(tipoDeChave!!.name),
            chave = if (this.tipoDeChave == TipoDeChave.ALEATORIA) UUID.randomUUID().toString()
            else this.chave!!,
            tipoDeConta = TipoDeConta.valueOf(tipoDeConta!!.name),
            conta = conta
        )
    }
}