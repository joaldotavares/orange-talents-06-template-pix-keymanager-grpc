package br.com.zup.edu.pix.banco

data class Titular(
    val tipo: TipoDeTitular,
    val nome: String,
    val numeroImposto: String
){
    enum class TipoDeTitular{
        NATURAL_PERSON,
        LEAGL_PERSON
    }
}
