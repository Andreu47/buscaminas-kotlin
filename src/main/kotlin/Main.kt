
import java.util.Scanner

private const val TAMANO_MINIMO = 5
private const val TAMANO_MAXIMO = 25

fun main() {

    val scanner = Scanner(System.`in`)

    println("===== BUSCAMINAS =====")

    // Pedimos el numero de filas
    var filas: Int

    do {
        print("Numero de filas ($TAMANO_MINIMO-$TAMANO_MAXIMO): ")
        filas = scanner.nextInt()

    } while (filas !in TAMANO_MINIMO..TAMANO_MAXIMO)


    // Pedimos el numero de columnas
    var columnas: Int

    do {
        print("Numero de columnas ($TAMANO_MINIMO-$TAMANO_MAXIMO): ")
        columnas = scanner.nextInt()

    } while (columnas !in TAMANO_MINIMO..TAMANO_MAXIMO)


    // Creamos una partida con las dimensiones elegidas
    val partida = Partida(filas, columnas)

    var jugando = true
    var haPerdido = false
    var haGanado = false


    // Mantenemos el juego activo hasta salir o encontrar una mina
    while (jugando && !haPerdido && !haGanado) {

        println()
        partida.mostrarTablero()

        println()
        println("$CIAN d - Descubrir casilla $RESET")
        println("$CIAN b - Poner o quitar bandera $RESET")
        println("$CIAN s - Salir $RESET")
        print("$CIAN Accion: $RESET")

        val accion = scanner.next().lowercase()

        when (accion) {

            "d", "b" -> {

                // Restamos uno porque los indices empiezan en cero
                print("Fila: ")
                val fila = scanner.nextInt() - 1

                print("Columna: ")
                val columna = scanner.nextInt() - 1

                if (accion == "d") {
                    haPerdido = partida.descubrirCasilla(fila, columna)
                } else {
                    partida.cambiarBandera(fila, columna)
                }
            }

            "s" -> {
                jugando = false
            }

            else -> {
                println("Accion no valida.")
            }
        }

        // Comprobamos si el jugador ha ganado
        if (!haPerdido) {
            haGanado = partida.haGanado()
        }
    }


    // Mostramos el resultado de la partida

    // Mostramos el resultado final de la partida
    if (haPerdido) {

        println()
        partida.mostrarTablero(true)
        println("$ROJO Has encontrado una mina. Has perdido. $RESET")

    } else if (haGanado) {

        println()
        partida.mostrarTablero()
        println("$VERDE Enhorabuena! Has descubierto todas las casillas. $RESET")

    } else {

        println("$CIAN Partida finalizada. $RESET")
    }

    scanner.close()
}
