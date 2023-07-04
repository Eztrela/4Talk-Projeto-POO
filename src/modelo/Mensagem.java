package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mensagem {
    private int id;
    private String texto;
    private Participante emitente;
    private Participante destinatario;
    private LocalDateTime datahora;

    public Mensagem(int id,String texto, Participante emitente, Participante destinatario) {
        this.id = id;
        this.texto = texto;
        this.emitente = emitente;
        this.destinatario = destinatario;

        this.datahora = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Participante getEmitente() {
        return emitente;
    }

    public Participante getDestinatario() {
        return destinatario;
    }

    public LocalDateTime getData() {
        return datahora;
    }

    @Override
    public String toString() {
        return "Mensagem{" +
                "id=" + id +
                ", texto='" + texto + '\'' +
                ", emitente=" + emitente.getNome() +
                ", destinatario=" + destinatario.getNome() +
                ", datahora=" + datahora +
                '}';
    }
}
