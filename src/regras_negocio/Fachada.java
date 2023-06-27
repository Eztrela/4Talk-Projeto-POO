package regras_negocio;

import java.util.ArrayList;
import java.util.Collections;
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
        Grupo grupo = repositorio.localizarGrupo(nome);
        if (grupo != null)
            throw new Exception("criar grupo - grupo ja existe:" + nome);

        //criar o grupo
        grupo = new Grupo(nome);
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
        if(individual == null)
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
                    Mensagem mensagemGrupo = new Mensagem(idMensagem, texto, grp, individual);
                    individual.adicionarMensagemRecebidas(mensagemGrupo);
                    grp.adicionarMensagemEnviada(mensagemGrupo);
                    repositorio.adicionar(mensagemGrupo);
                }
            }
        }
        //  usando mesmo id e texto, e adicionar essas copias no repositorio
        repositorio.salvarObjetos();
    }

    public static ArrayList<Mensagem> obterConversa(String nomeindividuo, String nomedestinatario) throws Exception{
        //localizar emitente no repositorio
        //localizar destinatario no repositorio
        //obter do emitente a lista  enviadas
        //obter do emitente a lista  recebidas

        //criar a lista conversa
        //Adicionar na conversa as mensagens da lista enviadas cujo destinatario é igual ao parametro destinatario
        //Adicionar na conversa as mensagens da lista recebidas cujo emitente é igual ao parametro destinatario
        //ordenar a lista conversa pelo id das mensagens
        //retornar a lista conversa
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
    }

    public static ArrayList<Mensagem> espionarMensagens(String nomeadministrador, String termo) throws Exception{
        //localizar individuo no repositorio
        //verificar se individuo é administrador
        //listar as mensagens que contem o termo no texto
    }

    public static ArrayList<String> ausentes(String nomeadministrador) throws Exception{
        //localizar individuo no repositorio
        //verificar se individuo é administrador
        //listar os nomes dos participante que nao enviaram mensagens
    }

}