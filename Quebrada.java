/* ===========================================================
 * Proyecto       : Monitoreo de calidad del agua - Quebrada La Iguana
 * Entidad        : Quebrada
 * Programado por : Willin Stewen Gutierrez Santos
 * Fecha          : 10/11/2026
 * -----------------------------------------------------------
 * HU-01  Registrar una quebrada con su nombre
 * RF-01  Registrar una quebrada con su nombre y asignarle su código
 * -----------------------------------------------------------
 * Criterios: CA-01.1 (bueno)
 * Estructuras: ninguna (esta clase es la entidad)
 * =========================================================== */
public class Quebrada {

    int codigo_quebrada;       // identificador propio: lo asigna el programa, nunca lo escribe la persona
    String nombre_quebrada;    // nombre de la quebrada: se puede corregir (RF-06 · CA-03.1)

    // RF-01 · CA-01.1 — arma una quebrada con su código y su nombre
    public Quebrada(int codigo_quebrada, String nombre_quebrada) {
        this.codigo_quebrada = codigo_quebrada;
        this.nombre_quebrada = nombre_quebrada;
    }
}
