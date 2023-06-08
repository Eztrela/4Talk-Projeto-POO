package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mensagem {
    private int id;
    private String texto;
    private Individual emitente;
    private Participante destinatario;
    private LocalDateTime datahora;

    public Mensagem(String texto, Individual emitente, Participante destinatario) {
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

    public Individual getEmitente() {
        return emitente;
    }

    public Participante getDestinatario() {
        return destinatario;
    }

    public LocalDateTime getDatahora() {
        return datahora;
    }
}
