# MemeFinder3020
Programa de clasificaci�n y b�squeda de im�genes
## Descripci�n
Aplicaci�n de escritorio para gestionar im�genes. Puedes a�adir, clasificarlas (con etiquetas y en categor�as), localizar duplicados o im�genes similares, o buscar por distintas caracter�sticas.

## Imagen del programa
![Pantallazo del programa](https://user-images.githubusercontent.com/36060286/80253251-382f2500-8679-11ea-919b-5f1c568a42f6.png)

## Uso del programa
### Prueba
Descargar o clonar repositorio e importar como proyecto Maven.
### Compilaci�n
En Eclipse, bot�n derecho sobre el proyecto. Run as -> Maven build...-> Run.
En la carpeta /target/ de donde tengas el proyecto se habr� generado un jar.
### Eliminaci�n
Si lo has ejecutado, en tu carpeta de usuario se habr� creado una carpeta llamada /memefinder3020/. Ah� se guardan todos los datos del programa y las im�genes. Con borrar la carpeta es suficiente.

### Posibles errores
* COMPILATION ERROR: No compiler is provided in this environment. Perhaps you are running on a JRE rather than a JDK?
En [esta respuesta](https://stackoverflow.com/a/21279068/11835818) se puede solucionar.

## Tecnolog�as usadas
* [SpringBoot](https://spring.io/projects/spring-boot) 2 - Inversi�n de control
* [Hibernate](https://hibernate.org/) 5 - Mapeo relacional y servicios
* [HSQLDB](http://hsqldb.org/) - Base de datos
* [JavaFX](https://openjfx.io/) 8 - Para las vistas y controladores
* [JUnit](https://junit.org/junit5/) 4 - Test

## Pr�ximas funcionalidades
* Ajustes generales
* Exportar/Importar conjuntos de im�genes
* Soporte de miniaturas (tumbnails)
* An�lisis profundo de im�genes
* Sugerencia de etiquetas en b�squedas
* Estad�sticas
* M�s estilos
* Nueva disposici�n de los resultados de b�squeda
* Soporte de *Drag & Drop*
* ~~Administraci�n de etiquetas~~


## Autor
[Basilio Cadaval Rodr�guez](https://www.linkedin.com/in/bcadaval/).