package gov.rr.iteraima.data.model

enum class Department(val title: String) {
    DIPRE("Diretoria de Presidência"),
    DIGOF("Diretoria de Gestão Orçamentária e Financeira"), 
    DSF("Diretoria de Serviços Fundiários"),
    DIRAD("Diretoria de Administração"),
    DIGEF("Diretoria de Gestão Fundiária");

    companion object {
        fun fromTitle(title: String): Department? = 
            values().find { it.title.equals(title, ignoreCase = true) }
    }
}
