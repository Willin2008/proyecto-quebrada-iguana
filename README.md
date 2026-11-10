# Prototipo – entidad Quebrada (HU-01 a HU-06)

Proyecto: calidad del agua de la quebrada La Iguaná · Ingeniería de Software I
Autor de esta parte: Willin Stewen Gutiérrez Santos

## Archivos
- `Quebrada.java` – la entidad: `codigo_quebrada` (lo asigna el programa) y `nombre_quebrada`.
- `PilaQuebradas.java` – pila propia para deshacer (solo se toca la de arriba).
- `Main.java` – menú, arreglo, operaciones (guardar, buscar, listar, corregir, eliminar, deshacer) y archivo.
- `puntos.txt` – **simula la tabla de puntos** (no se programa ese módulo). Formato: `codigo_punto;codigo_quebrada;nombre_punto;ubicacion`. El programa cuenta de ahí cuántos puntos tiene cada quebrada; ese número **no se guarda** en la quebrada.
- `quebradas.txt` – lo crea el programa al salir (`codigo;nombre`).

## Cómo correrlo
```
javac -encoding UTF-8 *.java
java -Dstdin.encoding=UTF-8 Main
```
En Windows (cmd) escribe antes `chcp 65001`. En la terminal de VS Code no hace falta.

## Para sacar las 6 capturas (empezar SIN quebradas.txt, para que los códigos arranquen en 1)
1. **Menú principal**: apenas arranca el programa.
2. **Quebradas guardadas**: opción 1 → Guardar `La Iguaná`, `La Presidenta`, `Santa Elena` → se ve el listado con código y puntos (3, 2, 3) y «Quebradas con menos de 3 puntos: 1».
3. **Falla al guardar**: Guardar `La Iguaná` otra vez → RF-02 · CA-01.3.
4. **Corregir**: opción 4 → `La Presidenta` → `La Presidenta Alta` (el código 2 no cambia).
5. **Eliminar y deshacer**: opción 5 → `La Presidenta Alta` (el listado se corre) → captura; opción 6 → vuelve y queda de última (CA-06.3).
6. **Cerrar y volver a abrir**: opción 0 y 0 → correr de nuevo → «Se cargaron 3 quebradas desde el archivo» (RF-129 · CA-01.2).

Otras pruebas por si la profesora las pide: nombre vacío (RF-03), buscar sin coincidencias (RF-73), deshacer con la pila vacía (RF-82), eliminar una que no existe (RF-78), corregir a un nombre que ya existe (RF-75).
