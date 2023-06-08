package modelo;

import java.util.ArrayList;

public class Grupo extends Participante{
    private ArrayList<Individual> individuos;

    public Grupo(){
        this.individuos = new ArrayList<>();
    }

    public ArrayList<Individual> getIndividuos() {
        return individuos;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "individuos=" + individuos +
                '}';
    }
}