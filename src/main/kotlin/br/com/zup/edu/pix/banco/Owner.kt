package br.com.zup.edu.pix.banco

data class Owner(
    val type: OwnerType,
    val name: String,
    val taxIdNumber: String
){
    enum class OwnerType{
        NATURAL_PERSON,
        LEAGL_PERSON
    }
}
