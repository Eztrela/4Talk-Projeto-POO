package modelo;

import java.util.ArrayList;

public class Participante {
    private String nome;
    private ArrayList<Mensagem> recebidas;

    private ArrayList<Mensagem> enviadas;

    public Participante(String nome){
        this.nome = nome;
        recebidas = new ArrayList<>();
        this.enviadas = new ArrayList<>();
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

    public ArrayList<Mensagem> getEnviadas() {
        return enviadas;
    }

    public void adicionarMensagemEnviada(Mensagem msg){
        this.enviadas.add(msg);
    }
    public void removerMensagemEnviada(Mensagem msg){
        this.enviadas.remove(msg);
    }

    public void adicionarMensagemRecebidas(Mensagem msg){
        this.recebidas.add(msg);
    }

    public void removerMensagemRecebida(Mensagem msg){
        this.recebidas.remove(msg);
    }

    @Override
    public String toString() {
        return "Participante{" +
                "nome='" + nome + '\'' +
                ", recebidas=" + recebidas +
                '}';
    }
}
