/* ===========================================================
 * Proyecto       : Monitoreo de calidad del agua - Quebrada La Iguana
 * Entidad        : Quebrada
 * Programado por : Willin Stewen Gutierrez Santos
 * Fecha          : 10/11/2026
 * -----------------------------------------------------------
 * HU-06  Deshacer la última eliminación de una quebrada
 * RF-09  El sistema permite deshacer la última eliminación de una quebrada y la devuelve al listado.
 * RF-82  Cuando no hay eliminaciones de quebradas por deshacer, el sistema avisa que no hay nada que deshacer.
 * RF-83  Al deshacer, el sistema devuelve solo la última quebrada eliminada.
 * -----------------------------------------------------------
 * Criterios: CA-06.1 (bueno) CA-06.2 CA-06.3 (fallas)
 * Estructuras: Pila propia (arreglo + una variable que marca la cima)
 * =========================================================== */
public class PilaQuebradas {

    // "private": solo esta clase puede tocar el arreglo. Asi nadie puede leer ni
    // cambiar el elemento del medio: en una pila solo se trabaja con el de arriba.
    private Quebrada[] datos = new Quebrada[100];
    private int indiceCima = -1;   // posicion de la cima (el elemento de arriba); -1 significa pila vacia

    // RF-09 · CA-06.1 — pone una quebrada encima de la pila
    public boolean apilar(Quebrada q) {
        if (indiceCima == datos.length - 1) {
            return false;              // la pila esta llena
        }
        indiceCima++;
        datos[indiceCima] = q;
        return true;
    }

    // RF-09 · CA-06.1 · CA-06.2 · CA-06.3 — saca y devuelve la de arriba; null si no hay ninguna
    public Quebrada desapilar() {
        if (estaVacia()) {
            return null;               // no hay nada que sacar: no es un error
        }
        Quebrada q = datos[indiceCima];
        datos[indiceCima] = null;
        indiceCima--;
        return q;
    }

    // RF-09 — cima: mira la de arriba sin sacarla; null si la pila esta vacia
    public Quebrada cima() {
        if (estaVacia()) {
            return null;
        }
        return datos[indiceCima];
    }

    // RF-09 · CA-06.2 — dice si la pila esta vacia
    public boolean estaVacia() {
        return indiceCima == -1;
    }

    // RF-09 — cuantas quebradas hay guardadas en la pila
    public int tamano() {
        return indiceCima + 1;
    }
}
