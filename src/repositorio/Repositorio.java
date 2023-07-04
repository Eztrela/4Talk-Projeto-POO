package repositorio;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.time.LocalDateTime;

import modelo.Individual;
import modelo.Grupo;
import modelo.Participante;
import modelo.Mensagem;

import javax.sound.midi.MidiMessage;


public class Repositorio {
    private TreeMap<String, Participante> participantes = new TreeMap<>();
    private ArrayList<Mensagem> mensagens = new ArrayList<>();

    public Repositorio() {
        carregarObjetos();
    }
    public int geraIdMensagem(){
        if (mensagens.size() == 0)
            return 1;
        return this.mensagens.get(mensagens.size()-1).getId() + 1;//TEM que verificar se isso aqui funciona
    }
    public void adicionar(Participante p) {
        participantes.put(p.getNome(), p);
    }

    public void remover(Participante p) {
        participantes.remove(p.getNome());
    }

    public Participante localizarParticipante(String nome) {
        return participantes.get(nome);
    }

    public Individual localizarIndividual(String nome){
            if (participantes.get(nome) instanceof Individual ind)
                return ind;
        return null;
    }

    public Grupo localizarGrupo(String nome){
        if(participantes.get(nome) instanceof Grupo grp)
            return grp;
        return null;
    }

    public Mensagem criarMensagem(int idMensagem, Participante emitente, Participante destinatario, String texto){
        Mensagem mensagem = new Mensagem(idMensagem,emitente,destinatario,texto);
        return mensagem;
    }

    public void adicionar(Mensagem mensagem) {
        mensagens.add(mensagem);
    }

    public void remover(Mensagem m) {
        mensagens.remove(m);
    }

    public ArrayList<Participante> getParticipantes(){
        ArrayList<Participante> prtes = new ArrayList<>();
        for(Participante part: participantes.values()){
            if(part instanceof Individual)
                prtes.add((Individual) part);
        }
        return prtes;
    }

    public ArrayList<Individual> getIndividuos(){
        ArrayList<Individual> inds = new ArrayList<>();
        for(Participante part: participantes.values()){
            if(part instanceof Individual)
                inds.add((Individual) part);
        }
        return inds;
    }

    public ArrayList<Grupo> getGrupos(){
        ArrayList<Grupo> grupos = new ArrayList<>();
        for(Participante grp: participantes.values()){
            if(grp instanceof Grupo)
                grupos.add((Grupo) grp);
        }
        return grupos;
    }

    public ArrayList<Mensagem> getMensagens(){
        return this.mensagens;
    }



    public void carregarObjetos()  	{
        // carregar para o repositorio os objetos dos arquivos csv
        try {
            //caso os arquivos nao existam, serao criados vazios
            File f1 = new File( new File(".\\mensagens.csv").getCanonicalPath() ) ;
            File f2 = new File( new File(".\\individuos.csv").getCanonicalPath() ) ;
            File f3 = new File( new File(".\\grupos.csv").getCanonicalPath() ) ;
            if (!f1.exists() || !f2.exists() || !f3.exists() ) {
                //System.out.println("criando arquivo .csv vazio");
                FileWriter arquivo1 = new FileWriter(f1); arquivo1.close();
                FileWriter arquivo2 = new FileWriter(f2); arquivo2.close();
                FileWriter arquivo3 = new FileWriter(f3); arquivo3.close();
                return;
            }
        }
        catch(Exception ex)		{
            throw new RuntimeException("criacao dos arquivos vazios:"+ex.getMessage());
        }

        String linha;
        String[] partes;

        try	{
            String nome,senha,administrador;
            File f = new File( new File(".\\individuos.csv").getCanonicalPath())  ;
            Scanner arquivo1 = new Scanner(f);	 //  pasta do projeto
            while(arquivo1.hasNextLine()) 	{
                linha = arquivo1.nextLine().trim();
                partes = linha.split(";");
                //System.out.println(Arrays.toString(partes));
                nome = partes[0];
                senha = partes[1];
                administrador = partes[2];
                Individual ind = new Individual(nome,senha,Boolean.parseBoolean(administrador));
                this.adicionar(ind);
            }
            arquivo1.close();
        }
        catch(Exception ex)		{
            throw new RuntimeException("leitura arquivo de individuos:"+ex.getMessage());
        }

        try	{
            String nome;
            Grupo grupo;
            Individual individuo;
            File f = new File( new File(".\\grupos.csv").getCanonicalPath())  ;
            Scanner arquivo2 = new Scanner(f);	 //  pasta do projeto
            while(arquivo2.hasNextLine()) 	{
                linha = arquivo2.nextLine().trim();
                partes = linha.split(";");
                //System.out.println(Arrays.toString(partes));
                nome = partes[0];
                grupo = new Grupo(nome);
                if(partes.length>1)
                    for(int i=1; i< partes.length; i++) {
                        individuo = this.localizarIndividual(partes[i]);
                        grupo.adicionar(individuo);
                        individuo.adicionar(grupo);
                    }
                this.adicionar(grupo);
            }
            arquivo2.close();
        }
        catch(Exception ex)		{
            throw new RuntimeException("leitura arquivo de grupos:"+ex.getMessage());
        }


        try	{
            String nomeemitente, nomedestinatario,texto;
            int id;
            LocalDateTime datahora;
            Mensagem m;
            Participante emitente,destinatario;
            File f = new File( new File(".\\mensagens.csv").getCanonicalPath() )  ;
            Scanner arquivo3 = new Scanner(f);	 //  pasta do projeto
            while(arquivo3.hasNextLine()) 	{
                linha = arquivo3.nextLine().trim();
                partes = linha.split(";");
                //System.out.println(Arrays.toString(partes));
                id = Integer.parseInt(partes[0]);
                texto = partes[1];
                nomeemitente = partes[2];
                nomedestinatario = partes[3];
                datahora = LocalDateTime.parse(partes[4], DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));
                emitente = this.localizarParticipante(nomeemitente);
                destinatario = this.localizarParticipante(nomedestinatario);
                m = new Mensagem(id,emitente,destinatario,texto, datahora);
                this.adicionar(m);
                emitente.adicionarMensagemEnviada(m);
                destinatario.adicionarMensagemRecebidas(m);
            }
            arquivo3.close();
        }
        catch(Exception ex)		{
            throw new RuntimeException("leitura arquivo de mensagens:"+ex.getMessage());
        }
    }


    public void	salvarObjetos()  {
        //gravar nos arquivos csv os objetos que estão no repositório
        try	{
            File f = new File( new File(".\\mensagens.csv").getCanonicalPath())  ;
            FileWriter arquivo1 = new FileWriter(f);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
            for(Mensagem m : mensagens) 	{
                arquivo1.write(	m.getId()+";"+
                        m.getTexto()+";"+
                        m.getEmitente().getNome()+";"+
                        m.getDestinatario().getNome()+";"+
                        m.getData().format(dateTimeFormatter)+"\n");
            }
            arquivo1.close();
        }
        catch(Exception e){
            throw new RuntimeException("problema na criação do arquivo  mensagens "+e.getMessage());
        }

        try	{
            File f = new File( new File(".\\individuos.csv").getCanonicalPath())  ;
            FileWriter arquivo2 = new FileWriter(f) ;
            for(Individual ind : this.getIndividuos()) {
                arquivo2.write(ind.getNome() +";"+ ind.getSenha() +";"+ ind.isAdministrador() +"\n");
            }
            arquivo2.close();
        }
        catch (Exception e) {
            throw new RuntimeException("problema na criação do arquivo  individuos "+e.getMessage());
        }

        try	{
            File f = new File( new File(".\\grupos.csv").getCanonicalPath())  ;
            FileWriter arquivo3 = new FileWriter(f) ;
            for(Grupo g : this.getGrupos()) {
                String texto="";
                for(Individual ind : g.getIndividuos())
                    texto += ";" + ind.getNome();
                arquivo3.write(g.getNome() + texto + "\n");
            }
            arquivo3.close();
        }
        catch (Exception e) {
            throw new RuntimeException("problema na criação do arquivo  grupos "+e.getMessage());
        }
    }
}