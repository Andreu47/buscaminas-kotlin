
private const val CASILLAS_POR_MINA = 6

// Colores del texto de la consola
const val RESET = "\u001B[0m"

const val ROJO = "\u001B[91m"
const val VERDE = "\u001B[92m"
const val AMARILLO = "\u001B[93m"
const val AZUL = "\u001B[94m"
const val MORADO = "\u001B[95m"
const val CIAN = "\u001B[96m"
const val BLANCO = "\u001B[97m"
const val GRIS = "\u001B[90m"


class Partida(private val filas: Int, private val columnas: Int) {

    // Cada posicion contiene un objeto Casilla
    private val tablero = Array(filas) {
        Array(columnas) { Casilla() }
    }

    private val cantidadMinas = filas * columnas / CASILLAS_POR_MINA
    private var minasGeneradas = false

    // --- MOSTRAR TABLERO ---

    fun mostrarTablero(revelarMinas: Boolean = false) {
        // Numeros de las columnas
        print("    ")
        for (columna in 1..columnas) {
            print("$CIAN${columna.toString().padStart(3)}$RESET")
        }
        println()

        // Recorremos todas las filas y columnas
        for (fila in 0 until filas) {
            print("$CIAN${(fila + 1).toString().padStart(3)}$RESET ")
            for (columna in 0 until columnas) {
                val casilla = tablero[fila][columna]
                val simbolo = obtenerSimbolo(casilla, revelarMinas)
                val color = obtenerColor(simbolo)
                print("$color${simbolo.padStart(3)}$RESET")
            }
            println()
        }
        mostrarContador()
    }

    private fun obtenerSimbolo(casilla: Casilla, revelarMinas: Boolean): String {
        // Comprobamos el estado en el orden correcto
        val simbolo = when {
            revelarMinas && casilla.tieneMina -> "*"
            casilla.tieneBandera -> "F"
            !casilla.descubierta -> "#"
            casilla.minasAlrededor == 0 -> " "
            else -> casilla.minasAlrededor.toString()
        }
        return simbolo
    }

    private fun obtenerColor(simbolo: String): String {
        // Cada numero y simbolo tiene su propio color
        val color = when (simbolo) {
            "*", "3" -> ROJO
            "F", "5" -> AMARILLO
            "#", "8" -> GRIS
            "1" -> AZUL
            "2" -> VERDE
            "4" -> MORADO
            "6" -> CIAN
            "7" -> BLANCO
            else -> RESET
        }
        return color
    }

    private fun mostrarContador() {
        var banderasColocadas = 0

        // Contamos las banderas de todo el tablero
        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {
                if (tablero[fila][columna].tieneBandera) {
                    banderasColocadas++
                }
            }
        }

        val minasRestantes = cantidadMinas - banderasColocadas
        println("\n${ROJO}Minas: $cantidadMinas$RESET | " +
                "${AMARILLO}Banderas: $banderasColocadas$RESET | " +
                "${CIAN}Restantes: $minasRestantes$RESET")
    }

    // --- PREPARAR LAS MINAS ---

    private fun colocarMinas(filaInicial: Int, columnaInicial: Int) {
        var minasColocadas = 0

        // Elegimos posiciones aleatorias y evitamos repetir minas
        while (minasColocadas < cantidadMinas) {
            val fila = (0 until filas).random()
            val columna = (0 until columnas).random()
            val casilla = tablero[fila][columna]
            val esPrimeraCasilla = fila == filaInicial && columna == columnaInicial

            if (!casilla.tieneMina && !esPrimeraCasilla) {
                casilla.tieneMina = true
                minasColocadas++
            }
        }
    }

    private fun calcularMinasAlrededor() {
        // Guardamos el numero de minas vecinas en cada casilla segura
        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {
                val casilla = tablero[fila][columna]
                if (!casilla.tieneMina) {
                    casilla.minasAlrededor = contarMinasVecinas(fila, columna)
                }
            }
        }
    }

    private fun contarMinasVecinas(fila: Int, columna: Int): Int {
        var contador = 0

        // Revisamos las ocho casillas que rodean la posicion
        for (filaVecina in fila - 1..fila + 1) {
            for (columnaVecina in columna - 1..columna + 1) {
                if (posicionValida(filaVecina, columnaVecina) &&
                    tablero[filaVecina][columnaVecina].tieneMina) {
                    contador++
                }
            }
        }
        return contador
    }

    // --- DESCUBRIR CASILLAS ---

    fun descubrirCasilla(fila: Int, columna: Int): Boolean {
        var haPerdido = false

        if (posicionValida(fila, columna)) {
            val casilla = tablero[fila][columna]

            if (casilla.descubierta && casilla.minasAlrededor > 0) {
                // Pulsar un numero descubierto intenta abrir sus vecinos
                haPerdido = descubrirAlrededor(fila, columna)
            } else if (!casilla.descubierta && !casilla.tieneBandera) {
                // Las casillas ocultas se abren con las reglas normales
                haPerdido = abrirCasilla(fila, columna)
            }
        } else {
            println("Posicion fuera del tablero.")
        }
        return haPerdido
    }

    private fun abrirCasilla(fila: Int, columna: Int): Boolean {
        // Las minas se crean al realizar el primer descubrimiento
        if (!minasGeneradas) {
            colocarMinas(fila, columna)
            calcularMinasAlrededor()
            minasGeneradas = true
        }

        val casilla = tablero[fila][columna]
        if (casilla.tieneMina) {
            casilla.descubierta = true
        } else {
            descubrirZona(fila, columna)
        }
        return casilla.tieneMina
    }

    private fun descubrirZona(fila: Int, columna: Int) {
        val casilla = tablero[fila][columna]

        // Solo abrimos casillas seguras, ocultas y sin bandera
        if (!casilla.descubierta && !casilla.tieneBandera && !casilla.tieneMina) {
            casilla.descubierta = true

            // Una casilla vacia abre sus vecinas automaticamente
            if (casilla.minasAlrededor == 0) {
                for (filaVecina in fila - 1..fila + 1) {
                    for (columnaVecina in columna - 1..columna + 1) {
                        if (posicionValida(filaVecina, columnaVecina)) {
                            descubrirZona(filaVecina, columnaVecina)
                        }
                    }
                }
            }
        }
    }

    private fun descubrirAlrededor(fila: Int, columna: Int): Boolean {
        var haPerdido = false

        // Solo abrimos vecinos si coinciden numero y banderas
        if (contarBanderasVecinas(fila, columna) == tablero[fila][columna].minasAlrededor) {
            for (filaVecina in fila - 1..fila + 1) {
                for (columnaVecina in columna - 1..columna + 1) {
                    if (posicionValida(filaVecina, columnaVecina) && !haPerdido) {
                        val casilla = tablero[filaVecina][columnaVecina]
                        if (!casilla.descubierta && !casilla.tieneBandera) {
                            // Reutilizamos las reglas normales de descubrir
                            haPerdido = descubrirCasilla(filaVecina, columnaVecina)
                        }
                    }
                }
            }
        }
        return haPerdido
    }

    private fun contarBanderasVecinas(fila: Int, columna: Int): Int {
        var contador = 0

        // Contamos las banderas de las casillas de alrededor
        for (filaVecina in fila - 1..fila + 1) {
            for (columnaVecina in columna - 1..columna + 1) {
                if (posicionValida(filaVecina, columnaVecina) &&
                    tablero[filaVecina][columnaVecina].tieneBandera) {
                    contador++
                }
            }
        }
        return contador
    }

    // --- BANDERAS Y VICTORIA ---

    fun cambiarBandera(fila: Int, columna: Int) {
        if (posicionValida(fila, columna)) {
            val casilla = tablero[fila][columna]
            // No permitimos banderas en casillas descubiertas
            if (!casilla.descubierta) {
                casilla.tieneBandera = !casilla.tieneBandera
            }
        } else {
            println("Posicion fuera del tablero.")
        }
    }

    fun haGanado(): Boolean {
        var casillasDescubiertas = 0

        // Contamos las casillas seguras que ya se han abierto
        for (fila in 0 until filas) {
            for (columna in 0 until columnas) {
                val casilla = tablero[fila][columna]
                if (casilla.descubierta && !casilla.tieneMina) {
                    casillasDescubiertas++
                }
            }
        }
        val casillasSeguras = filas * columnas - cantidadMinas
        return minasGeneradas && casillasDescubiertas == casillasSeguras
    }

    private fun posicionValida(fila: Int, columna: Int): Boolean {
        return fila in 0 until filas && columna in 0 until columnas
    }
}

