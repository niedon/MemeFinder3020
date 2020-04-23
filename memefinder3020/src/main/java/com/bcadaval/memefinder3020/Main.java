package com.bcadaval.memefinder3020;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bcadaval.memefinder3020.concurrencia.TaskSplash;
import com.bcadaval.memefinder3020.controlador.SplashControlador;
import com.bcadaval.memefinder3020.utils.Constantes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@SpringBootApplication
public class Main extends Application {
	
    public static void main(String[] args) {
    	launch(args);
    }
    
	@Override
	public void start(Stage stage) throws Exception {
		
		Stage carga = new Stage(StageStyle.UNDECORATED);
		
		TaskSplash ts = new TaskSplash(stage, carga);
		SplashControlador con = new SplashControlador();
		FXMLLoader load = new FXMLLoader(getClass().getResource(String.format(Constantes.RUTA_FXML_RFE, Constantes.NOMBRE_PANTALLA_SPLASH)));
		load.setController(con);
		carga.setScene(new Scene(load.load()));
		carga.show();
		con.asignarBindings(ts.messageProperty(),ts.progressProperty());
		
		new Thread(ts, "Carga").start();
		
	}
	
}
