
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
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/* ****************************************************************
* Classe: Principal
* Funcao: Configuracao e inicializacao da aplicacao JavaFx
* Descricao: Contem o metodo "main" e "start" responsaveis 
*            configurar e excutar a aplicacao JavaFx.
***************************************************************** */
public class Principal extends Application {
  public static void main(String[] args) throws Exception {
    launch(args);// inicia a aplicacao JavaFX
  }// fim do metodo main

  /* ***************************************************************
   * Metodo: start
   * Funcao: Configurar os elementos contidos na interface grafica
   * e iniciar a aplicacao.
   * Parametros: Stage janelaPrincipal = janela que sera configurada
   * pelo metodo
   * Retorno: void
   *************************************************************** */

  @Override
  public void start(Stage janelaPrincipal) throws Exception {

    // cria uma instancia da classe "ControleTelaPrincipal" que sera controladora da
    // janela
    ControleTelaPrincipal controleTelaPrincipal = new ControleTelaPrincipal();

    // instancia o fxmlLoader e define a localizacao do arquivo .fxml que contem os
    // elementos graficos que serao associados a janela
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("TelaPrincipal.fxml"));
    fxmlLoader.setController(controleTelaPrincipal);// define a classe controladora da janela

    Parent root = fxmlLoader.load();// carrega a estrutura grafica da interface
    Scene tela = new Scene(root);// cria a cena e define a estrutura grafica que sera exibida por ela

    Image iconeJanela = new Image("img/icone_janela.png");// instancia a imagem icone da janela
    janelaPrincipal.getIcons().add(iconeJanela);// define a o icone da janela
    janelaPrincipal.setTitle("Trabalho 03 - Jantar dos Filósofos");// define o titulo da janela
    janelaPrincipal.setResizable(false);// impede que a janela seja redmensionada durante a execucao
    janelaPrincipal.centerOnScreen();// centraliza a exibicao da janela durante a execucao

    janelaPrincipal.setScene(tela);// define a cena que sera exibida na tela
    janelaPrincipal.show();// exibe a cena
  }// fim do metodo start

}// fim da classe Principal