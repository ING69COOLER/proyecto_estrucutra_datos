package co.proyecto.estructuras;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import co.proyecto.model.Ubicacion;
import co.proyecto.repository.UbicacionRepository;
import jakarta.annotation.PostConstruct;

/*
Padre(i) = ⌊(i - 1) / 2⌋
HijoIzq(i) = 2 * i + 1
HijoDer(i) = 2 * i + 2
 */
@Component
public class ColaPrioridad {
    public ArrayList<Ubicacion> minHeap;

    UbicacionRepository ubicacionRepository;

    public ColaPrioridad (UbicacionRepository ubicaciones){
        minHeap = new ArrayList<>();
        this.ubicacionRepository = ubicaciones;
    }

    @PostConstruct
    public void init(){
        cargarTodo();
    }

    public void cargarTodo(){
        List<Ubicacion> ubicaciones = ubicacionRepository.findAllWithRecursos();
        for (Ubicacion ubicacion : ubicaciones) {
            add(ubicacion);
        }
    }

    public void add(Ubicacion valor){
        if (minHeap.isEmpty()) {
            minHeap.add(valor);
            return;
        } 

        minHeap.add(valor);
        normalizarUp(minHeap, minHeap.size()-1);
    }

    public Ubicacion pool(){
        
        if (minHeap.isEmpty()) {
            return null; 
        }

        if (minHeap.size() == 1) {
            Ubicacion ubicacion = minHeap.get(0);
            minHeap.remove(0);
            return ubicacion;
        }

        Ubicacion raiz = minHeap.get(0);
        minHeap.set(0, minHeap.get(minHeap.size()-1));
        minHeap.remove(minHeap.size()-1);

        normalizarDown(minHeap, 0);
        return raiz;
    }

    public Ubicacion peek(){
        if (!minHeap.isEmpty()) {
            return minHeap.get(0);
        }
        return null;
    }

    private void normalizarUp(ArrayList<Ubicacion> minHeap, int actual){
        if (actual <= 0) {
            return;
        }

        if (minHeap.get(actual).compareTo(minHeap.get((actual-1)/2)) < 0) {
            int hijo = actual;
            actual = (actual-1)/2;
            Ubicacion aux = minHeap.get(actual);
            minHeap.set(actual, minHeap.get(hijo));
            minHeap.set(hijo, aux);
            normalizarUp(minHeap, actual);
        } else {
            return;
        }
    }

    private void normalizarDown(ArrayList<Ubicacion> minHeap, int actual) {
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
            Ubicacion temp = minHeap.get(actual);
            minHeap.set(actual, minHeap.get(menor));
            minHeap.set(menor, temp);

            // Llamada recursiva para continuar bajando
            normalizarDown(minHeap, menor);
        }
    }

}
