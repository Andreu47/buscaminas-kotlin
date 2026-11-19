import java.util.Scanner

private const val TAMANO_MINIMO = 5
private const val TAMANO_MAXIMO = 15

fun main() {
    val scanner = Scanner(System.`in`)

    println("$MORADO ===== BUSCAMINAS ===== $RESET")
    val filas = pedirDimension(scanner, "filas")
    val columnas = pedirDimension(scanner, "columnas")

    // Iniciamos el juego con el tablero elegido
    val partida = Partida(filas, columnas)
    jugarPartida(scanner, partida)
    scanner.close()
}

private fun jugarPartida(scanner: Scanner, partida: Partida) {
    var salir = false
    var haPerdido = false
    println("$AMARILLO \nComandos: d fila columna | b fila columna | s para salir $RESET")

    // Procesamos movimientos hasta ganar, perder o salir
    while (!salir && !haPerdido && !partida.haGanado()) {
        println()
        partida.mostrarTablero()
        print("$MORADO \nAccion: $RESET")

        // Separamos el comando en sus distintas partes
        val partes = scanner.nextLine().trim().split(" ").filter { it.isNotEmpty() }
        if (partes.size == 1 && partes[0].lowercase() == "s") {
            salir = true
        } else {
            haPerdido = ejecutarMovimiento(partes, partida)
        }
    }
    mostrarResultado(partida, haPerdido, salir)
}

private fun pedirDimension(scanner: Scanner, nombre: String): Int {
    var dimension: Int

    // Repetimos la pregunta hasta recibir un numero valido
    do {
        print("Numero de $nombre ($TAMANO_MINIMO-$TAMANO_MAXIMO): ")
        dimension = scanner.nextLine().trim().toIntOrNull() ?: 0
        if (dimension !in TAMANO_MINIMO..TAMANO_MAXIMO) {
            println("Introduce un numero entre $TAMANO_MINIMO y $TAMANO_MAXIMO.")
        }
    } while (dimension !in TAMANO_MINIMO..TAMANO_MAXIMO)

    return dimension
}

private fun ejecutarMovimiento(partes: List<String>, partida: Partida): Boolean {
    var haPerdido = false

    // Un movimiento debe tener una accion y dos coordenadas
    if (partes.size == 3) {
        val fila = partes[1].toIntOrNull()
        val columna = partes[2].toIntOrNull()

        if (fila != null && columna != null) {
            when (partes[0].lowercase()) {
                "d" -> haPerdido = partida.descubrirCasilla(fila - 1, columna - 1)
                "b" -> partida.cambiarBandera(fila - 1, columna - 1)
                else -> println("$CIAN Accion no valida. Usa d, b o s. $RESET")
            }
        } else {
            println("$ROJO La fila y la columna deben ser numeros. $RESET")
        }
    } else {
        println("$ROJO Formato incorrecto. Ejemplo: d 3 5 $RESET")
    }
    return haPerdido
}

private fun mostrarResultado(partida: Partida, haPerdido: Boolean, salir: Boolean) {
    println()

    // Mostramos el tablero final cuando ganamos o perdemos
    if (haPerdido) {
        partida.mostrarTablero(true)
        println("$ROJO Has encontrado una mina. Has perdido. $RESET")
    } else if (!salir && partida.haGanado()) {
        partida.mostrarTablero()
        println("$VERDE Enhorabuena! Has descubierto todas las casillas. $RESET")
    } else {
        println("$CIAN Partida finalizada. $RESET")
    }
}