
private const val CASILLAS_POR_MINA = 6

// Colores del texto de la consola
private const val RESET = "\u001B[0m"

private const val ROJO = "\u001B[91m"
private const val VERDE = "\u001B[92m"
private const val AMARILLO = "\u001B[93m"
private const val AZUL = "\u001B[94m"
private const val MORADO = "\u001B[95m"
private const val CIAN = "\u001B[96m"
private const val BLANCO = "\u001B[97m"
private const val GRIS = "\u001B[90m"


class Partida(private val filas: Int, private val columnas: Int) {

    // Creamos una matriz con todas las casillas del tablero
    private val tablero = Array(filas) { Array(columnas) { Casilla() } }

    private val cantidadMinas = filas * columnas / CASILLAS_POR_MINA
    private var minasGeneradas = false



    fun mostrarTablero(revelarMinas: Boolean = false) {

        // Mostramos los numeros de las columnas
        print("    ")

        for (columna in 1..columnas) {
            print("$CIAN${columna.toString().padStart(3)}$RESET")
        }

        println()

        // Recorremos todas las casillas del tablero
        for (fila in 0 until filas) {

            print("$CIAN${(fila + 1).toString().padStart(3)}$RESET ")

            for (columna in 0 until columnas) {

                val casilla = tablero[fila][columna]

                // Elegimos el simbolo segun el estado de la casilla
                val simbolo = when {
                    revelarMinas && casilla.tieneMina -> "*"
                    casilla.tieneBandera -> "F"
                    !casilla.descubierta -> "#"
                    casilla.minasAlrededor == 0 -> " "
                    else -> casilla.minasAlrededor.toString()
                }

                // Asignamos un color a cada simbolo
                val color = when (simbolo) {
                    "*" -> ROJO
                    "F" -> AMARILLO
                    "#" -> GRIS
                    "1" -> AZUL
                    "2" -> VERDE
                    "3" -> ROJO
                    "4" -> MORADO
                    "5" -> AMARILLO
                    "6" -> CIAN
                    "7" -> BLANCO
                    "8" -> GRIS
                    else -> RESET
                }

                // Mostramos la casilla con su color
                print("$color${simbolo.padStart(3)}$RESET")
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

    private fun descubrirZona(fila: Int, columna: Int) {

        val casilla = tablero[fila][columna]

        // Solo descubrimos casillas ocultas, sin bandera y sin mina
        if (!casilla.descubierta && !casilla.tieneBandera && !casilla.tieneMina) {

            casilla.descubierta = true

            // Si no hay minas alrededor, descubrimos las casillas vecinas
            if (casilla.minasAlrededor == 0) {

                for (filaVecina in fila - 1..fila + 1) {
                    for (columnaVecina in columna - 1..columna + 1) {

                        // Comprobamos que la posicion vecina exista
                        if (filaVecina in 0 until filas && columnaVecina in 0 until columnas) {
                            descubrirZona(filaVecina, columnaVecina)
                        }
                    }
                }
            }
        }
    }

    fun descubrirCasilla(fila: Int, columna: Int): Boolean {

        var haPerdido = false

        // Comprobamos que la posicion este dentro del tablero
        if (fila in 0 until filas && columna in 0 until columnas) {

            val casilla = tablero[fila][columna]

            // Solo podemos descubrir casillas ocultas y sin bandera
            if (!casilla.descubierta && !casilla.tieneBandera) {

                // Generamos las minas en el primer movimiento
                if (!minasGeneradas) {
                    colocarMinas(fila, columna)
                    calcularMinasAlrededor()
                    minasGeneradas = true
                }

                // Comprobamos si la casilla contiene una mina
                if (casilla.tieneMina) {
                    casilla.descubierta = true
                    haPerdido = true
                } else {
                    descubrirZona(fila, columna)
                }
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
