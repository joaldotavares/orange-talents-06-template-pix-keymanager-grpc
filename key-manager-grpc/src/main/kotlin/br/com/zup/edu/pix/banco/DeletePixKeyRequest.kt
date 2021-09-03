package br.com.zup.edu.pix.banco

import br.com.zup.edu.pix.conta.ContaAssociada

data class DeletePixKeyRequest(
    val key: String,
    val participant: String = ContaAssociada.ITAU_UNIBANCO_ISPB
)
