data class Casilla(
    // Estado de la casilla: si tiene mina
    var tieneMina: Boolean = false,
    // Estado de la casilla: si esta descubierta
    var descubierta: Boolean = false,
    // Estado de la casilla: si tiene bandera o no
    var tieneBandera: Boolean = false,
    // Las minas que hay alrededor de la casilla
    var minasAlrededor: Int = 0
)