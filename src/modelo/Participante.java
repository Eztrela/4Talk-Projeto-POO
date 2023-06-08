package modelo;

import java.util.ArrayList;

public class Participante {
    private String nome;
    private ArrayList<Mensagem> recebidas;

    public Participante(String nome){
        this.nome = nome;
        recebidas = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<Mensagem> getRecebidas() {
        return recebidas;
    }

    @Override
    public String toString() {
        return "Participante{" +
                "nome='" + nome + '\'' +
                ", recebidas=" + recebidas +
                '}';
    }
}
