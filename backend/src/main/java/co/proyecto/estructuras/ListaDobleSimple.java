package co.proyecto.estructuras;

import java.util.NoSuchElementException;

public class ListaDobleSimple<T> {
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamaño;

    private static class Nodo<T> {
        T elemento;
        Nodo<T> siguiente;
        Nodo<T> anterior;

        Nodo(T elemento) {
            this.elemento = elemento;
        }
    }

    public ListaDobleSimple() {
        cabeza = null;
        cola = null;
        tamaño = 0;
    }

    public boolean estaVacia() {
        return tamaño == 0;
    }

    public int obtenerTamaño() {
        return tamaño;
    }

    public void agregarAlInicio(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (estaVacia()) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            nuevoNodo.siguiente = cabeza;
            cabeza.anterior = nuevoNodo;
            cabeza = nuevoNodo;
        }
        tamaño++;
    }

    public void agregarAlFinal(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (estaVacia()) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            nuevoNodo.anterior = cola;
            cola.siguiente = nuevoNodo;
            cola = nuevoNodo;
        }
        tamaño++;
    }

    public T eliminarDelInicio() {
        if (estaVacia()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        T elemento = cabeza.elemento;
        if (cabeza == cola) { // Solo un elemento
            cabeza = null;
            cola = null;
        } else {
            cabeza = cabeza.siguiente;
            cabeza.anterior = null;
        }
        tamaño--;
        return elemento;
    }

    public T eliminarDelFinal() {
        if (estaVacia()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        T elemento = cola.elemento;
        if (cabeza == cola) { // Solo un elemento
            cabeza = null;
            cola = null;
        } else {
            cola = cola.anterior;
            cola.siguiente = null;
        }
        tamaño--;
        return elemento;
    }

    public void imprimirDesdeInicio() {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            System.out.print(actual.elemento + " ");
            actual = actual.siguiente;
        }
        System.out.println();
    }

    public void imprimirDesdeFin() {
        Nodo<T> actual = cola;
        while (actual != null) {
            System.out.print(actual.elemento + " ");
            actual = actual.anterior;
        }
        System.out.println();
    }

    // Otros métodos como buscar, eliminar por valor, etc. se pueden agregar según necesidad
}