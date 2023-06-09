package modelo;

import java.util.ArrayList;

public class Individual extends Participante {
    private String senha;
    private boolean administrador;
    private ArrayList<Grupo> grupos;

    public Individual(String nome,String senha, boolean administrador) {
        super(nome);
        this.senha = senha;
        this.administrador = administrador;
        this.grupos = new ArrayList<>();
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAdministrador() {
        return administrador;
    }

    public void setAdministrador(boolean administrador) {
        this.administrador = administrador;
    }

    public ArrayList<Grupo> getGrupos() {
        return grupos;
    }

    public void adicionar(Grupo grupo){
        this.grupos.add(grupo);
    }

    public Grupo localizarGrupo(String nome){
        for(Grupo grp: grupos){
            if(grp.getNome().equals(nome))
                return grp;
        }
        return null;
    }

}
