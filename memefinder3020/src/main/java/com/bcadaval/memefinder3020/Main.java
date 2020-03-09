package com.bcadaval.memefinder3020;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.bcadaval.memefinder3020.controlador.InicioControlador;
import com.bcadaval.memefinder3020.principal.GestorDeVentanas;
import com.bcadaval.memefinder3020.principal.SpringFxmlLoader;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
public class Main extends Application {
	
	@Autowired
	private ApplicationContext ctx;
	
    public static void main(String[] args) {
    	try {
    		launch(args);
		} catch (Exception e) {
			Throwable t = e;
			while(true) {
				System.out.println(e.getCause());
				System.out.println(e.getMessage());
				System.out.println("-------");
				if(t.getCause()==null)break;
				else t=t.getCause();
			}
			System.out.println("------fin");
		}
        
    }
    
    

	@Override
	public void init() throws Exception {
		ctx = SpringApplication.run(Main.class);
	}



	@Override
	public void start(Stage stage) throws Exception {

		ctx.getBean(SpringFxmlLoader.class).cargaVistas(stage);
		
		Scene escena = new Scene((Parent) ctx.getBean(InicioControlador.class).getVista(), 1024, 768);
		stage.setScene(escena);
		
		ctx.getBean(GestorDeVentanas.class).setEscena(escena);
		
		stage.show();
		
	}
	
}
