package co.proyecto.logic.estructuraGrafo;

public class Nodo <T extends Comparable> {
    private T valor;

    public Nodo(T valor){
        this.valor = valor;
    }

    public T getValor() {
        return valor;
    }

    public void setValor(T valor) {
        this.valor = valor;
    }
}
