package Model;
impot java.util.Map;
public class Jogador {
    private String id;
    private int saldo;
    private String nome;
    private String email;
    private String senha;
    private Map<String, NFT> nfts = new TreeMap<>();

    public Jogador(String nome, int saldo, String email, String senha, Map nfts){
        this.id = id;
        this.nome = nome;
        this.saldo = saldo;
        this.email = email;
        this.senha = senha;
        this.nfts = nfts;
    }

    public void verSeusNFTS(){
        String lista = "Token: ";
        for (NFT nft : nfts.values()) {
            lista += nft.getToken() + ", Nome: ";
            lista += nft.getNome() + "\nDescrição: ";
            lista += nft.getDescricao() + "\n Status de venda: ";
            int status = nft.getStatus();
            if (status == 1) {
                lista += "À venda por: ";
                lista += Integer.toString(nft.getValor());
            }
            else if (status == 2) {
		lista += "Em leilão. Aposta mais alta: ";
                lista += Integer.toString(nft.getOfertaMaisAlta());
            }
            else {
		lista += "Não está à venda.";
            }
        }
        return lista;
    }

    public String verListaNFT(){ //usado durante os casos de uso comprarNFT e fazerOferta
        return MarketPlace.listaItensAVenda();
    }

    public void anunciarNFT(NFT nft, int valor){
        MarketPlace.iniciarVenda(nft, valor, this);
    }

    public void cancelarVendaNFT(NFT nft){
        MarketPlace.cancelarVenda(nft, this);
    }

    public void comprarNFT(NFT nft){
        //ofertando pelo valor total, no marketplace checa o tipo de status e vem pra ca se for de compra
        Oferta compra = new Oferta(this, nft.getValor());
        MarketPlace.fazerOferta(nft, compra);
    }

    public void leiloarNFT(NFT nft, int ofertaMinima, int ofertaMaxima){
        Leilao leilao = new Leilao(ofertaMinima, ofertaMaxima);
        MarketPlace.iniciarLeilao(nft, leilao, this);
    }

    public void fazerOferta(NFT nft){
        Oferta compra = new Oferta(this, oferta);
        MarketPlace.fazerOferta(nft, compra);
    }

    public void enviarFeedbackAplicativo(String feedback){
        Feedback.adicionarFeedback(feedback);
    }

    public int getSaldo() {
        return saldo;
    }
    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }
    public String getNome() {
        return nome;
    }
}

package Model;
public static class Feedback {
    private static List<String> feedbacks = new ArrayList<>();

    public void adicionarFeedback(String feedback)) {
        feedbacks.add(feedback);
    }
    public String verFeedback() {
        return String.join("\n", feedbacks);
    }
}

package Model;
public class Leilao {
    private Oferta ofertaMaisAlta;
    private int ofertaMinima;
    private int ofertaMaxima;

    public Leilao(int ofertaMinima, int ofertaMaxima){
        this.ofertaMinima = ofertaMinima;
        this.ofertaMaxima = ofertaMaxima;
    }

    public int getOfertaMaisAlta(){
        return ofertaMaisAlta.getValor();
    }
    public void setOfertaMaisAlta(Oferta oferta){
        ofertaMaisAlta = oferta;
    }
    public int getOfertaMinima(){
        return ofertaMinima;
    }
    public int getOfertaMaxima(){
        return ofertaMaxima;
    }
}

pacacke Model;
public class Dados {
    Jogador jogador;
    NFT nft;
    int valor;

    public Dados(Jogador jogador, NFT nft, int valor) {
        this.jogador = jogador;
        this.nft = nft;
        this.valor = valor;
    }
}

package Control;
impot java.util.Map;
public static class MarketPlace {
    private static Map<String, NFT> NFTSaVenda = new TreeMap<>();
    private static List<Dados> registrosDeCompra = new ArrayList<>(); //que jogador vai ter acesso a isso?

    public static String[] listaItensAVenda() {
        String lista = "Token: ";
        for (NFT nft : NFTSaVenda.values()) {
            lista += nft.getToken() + ", Nome: ";
            lista += nft.getNome() + "\nDescrição: ";
            lista += nft.getDescricao() + "\n Status de venda: ";
            int status = nft.getStatus();
            if (status == 1) {
                lista += "À venda por: ";
                lista += Integer.toString(nft.getValor());
            }
            else { //não haverão NFTs com status 0 na lista, serão removidas no mesmo instante em que forem compradas
		lista += "Em leilão. Aposta mais alta: ";
                lista += Integer.toString(nft.getOfertaMaisAlta());
            }
            lista += "\nDono: " + nft.getDono().getNome();
        }
        return lista;
    }

    public static void fazerOferta(NFT nft, Oferta oferta) { //usa-se oferta para agrupar tanto o comprador quanto o valor da oferta
        Comprador comprador = oferta.getComprador();
        if (comprador == nft.getDono()) {
            System.out.println("Não é possível comprar suas próprias NFTS.");
            return;
        }
        if (nft.getStatus() == 1) { //compra
            int preco = nft.getValor();
            if (comprador.getSaldo() < preco) {
                System.out.println("Saldo insuficiente para realizar essa transação.");
                return;
            }
            trasferenciaDeNFT(nft, comprador, preco);
        }
        else { //leilao
            atualizarOfertaLeilao(oferta, nft.getLeilao());
        }
    }

    public static void iniciarVenda(NFT nft, int valor, Jogador jogador) {
        if (nft.getDono() != jogador) {
            System.out.println("Essa NFT não pertence à você.");
        }
        else {
            nft.setStatus(1);
            nft.setValor(valor);
            NFTSaVenda.put(nft.getToken(), nft);
        }
    }

    public static void trasferenciaDeNFT(NFT nft, Jogador comprador, int preco) {
            comprador.setSaldo(comprador.getSaldo() - preco);
            Dados compra = new Dados(comprador, nft, preco);
            registrosDeCompra.add(compra);
            nft.getDono().remove(nft.getToken());
            nft.setStatus(0);
            nft.valor = 0;
            nft.getLeilao() = null;
            comprador.nfts.put(nft.getToken(), nft);
            System.out.println("Compra realizada com sucesso.");
            System.out.println("Saldo atual: " + Integer.toString(comprador.getSaldo()));
    }

    public static void iniciarLeilao(NFT nft, Leilao leilao, Jogador jogador) {
        if (nft.getDono() != jogador) {
            System.out.println("Essa NFT não pertence à você.");
        }
        else {
            nft.setStatus(2);
            nft.setLeilao(leilao);
            NFTSaVenda.put(nft.getToken(), nft);
        }
    }

    public static void cancelarVenda(NFT nft, Jogador jogador) {
        if (nft.getDono() != jogador) {
            System.out.println("Essa NFT não pertence à você.");
        }
        else {
            NFTSaVenda.remove(nft.getToken());
            nft.setStatus(0);
            nft.setValor(0);
        }
    }

    public static void atualizarOfertaLeilao(Oferta oferta, Leilao leilao){
        int valor = oferta.getValor();
        if (valor > leilao.getOfertaMaisAlta() && valor > leilao.getOfertaMinima() && valor <= leilao.getOfertaMaxima()){
            if (valor == leilao.getOfertaMaxima()) {
                trasferenciaDeNFT(oferta.getComprador());
            }
            else {
                leilao.setOfertaMaisAlta(oferta);
            }
        }
        else {
            System.out.println("Oferta inválida.");
        }
    }
}

package View;
import java.util.Scanner;
public class Main {
    public static void Main(String[] args){
        String opcao;
        String numero = -1;
        Scanner scanner = new Scanner(System.in);
        String menu = "------------- Aetherforge -------------\n\nSelecione uma opção:\n[1]Anunciar um NFT\n[2]Cancelar uma venda\n[3]Comprar um NFT\n[4]Leiloar um NFT\n[5]Fazer uma oferta em um leilão\n[6]Enviar feedback sobre o aplicativo\n[7]Encerrar o programa";

        NFT n1 = new NFT("TKN12345", "Espada Lendária", "Uma espada lendária com poderes mágicos", 2); //trocar descrições e nomes
        NFT n2 = new NFT("TKN23456", "Escudo Épico", "Um escudo épico que pode resistir a qualquer ataque", 1);
        NFT n3 = new NFT("TKN34567", "Armadura Mística", "Uma armadura mística que aumenta a defesa", 2);
        NFT n4 = new NFT("TKN45678", "Poção de Vida", "Uma poção que restaura a vida do personagem", 0);
        NFT n5 = new NFT("TKN56789", "Chave Mestra", "Uma chave que pode abrir qualquer fechadura", 1);
        NFT n6 = new NFT("TKN67890", "Mapa do Tesouro", "Um mapa que leva a um tesouro escondido", 0);
        NFT n7 = new NFT("TKN78901", "Anel de Velocidade", "Um anel que aumenta a velocidade do personagem", 0);
        NFT n8 = new NFT("TKN89012", "Livro de Magia", "Um livro que permite ao personagem aprender novas magias", 0);

        Jogador jogador1 = new Jogador("Joao", 6000, "joaopedro@gmail.com", "baldi", new NFT[] { n1, n2 });
        n1.setDono(jogador1);
        n2.setDono(jogador1);
        Jogador jogador2 = new Jogador("Rodrigo", 4000, "rodrigamer@gmail.com", "rodrigo123", new NFT[] { n3, n4 });
        n3.setDono(jogador2);
        n4.setDono(jogador2);
        Jogador jogador3 = new Jogador("Rafael", 2000, "byakko@gmail.com", "tiger", new NFT[] { n5, n6 });
        n5.setDono(jogador3);
        n6.setDono(jogador3);
        Jogador jogador4 = new Jogador("Igor", 1000, "igor@gmail.com", "igor321", new NFT[] { n7, n8 });
        n7.setDono(jogador4);
        n8.setDono(jogador4);

        System.out.println(menu);
        while (opcao != 7)
        {
            opcao = scanner.nextLine().strip();

            try {
                numero = Integer.parseInt(opcao);
                if (numero < 1 || numero > 7) {
                    throw new IllegalArgumentException("O número deve estar entre 1 e 7");
                }
            }
            catch {
                System.out.println("Erro: Opção não identificada");
            }
            TratarEntrada.Main(jogador1, numero);
        }
    }
}

package Control;
import Model.*; //jogador
import java.util.Scanner;
public class TratarEntrada {
    public static void main(Jogador jogador, int opcao) {
        Scanner scanner = new Scanner(System.in);
        String texto;
        switch(opcao) {
            case 1:
                System.out.println(jogador.verSeusNFTS());
                System.out.println("Digite o token do NFT escolhido: ");
                texto = scanner.nextLine().strip();
                NFT nft = jogador.nfts.get(texto);
                if (nft != null) {
                    if (nft.getStatus() == 0) {
                        jogador.anunciarNFT(nft);
                    }
                    else {
                        System.out.println("Erro: " + checarStatusNFT(nft));
                    }
                }
                else {
                    System.out.println("O NFT inserido não foi encontrado.");
                }
            case 2:
                System.out.println(jogador.verSeusNFTS());
                System.out.println("Digite o token do NFT escolhido: ");
                texto = scanner.nextLine().strip();
                NFT nft = jogador.nfts.get(texto);
                if (nft != null) {
                    if (nft.getStatus() == 1) {
                        jogador.cancelarVenda(nft);
                    }
                    else {
                        System.out.println("Erro: " + checarStatusNFT(nft));
                    }
                }
                else {
                    System.out.println("O NFT inserido não foi encontrado.");
                }
                break;
            case 3:
                System.out.println(jogador.verListaNFT());
                System.out.println("Digite o token do NFT escolhido: ");
                texto = scanner.nextLine().strip();
                NFT nft = MarketPlace.NFTSaVenda.get(texto);
                if (nft != null) {
                    if (nft.getStatus() == 1) {
                        jogador.comprarNFT(nft);
                    }
                    else {
                        System.out.println("Erro: " + checarStatusNFT(nft));
                    }
                }
                else {
                    System.out.println("O NFT inserido não foi encontrado.");
                }
                break;
            case 4:
                System.out.println(jogador.verSeusNFTS());
                System.out.println("Digite o token do NFT escolhido: ");
                texto = scanner.nextLine().strip();
                NFT nft = jogador.nfts.get(texto);
                if (nft != null) {
                    if (nft.getStatus() == 0) {
                        jogador.leiloarNFT(nft);
                    }
                    else {
                        System.out.println("Erro: " + checarStatusNFT(nft));
                    }
                }
                else {
                    System.out.println("O NFT inserido não foi encontrado.");
                }
                break;
            case 5:
                System.out.println(jogador.verListaNFT());
                System.out.println("Digite o token do NFT escolhido: ");
                texto = scanner.nextLine().strip();
                NFT nft = MarketPlace.NFTSaVenda.get(texto);
                if (nft != null) {
                    if (nft.getStatus() == 2) {
                        jogador.fazerOferta(nft);
                    }
                    else {
                        System.out.println("Erro: " + checarStatusNFT(nft));
                    }
                }
                else {
                    System.out.println("O NFT inserido não foi encontrado.");
                }
                break;
            case 6:
                System.out.println("Digite seu feedback: ");
                texto = scanner.nextLine().strip();
                jogador.enviarFeedbackAplicativo(texto);
                System.out.println("Feedback enviado.");
                break;
        }
    }

    public static String checarStatusNFT(NFT nft) {
        String resultado = "O NFT ";
        switch (nft.getStatus()) {
            case 0 -> resultado += " não está a venda nem em leilão.";
            case 1 -> resultado += "está à venda.";
            case 2 -> resultado += "está em leilão";
        }
    }
}

pacakge Model;
public class Oferta {
    private Jogador comprador;
    private int valor;

    public Oferta(Jogador comprador, int valor){
        this.comprador = comprador;
        this.valor = valor;
    }

    public Jogador getComprador(){
        return comprador;
    }
    public int getValor(){
        return valor;
    }
}

package Model;
public class NFT {
    private String token;
    private String nome;
    private Jogador dono;
    private String descricao;
    private int status; //0 - nao está a venda, 1 - a venda, 2 - leilao
    private int valor;
    private Leilao leilao;

    public NFT(String token, String nome, String descricao, int status){
        this.token = token;
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
    }

    public void setLeilao(Leilao leilao){
        this.leilao = leilao;
    }
    public Leilao getLeilao() {
        return leilao;
    }
    public void setDono(Jogador dono){
        this.dono = dono;
    }
    public Jogador getDono(){
        return dono;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return status;
    }
    public void setValor(int valor){
        this.valor = valor;
    }
    public int getValor(){
        return valor;
    }
    public String getToken(){
        return token;
    }
}
