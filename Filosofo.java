
/* ***************************************************************
* Autor............: Thallis Luciano Curcino Nunes
* Matricula........: 202211065
* Inicio...........: 14/10/2023
* Ultima alteracao.: 29/10/2023
* Nome.............: Programacao Concorrente - Trabalho 03.
* Funcao...........: Solucionar o "Problema do Jantar dos  
*                    Filosofos", um classico problema de 
*                    comunicacao entre processos utilizando a 
*                    programacao concorrente e possibilitar a 
*                    simulacao de varios cenarios de execucao por 
*                    meio da interface grafica desenvolvida.
*************************************************************** */

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
/* ****************************************************************
* Classe: Filosofo
* Funcao: Representar um filosofo e seus comportamentos durante o 
*         jantar.
* Descricao: A classe utiliza Threads para representar um filosofo.
*            Os seus metodos e variaveis sao utilizados para simular
*            o comportamento do filosofo durante o jantar. A classe 
*            interage diretamente com a classe ControleTelaPrincipal 
*            que faz o controle de acesso a Regiao Critica 
*            (garfos) utilizando Semaforos.
***************************************************************** */

public class Filosofo extends Thread {
  // ATRIBUTOS DA CLASSE
  private final int id;// id do filosofo (posicao do filosofo no array)
  private final int PENSANDO = 0;// o filosofo esta pensando
  private final int COM_FOME = 1;// o filosofo esta tentando pegar os dois garfos para comer
  private final int COMENDO = 2;// o filosofo esta comendo
  private final int vizinhoEsq;// id do filosofo que esta sentado a esquerda do filosofo 'i'
  private final int vizinhoDir;// id do filosofo que esta sentado a direita do filosofo 'i'
  private ControleTelaPrincipal controle;// instancia da classe controladora da tela
  private int tempoPensando = 4000;// tempo que o filosofo passa pensando, inicialmente 4 segundos
  private int tempoComendo = 4000;// tempo que o filosofo passa comendo, inicialmente 4 segundos
  private boolean isParado = false;// indica se o filosofo esta parado (TRUE) ou nao (FALSE)
  private boolean isInterrompida = false;// flag que indica se a thread foi interrompida (TRUE) ou nao (FALSE)
  private ImageView imgvFilosofo;// camada de imagem que representa o filosofo na tela
  private Image imgPensando;// imagem que representa o filosofo no estado PENSANDO
  private Image imgComFome;// imagem que representa o filosofo no estado COM_FOME
  private Image imgComendo;// imagem que representa o filosofo no estado COMENDO
  private volatile ImageView imgvGarfoEsquerdo;// camada de imagem que representa o garfo a ESQ do filosofo
  private volatile ImageView imgvGarfoDireito;// camada de imagem que representa o garfo a DIR do filosofo
  private Label painelDeInformacoes;// camada de texto onde sao exibidas as informacoes do filosofo

  /* ***************************************************************
   * Metodo: Construtor
   * Funcao: instanciar objetos da classe e inicializar os seus
   * atributos.
   * Parametros: id = posicao do filosofo no array.
   * controleDaTela = instancia da classe controladora.
   * da tela
   * Retorno: void.
   *************************************************************** */
  public Filosofo(int id, ControleTelaPrincipal controleDaTela) {
    super("Filosofo " + (id));// define o nome da Thread
    this.id = id;// define o id do filosofo (posicao no array)
    this.controle = controleDaTela;// define a classe controladora da tela
    this.vizinhoEsq = controle.getVizinhoDaEsquerda(this.id);// define o id do filosofo a ESQUERDA do filosofo 'i'
    this.vizinhoDir = controle.getVizinhoDaDireita(this.id);// define o id do filosofo a DIREITA do filosofo 'i'
    this.imgvFilosofo = controle.getImageViewFilosofo(this.id); // define a camada de imagem que representa o filosofo
    this.imgPensando = controle.getImagemPensando(this.id);// define a imagem que representa o filosofo PENSANDO
    this.imgComFome = controle.getImagemComFome(this.id);// define a imagem que representa o filosofo COM_FOME
    this.imgComendo = controle.getImagemComendo(this.id);// define a imagem que representa o filosofo COMENDO
    // define as camadas de imagens que representam os garfos do filosofo
    this.imgvGarfoEsquerdo = controle.getGarfoEsquerdo(this.id);// define a camada de imagem para o gafo ESQUERDO
    this.imgvGarfoDireito = controle.getGarfoDireito(this.id);// define a camada de imagem para o gafo DIREITO
    this.painelDeInformacoes = controle.getPainelDeInfo(id);// define a camada de texto que exibe as infos do filosofo
  }// fim do construtor da classe

  /* ***************************************************************
   * Metodo: run
   * Funcao: definir o comportamento ao iniciar a Thread
   * (Filosofo.start()).
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  @Override
  public void run() {
    while (!isInterrompida) {// executa a loop enquanto a thread nao for interrompida
      if (isInterrompida) {// verifica a flag que sinaliza a interrupcao da thread
        break;// sai do laco se a thread for interrompida
      } // fim do if

      pensar();// PENSANDO de acordo com o valor de "tempoPensando"

      if (isInterrompida) {// verifica a flag que sinaliza a interrupcao da thread
        break;// sai do laco se a thread for interrompida
      } // fim do if

      pegarGarfos(id);// fica COM_FOME e tenta pegar os garfos(pode ser bloqueado)

      if (isInterrompida) {// verifica a flag que sinaliza a interrupcao da thread
        break;// sai do laco se a thread for interrompida
      } // fim do if

      comer();// COMENDO de acordo com o valor de "tempoComendo"

      if (isInterrompida) {// verifica a flag que sinaliza a interrupcao da thread
        break;// sai do laco se a thread for interrompida
      } // fim do if

      devolverGarfos(id);// devolve os garfos a mesa e volta a pensar (sinaliza aos vizinhos)

      if (isInterrompida) {// verifica a flag que sinaliza a interrupcao da thread
        break;// se a thread foi interrompida, sai do laco
      } // fim do if
    } // fim do while
    reiniciar();// muda para a imagem do filosofo PENSANDO informa que os filosofo parou
  }// fim do metodo run

  // METODOS QUE REPRESENTAM AS ACOES DO FILOSOFO DURANTE O JANTAR

  /* ***************************************************************
   * Metodo: comparaValores
   * Funcao: simular o comportamento do filosofo PENSANDO.
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  public void pensar() {
    mudarImagem(PENSANDO);// muda para a imagem que representa o filosofo PENSANDO
    exibirInformacoes(false);// exibe os tempo que o filosofo passa em cada estado
    try {
      Thread.sleep(tempoPensando);// a thread dorme de acordo com o valor de "tempoPensando"
    } catch (InterruptedException exc) {// captura a excecao gerada ao interromper a thread
      interromper();// atualiza o valor da flag de interrupcao
      return;// finaliza a execucao do metodo pensar()
    } // fim do bloco try catch
  }// fim do metodo pensar

  /* ***************************************************************
   * Metodo: comer
   * Funcao: simular o comportamento do filosofo COMENDO.
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  public void comer() {
    mudarImagem(COMENDO);// muda para a imagem que representa o filosofo COMENDO
    exibirInformacoes(false);// exibe os tempo que o filosofo passa em cada estado
    try {
      Thread.sleep(tempoComendo);// a thread dorme de acordo com o valor de "tempoComendo"
    } catch (InterruptedException exc) {// captura a excecao gerada ao interromper a thread
      interromper();// atualiza o valor da flag de interrupcao
      return;// finaliza a execucao do metodo comer()
    } // fim do bloco try catch
  }// fim do metodo comer

  /* ***************************************************************
   * Metodo: pegarGarfos
   * Funcao: implementar a logica de um filosofo tentando pegar os
   * dois garfos da mesa. Caso um dos garfos esteja ocupado
   * o filosofo fica bloqueado (COM_FOME) no semaforo
   * aguardando uma nova chance de tentar pegar os garfos.
   * Parametros: id = filosofo que esta tentando pegar os garfos.
   * Retorno: void.
   *************************************************************** */
  public void pegarGarfos(int id) {// id eh o filosofo que esta tentando pegar os garfos
    try {
      controle.mutex.acquire();// entra na RC (DOWN no semaforo)
      controle.estados[id] = COM_FOME;// sinaliza que o filosofo 'id' esta COM_FOME
      mudarImagem(COM_FOME);// muda para a imagem que representa o filosofo COM_FOME
      testarGarfos(id);// testa se o filosofo pode comer com os dois garfos
      controle.mutex.release();// deixa a RC (UP no semaforo)
      controle.arraySemaforos[id].acquire();// aguarda ate os dois garfos estiverem disponiveis para serem pegos
      tirarGarfosDaMesa();// retira os garfos da mesa (desabilita as imagens) para poder comer
    } catch (InterruptedException exc) {// captura a excecao gerada ao interromper a thread
      interromper();// atualiza o valor da flag de interrupcao
      return;// finaliza a execucao do metodo pegarGarfos()
    } // fim do bloco try catch
  }// fim do metodo pegarGarfos

  /* ***************************************************************
   * Metodo: devolverGarfos
   * Funcao: implementar a logica de um filosofo devolvendo os garfos
   * para a mesa. Ao devolver os garfos o filosofo verifica
   * algum dos seus vizinhos esta COM_FOME para que o
   * vizinho possa comer.
   * Parametros: id = filosofo que esta devolvendo os garfos.
   * Retorno: void.
   *************************************************************** */
  public void devolverGarfos(int id) {// id eh o numero do filosofo que esta devolvendo os garfos
    try {
      controle.mutex.acquire();// entra na RC (DOWN no semaforo)
      controle.estados[id] = PENSANDO;// o filosofo esta PENSANDO, libera os garfos
      mudarImagem(PENSANDO);// muda para a imagem que representa o filosofo PENSANDO
      devolverGarfosParaMesa();// devolve os garfos para a mesa (habilita as imagens), pois terminou de comer
      testarGarfos(vizinhoEsq);// verifica se o vizinho da ESQUERDA pode comer agora
      testarGarfos(vizinhoDir);// verifica se o vizinho da DIREITA pode comer agora
      controle.mutex.release();// deixa a RC (UP no semaforo)
    } catch (InterruptedException exc) {// captura a excecao gerada ao interromper a thread
      interromper();// atualiza o valor da flag de interrupcao
      return;// finaliza a execucao do metodo devolverGarfos()
    } // fim do bloco try catch
  }// fim do metodo pegarGarfo

  /* ***************************************************************
   * Metodo: testarGarfos
   * Funcao: implementar a logica de um filosofo que esta verificando
   * se os seus vizinhos nao estao COMENDO, para que ele possa
   * pegar os garfos.
   * Parametros: id = filosofo que esta tentando
   * Retorno: void.
   *************************************************************** */
  public void testarGarfos(int id) {// id eh o filosofo que esta fazendo a verificacao
    // verifica se o filosofo esta com fome e os seus vizinhos nao estao comendo
    if (controle.estados[id] == COM_FOME && controle.estados[vizinhoEsq] != COMENDO
        && controle.estados[vizinhoDir] != COMENDO) {
      controle.estados[id] = COMENDO;// atualiza o estado do filosofo, pega os dois garfos
      controle.arraySemaforos[id].release();// libera o semaforo correspondente ao filosofo para que ele possa comer
    } // fim do if
  }// fim do metodo testar

  /* ***************************************************************
   * Metodo: parar
   * Funcao: parar a execucao da thread e sinalizar essa mudanca de
   * estado.
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  public void parar() {
    isParado = true;// atualiza a flag de estado do filosofo
    exibirInformacoes(isParado);// exibe a info de que o filosofo esta parado
    this.suspend();// para a thread
  }// fim do metodo parar

  /* ***************************************************************
   * Metodo: retomar
   * Funcao: retomar a execucao da thread e sinalizar essa mudanca de
   * estado.
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  public void retomar() {
    isParado = false;// atualiza a flag de estado do filosofo
    exibirInformacoes(isParado);// exibe os tempo que o filosofo passa em cada estado
    this.resume();// retoma a execucao da thread
  }// fim do metodo retomar

  /* ***************************************************************
   * Metodo: interromper
   * Funcao: atualizar o valor da flag de interrupcao para que a
   * thread finalize sua execuacao (sair do laco do
   * metodo run()).
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  public void interromper() {
    isInterrompida = true;// atualiza a flag de interrupcao para que a thread finaliza a execucao
  }// fim do metodo interromper

  /* ***************************************************************
   * Metodo: reiniciar
   * Funcao: mudar imagem para a imagem do filosofo PENSANDO e exibir a
   * info de que filosofo esta parado apos finalizar a thread.
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  public void reiniciar() {
    mudarImagem(PENSANDO);// muda a imagem que representa om filosofo PENSANDO
    exibirInformacoes(true);// exibe a info de que o filosofo esta parado
  }// fim do metodo reiniciar

  /* ***************************************************************
   * Metodo: pensarMaisRapido
   * Funcao: decrementar o valor de "tempoPensando" para que o filosofo
   * passe mais rapido pelo estado de PENSANDO (pense mais rapido).
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  public void pensarMaisRapido() {
    if (tempoPensando >= 3000) {// o filosofo pode pensar por no minimo 2 seg
      tempoPensando -= 1000;// diminui em 1 seg o tempo que thread ira dormir
    } // fim do if
    exibirInformacoes(false);// exibe a atualizacao no valor de "tempoPensando"
  }// fim do metodo pensarMaisRapido

  /* ***************************************************************
   * Metodo: pensarMaisDevagar
   * Funcao: incrementar o valor de "tempoPensando" para que o filosofo
   * demore mais para passar pelo estado de PENSANDO (pense
   * mais devagar).
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  public void pensarMaisDevagar() {
    if (tempoPensando <= 7000) {// o filosofo pode pensar por no maximo 8 seg
      tempoPensando += 1000;// aumenta em 1 seg o tempo que thread ira dormir
    } // fim do if
    exibirInformacoes(false);// exibe a atualizacao no valor de "tempoPensando"
  }// fim do metodo pensarMaisDevagar

  /* ***************************************************************
   * Metodo: comerMaisRapido
   * Funcao: decrementar o valor de "tempoComendo" para que o filosofo
   * passe mais rapido pelo estado de COMENDO (coma mais rapido).
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  public void comerMaisRapido() {
    if (tempoComendo >= 3000) {// o filosofo pode comer por no minimo 2 seg
      tempoComendo -= 1000;// diminui em 1 seg o tempo que thread ira dormir
    } // fim do if
    exibirInformacoes(false);// exibe a atualizacao no valor de "tempoComendo"
  }// fim do metodo comerMaisRapido

  /* ***************************************************************
   * Metodo: comerMaisDevagar
   * Funcao: incrementar o valor de "tempoComendo" para que o filosofo
   * demore mais para passar pelo estado de COMENDO (coma mais
   * devagar).
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  public void comerMaisDevagar() {
    if (tempoComendo <= 7000) {// o filosofo pode comer por no maximo 8 seg
      tempoComendo += 1000;// aumenta em 1 seg o tempo que thread ira dormir
    } // fim do if
    exibirInformacoes(false);// exibe a atualizacao no valor de "tempoComendo"
  }// fim do metodo diminuirVelocidadeComer

  /* ***************************************************************
   * Metodo: mudarImagem
   * Funcao: atualizar a imagem que representa o filosofo a imagem de
   * um estado especifo (PENSANDO, COM_FOME OU COMENDO).
   * Parametros: estado = imagem que deve ser exibida.
   * Retorno: void.
   *************************************************************** */
  public void mudarImagem(int estado) {// estado eh a imagem que deve ser exibida (PENSANDO, COM_FOME OU COMENDO)
    switch (estado) {// estado atual do filosofo
      case 0: {// MUDAR PARA IMAGEM DO FILOSOFO PENSANDO
        Platform.runLater(() -> this.imgvFilosofo.setImage(imgPensando));// muda a imagem exibida
        break;
      } // fim do case 0 (PENSANDO)
      case 1: {// MUDAR PARA A IMAGEM DO FILOSOFO COM_FOME
        Platform.runLater(() -> this.imgvFilosofo.setImage(imgComFome));// muda a imagem exibida
        break;
      } // fim do case 1 (COM FOME)
      case 2: {// MUDAR PARA A IMAGEM DO FILOSOFO COMENDO
        Platform.runLater(() -> this.imgvFilosofo.setImage(imgComendo));// muda a imagem exibida
        break;
      } // fim do case 2 (COMENDO)
    }// fim do switch (estado)
  }// fim do metodo mudarImagem

  /* ***************************************************************
   * Metodo: tirarGarfosDaMesa
   * Funcao: tornar as imagens dos garfos ESQUERDO e DIREITO invisiveis
   * para simular a retirada dos garfos da mesa (garfos ocupados).
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  public void tirarGarfosDaMesa() {
    // espera ate os DOIS garfos estarem VISIVEIS para evitar erros de representacao
    while (!imgvGarfoEsquerdo.isVisible() || !imgvGarfoDireito.isVisible()) {// espera ate os garfos estarem visiveis
      try {
        Thread.sleep(1);// espera 1 milisegundo
      } catch (InterruptedException exc) {// captura a excecao gerada ao interromper a thread
        interromper();// atualiza o valor da flag de interrupcao
        break;// finaliza a execucao do metodo devolverGarfos()
      } // fim do bloco try catch
    } // fim do while

    Platform.runLater(() -> { // atualizacoes na interface grafica
      this.imgvGarfoEsquerdo.setVisible(false);// desabilita a imagem do garfo ESQUERDO
      this.imgvGarfoDireito.setVisible(false);// desabilita a imagem do garfo DIREITO
    });// fim das atualizacoes na interface grafica
  }// fim do metodo tirarGarfoDaMesa

  /* ***************************************************************
   * Metodo: devolverGarfosParaMesa
   * Funcao: tornar as imagens dos garfos ESQUERDO e DIREITO visiveis
   * para simular a devolucao dos garfos para a mesa (garfos
   * livres).
   * Parametros: void.
   * Retorno: void.
   *************************************************************** */
  public void devolverGarfosParaMesa() {
    Platform.runLater(() -> {// atualizacoes na interface grafica
      this.imgvGarfoEsquerdo.setVisible(true);// habilita a imagem do garfo ESQUERDO
      this.imgvGarfoDireito.setVisible(true);// habilita a imagem do garfo DIREITO
    });// fim das atualizacoes na interface grafica
  }// fim do metodo devolverGarfosParaMesa

  /* ***************************************************************
   * Metodo: exibirInformacoes
   * Funcao: exibir os valores das variaiveis "tempoPensando" e
   * "tempoComendo" para informar o tempo que o filosofo
   * passara nos estados PENSANDO E COMENDO, respectivamente.
   * Se o filosofo estiver parado exibe a mensagem apropriada.
   * Parametros: filosofoParou. = flag que indica se o metodo deve a
   * mensagem "Parado." ou nao.
   * Retorno: void.
   *************************************************************** */
  public void exibirInformacoes(boolean exibirMensagemParado) {// exibir a mensagem "Parado." ou nao, TRUE ou FALSE
    if (exibirMensagemParado) {// se o filosofo estiver parado
      Platform.runLater(() -> this.painelDeInformacoes.setText("Parado."));// muda o texto exibido no painel
    } else {// filosofo esta executando (exibir os tempos PENSANDO e COMENDO)
      Platform.runLater(() -> {// atualizacoes na interface grafica
        this.painelDeInformacoes.setText("Pensar: " + (tempoPensando / 1000) + " seg | Comer: "
            + (tempoComendo / 1000) + " seg");// muda o texto exibido no painel
      });// fim das atualizacoes na interface grafica
    } // fim do bloco if-else
  }// fim do metodo exibirInformacoes

  /* ***************************************************************
   * Metodo: isParado
   * Funcao: retornar os estado de execucao da thread
   * Parametros: void.
   * Retorno: isParado = thread parada (true) ou em execucao (false)
   *************************************************************** */
  public boolean isParado() {
    return isParado;
  }// fim do metodo isParado

}// fim da classe Filosofo