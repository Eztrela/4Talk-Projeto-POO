package modelo;

import java.util.ArrayList;

public class Grupo extends Participante{
    private ArrayList<Individual> individuos;

    public Grupo(String nome){
        super(nome);
        this.individuos = new ArrayList<>();
    }

    public ArrayList<Individual> getIndividuos() {
        return individuos;
    }

    public void adicionar(Individual indvidual){
        this.individuos.add(indvidual);
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "individuos=" + individuos +
                '}';
    }
}
