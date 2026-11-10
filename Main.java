import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

/* ===========================================================
 * Proyecto       : Monitoreo de calidad del agua - Quebrada La Iguana
 * Entidad        : Quebrada
 * Programado por : Willin Stewen Gutierrez Santos
 * Fecha          : 10/11/2026
 * -----------------------------------------------------------
 * HU-01  Registrar una quebrada con su nombre
 * HU-02  Buscar una quebrada por su nombre o por parte de él
 * HU-03  Corregir el nombre de una quebrada
 * HU-04  Eliminar una quebrada
 * HU-05  Listar las quebradas con su número de puntos
 * HU-06  Deshacer la última eliminación de una quebrada
 * RF-01  El sistema permite registrar una quebrada con su nombre y
 *        le asigna su código.
 * RF-02  Cuando la quebrada ya está registrada, el sistema rechaza
 *        el registro y avisa que ya existe.
 * RF-03  Cuando el nombre de la quebrada está vacío, el sistema
 *        rechaza el registro y avisa que el nombre es obligatorio.
 * RF-04  Cuando ya hay 100 quebradas registradas, el sistema rechaza
 *        el registro y avisa que no hay espacio.
 * RF-05  El sistema permite buscar una quebrada por su nombre
 *        completo y muestra su nombre y cuántos puntos tiene
 *        registrados.
 * RF-06  El sistema permite corregir el nombre de una quebrada y
 *        muestra el cambio en el listado.
 * RF-07  El sistema permite eliminar una quebrada sin dejar huecos
 *        en el listado.
 * RF-08  El sistema lista las quebradas con su número de puntos,
 *        contado a partir de los puntos registrados.
 * RF-09  El sistema permite deshacer la última eliminación de una
 *        quebrada y la devuelve al listado.
 * RF-72  El sistema permite buscar quebradas por parte de su nombre
 *        y muestra solo las que contienen ese texto.
 * RF-73  Cuando la búsqueda de quebradas no encuentra coincidencias,
 *        el sistema avisa que no encontró ninguna.
 * RF-74  Cuando el campo de búsqueda de quebradas está vacío, el
 *        sistema no busca y pide escribir un nombre.
 * RF-75  Cuando el nombre corregido ya pertenece a otra quebrada, el
 *        sistema rechaza el cambio, conserva el nombre anterior y
 *        avisa que ya existe.
 * RF-76  Cuando la quebrada a corregir no está registrada, el
 *        sistema avisa que no existe.
 * RF-77  Cuando el nombre corregido está vacío, el sistema rechaza
 *        el cambio, conserva el nombre anterior y avisa que el
 *        nombre es obligatorio.
 * RF-78  Cuando la quebrada a eliminar no está registrada, el
 *        sistema avisa que no existe y no cambia el listado.
 * RF-79  Cuando el nombre de la quebrada a eliminar está vacío, el
 *        sistema no elimina nada y pide escribir un nombre.
 * RF-80  Cuando no hay quebradas registradas, el sistema avisa que
 *        aún no hay ninguna.
 * RF-81  Al listar las quebradas, el sistema avisa cuántas tienen
 *        menos de 3 puntos.
 * RF-82  Cuando no hay eliminaciones de quebradas por deshacer, el
 *        sistema avisa que no hay nada que deshacer.
 * RF-83  Al deshacer, el sistema devuelve solo la última quebrada
 *        eliminada.
 * RF-129  Al cerrar el programa, el sistema guarda las quebradas en
 *        el archivo de texto y las carga al iniciar.
 * RN-01  Una quebrada se monitorea en tres puntos
 * RNF-03 Conservar los datos al cerrar y volver a abrir
 * -----------------------------------------------------------
 * Criterios: todos los de HU-01 a HU-06 (CA-01.1 a CA-01.5 · CA-02.1 a CA-02.4 · CA-03.1 a CA-03.4 · CA-04.1 a CA-04.3 · CA-05.1 a CA-05.3 · CA-06.1 a CA-06.3)
 * Estructuras: Arreglo con contador y pila propia (PilaQuebradas)
 * =========================================================== */
public class Main {

    // ------------------------------------------------------------------
    // DATOS DEL PROGRAMA
    // ------------------------------------------------------------------
    static Quebrada[] quebradas = new Quebrada[100];   // el arreglo: 100 casillas
    static int contador = 0;                           // cuantas casillas estan ocupadas
    static int ultimoCodigo = 0;                       // el ultimo codigo asignado: solo sube, para no repetir codigos
    static PilaQuebradas pila = new PilaQuebradas();   // guarda lo eliminado, para deshacer

    // Lee lo que escribe el usuario, con la misma codificacion que usa tu consola;
    // asi la tilde de "Iguaná" no se convierte en un signo de interrogacion.
    static Scanner sc = new Scanner(System.in, codificacion("stdin.encoding", "sun.stdin.encoding"));

    // Busca la codificacion de la consola (cambia segun la version de Java); si no la encuentra usa UTF-8
    static String codificacion(String propiedad, String propiedadVieja) {
        String enc = System.getProperty(propiedad);
        if (enc == null) {
            enc = System.getProperty(propiedadVieja);
        }
        if (enc == null) {
            enc = "UTF-8";
        }
        return enc;
    }

    static final String ARCHIVO = "quebradas.txt";     // archivo de texto donde se guardan las quebradas: codigo;nombre
    static final String ARCHIVO_PUNTOS = "puntos.txt"; // simula la tabla de puntos: codigo_punto;codigo_quebrada;nombre_punto;ubicacion
    static final String FLECHA = "->";                 // si tu consola muestra bien "→", puedes ponerlo
    static final String AVISO = "(!)";                 // si tu consola muestra bien "⚠", puedes ponerlo

    static String mensaje = "";   // lo que se muestra en la linea MENSAJE de la pantalla
    static String filtro = "";    // texto de la ultima busqueda ("" = mostrar todas)

    // ------------------------------------------------------------------
    // BUSCAR (lo usa tambien el programa de Simon para validar la quebrada)
    // ------------------------------------------------------------------

    // RF-05 · CA-02.1 — devuelve la posicion de la quebrada con ese nombre, o -1 si no esta
    static int buscarIndice(String nombre_quebrada) {
        for (int i = 0; i < contador; i++) {
            if (quebradas[i].nombre_quebrada.equals(nombre_quebrada)) {
                return i;
            }
        }
        return -1;
    }

    // RF-05 · CA-02.1 — busca una quebrada por su nombre completo; devuelve null si no existe
    static Quebrada buscarPorNombre(String nombre_quebrada) {
        int pos = buscarIndice(nombre_quebrada);
        if (pos == -1) {
            return null;
        }
        return quebradas[pos];
    }

    // RF-72 · CA-02.2 — cuenta cuantas quebradas tienen ese texto dentro de su nombre
    static int contarCoincidencias(String texto) {
        int cuantas = 0;
        for (int i = 0; i < contador; i++) {
            if (quebradas[i].nombre_quebrada.toLowerCase().contains(texto.toLowerCase())) {
                cuantas++;
            }
        }
        return cuantas;
    }

    // RF-08 · CA-05.1 — cuenta los puntos de una quebrada leyendo el archivo de puntos.
    // El numero de puntos NO se guarda en la quebrada: se calcula cada vez que se lista.
    static int contarPuntos(int codigo_quebrada) {
        File f = new File(ARCHIVO_PUNTOS);
        if (!f.exists()) {
            return 0;
        }
        int cuantos = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea = br.readLine();
            while (linea != null) {
                String[] partes = linea.split(";");
                if (partes.length >= 2 && partes[1].trim().equals(String.valueOf(codigo_quebrada))) {
                    cuantos++;
                }
                linea = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            System.out.println("No se pudo leer el archivo " + ARCHIVO_PUNTOS);
        }
        return cuantos;
    }

    // ------------------------------------------------------------------
    // OPERACIONES (C R U D L + deshacer)
    // ------------------------------------------------------------------

    // RF-01 · CA-01.1 — registra una quebrada con su nombre y le asigna su codigo
    // RF-03 · CA-01.4 — rechaza si el nombre esta vacio y avisa
    // RF-02 · CA-01.3 — rechaza si la quebrada ya esta registrada y avisa
    // RF-04 · CA-01.5 — rechaza si el arreglo ya tiene 100 quebradas y avisa
    static boolean crearQuebrada(String nombre_quebrada) {
        if (nombre_quebrada.equals("")) {
            mensaje = AVISO + " El nombre es obligatorio.   (RF-03 · CA-01.4)";
            return false;
        }
        if (buscarPorNombre(nombre_quebrada) != null) {
            mensaje = AVISO + " Esa quebrada ya está registrada.   (RF-02 · CA-01.3)";
            return false;
        }
        if (contador == quebradas.length) {
            mensaje = AVISO + " No hay espacio para más quebradas.   (RF-04 · CA-01.5)";
            return false;
        }
        ultimoCodigo++;                                          // el codigo lo asigna el programa
        quebradas[contador] = new Quebrada(ultimoCodigo, nombre_quebrada);
        contador++;
        return true;
    }

    // RF-06 · CA-03.1 — corrige el nombre de una quebrada (el codigo no cambia)
    // RF-76 · CA-03.3 — avisa si la quebrada a corregir no existe
    // RF-77 · CA-03.4 — rechaza el cambio si el nombre nuevo esta vacio y conserva el anterior
    // RF-75 · CA-03.2 — rechaza el cambio si el nombre nuevo ya es de otra quebrada y conserva el anterior
    static boolean corregirQuebrada(String nombre_quebrada, String nombre_nuevo) {
        Quebrada q = buscarPorNombre(nombre_quebrada);
        if (q == null) {
            mensaje = AVISO + " Esa quebrada no existe.   (RF-76 · CA-03.3)";
            return false;
        }
        if (nombre_nuevo.equals("")) {
            mensaje = AVISO + " El nombre es obligatorio; se conserva el anterior.   (RF-77 · CA-03.4)";
            return false;
        }
        Quebrada otra = buscarPorNombre(nombre_nuevo);
        if (otra != null && otra != q) {
            mensaje = AVISO + " Ese nombre ya existe; se conserva el anterior.   (RF-75 · CA-03.2)";
            return false;
        }
        q.nombre_quebrada = nombre_nuevo;
        return true;
    }

    // RF-07 · CA-04.1 — elimina corriendo las demas una casilla a la izquierda, sin dejar huecos
    // RF-79 · CA-04.3 — avisa si el nombre esta vacio y no elimina nada
    // RF-78 · CA-04.2 — avisa si la quebrada a eliminar no existe y no cambia el listado
    static boolean eliminarQuebrada(String nombre_quebrada) {
        if (nombre_quebrada.equals("")) {
            mensaje = AVISO + " Escriba el nombre de la quebrada.   (RF-79 · CA-04.3)";
            return false;
        }
        int pos = buscarIndice(nombre_quebrada);
        if (pos == -1) {
            mensaje = AVISO + " Esa quebrada no existe.   (RF-78 · CA-04.2)";
            return false;
        }
        pila.apilar(quebradas[pos]);                   // se guarda en la pila para poder deshacer
        for (int i = pos; i < contador - 1; i++) {
            quebradas[i] = quebradas[i + 1];           // cada una pasa a la casilla de su izquierda
        }
        quebradas[contador - 1] = null;                // la ultima casilla queda libre
        contador--;                                    // ahora hay una menos
        return true;
    }

    // RF-09 · CA-06.1 — devuelve al listado la ultima quebrada eliminada (queda de ultima: CA-06.3)
    // RF-82 · CA-06.2 — si no hay nada que deshacer, avisa (no es un error)
    static boolean deshacerEliminacion() {
        Quebrada q = pila.desapilar();
        if (q == null) {
            mensaje = AVISO + " No hay nada que deshacer.   (RF-82 · CA-06.2)";
            return false;
        }
        quebradas[contador] = q;
        contador++;
        return true;
    }

    // ------------------------------------------------------------------
    // ARCHIVO DE TEXTO (RNF-03)
    // ------------------------------------------------------------------

    // RF-129 · CA-01.2 — guarda las quebradas en el archivo: una por linea, con el formato  codigo;nombre
    static void guardarArchivo() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO));
            for (int i = 0; i < contador; i++) {
                pw.println(quebradas[i].codigo_quebrada + ";" + quebradas[i].nombre_quebrada);
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("No se pudo guardar el archivo " + ARCHIVO);
        }
    }

    // RF-129 · CA-01.2 — al iniciar, carga las quebradas del archivo; si no existe, empieza vacio
    static void cargarArchivo() {
        File f = new File(ARCHIVO);
        if (!f.exists()) {
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea = br.readLine();                  // lee la primera linea
            while (linea != null) {                        // null significa que ya no quedan lineas
                String[] partes = linea.split(";");        // corta la linea en el ";"
                if (partes.length == 2) {
                    int codigo = Integer.parseInt(partes[0]);
                    quebradas[contador] = new Quebrada(codigo, partes[1]);
                    contador++;
                    if (codigo > ultimoCodigo) {
                        ultimoCodigo = codigo;             // asi el siguiente codigo no se repite
                    }
                }
                linea = br.readLine();                     // lee la siguiente
            }
            br.close();
        } catch (Exception e) {
            System.out.println("No se pudo leer el archivo " + ARCHIVO);
        }
    }

    // ------------------------------------------------------------------
    // LECTURA DE LO QUE ESCRIBE EL USUARIO
    // ------------------------------------------------------------------

    static String leerTexto(String pregunta) {
        System.out.print(pregunta);
        return sc.nextLine().trim();       // trim quita los espacios de los extremos
    }

    // ------------------------------------------------------------------
    // PANTALLAS (iguales a las Figuras de la seccion 9 del documento)
    // ------------------------------------------------------------------

    static void mostrarMenuPrincipal() {
        String raya = "=".repeat(100);
        System.out.println();
        System.out.println(raya);
        System.out.println(" MONITOREO DE CALIDAD DEL AGUA · QUEBRADA LA IGUANÁ          (pantalla principal)");
        System.out.println(" Realiza: HU-01 a HU-06  |  CU-01 · CU-12  |  RNF-03");
        System.out.println(raya);
        System.out.println("MENSAJE : " + mensaje);
        System.out.println("-".repeat(100));
        System.out.println();
        System.out.println(" MENÚ                                                                   REQUISITOS");
        System.out.println("   1  Quebradas   " + FLECHA + " abre la pantalla de quebradas                     HU-01 a HU-06 · CU-01 · CU-12");
        System.out.println("   0  Salir       " + FLECHA + " graba en el archivo y cierra el programa           RNF-03 · RF-129");
        System.out.println();
    }

    // RF-08 · CA-05.1 — muestra las quebradas (o solo las de la busqueda) con su codigo y su numero de puntos
    // RF-81 · CA-05.3 — y avisa cuantas quebradas tienen menos de 3 puntos
    static void mostrarListado() {
        if (filtro.equals("")) {
            System.out.println(" LISTADO");
        } else {
            System.out.println(" LISTADO (coincidencias con \"" + filtro + "\")");
        }
        System.out.println("   codigo_quebrada    nombre_quebrada       puntos");
        int incompletas = 0;
        for (int i = 0; i < contador; i++) {
            if (filtro.equals("") || quebradas[i].nombre_quebrada.toLowerCase().contains(filtro.toLowerCase())) {
                int puntos = contarPuntos(quebradas[i].codigo_quebrada);
                System.out.printf("   %-19d%-22s%d%n", quebradas[i].codigo_quebrada, quebradas[i].nombre_quebrada, puntos);
                if (puntos < 3) {
                    incompletas++;
                }
            }
        }
        System.out.println("   Quebradas con menos de 3 puntos: " + incompletas + "    (RF-81 · CA-05.3)");
    }

    static void opcion(String numero, String nombre, String que, String requisitos) {
        System.out.printf("   %s  %-9s %s %-62s %s%n", numero, nombre, FLECHA, que, requisitos);
    }

    static void mostrarPantallaQuebradas() {
        String raya = "=".repeat(100);
        System.out.println();
        System.out.println(raya);
        System.out.println(" QUEBRADAS     Realiza: HU-01 a HU-06  |  CU-01 · CU-12  |  RN-01 · RNF-03");
        System.out.println(" Requisitos:   RF-01 a RF-09 · RF-72 a RF-83 · RF-129");
        System.out.println(raya);
        System.out.println("MENSAJE : " + mensaje);
        System.out.println("-".repeat(100));
        System.out.println();
        mostrarListado();
        System.out.println();
        System.out.println(" OPCIONES                                                                          REQUISITOS");
        opcion("1", "Guardar", "pide nombre_quebrada, le asigna su código y graba", "RF-01 a RF-04");
        opcion("2", "Buscar", "pide un texto y muestra las quebradas cuyo nombre lo contiene", "RF-05 · RF-72 a RF-74");
        opcion("3", "Listar", "muestra todas las quebradas con su número de puntos", "RF-08 · RF-80 a RF-81");
        opcion("4", "Corregir", "pide el nombre de la quebrada y el nombre nuevo", "RF-06 · RF-75 a RF-77");
        opcion("5", "Eliminar", "pide el nombre de la quebrada y la quita del listado", "RF-07 · RF-78 a RF-79");
        opcion("6", "Deshacer", "devuelve la última quebrada eliminada", "RF-09 · RF-82 a RF-83");
        opcion("0", "Volver", "regresa al menú principal; los datos se guardan en el archivo", "RF-129");
        System.out.println();
    }

    // Pantalla de quebradas: repite hasta que el usuario elige 0 (Volver)
    static void pantallaQuebradas() {
        String opcion = "";
        mensaje = "";
        filtro = "";
        while (!opcion.equals("0")) {
            mostrarPantallaQuebradas();
            opcion = leerTexto("Opción: ");
            mensaje = "";                       // el mensaje anterior ya se mostro; se limpia
            if (!opcion.equals("2")) {
                filtro = "";                    // cualquier opcion que no sea buscar vuelve a mostrar todas
            }

            if (opcion.equals("1")) {
                String nombre = leerTexto("nombre_quebrada: ");
                crearQuebrada(nombre);
            } else if (opcion.equals("2")) {
                String texto = leerTexto("Texto a buscar: ");
                if (texto.equals("")) {
                    mensaje = AVISO + " Escriba un nombre o parte de él.   (RF-74 · CA-02.4)";
                } else if (contarCoincidencias(texto) == 0) {
                    mensaje = AVISO + " No se encontró ninguna coincidencia.   (RF-73 · CA-02.3)";
                } else {
                    filtro = texto;
                }
            } else if (opcion.equals("3")) {
                if (contador == 0) {
                    mensaje = AVISO + " Aún no hay quebradas registradas.   (RF-80 · CA-05.2)";
                }
            } else if (opcion.equals("4")) {
                String nombre = leerTexto("nombre_quebrada a corregir: ");
                String nuevo = leerTexto("nombre nuevo: ");
                corregirQuebrada(nombre, nuevo);
            } else if (opcion.equals("5")) {
                String nombre = leerTexto("nombre_quebrada a eliminar: ");
                eliminarQuebrada(nombre);
            } else if (opcion.equals("6")) {
                deshacerEliminacion();
            } else if (!opcion.equals("0")) {
                mensaje = AVISO + " Opción no válida.";
            }
        }
        mensaje = "";
    }

    // ------------------------------------------------------------------
    // PROGRAMA PRINCIPAL
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        try {
            // Hace que las tildes y los simbolos salgan bien en tu consola (usa la codificacion de la consola)
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, codificacion("stdout.encoding", "sun.stdout.encoding")));
        } catch (Exception e) {
            // si no se puede, se usa la salida normal
        }
        cargarArchivo();                                                    // RNF-03
        mensaje = "Se cargaron " + contador + " quebradas desde el archivo.   (RNF-03 · RF-129 · CA-01.2)";
        String opcion = "";
        while (!opcion.equals("0")) {
            mostrarMenuPrincipal();
            opcion = leerTexto("Opción: ");
            mensaje = "";
            if (opcion.equals("1")) {
                pantallaQuebradas();
            } else if (!opcion.equals("0")) {
                mensaje = AVISO + " Opción no válida.";
            }
        }
        guardarArchivo();                                                   // RNF-03
        System.out.println("Datos guardados en " + ARCHIVO + ". Hasta pronto.");
    }
}
