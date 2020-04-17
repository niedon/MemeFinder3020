package com.bcadaval.memefinder3020;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.bcadaval.memefinder3020.serviciostest.IntegracionImagenBusqueda;
import com.bcadaval.memefinder3020.serviciostest.IntegracionImagenCategoria;
import com.bcadaval.memefinder3020.serviciostest.ServicioCategoriaTest;
import com.bcadaval.memefinder3020.serviciostest.ServicioEtiquetaTest;
import com.bcadaval.memefinder3020.serviciostest.ServicioImagenTest;

@RunWith(Suite.class)
@SuiteClasses({
	ServicioEtiquetaTest.class,
	ServicioCategoriaTest.class,
	ServicioImagenTest.class,
	IntegracionImagenBusqueda.class,
	IntegracionImagenCategoria.class
	})
public class AppTest {
	public static final String RUTA_TESTIMAGENES_RF = "/testimg/%s";
}
