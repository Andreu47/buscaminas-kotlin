data class Casilla(
    // Estado de la casilla: si tiene mina
    var tieneMina: Boolean = false,
    // Estado de la casilla: si tiene mina
    var descubierta: Boolean = false,
    // Estado de la casilla: si tiene mina
    var tieneBandera: Boolean = false,
    // Las minas que hay alrededor de la casilla
    var minasAlrededor: Int = 0
)