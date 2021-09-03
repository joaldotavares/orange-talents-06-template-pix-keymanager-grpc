package br.com.zup.edu.pix.banco

import java.time.LocalDateTime

class CreatePixKeyResponse (
    val keyType: PixKeyType,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
)
