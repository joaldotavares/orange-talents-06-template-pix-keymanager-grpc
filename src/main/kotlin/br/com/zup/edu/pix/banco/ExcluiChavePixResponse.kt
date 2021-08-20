package br.com.zup.edu.pix.banco

import java.time.LocalDateTime

data class ExcluiChavePixResponse(
    val chave: String,
    val instituicao: String,
    val excluidoEm: LocalDateTime
)
