package co.proyecto.estructuras;

import java.util.ArrayList;

/*
Padre(i) = ⌊(i - 1) / 2⌋
HijoIzq(i) = 2 * i + 1
HijoDer(i) = 2 * i + 2
 */

public class ColaPrioridad<T extends Comparable<T>> {
    public ArrayList<T> minHeap;

    public ColaPrioridad (){
        minHeap = new ArrayList<>();
    }
    public void add(T valor){
        if (minHeap.isEmpty()) {
            minHeap.add(valor);
            return;
        } 

        minHeap.add(valor);
        normalizarUp(minHeap, minHeap.size()-1);
    }

    public T pool(){
        // para eliminar el valor de la raiz, se reemplaza por el ultimo y se normaliza hacia abajo
        T raiz = minHeap.get(0);
        minHeap.set(0, minHeap.get(minHeap.size()-1));
        minHeap.remove(minHeap.size()-1);

        normalizarDown(minHeap, 0);
        return raiz;
    }

    public T peek(){
        if (!minHeap.isEmpty()) {
            return minHeap.get(0);
        }
        return null;
    }

    private void normalizarUp(ArrayList<T> minHeap, int actual){
        if (actual <= 0) {
            return;
        }

        if (minHeap.get(actual).compareTo(minHeap.get((actual-1)/2)) < 0) {
            int hijo = actual;
            actual = (actual-1)/2;
            T aux = minHeap.get(actual);
            minHeap.set(actual, minHeap.get(hijo));
            minHeap.set(hijo, aux);
            normalizarUp(minHeap, actual);
        } else {
            return;
        }
    }

    private void normalizarDown(ArrayList<T> minHeap, int actual) {
        int left = (2 * actual) + 1;
        int right = (2 * actual) + 2;

        boolean tieneIzquierdo = left < minHeap.size();
        boolean tieneDerecho = right < minHeap.size();

        // Si no tiene hijos, detener
        if (!tieneIzquierdo && !tieneDerecho) return;

        // Determinar el hijo más pequeño que exista
        int menor;
        if (tieneDerecho && minHeap.get(right).compareTo(minHeap.get(left)) < 0) {
        menor = right;
        } else {
            menor = left;
        }

        // Si el hijo menor es más pequeño que el padre, intercambiar
        if (minHeap.get(menor).compareTo(minHeap.get(actual)) < 0) {
            T temp = minHeap.get(actual);
            minHeap.set(actual, minHeap.get(menor));
            minHeap.set(menor, temp);

            // Llamada recursiva para continuar bajando
            normalizarDown(minHeap, menor);
        }
    }

}
