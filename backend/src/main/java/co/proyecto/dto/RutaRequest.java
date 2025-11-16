package co.proyecto.dto;
// esta clase traduce el enviado de info usando la notacion de objetos, se usa en el controller

import co.proyecto.model.Ubicacion;

public class RutaRequest {
    Ubicacion origen;
    Ubicacion destino;
    public RutaRequest() {
    }
    public Ubicacion getOrigen() {
        return origen;
    }
    public void setOrigen(Ubicacion origen) {
        this.origen = origen;
    }
    public Ubicacion getDestino() {
        return destino;
    }
    public void setDestino(Ubicacion destino) {
        this.destino = destino;
    }
    
}
