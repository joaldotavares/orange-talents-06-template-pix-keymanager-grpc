package br.com.zup.edu.pix.chave.consulta

import br.com.zup.edu.CarregaChavePixResponse
import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta
import com.google.protobuf.Timestamp
import java.time.ZoneId

class CarregaChavePixConverter {

    fun converter(chaveInfo: ChavePixInfo): CarregaChavePixResponse {
        return CarregaChavePixResponse.newBuilder()
            .setClienteId(chaveInfo.clienteId.toString())
            .setPixId(chaveInfo.pixId.toString())
            .setChave(CarregaChavePixResponse.ChavePix.newBuilder()
                .setTipoDeChave(TipoDeChave.valueOf(chaveInfo.tipoDeChave.name))
                .setChave(chaveInfo.chave)
                .setConta(CarregaChavePixResponse.ChavePix.ContaInfo.newBuilder()
                    .setTipoDeConta(TipoDeConta.valueOf(chaveInfo.tipoDeConta.name))
                    .setInstituicao(chaveInfo.conta.instituicao)
                    .setNomeDoTitular(chaveInfo.conta.nomeDoTitular)
                    .setCpfDoTitular(chaveInfo.conta.cpfDoTitular)
                    .setAgencia(chaveInfo.conta.agencia)
                    .setNumeroDaConta(chaveInfo.conta.numeroDaConta)
                    .build())
                .setCriadoEm(chaveInfo.criadaEm.let {
                    val createAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createAt.epochSecond)
                        .setNanos(createAt.nano)
                        .build()
                })
            )
            .build()
    }
}