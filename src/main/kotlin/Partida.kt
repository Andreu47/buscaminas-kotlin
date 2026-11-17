
private const val CASILLAS_POR_MINA = 4

class Partida(private val filas: Int, private val columnas: Int) {

    // Creamos una matriz con todas las casillas del tablero
    private val tablero = Array(filas) { Array(columnas) { Casilla() } }

    private val cantidadMinas = filas * columnas / CASILLAS_POR_MINA
    private var minasGeneradas = false


    fun mostrarTablero(revelarMinas: Boolean = false) {

        // Mostramos los numeros de las columnas
        print("    ")

        for (columna in 1..columnas) {
            print(columna.toString().padStart(3))
        }

        println()

        // Recorremos las filas y columnas del tablero
        for (fila in 0 until filas) {
            print((fila + 1).toString().padStart(3) + " ")

            for (columna in 0 until columnas) {
                val casilla = tablero[fila][columna]

                // Elegimos el simbolo segun el estado de la casilla
                val simbolo = when {
                    revelarMinas && casilla.tieneMina -> "*"
                    casilla.tieneBandera -> "F"
                    !casilla.descubierta -> "#"
                    casilla.minasAlrededor == 0 -> "."
                    else -> casilla.minasAlrededor.toString()
                }

                print(simbolo.padStart(3))
            }

            println()
        }
    }

    private fun colocarMinas(filaInicial: Int, columnaInicial: Int) {

        var minasColocadas = 0

        // Elegimos posiciones aleatorias sin repetir minas
        while (minasColocadas < cantidadMinas) {

            val fila = (0 until filas).random()
            val columna = (0 until columnas).random()

            val casilla = tablero[fila][columna]

            val primeraCasilla = fila == filaInicial && columna == columnaInicial

            if (!casilla.tieneMina && !primeraCasilla) {
                casilla.tieneMina = true
                minasColocadas++
            }
        }
    }

    private fun calcularMinasAlrededor() {

        // Contamos las minas vecinas de cada casilla sin mina
        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {

                val casilla = tablero[fila][columna]

                if (!casilla.tieneMina) {
                    var minasCercanas = 0

                    // Revisamos las posiciones que rodean la casilla
                    for (filaVecina in fila - 1..fila + 1) {
                        for (columnaVecina in columna - 1..columna + 1) {

                            val posicionValida = filaVecina in 0 until filas &&
                                    columnaVecina in 0 until columnas

                            if (posicionValida && tablero[filaVecina][columnaVecina].tieneMina) {
                                minasCercanas++
                            }
                        }
                    }

                    casilla.minasAlrededor = minasCercanas
                }
            }
        }
    }

    fun descubrirCasilla(fila: Int, columna: Int): Boolean {

        var haPerdido = false

        // Comprobamos que la posicion este dentro del tablero
        if (fila in 0 until filas && columna in 0 until columnas) {

            val casilla = tablero[fila][columna]

            // Solo descubrimos casillas ocultas y sin bandera
            if (!casilla.descubierta && !casilla.tieneBandera) {

                // Generamos las minas en el primer movimiento
                if (!minasGeneradas) {
                    colocarMinas(fila, columna)
                    calcularMinasAlrededor()
                    minasGeneradas = true
                }

                casilla.descubierta = true
                haPerdido = casilla.tieneMina
            }

        } else {
            println("Posicion fuera del tablero.")
        }

        return haPerdido
    }

    fun cambiarBandera(fila: Int, columna: Int) {

        // Comprobamos que la posicion este dentro del tablero
        if (fila in 0 until filas && columna in 0 until columnas) {

            val casilla = tablero[fila][columna]

            // Las banderas solo se pueden colocar en casillas ocultas
            if (!casilla.descubierta) {
                casilla.tieneBandera = !casilla.tieneBandera
            }

        } else {
            println("Posicion fuera del tablero.")
        }
    }
}
