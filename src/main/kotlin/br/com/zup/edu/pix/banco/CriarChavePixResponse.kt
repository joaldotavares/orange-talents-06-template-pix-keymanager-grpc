package br.com.zup.edu.pix.banco

import java.time.LocalDateTime

class CriarChavePixResponse (
    val tipo: TipoChave,
    val chave: String,
    val contaBancaria: ContaBancaria,
    val titular: Titular,
    val criadoEm: LocalDateTime
)
