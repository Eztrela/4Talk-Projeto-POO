package regras_negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Predicate;

import modelo.Grupo;
import modelo.Individual;
import modelo.Mensagem;
import modelo.Participante;
import repositorio.Repositorio;

public class Fachada {
    private Fachada() {}

    private static Repositorio repositorio = new Repositorio();


    public static ArrayList<Individual> listarIndividuos() {
        return repositorio.getIndividuos();
    }
    public static ArrayList<Grupo> listarGrupos() {
        return repositorio.getGrupos();
    }
    public static ArrayList<Mensagem> listarMensagens() {
        return repositorio.getMensagens();
    }

    public static ArrayList<Mensagem> listarMensagensEnviadas(String nome) throws Exception{
        Individual ind = repositorio.localizarIndividual(nome);
        if(ind == null)
            throw new Exception("listar  mensagens enviadas - nome nao existe: " + nome);

        return ind.getEnviadas();
    }

    public static ArrayList<Mensagem> listarMensagensRecebidas(String nome) throws Exception{
        Participante participante = repositorio.localizarParticipante(nome);
        if(participante == null){
            throw new Exception("listar mensagens recebidas - nome nao existe: " + nome);
        }
        return participante.getRecebidas();
    }

    public static void criarIndividuo(String nome, String senha) throws  Exception{
        if(nome.isEmpty())
            throw new Exception("criar individual - nome vazio:");
        if(senha.isEmpty())
            throw new Exception("criar individual - senha vazia:");

        Participante participante = repositorio.localizarParticipante(nome);
        if(participante != null)
            throw new Exception("criar individual - nome ja existe: " + nome);


        Individual individuo = new Individual(nome,senha, false);
        repositorio.adicionar(individuo);
        repositorio.salvarObjetos();
    }

    public static void criarAdministrador(String nome, String senha) throws  Exception{
        if(nome.isEmpty())
            throw new Exception("criar individual - nome vazio:");
        if(senha.isEmpty())
            throw new Exception("criar individual - senha vazia:");


        Individual administrador = repositorio.localizarIndividual(nome);
        if(administrador != null)
            throw new Exception("criar administrador - nome ja existe: "+ nome);

        administrador = new Individual(nome, senha, true);
        repositorio.adicionar(administrador);
        repositorio.salvarObjetos();
    }


    public static void criarGrupo(String nome) throws  Exception{

        if(nome.isEmpty())
            throw new Exception("criar individual - nome vazio:");

        //localizar nome no repositorio
        Participante participante = repositorio.localizarParticipante(nome);
        if (participante != null && participante instanceof Individual)
            throw new Exception("criar grupo - Nome do grupo já utilizado por um individuo:" + nome);
        else if(participante != null && participante instanceof Grupo)
            throw new Exception("criar grupo - Grupo ja existe:" + nome);

        //criar o grupo
        Grupo grupo = new Grupo(nome);
        repositorio.adicionar(grupo);
        repositorio.salvarObjetos();
    }

    public static void inserirGrupo(String nomeindividuo, String nomegrupo) throws  Exception{
        if(nomeindividuo.isEmpty())
            throw new Exception("inserir grupo - nomeindividuo vazio:");
        if(nomegrupo.isEmpty())
            throw new Exception("inserir grupo - nomegrupo vazia:");

        //localizar nomeindividuo no repositorio
        Individual individual = repositorio.localizarIndividual(nomeindividuo);
        if(individual == null)
            throw new Exception("inserir grupo - nomeindividuo nao existe: " + nomeindividuo);

        //localizar nomegrupo no repositorio
        Grupo grupo = repositorio.localizarGrupo(nomegrupo);
        if(grupo == null)
            throw new Exception("inserir grupo - nomeingrupo nao existe: " + nomegrupo);

        //verificar se individuo nao esta no grupo
        if (grupo.getIndividuos().contains(individual))
            throw  new Exception("inserir grupo - nomeindividuo já participa do grupo");

        //adicionar individuo com o grupo e vice-versa
        grupo.adicionar(individual);
        repositorio.salvarObjetos();
    }

    public static void removerGrupo(String nomeindividuo, String nomegrupo) throws  Exception{
        if(nomeindividuo.isEmpty())
            throw new Exception("inserir grupo - nomeindividuo vazio:");

        if(nomegrupo.isEmpty())
            throw new Exception("inserir grupo - nomegrupo vazia:");

        //localizar nomeindividuo no repositorio
        Individual individual = repositorio.localizarIndividual(nomeindividuo);
        if(individual == null)
            throw new Exception("inserir grupo - nomeindividuo nao existe: " + nomeindividuo);

        //localizar nomegrupo no repositorio
        Grupo grupo = repositorio.localizarGrupo(nomegrupo);
        if(individual == null)
            throw new Exception("inserir grupo - nomeingrupo nao existe: " + nomegrupo);

        //verificar se individuo ja esta no grupo
        if (!grupo.getIndividuos().contains(individual))
            throw  new Exception("inserir grupo - nomeindividuo nao participa do grupo");

        //remover individuo com o grupo e vice-versa
        grupo.remover(individual);
        repositorio.salvarObjetos();
    }


    public static void criarMensagem(String nomeEmitente, String nomeDestinatario, String texto) throws Exception{
        if(texto.isEmpty())
            throw new Exception("criar mensagem - texto vazio:");

        Individual emitente = repositorio.localizarIndividual(nomeEmitente);
        if(emitente == null)
            throw new Exception("criar mensagem - emitente nao existe:" + nomeEmitente);

        Participante destinatario = repositorio.localizarParticipante(nomeDestinatario);
        if(destinatario == null)
            throw new Exception("criar mensagem - destinatario nao existe:" + nomeEmitente);
        if(destinatario instanceof Grupo g && emitente.localizarGrupo(g.getNome())==null)
            throw new Exception("criar mensagem - grupo nao permitido:" + nomeDestinatario);


        //cont.
        //gerar id no repositorio para a mensagem
        int idMensagem = repositorio.geraIdMensagem();

        Mensagem mensagem = repositorio.criarMensagem(idMensagem,emitente,destinatario,texto);
        //adicionar mensagem ao emitente e destinatario
        emitente.adicionarMensagemEnviada(mensagem);
        destinatario.adicionarMensagemRecebidas(mensagem);
        //adicionar mensagem ao repositorio
        repositorio.adicionar(mensagem);
        //
        //caso destinatario seja tipo Grupo então criar copias da mensagem, tendo o grupo como emitente e cada membro do grupo como destinatario,
        if (destinatario instanceof Grupo grp){
            for (Individual individual: grp.getIndividuos()){
                if(!individual.equals(emitente)) {
                    Mensagem mensagemGrupo = repositorio.criarMensagem(idMensagem, grp, individual, texto);
                    individual.adicionarMensagemRecebidas(mensagemGrupo);
                    grp.adicionarMensagemEnviada(mensagemGrupo);
                    repositorio.adicionar(mensagemGrupo);
                }
            }
        }
        //  usando mesmo id e texto, e adicionar essas copias no repositorio
        repositorio.salvarObjetos();
    }

    public static ArrayList<Mensagem> obterConversa(String nomeIndividuo, String nomeDestinatario) throws Exception{
        //localizar emitente no repositorio
        Individual individual = repositorio.localizarIndividual(nomeIndividuo);
        if(individual == null)
            throw new Exception("criar mensagem - emitente nao existe:" + nomeIndividuo);

        //localizar destinatario no repositorio
        Participante destinatario = repositorio.localizarParticipante(nomeDestinatario);
        if(destinatario == null)
            throw new Exception("criar mensagem - destinatario nao existe:" + nomeIndividuo);

        //obter do emitente a lista  enviadas
        ArrayList<Mensagem> enviadasIndividuo = individual.getEnviadas();
        //obter do emitente a lista  recebidas
        ArrayList<Mensagem> recebidasIndividuo = individual.getRecebidas();

        //criar a lista conversa
        ArrayList<Mensagem> conversa = new ArrayList<>();
        //Adicionar na conversa as mensagens da lista enviadas cujo destinatario é igual ao parametro destinatario
        for (Mensagem mensagem: enviadasIndividuo){
            if(mensagem.getDestinatario().equals(destinatario))
                conversa.add(mensagem);
        }
        //Adicionar na conversa as mensagens da lista recebidas cujo emitente é igual ao parametro destinatario
        for (Mensagem mensagem: recebidasIndividuo){
            if(mensagem.getEmitente().equals(destinatario))
                conversa.add(mensagem);
        }
        //ordenar a lista conversa pelo id das mensagens
        Collections.sort(conversa, new Comparator<Mensagem>() {
            @Override
            public int compare(Mensagem m1, Mensagem m2) {
                return m1.getData().compareTo(m2.getData());
            }
        });
        //retornar a lista conversa
        return conversa;

    }

    public static void apagarMensagem(String nomeindividuo, int id) throws  Exception{
        Individual emitente = repositorio.localizarIndividual(nomeindividuo);
        if(emitente == null)
            throw new Exception("apagar mensagem - nome nao existe:" + nomeindividuo);

        Mensagem m = emitente.localizarEnviada(id);
        if(m == null)
            throw new Exception("apagar mensagem - mensagem nao pertence a este individuo:" + id);

        emitente.removerEnviada(m);
        Participante destinatario = m.getDestinatario();
        destinatario.removerRecebida(m);
        repositorio.remover(m);

        if(destinatario instanceof Grupo g) {
            ArrayList<Mensagem> lista = destinatario.getEnviadas();
            lista.removeIf(new Predicate<Mensagem>() {
                @Override
                public boolean test(Mensagem t) {
                    if(t.getId() == m.getId()) {
                        t.getDestinatario().removerRecebida(t);
                        repositorio.remover(t);
                        return true;        //apaga mensagem da lista
                    }
                    else
                        return false;
                }

            });
        }
        repositorio.salvarObjetos();
    }

    public static ArrayList<Mensagem> espionarMensagens(String nomeadministrador, String termo) throws Exception{
        //localizar individuo no repositorio
        Individual administrador = repositorio.localizarIndividual(nomeadministrador);
        if(administrador == null)
            throw new Exception("apagar mensagem - nome nao existe:" + nomeadministrador);
        //verificar se individuo é administrador
        ArrayList<Mensagem> mensagensComTermo = new ArrayList<>();
        if(administrador.isAdministrador()){
            ArrayList<Mensagem> todasMensagens = repositorio.getMensagens();
        //listar as mensagens que contem o termo no texto
            if(termo.equals("")){
                return todasMensagens;
            }
            for(Mensagem mensagem: todasMensagens){
                if(mensagem.getTexto().contains(termo))
                    mensagensComTermo.add(mensagem);
            }
        }
        return mensagensComTermo;
    }

    public static ArrayList<String> ausentes(String nomeadministrador) throws Exception{
        //localizar individuo no repositorio
        Individual administrador = repositorio.localizarIndividual(nomeadministrador);
        if(administrador == null)
            throw new Exception("ausentes - nome nao existe:" + nomeadministrador);
        //verificar se individuo é administrador
        ArrayList<String> ausentes = new ArrayList<>();
        if(!administrador.isAdministrador())
            throw new Exception("O indiviuo "+ nomeadministrador + "nao e administrador");
        //listar os nomes dos participante que nao enviaram mensagens
        ArrayList<Participante> participantes = repositorio.getParticipantes();
        for(Participante participante: participantes){
            if (participante.getEnviadas().isEmpty())
                ausentes.add(participante.getNome());
        }
        return ausentes;
    }

    public static Individual validarIndividuo(String nomeindividuo, String senha)throws Exception{
        Individual admin = repositorio.localizarIndividual(nomeindividuo);
        if(admin == null)
            throw new Exception("validarIndividuo - nenhum usuario com esse nome cadastrado:" + nomeindividuo);
        else if(!admin.getSenha().equals(senha))
            throw new Exception("validarIndividuo - senha digitada esta incorreta");
        if (admin.getSenha().equals(senha))
            return admin;
        return null;

    }

}