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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/* ****************************************************************
* Classe: ControleTelaPrincipal
* Funcao: Representar um filosofo e seus comportamentos durante o 
*         jantar.
* Descricao: A classe instancia os objetos "Filosofo" e controla o acesso 
*            a Regiao Critica (garfos) utilizando os Semaforos.
*            Possui instancias para os objetos contidos na interface 
*            grafica e metodos "ouvintes" que respondem de acordo as 
*            interacoes com esses objetos.
***************************************************************** */
public class ControleTelaPrincipal implements Initializable {

  // INSTANCIAS PARA AS CAMADAS DE IMAGEM QUE REPRESENTAM OS FILOSOFOS NA TELA
  @FXML
  private ImageView imgv_Filosofo_01;
  @FXML
  private ImageView imgv_Filosofo_02;
  @FXML
  private ImageView imgv_Filosofo_03;
  @FXML
  private ImageView imgv_Filosofo_04;
  @FXML
  private ImageView imgv_Filosofo_05;

  // INSTANCIAS PARA AS CAMADAS DE IMAGEM QUE REPRESNTAM OS GARFOS
  @FXML
  private ImageView imgv_Garfo01;
  @FXML
  private ImageView imgv_Garfo02;
  @FXML
  private ImageView imgv_Garfo03;
  @FXML
  private ImageView imgv_Garfo04;
  @FXML
  private ImageView imgv_Garfo05;

  // INSTANCIAS PARA OS BOTOES QUE AUMENTAM O TEMPO PENSANDO
  @FXML
  private Button btn_AumentarVelPensar_Filo01;
  @FXML
  private Button btn_AumentarVelPensar_Filo02;
  @FXML
  private Button btn_AumentarVelPensar_Filo03;
  @FXML
  private Button btn_AumentarVelPensar_Filo04;
  @FXML
  private Button btn_AumentarVelPensar_Filo05;

  // INSTANCIAS PARA OS BOTOES QUE DIMINUEM O TEMPO PENSANDO
  @FXML
  private Button btn_DiminuirVelPensar_Filo01;
  @FXML
  private Button btn_DiminuirVelPensar_Filo02;
  @FXML
  private Button btn_DiminuirVelPensar_Filo03;
  @FXML
  private Button btn_DiminuirVelPensar_Filo04;
  @FXML
  private Button btn_DiminuirVelPensar_Filo05;

  // INSTANCIAS PARA OS BOTOES QUE AUMENTAM O TEMPO COMENDO
  @FXML
  private Button btn_AumentarVelComer_Filo01;
  @FXML
  private Button btn_AumentarVelComer_Filo02;
  @FXML
  private Button btn_AumentarVelComer_Filo03;
  @FXML
  private Button btn_AumentarVelComer_Filo04;
  @FXML
  private Button btn_AumentarVelComer_Filo05;

  // INSTANCIAS PARA OS BOTOES DIMINUEM O TEMPO COMENDO
  @FXML
  private Button btn_DiminuirVelComer_Filo01;
  @FXML
  private Button btn_DiminuirVelComer_Filo02;
  @FXML
  private Button btn_DiminuirVelComer_Filo03;
  @FXML
  private Button btn_DiminuirVelComer_Filo04;
  @FXML
  private Button btn_DiminuirVelComer_Filo05;

  // INSTANCIAS DOS BOTOES QUE PARA/RETOMAM A EXECUCAO DOS FILOSOFOS
  @FXML
  private Button btn_Parar_Retomar_Filo01;
  @FXML
  private Button btn_Parar_Retomar_Filo02;
  @FXML
  private Button btn_Parar_Retomar_Filo03;
  @FXML
  private Button btn_Parar_Retomar_Filo04;
  @FXML
  private Button btn_Parar_Retomar_Filo05;

  // INSTANCIAS PARA AS CAMADAS DE TEXTO QUE EXIBEM OS TEMPOS DOS FILOSOFOS EM
  // CADA ESTADO
  @FXML
  private Label painelInfo_Filo01;
  @FXML
  private Label painelInfo_Filo02;
  @FXML
  private Label painelInfo_Filo03;
  @FXML
  private Label painelInfo_Filo04;
  @FXML
  private Label painelInfo_Filo05;

  // INSTANCIAS PARA OS BOTOES QUE INICIAM OU REINICIAM A SIMULACAO
  @FXML
  private Button btn_Reiniciar;
  @FXML
  private Button btn_Iniciar;

  // SEMAFOROS UTILIZADOS PARA O CONTROLE DE ACESSO A REGIAO CRITICA
  private final int N_Filosofos = 5;// define o numero de filosofos
  public int[] estados = new int[N_Filosofos];// cada posicao no array corresponde ao estado do filisofo "i"
  public Semaphore mutex = new Semaphore(1);// semaforo para a exclusao mutua
  public Semaphore[] arraySemaforos = new Semaphore[N_Filosofos];// semaforo "i" corresponde ao filosofo "i"

  // Instacias para as Threads que reprensentam os filosofos
  private Filosofo filosofo_01, filosofo_02, filosofo_03, filosofo_04, filosofo_05;

  // Array com as threads que representam os filosofos
  public Filosofo[] arrayDeFilosofos = new Filosofo[] { filosofo_01, filosofo_02, filosofo_03, filosofo_04,
      filosofo_05 };

  // ELEMENTOS QUE COMPOEM A INTERFACE GRAFICA
  // Array com as camadas de imagem que representam todos os filosofos na tela
  private ImageView[] arrayImgvFilosofos;
  // Array com as camadas de imagem que representam todos os garfos na tela
  private ImageView[] arrayImgvGarfos;
  // Array com os paineis de informacoes correspondentes a cada filosofo
  private Label[] arrayLblPaineisDeInfo;

  // METODOS PARA CONTROLE DAS THREADS E SEMAFOROS
  /* ***************************************************************
   * Metodo: criarThreadsDosFilosofos
   * Funcao: percorrer o "arrayDeFilosofos" para instanciar novos
   * objetos Filosfo atribuindo o valor do "id" e classe
   * controladora (ControleTelaPrincipal)
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void criarThreadsDosFilosofos() {
    for (int i = 0; i < N_Filosofos; i++) {// percorre o arrayDeFilosofos
      arrayDeFilosofos[i] = new Filosofo(i, this);// intancia os objetos do array definindo "id" e "controle"
    } // fim do for
  }// fim do metodo criarThreadsDosFilosofos

  /* ***************************************************************
   * Metodo: iniciarThreadsDosFilosofos
   * Funcao: percorrer o "arrayDeFilosofos" para iniciar a execucao
   * de cada thread (Thread.start()) correspondente aos
   * filosofos.
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void iniciarThreadsDosFilosofos() {
    for (int i = 0; i < N_Filosofos; i++) {// percorre o array de filosofos
      arrayDeFilosofos[i].start();// inicia a execucao da thread
    } // fim do for
  }// fim do metodo iniciarThreadsDosFilosofos

  /* ***************************************************************
   * Metodo: interromperThreadsDosFilosofos
   * Funcao: percorrer o "arrayDeFilosofos" para lancar uma flag sinalizando
   * que a thread deve finalizar sua execucao. Se a thread estiver
   * parada o metodo retoma sua execucao para que a thread seja
   * finalizada da maneira correta.
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void interromperThreadsDosFilosofos() {
    for (int i = 0; i < N_Filosofos; i++) {// percorre o array de filosofos
      if (arrayDeFilosofos[i].isParado()) {// se a thread estiver parada retoma sua execucao
        arrayDeFilosofos[i].retomar();// retoma a excucao
      } // fim do if
      arrayDeFilosofos[i].interrupt();// lanca a flag indicando que a thread deve finalizar
    } // fim do for
  }// fim do metodo interromperThreadsDosFilosofos

  /* ***************************************************************
   * Metodo: inicializarArraysDeControle
   * Funcao: inicializar os arrays de semaforos e de estados com valores
   * predefinidos. Inicialmente todos os semaforos do array estaos
   * bloqueados e todos os filosofos estao no estado PENSANDO.
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void inicializarArraysDeControle() {
    for (int i = 0; i < N_Filosofos; i++) {// percorre os arrays
      arraySemaforos[i] = new Semaphore(0); // inicialmente todos estao bloqueados
      estados[i] = 0;// inicialmente todos estao pensando
    } // fim do for
  }// fim do metodo inicializarArraysDeControle

  // METODO PARA INICIALIZAR OS ARRAYS DE ELEMENTOS DA INTERFACE GRAFICA
  /* ***************************************************************
   * Metodo: inicializarArraysDaInterface
   * Funcao: inicializaR os arrays que contem as referencias aos
   * elementos contidos na interface grafica (Camadas de
   * imagem para os filosofos e os garfos e camada de
   * texto para exibir os tempos em cada estado).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void inicializarArraysDaInterface() {
    // inicializa o array atribuindo as camadas de imagem correspondentes aos
    // filosofos, na ordem correta
    arrayImgvFilosofos = new ImageView[] { imgv_Filosofo_01, imgv_Filosofo_02, imgv_Filosofo_03,
        imgv_Filosofo_04, imgv_Filosofo_05 };
    // inicializa o array atribuindo as camadas de imagem correspondentes aos
    // garfos, na ordem correta
    arrayImgvGarfos = new ImageView[] { imgv_Garfo01, imgv_Garfo02, imgv_Garfo03, imgv_Garfo04,
        imgv_Garfo05 };
    // inicializa o array atribuindo as camadas de text correspondentes aos paineis
    // de cada filosofo
    // , na ordem correta
    arrayLblPaineisDeInfo = new Label[] { painelInfo_Filo01, painelInfo_Filo02, painelInfo_Filo03, painelInfo_Filo04,
        painelInfo_Filo05 };
  }// fim do metodo inicializarArraysDaInterface

  /* ***************************************************************
   * Metodo: initialize
   * Funcao: definir acoes tomadas ao iniciar aplicacao
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void initialize(URL location, ResourceBundle resources) {
    inicializarArraysDeControle();// inicializa o array de semaforos e de estados
    inicializarArraysDaInterface();// inicializa os arrays que contem os elementos da interface
    desabilitarBtnReiniciar();// oculta e desabilita para interacoes o botoao "Reiniciar Simulacao"
    desabilitarBotoesDeControleDosFilosofos();// desabilita para interacoes os botoes de controle de cada filosofo
  }// fim do metodo initialize

  // METODOS "OUVINTES" PARA AS INTERACOES COM ELEMENTOS DA INTERFACE GRAFICA
  /* ***************************************************************
   * Metodo: acaoBtnIniciar
   * Funcao: definir as acoes a serem tomadas ao clicar no botao
   * "Iniciar Simulacao".
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnIniciar() {
    exibirInfoDeControleSimulacao();// exibe a janela com as instrucoes de controle sobre a simulacao

    Platform.runLater(() -> {// atualizacoes na interface grafica
      desabilitarBtnIniciar();// oculta e desabilita o botao "Iniciar Simulacao" para interacoes
      habilitarBotoesDeControleDosFilosofos();// habilita os controles de cada filosofo para interacoes
      habilitarBtnReiniciar();// exibe e habilita o botao "Reiniciar Simulacao" para interacoes
    });// fim das atualizacoes na interface grafica

    mutex = new Semaphore(1);// inicia o semaforo para a exclusao mutua
    inicializarArraysDeControle();// inicia os valores dos arrays de semaforos e estados
    criarThreadsDosFilosofos();// cria as threads
    iniciarThreadsDosFilosofos();// inicia as threads
  }// fim do metodo acaoBtnIniciar

  /* ***************************************************************
   * Metodo: acaoBtnReiniciar
   * Funcao: definir as acoes a serem tomadas ao clicar no botao
   * "Reiniciar Simulacao".
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnReiniciar() {
    interromperThreadsDosFilosofos();// interrompe as threads dos filosofos
    exibirInfoReiniciou();// exibe uma janela informando que a simulacao foi reiniciada
    // mudancas feitas na interface grafica
    Platform.runLater(() -> {// atualizacoes na interface grafica
      desabilitarBtnReiniciar();// oculta e desabilita para interacoes os botao "Reiniciar Simulacao"
      reiniciarBotoesParar_Retomar();// muda o texto dos paines de info de cada filosofo para "Parado."
      reiniciarArrayDeGarfos();// torna todos os garfos visiveis
      desabilitarBotoesDeControleDosFilosofos();// deasabilita os controles de cada filosofo para interacoes
    });// fim das atualizacoes na interface grafica
    habilitarBtnIniciar();// por ultimo, exibe e habilita o botao "Iniciar Simulacao" para interacoes
  }// fim do metodo acaoBtnReiniciar

  // "OUVINTES" PARA OS BOTOES QEU AUMENTAM TEMPO PENSANDO (PENSAR MAIS DEVAGAR)
  /* ***************************************************************
   * Metodo: acaoBtnAumentarVelPensar_Filosofo01
   * Funcao: aumentar o valor de "tempoPensando" do filosofo apos clicar
   * no botao "+" para que o filosofo pense por mais tempo
   * (devagar).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnAumentarVelPensar_Filosofo01() {
    arrayDeFilosofos[0].pensarMaisDevagar();// aumenta o tempo que o filosofo passa pensando
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo01

  /* ***************************************************************
   * Metodo: acaoBtnAumentarVelPensar_Filosofo02
   * Funcao: aumentar o valor de "tempoPensando" do filosofo apos clicar
   * no botao "+" para que o filosofo pense por mais tempo
   * (devagar).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnAumentarVelPensar_Filosofo02() {
    arrayDeFilosofos[1].pensarMaisDevagar();// aumenta o tempo que o filosofo passa pensando
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo02

  /* ***************************************************************
   * Metodo: acaoBtnAumentarVelPensar_Filosofo03
   * Funcao: aumentar o valor de "tempoPensando" do filosofo apos clicar
   * no botao "+" para que o filosofo pense por mais tempo
   * (devagar).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnAumentarVelPensar_Filosofo03() {
    arrayDeFilosofos[2].pensarMaisDevagar();// aumenta o tempo que o filosofo passa pensando
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo03

  /* ***************************************************************
   * Metodo: acaoBtnAumentarVelPensar_Filosofo04
   * Funcao: aumentar o valor de "tempoPensando" do filosofo apos clicar
   * no botao "+" para que o filosofo pense por mais tempo
   * (devagar).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnAumentarVelPensar_Filosofo04() {
    arrayDeFilosofos[3].pensarMaisDevagar();// aumenta o tempo que o filosofo passa pensando
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo04

  /* ***************************************************************
   * Metodo: acaoBtnAumentarVelPensar_Filosofo05
   * Funcao: aumentar o valor de "tempoPensando" do filosofo apos clicar
   * no botao "+" para que o filosofo pense por mais tempo
   * (devagar).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnAumentarVelPensar_Filosofo05() {
    arrayDeFilosofos[4].pensarMaisDevagar();// aumenta o tempo que o filosofo passa pensando
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo05

  // "OUVINTES" PARA OS BOTOES QUE DIMINUEM O TEMPO PENSANDO (PENSAR MAIS RAPIDO)
  /* ***************************************************************
   * Metodo: acaoBtnDiminuirVelPensar_Filosofo01
   * Funcao: diminuir o valor de "tempoPensando" do filosofo apos clicar
   * no botao "-" para que o filosofo pense por menos tempo
   * (rapido).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnDiminuirVelPensar_Filosofo01() {
    arrayDeFilosofos[0].pensarMaisRapido();// diminui o tempo que o filosofo passa pensando
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo01

  /* ***************************************************************
   * Metodo: acaoBtnDiminuirVelPensar_Filosofo02
   * Funcao: diminuir o valor de "tempoPensando" do filosofo apos clicar
   * no botao "-" para que o filosofo pense por menos tempo
   * (rapido).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnDiminuirVelPensar_Filosofo02() {
    arrayDeFilosofos[1].pensarMaisRapido();// diminui o tempo que o filosofo passa pensando
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo02

  /* ***************************************************************
   * Metodo: acaoBtnDiminuirVelPensar_Filosofo03
   * Funcao: diminuir o valor de "tempoPensando" do filosofo apos clicar
   * no botao "-" para que o filosofo pense por menos tempo
   * (rapido).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnDiminuirVelPensar_Filosofo03() {
    arrayDeFilosofos[2].pensarMaisRapido();// diminui o tempo que o filosofo passa pensando
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo03

  /* ***************************************************************
   * Metodo: acaoBtnDiminuirVelPensar_Filosofo04
   * Funcao: diminuir o valor de "tempoPensando" do filosofo apos clicar
   * no botao "-" para que o filosofo pense por menos tempo
   * (rapido).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnDiminuirVelPensar_Filosofo04() {
    arrayDeFilosofos[3].pensarMaisRapido();// diminui o tempo que o filosofo passa pensando
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo04

  /* ***************************************************************
   * Metodo: acaoBtnDiminuirVelPensar_Filosofo05
   * Funcao: diminuir o valor de "tempoPensando" do filosofo apos clicar
   * no botao "-" para que o filosofo pense por menos tempo
   * (rapido).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnDiminuirVelPensar_Filosofo05() {
    arrayDeFilosofos[4].pensarMaisRapido();// diminui o tempo que o filosofo passa pensando
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo05

  // "OUVINTES" PARA BOTOES QUE AUMENTAM O TEMPO COMENDO (COMER MAIS DEVAGAR)
  /* ***************************************************************
   * Metodo: acaoBtnAumentarVelComer_Filosofo01
   * Funcao: aumentar o valor de "tempoComendo" do filosofo apos clicar
   * no botao "+" para que o filosofo coma por mais tempo
   * (devagar).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnAumentarVelComer_Filosofo01() {
    arrayDeFilosofos[0].comerMaisDevagar();// aumenta o tempo que o filosofo passa comendo
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo01

  /* ***************************************************************
   * Metodo: acaoBtnAumentarVelComer_Filosofo02
   * Funcao: aumentar o valor de "tempoComendo" do filosofo apos clicar
   * no botao "+" para que o filosofo coma por mais tempo
   * (devagar).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnAumentarVelComer_Filosofo02() {
    arrayDeFilosofos[1].comerMaisDevagar();// aumenta o tempo que o filosofo passa comendo
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo02

  /* ***************************************************************
   * Metodo: acaoBtnAumentarVelComer_Filosofo03
   * Funcao: aumentar o valor de "tempoComendo" do filosofo apos clicar
   * no botao "+" para que o filosofo coma por mais tempo
   * (devagar).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnAumentarVelComer_Filosofo03() {
    arrayDeFilosofos[2].comerMaisDevagar();// aumenta o tempo que o filosofo passa comendo
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo03

  /* ***************************************************************
   * Metodo: acaoBtnAumentarVelComer_Filosofo04
   * Funcao: aumentar o valor de "tempoComendo" do filosofo apos clicar
   * no botao "+" para que o filosofo coma por mais tempo
   * (devagar).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnAumentarVelComer_Filosofo04() {
    arrayDeFilosofos[3].comerMaisDevagar();// aumenta o tempo que o filosofo passa comendo
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo04

  /* ***************************************************************
   * Metodo: acaoBtnAumentarVelComer_Filosofo05
   * Funcao: aumentar o valor de "tempoComendo" do filosofo apos clicar
   * no botao "+" para que o filosofo coma por mais tempo
   * (devagar).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnAumentarVelComer_Filosofo05() {
    arrayDeFilosofos[4].comerMaisDevagar();// aumenta o tempo que o filosofo passa comendo
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo05

  // "OUVINTES" PARA OS BOTOES QUE DIMINUEM O TEMPO COMENDO (COMER MAIS RAPIDO)
  /* ***************************************************************
   * Metodo: acaoBtnDiminuirVelComer_Filosofo01
   * Funcao: diminuir o valor de "tempoComendo" do filosofo apos clicar
   * no botao "-" para que o filosofo coma por menos tempo
   * (rapido).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnDiminuirVelComer_Filosofo01() {
    arrayDeFilosofos[0].comerMaisRapido();// diminui o tempo que o filosofo passa comendo
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo01

  /* ***************************************************************
   * Metodo: acaoBtnDiminuirVelComer_Filosofo02
   * Funcao: diminuir o valor de "tempoComendo" do filosofo apos clicar
   * no botao "-" para que o filosofo coma por menos tempo
   * (rapido).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnDiminuirVelComer_Filosofo02() {
    arrayDeFilosofos[1].comerMaisRapido();// diminui o tempo que o filosofo passa comendo
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo02

  /* ***************************************************************
   * Metodo: acaoBtnDiminuirVelComer_Filosofo03
   * Funcao: diminuir o valor de "tempoComendo" do filosofo apos clicar
   * no botao "-" para que o filosofo coma por menos tempo
   * (rapido).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnDiminuirVelComer_Filosofo03() {
    arrayDeFilosofos[2].comerMaisRapido();// diminui o tempo que o filosofo passa comendo
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo03

  /* ***************************************************************
   * Metodo: acaoBtnDiminuirVelComer_Filosofo04
   * Funcao: diminuir o valor de "tempoComendo" do filosofo apos clicar
   * no botao "-" para que o filosofo coma por menos tempo
   * (rapido).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnDiminuirVelComer_Filosofo04() {
    arrayDeFilosofos[3].comerMaisRapido();// diminui o tempo que o filosofo passa comendo
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo04

  /* ***************************************************************
   * Metodo: acaoBtnDiminuirVelComer_Filosofo05
   * Funcao: diminuir o valor de "tempoComendo" do filosofo apos clicar
   * no botao "-" para que o filosofo coma por menos tempo
   * (rapido).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnDiminuirVelComer_Filosofo05() {
    arrayDeFilosofos[4].comerMaisRapido();// diminui o tempo que o filosofo passa comendo
  }// fim do metodo acaoBtnAumentarVelPensar_Filosofo05

  // "OUVINTES" PARA OS BOTOES QUE PARAM OU RETOMAM E EXECUCAO DOS FILOSOFOS
  /* ***************************************************************
   * Metodo: acaoBtnParar_Retomar_Filosofo01
   * Funcao: definir as acoes tomadas de acordo coma as interacoes com o
   * botao "Parar/Retomar" do filosofo. O metodo pode desabilitar
   * ou habilitar os botoes de controle de tempo de acordo com o
   * estado do filosofo (parado ou nao).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnParar_Retomar_Filosofo01() {
    if (arrayDeFilosofos[0].isParado()) {// filosofo esta parado
      arrayDeFilosofos[0].retomar();// retoma a execucao da thread
      Platform.runLater(() -> {// atualizacoes na interface grafica
        habilitarBotoesDeVelocidade_Filo01();// habilita os botoes de velocidade do filosofo
        btn_Parar_Retomar_Filo01.setText("Parar");// muda texto exibido no botao
      });// fim das atualizacoes na interface grafica
    } else {// sfilosofo nao esta parado
      arrayDeFilosofos[0].parar();// para a execucao da thread
      Platform.runLater(() -> {// atualizacoes na interface grafica
        desabilitarBotoesDeVelocidade_Filo01();// desabilita os botoes de veloicade do filosofo
        btn_Parar_Retomar_Filo01.setText("Retomar");// muda texto exibido no botao
      });// fim das atualizacoes na interface grafica
    } // fim do bloco if-else
  }// fim do metodo acaoBtnParar_Retomar_Filosofo01

  /* ***************************************************************
   * Metodo: acaoBtnParar_Retomar_Filosofo02
   * Funcao: definir as acoes tomadas de acordo coma as interacoes com o
   * botao "Parar/Retomar" do filosofo. O metodo pode desabilitar
   * ou habilitar os botoes de controle de tempo de acordo com o
   * estado do filosofo (parado ou nao).
   * Parametros: void
   * Retorno: void
   *************************************************************** */ 
  public void acaoBtnParar_Retomar_Filosofo02() {
    if (arrayDeFilosofos[1].isParado()) {// filosofo esta parado
      Platform.runLater(() -> {// atualizacoes na interface grafica
        habilitarBotoesDeVelocidade_Filo02();// habilita os botoes de velocidade do filosofo
        btn_Parar_Retomar_Filo02.setText("Parar");// muda texto exibido no botao
      });// fim das atualizacoes na interface grafica
      arrayDeFilosofos[1].retomar();// retoma a execucao da thread
    } else {// filosofo nao esta parado
      Platform.runLater(() -> {// atualizacoes na interface grafica
        desabilitarBotoesDeVelocidade_Filo02();// desabilita os botoes de veloicade do filosofo
        btn_Parar_Retomar_Filo02.setText("Retomar");// muda texto exibido no botao
      });// fim das atualizacoes na interface grafica
      arrayDeFilosofos[1].parar();// para a execucao da thread
    } // fim do bloco if-else
  }// fim do metodo acaoBtnParar_Retomar_Filosofo02

  /* ***************************************************************
   * Metodo: acaoBtnParar_Retomar_Filosofo03
   * Funcao: definir as acoes tomadas de acordo coma as interacoes com o
   * botao "Parar/Retomar" do filosofo. O metodo pode desabilitar
   * ou habilitar os botoes de controle de tempo de acordo com o
   * estado do filosofo (parado ou nao).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnParar_Retomar_Filosofo03() {
    if (arrayDeFilosofos[2].isParado()) {// filosofo esta parado
      Platform.runLater(() -> {// atualizacoes na interface grafica
        habilitarBotoesDeVelocidade_Filo03();// habilita os botoes de velocidade do filosofo
        btn_Parar_Retomar_Filo03.setText("Parar");// muda texto exibido no botao
      });// fim das atualizacoes na interface grafica
      arrayDeFilosofos[2].retomar();// retoma a execucao da thread
    } else {// filosofo nao esta parado
      Platform.runLater(() -> {// atualizacoes na interface grafica
        desabilitarBotoesDeVelocidade_Filo03();// desabilita os botoes de veloicade do filosofo
        btn_Parar_Retomar_Filo03.setText("Retomar");// muda texto exibido no botao
      });// fim das atualizacoes na interface grafica
      arrayDeFilosofos[2].parar();// para a execucao da thread
    } // fim do bloco if-else
  }// fim do metodo acaoBtnParar_Retomar_Filosofo03

  /* ***************************************************************
   * Metodo: acaoBtnParar_Retomar_Filosofo04
   * Funcao: definir as acoes tomadas de acordo coma as interacoes com o
   * botao "Parar/Retomar" do filosofo. O metodo pode desabilitar
   * ou habilitar os botoes de controle de tempo de acordo com o
   * estado do filosofo (parado ou nao).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnParar_Retomar_Filosofo04() {
    if (arrayDeFilosofos[3].isParado()) {// filosofo esta parado
      Platform.runLater(() -> {// atualizacoes na interface grafica
        habilitarBotoesDeVelocidade_Filo04();// habilita os botoes de velocidade do filosofo
        btn_Parar_Retomar_Filo04.setText("Parar");// muda texto exibido no botao
      });// fim das atualizacoes na interface grafica
      arrayDeFilosofos[3].retomar();// retoma a execucao da thread
    } else {// filosofo nao esta parado
      Platform.runLater(() -> {// atualizacoes na interface grafica
        desabilitarBotoesDeVelocidade_Filo04();// desabilita os botoes de veloicade do filosofo
        btn_Parar_Retomar_Filo04.setText("Retomar");// muda texto exibido no botao
      });// fim das atualizacoes na interface grafica
      arrayDeFilosofos[3].parar();// para a execucao da thread
    } // fim do bloco if-else
  }// fim do metodo acaoBtnParar_Retomar_Filosofo04

  /* ***************************************************************
   * Metodo: acaoBtnParar_Retomar_Filosofo05
   * Funcao: definir as acoes tomadas de acordo coma as interacoes com o
   * botao "Parar/Retomar" do filosofo. O metodo pode desabilitar
   * ou habilitar os botoes de controle de tempo de acordo com o
   * estado do filosofo (parado ou nao).
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void acaoBtnParar_Retomar_Filosofo05() {
    if (arrayDeFilosofos[4].isParado()) {// filosofo esta parado
      Platform.runLater(() -> {// atualizacoes na interface grafica
        habilitarBotoesDeVelocidade_Filo05();// habilita os botoes de velocidade do filosofo
        btn_Parar_Retomar_Filo05.setText("Parar");// muda texto exibido no botao
      });// fim das atualizacoes na interface grafica
      arrayDeFilosofos[4].retomar();// retoma a execucao da thread
    } else {// filosofo nao esta parado
      Platform.runLater(() -> {// atualizacoes na interface grafica
        desabilitarBotoesDeVelocidade_Filo05();// desabilita os botoes de veloicade do filosofo
        btn_Parar_Retomar_Filo05.setText("Retomar");// muda texto exibido no botao
      });// fim das atualizacoes na interface grafica
      arrayDeFilosofos[4].parar();// para a execucao da thread
    } // fim do bloco if-else
  }// fim do metodo acaoBtnParar_Retomar_Filosofo05

  // CONTROLE DE ELEMENTOS DA INTERFACE GRAFICA
  /* ***************************************************************
   * Metodo: exibirInfoReiniciou
   * Funcao: criar e exibir uma janela que informa que a simulacao foi
   * reiniciada.
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void exibirInfoReiniciou() {
    Alert aviso = new Alert(AlertType.INFORMATION);// janela informacao

    VBox caixaDeDialogo = new VBox();/// caixa de dialogo que contem o texto a ser exibido
    caixaDeDialogo.setPrefWidth(500);// largura
    caixaDeDialogo.setPrefHeight(60);// altura
    caixaDeDialogo.getChildren().add(new Label(
        "Por favor, aguarde alguns segundos antes de iniciar uma nova simulacao para"
            + "\nque os filosofos possam se reposicionar na mesa."));
    aviso.getDialogPane().setContent(caixaDeDialogo);// define o conteudo exibido na janela
    aviso.setTitle("Atencao!");// define o o titulo da janela
    aviso.setHeaderText("Simulacao reiniciada com sucesso!");// define o cabecalho da janela
    aviso.show();// exibe a janela
  }// fim do metodo exibirInfoReiniciou

  /* ***************************************************************
   * Metodo: exibirInfoDeControleSimulacao
   * Funcao: criar e exibir uma janela que informa como controlar a
   * simulacao do jantar.
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void exibirInfoDeControleSimulacao() {
    Alert aviso = new Alert(AlertType.INFORMATION);// janela de informacao

    VBox caixaDeDialogo = new VBox();/// caixa de dialogo que contem o texto de informacao
    caixaDeDialogo.setPrefWidth(500);// largura
    caixaDeDialogo.setPrefHeight(150);// altura
    caixaDeDialogo.getChildren().add(new Label(// texto da janela
        "1. Utilize os botoes '+' ou '-' para AUMENTAR ou DIMINUIR o tempo que o filosofo passa"
            + "\nPENSANDO ou COMENDO."
            + "\n2. Cada filosofo pode Pensar ou Comer por no minimo 2 seg e no maximo 8 seg. Quanto"
            + "\nMENOR o tempo, MAIS RAPIDO o filosofo Come ou Pensa."
            + "\n3. Utilize os botoes 'Parar' ou 'Retomar' para parar ou retomar a EXECUCAO do filosofo."
            + "\nObs: Apos fechar esta janela, aguarde alguns segundos para o jantar comecar."
            + "\nBom apetite! ;)"));
    aviso.getDialogPane().setContent(caixaDeDialogo);// define o conteudo da janela
    aviso.setTitle("Atencao!");// define o o titulo da janela
    aviso.setHeaderText("Instrucoes para controlar a simulacao do jantar:");// define o cabecalho
    aviso.showAndWait();// exibe e espera a janela ser fechada para seguir a execucao do programa
  }// fim do metodo exibirInfoDeControleDaSimulacao

  /* ***************************************************************
   * Metodo: habilitarBotoesDeControleDosFilosofos
   * Funcao: habilitar os botoes de controle de cada filosofo para
   * interacoes.
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void habilitarBotoesDeControleDosFilosofos() {
    // habilita os botoes de controle de tempo de cada filosofo para interacoes
    habilitarBotoesDeVelocidade_Filo01();// habilita os botoes para interacoes
    habilitarBotoesDeVelocidade_Filo02();// habilita os botoes para interacoes
    habilitarBotoesDeVelocidade_Filo03();// habilita os botoes para interacoes
    habilitarBotoesDeVelocidade_Filo04();// habilita os botoes para interacoes
    habilitarBotoesDeVelocidade_Filo05();// habilita os botoes para interacoes
    // habilita os botoes "Parar/Retomar" de cada filosofo
    btn_Parar_Retomar_Filo01.setDisable(false);// habilita o botao para interacoes
    btn_Parar_Retomar_Filo02.setDisable(false);// habilita o botao para interacoes
    btn_Parar_Retomar_Filo03.setDisable(false);// habilita o botao para interacoes
    btn_Parar_Retomar_Filo04.setDisable(false);// habilita o botao para interacoes
    btn_Parar_Retomar_Filo05.setDisable(false);// habilita o botao para interacoes
  }// fim do metodo habilitarBotoesDeControleDosFilosofos

  /* ***************************************************************
   * Metodo: desabilitarBotoesDeControleDosFilosofos
   * Funcao: desabilitar os botoes de controle de cada filosofo para
   * interacoes.
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void desabilitarBotoesDeControleDosFilosofos() {
    // desabilita os botoes de controle de tempo de cada filosofo para interacoes
    desabilitarBotoesDeVelocidade_Filo01();// desabilita os botoes para interacoes
    desabilitarBotoesDeVelocidade_Filo02();// desabilita os botoes para interacoes
    desabilitarBotoesDeVelocidade_Filo03();// desabilita os botoes para interacoes
    desabilitarBotoesDeVelocidade_Filo04();// desabilita os botoes para interacoes
    desabilitarBotoesDeVelocidade_Filo05();// desabilita os botoes para interacoes
    // desabilita os botoes "Parar/Retomar" de cada filosofo
    btn_Parar_Retomar_Filo01.setDisable(true);// desabilita o botao para interacoes
    btn_Parar_Retomar_Filo02.setDisable(true);// desabilita o botao para interacoes
    btn_Parar_Retomar_Filo03.setDisable(true);// desabilita o botao para interacoes
    btn_Parar_Retomar_Filo04.setDisable(true);// desabilita o botao para interacoes
    btn_Parar_Retomar_Filo05.setDisable(true);// desabilita o botao para interacoes
  }// fim do metodo desabilitarBotoesDeControleDosFilosofos

  /* ***************************************************************
   * Metodo: habilitarBotoesDeVelocidade_Filo01
   * Funcao: habilitar os botoes de controle de tempo do filosofo 01
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void habilitarBotoesDeVelocidade_Filo01() {
    // botao para aumentar o tempo pensando
    btn_AumentarVelPensar_Filo01.setDisable(false);// habilita o botao para interacoes
    // botao para diminuir o tempo pensando
    btn_DiminuirVelPensar_Filo01.setDisable(false);// habilita o botao para interacoes
    // botao para aumentar o tempo comendo
    btn_AumentarVelComer_Filo01.setDisable(false);// habilita o botao para interacoes
    // botao para diminuir o tempo comendo
    btn_DiminuirVelComer_Filo01.setDisable(false);// habilita o botao para interacoes
  }// fim para habilitarBotoesDeVelocidade_Filo01

  /* ***************************************************************
   * Metodo: habilitarBotoesDeVelocidade_Filo02
   * Funcao: habilitar os botoes de controle de tempo do filosofo 02
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void habilitarBotoesDeVelocidade_Filo02() {
    // botao para aumentar o tempo pensando
    btn_AumentarVelPensar_Filo02.setDisable(false);// habilita o botao para interacoes
    // botao para diminuir o tempo pensando
    btn_DiminuirVelPensar_Filo02.setDisable(false);// habilita o botao para interacoes
    // botao para aumentar o tempo comendo
    btn_AumentarVelComer_Filo02.setDisable(false);// habilita o botao para interacoes
    // botao para diminuir o tempo comendo
    btn_DiminuirVelComer_Filo02.setDisable(false);// habilita o botao para interacoes
  }// fim para habilitarBotoesDeVelocidade_Filo02

  /* ***************************************************************
   * Metodo: habilitarBotoesDeVelocidade_Filo03
   * Funcao: habilitar os botoes de controle de tempo do filosofo 03
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void habilitarBotoesDeVelocidade_Filo03() {
    // botao para aumentar o tempo pensando
    btn_AumentarVelPensar_Filo03.setDisable(false);// habilita o botao para interacoes
    // botao para diminuir o tempo pensando
    btn_DiminuirVelPensar_Filo03.setDisable(false);// habilita o botao para interacoes
    // botao para aumentar o tempo comendo
    btn_AumentarVelComer_Filo03.setDisable(false);// habilita o botao para interacoes
    // botao para diminuir o tempo comendo
    btn_DiminuirVelComer_Filo03.setDisable(false);// habilita o botao para interacoes
  }// fim para habilitarBotoesDeVelocidade_Filo03

  /* ***************************************************************
   * Metodo: habilitarBotoesDeVelocidade_Filo04
   * Funcao: habilitar os botoes de controle de tempo do filosofo 04
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void habilitarBotoesDeVelocidade_Filo04() {
    // botao para aumentar o tempo pensando
    btn_AumentarVelPensar_Filo04.setDisable(false);// habilita o botao para interacoes
    // botao para diminuir o tempo pensando
    btn_DiminuirVelPensar_Filo04.setDisable(false);// habilita o botao para interacoes
    // botao para aumentar o tempo comendo
    btn_AumentarVelComer_Filo04.setDisable(false);// habilita o botao para interacoes
    // botao para diminuir o tempo comendo
    btn_DiminuirVelComer_Filo04.setDisable(false);// habilita o botao para interacoes
  }// fim para habilitarBotoesDeVelocidade_Filo04

  /* ***************************************************************
   * Metodo: habilitarBotoesDeVelocidade_Filo05
   * Funcao: habilitar os botoes de controle de tempo do filosofo 05
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void habilitarBotoesDeVelocidade_Filo05() {
    btn_AumentarVelPensar_Filo05.setDisable(false);// habilita o botao para interacoes
    // botao para diminuir o tempo pensando
    btn_DiminuirVelPensar_Filo05.setDisable(false);// habilita o botao para interacoes
    // botao para aumentar o tempo comendo
    btn_AumentarVelComer_Filo05.setDisable(false);// habilita o botao para interacoes
    // botao para diminuir o tempo comendo
    btn_DiminuirVelComer_Filo05.setDisable(false);// habilita o botao para interacoes
  }// fim para habilitarBotoesDeVelocidade_Filo05

  /* ***************************************************************
   * Metodo: habilitarBotoesDeVelocidade_Filo01
   * Funcao: desabilitar os botoes de controle de tempo do filosofo 01
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void desabilitarBotoesDeVelocidade_Filo01() {
    // botao para aumentar tempo pensando
    btn_AumentarVelPensar_Filo01.setDisable(true);// desabilita o botao para interacoes
    // botao para diminuir tempo pensando
    btn_DiminuirVelPensar_Filo01.setDisable(true);// desabilita o botao para interacoes
    // botao para aumentar tempo comendo
    btn_AumentarVelComer_Filo01.setDisable(true);// desabilita o botao para interacoes
    // botao para diminuir tempo comendo
    btn_DiminuirVelComer_Filo01.setDisable(true);// desabilita o botao para interacoes
  }// fim do metodo desabilitarBotoesDeVelocidade_Filo01

  /* ***************************************************************
   * Metodo: habilitarBotoesDeVelocidade_Filo02
   * Funcao: desabilitar os botoes de controle de tempo do filosofo 02
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void desabilitarBotoesDeVelocidade_Filo02() {// desabilita os botoes de controle de velocidade do filosofo
    // botao para aumentar tempo pensando
    btn_AumentarVelPensar_Filo02.setDisable(true);// desabilita o botao para interacoes
    // botao para diminuir tempo pensando
    btn_DiminuirVelPensar_Filo02.setDisable(true);// desabilita o botao para interacoes
    // botao para aumentar tempo comendo
    btn_AumentarVelComer_Filo02.setDisable(true);// desabilita o botao para interacoes
    // botao para diminuir tempo comendo
    btn_DiminuirVelComer_Filo02.setDisable(true);// desabilita o botao para interacoes
  }// fim do metodo desabilitarBotoesDeVelocidade_Filo02

  /* ***************************************************************
   * Metodo: habilitarBotoesDeVelocidade_Filo03
   * Funcao: desabilitar os botoes de controle de tempo do filosofo 03
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void desabilitarBotoesDeVelocidade_Filo03() {// desabilita os botoes de controle de velocidade do filosofo
    // botao para aumentar tempo pensando
    btn_AumentarVelPensar_Filo03.setDisable(true);// desabilita o botao para interacoes
    // botao para diminuir tempo pensando
    btn_DiminuirVelPensar_Filo03.setDisable(true);// desabilita o botao para interacoes
    // botao para aumentar tempo comendo
    btn_AumentarVelComer_Filo03.setDisable(true);// desabilita o botao para interacoes
    // botao para diminuir tempo comendo
    btn_DiminuirVelComer_Filo03.setDisable(true);// desabilita o botao para interacoes
  }// fim do metodo desabilitarBotoesDeVelocidade_Filo03

  /* ***************************************************************
   * Metodo: habilitarBotoesDeVelocidade_Filo04
   * Funcao: desabilitar os botoes de controle de tempo do filosofo 04
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void desabilitarBotoesDeVelocidade_Filo04() {
    // botao para aumentar tempo pensando
    btn_AumentarVelPensar_Filo04.setDisable(true);// desabilita o botao para interacoes
    // botao para diminuir tempo pensando
    btn_DiminuirVelPensar_Filo04.setDisable(true);// desabilita o botao para interacoes
    // botao para aumentar tempo comendo
    btn_AumentarVelComer_Filo04.setDisable(true);// desabilita o botao para interacoes
    // botao para diminuir tempo comendo
    btn_DiminuirVelComer_Filo04.setDisable(true);// desabilita o botao para interacoes
  }// fim do metodo desabilitarBotoesDeVelocidade_Filo04

  /* ***************************************************************
   * Metodo: habilitarBotoesDeVelocidade_Filo05
   * Funcao: desabilitar os botoes de controle de tempo do filosofo 05
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void desabilitarBotoesDeVelocidade_Filo05() {
    btn_AumentarVelPensar_Filo05.setDisable(true);// desabilita o botao para interacoes
    // botao para diminuir tempo pensando
    btn_DiminuirVelPensar_Filo05.setDisable(true);// desabilita o botao para interacoes
    // botao para aumentar tempo comendo
    btn_AumentarVelComer_Filo05.setDisable(true);// desabilita o botao para interacoes
    // botao para diminuir tempo comendo
    btn_DiminuirVelComer_Filo05.setDisable(true);// desabilita o botao para interacoes
  }// fim do metodo desabilitarBotoesDeVelocidade_Filo05

  /* ***************************************************************
   * Metodo: habilitarBtnReiniciar
   * Funcao: habilitar e exibir o botao "Reiniciar Simulacao" para
   * interacoes.
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void habilitarBtnReiniciar() {
    btn_Reiniciar.setDisable(false);// habilita o botao
    btn_Reiniciar.setOpacity(1);// exibe o botao
  }// fim do metodo habilitarBtnReiniciar

  /* ***************************************************************
   * Metodo: habilitarBtnReiniciar
   * Funcao: desbilitar e ocultar o botao "Reiniciar Simulacao" para
   * interacoes.
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void desabilitarBtnReiniciar() {
    btn_Reiniciar.setDisable(true);// desabilita o botao
    btn_Reiniciar.setOpacity(0);// oculta o botao
  }// fim do metodo desabilitarBtnReiniciar

  /* ***************************************************************
   * Metodo: habilitarBtnIniciar
   * Funcao: habilitar e exibir o botao "Iniciar Simulacao" para
   * interacoes.
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void habilitarBtnIniciar() {
    btn_Iniciar.setDisable(false);// habilita o botao
    btn_Iniciar.setOpacity(1);// exibe o botao
  }// fim do metodo habilitarBtnIniciar

  /* ***************************************************************
   * Metodo: desabilitarBtnIniciar
   * Funcao: desabilitar e ocultar o botao "Iniciar Simulacao" para
   * interacoes.
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void desabilitarBtnIniciar() {
    btn_Iniciar.setDisable(true);// desabilita o botao
    btn_Iniciar.setOpacity(0);// exibe o botao
  }// fim do metodo desabilitarBtnIniciar

  /* ***************************************************************
   * Metodo: reiniciarBotoesParar_Retomar
   * Funcao: mudar o texto exibido no painel de cada filofo para
   * "Parado.".
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void reiniciarBotoesParar_Retomar() {
    // muda o texto exibido no botao de cada filosofo
    btn_Parar_Retomar_Filo01.setText("Parar");
    btn_Parar_Retomar_Filo02.setText("Parar");
    btn_Parar_Retomar_Filo03.setText("Parar");
    btn_Parar_Retomar_Filo04.setText("Parar");
    btn_Parar_Retomar_Filo05.setText("Parar");
  }// fim do metodo reiniciarBotoesParar_Retomar

  /* ***************************************************************
   * Metodo: reiniciarArrayDeGarfos
   * Funcao: tornar todos os garfos visiveis para uma nova simulacao
   * Parametros: void
   * Retorno: void
   *************************************************************** */
  public void reiniciarArrayDeGarfos() {
    for (int i = 0; i < arrayImgvGarfos.length; i++) {
      arrayImgvGarfos[i].setVisible(true);// torna o garfo visivel
    } // fim do for
  }// fim do metodo reiniciarArrayDeGarfos

  // METODOS GETTERS QUE RETORNAM OS ELEMENTOS UTILIZADOS PARA INICIAR OS OBJETOS
  // "FILOSOFO"

  /* ***************************************************************
   * Metodo: getImageViewFilosofo
   * Funcao: retornar a camada de imagem correnspondente ao filosofo
   * na tela.
   * Parametros: id = filosofo que chamou o metodo
   * Retorno: ImageView = camada de imagem correspondente ao filosofo
   *************************************************************** */
  public ImageView getImageViewFilosofo(int id) {
    return (arrayImgvFilosofos[id]);// retorna a camada de imagem correspondente ao filosofo "id"
  }// fim do metodo getImageViewFilosofo

  /* ***************************************************************
   * Metodo: getGarfoEsquerdo
   * Funcao: retornar a camada de imagem correnspondente ao garfo que
   * esta ESQUERDA do filosofo "id".
   * Parametros: id = filosofo que chamou o metodo
   * Retorno: ImageView = camada de imagem correspondente ao garfo
   * ESQUERDO.
   *************************************************************** */
  public ImageView getGarfoEsquerdo(int id) {
    return (arrayImgvGarfos[id]);// retorna o ImageView que corresponde ao garfo do filosofo
  }// fim do metodo getGarfoEsquerdo

  /* ***************************************************************
   * Metodo: getGarfoEsquerdo
   * Funcao: retornar a camada de imagem correnspondente ao garfo que
   * esta DIREITA do filosofo "id".
   * Parametros: id = filosofo que chamou o metodo
   * Retorno: ImageView = camada de imagem correspondente ao garfo
   * DIREITO.
   *************************************************************** */
  public ImageView getGarfoDireito(int id) {
    return (arrayImgvGarfos[(id + 4) % N_Filosofos]);// retorna o ImageView que corresponde ao garfo do filosofo
  }// fim do metodo getGarfoDireito

  /* ***************************************************************
   * Metodo: getGarfoEsquerdo
   * Funcao: retorna o id do filosofo que esta sentado a ESQUERDA do
   * filosofo "id".
   * Parametros: id = filosofo que chamou o metodo
   * Retorno: int = id do vizinho da ESQUERDA.
   *************************************************************** */
  public int getVizinhoDaEsquerda(int id) {
    return ((id + 4) % N_Filosofos);// calcula e retorna o id do vizinho
  }// fim do metodo getVizinhoDaEsquerda

  /* ***************************************************************
   * Metodo: getGarfoEsquerdo
   * Funcao: retorna o id do filosofo que esta sentado a DIREITA do
   * filosofo "id".
   * Parametros: id = filosofo que chamou o metodo
   * Retorno: int = id do vizinho da DIREITA.
   *************************************************************** */
  public int getVizinhoDaDireita(int id) {
    return ((id + 1) % N_Filosofos);// calcula e retorna o id do vizinho
  }// fim do metodo getVizinhoDaDireita

  /* ***************************************************************
   * Metodo: getImagemPensando
   * Funcao: retorna a imagem correspondente ao filosofo "id" PENSANDO.
   * Parametros: id = filosofo que chamou o metodo
   * Retorno: Image = imagem do filosofo PENSANDO.
   *************************************************************** */
  public Image getImagemPensando(int id) {
    Image imagemPensando = null;// instancia para a imagem que sera retornada
    switch (id) {// retorna a do filosofo "PENSANDO" de acordo com o id
      case 0: {
        imagemPensando = new Image("img/filosofo01_pensando.png");// atribui o caminho da imagem
        break;
      } // fim do caso 0
      case 1: {
        imagemPensando = new Image("img/filosofo02_pensando.png");// atribui o caminho da imagem
        break;
      } // fim do caso 1
      case 2: {
        imagemPensando = new Image("img/filosofo03_pensando.png");// atribui o caminho da imagem
        break;
      } // fim do caso 2
      case 3: {
        imagemPensando = new Image("img/filosofo04_pensando.png");// atribui o caminho da imagem
        break;
      } // fim do caso 3
      case 4: {
        imagemPensando = new Image("img/filosofo05_pensando.png");// atribui o caminho da imagem
        break;
      } // fim do caso 4
    }// fim do switch
    return (imagemPensando);
  }// fim do metodo getImagemPensando

  /* ***************************************************************
   * Metodo: getImagemComFome
   * Funcao: retorna a imagem correspondente ao filosofo "id" COM_FOME.
   * Parametros: id = filosofo que chamou o metodo
   * Retorno: Image = imagem do filosofo COM_FOME.
   *************************************************************** */
  public Image getImagemComFome(int id) {
    Image imagemComFome = null;// instancia para a imagem que sera retornada
    switch (id) {// retorna a imagem filosofo "COM_FOME" de acordo com o id
      case 0: {
        imagemComFome = new Image("img/filosofo01_com_fome.png");// atribui o caminho da imagem
        break;
      } // fim do caso 0
      case 1: {
        imagemComFome = new Image("img/filosofo02_com_fome.png");// atribui o caminho da imagem
        break;
      } // fim do caso 1
      case 2: {
        imagemComFome = new Image("img/filosofo03_com_fome.png");// atribui o caminho da imagem
        break;
      } // fim do caso 2
      case 3: {
        imagemComFome = new Image("img/filosofo04_com_fome.png");// atribui o caminho da imagem
        break;
      } // fim do caso 3
      case 4: {
        imagemComFome = new Image("img/filosofo05_com_fome.png");// atribui o caminho da imagem
        break;
      } // fim do caso 4
    }// fim do switch
    return (imagemComFome);
  }// fim do metodo getImagemComFome

  /* ***************************************************************
   * Metodo: getImagemComendo
   * Funcao: retorna a imagem correspondente ao filosofo "id" COMENDO.
   * Parametros: id = filosofo que chamou o metodo
   * Retorno: Image = imagem do filosofo COMENDO.
   *************************************************************** */
  public Image getImagemComendo(int id) {
    Image imagemComendo = null;// instancia para a imagem que sera retornada
    switch (id) {// retorna a imagem filosofo "COMENDO" de acordo com o id
      case 0: {
        imagemComendo = new Image("img/filosofo01_comendo.png");// atribui o caminho da imagem
        break;
      } // fim do caso 0
      case 1: {
        imagemComendo = new Image("img/filosofo02_comendo.png");// atribui o caminho da imagem
        break;
      } // fim do caso 1
      case 2: {
        imagemComendo = new Image("img/filosofo03_comendo.png");// atribui o caminho da imagem
        break;
      } // fim do caso 2
      case 3: {
        imagemComendo = new Image("img/filosofo04_comendo.png");// atribui o caminho da imagem
        break;
      } // fim do caso 3
      case 4: {
        imagemComendo = new Image("img/filosofo05_comendo.png");// atribui o caminho da imagem
        break;
      } // fim do caso 4
    }// fim do switch
    return (imagemComendo);
  }// fim do metodo getImagemComFome

  /* ***************************************************************
   * Metodo: getPainelDeInfo
   * Funcao: retorna a camada de texto que exibe os tempos
   * correspondentes ao filosofo "id" em cada estado.
   * Parametros: id = filosofo que chamou o metodo
   * Retorno: Label = camada de texto que exibe as infos do filosofo.
   *************************************************************** */
  public Label getPainelDeInfo(int id) {
    return arrayLblPaineisDeInfo[id];// retorna a label correspondente
  }// fim do metodo getPainelDeInfo

}// fim da classe ControleTelaPrincipal