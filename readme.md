# MemeFinder3020
Programa de clasificación y búsqueda de imágenes
## Descripción
Aplicación de escritorio para gestionar imágenes. Puedes añadir, clasificarlas (con etiquetas y en categorías), localizar duplicados o imágenes similares, o buscar por distintas características.

## Imagen del programa
![Pantallazo del programa](https://user-images.githubusercontent.com/36060286/80253251-382f2500-8679-11ea-919b-5f1c568a42f6.png)

## Uso del programa
### Prueba
Descargar o clonar repositorio e importar como proyecto Maven.
### Compilación
En Eclipse, botón derecho sobre el proyecto. Run as -> Maven build...-> Run.
En la carpeta /target/ de donde tengas el proyecto se habrá generado un jar.
### Eliminación
Si lo has ejecutado, en tu carpeta de usuario se habrá creado una carpeta llamada /memefinder3020/. Ahí se guardan todos los datos del programa y las imágenes. Con borrar la carpeta es suficiente.

### Posibles errores
* COMPILATION ERROR: No compiler is provided in this environment. Perhaps you are running on a JRE rather than a JDK?
En [esta respuesta](https://stackoverflow.com/a/21279068/11835818) se puede solucionar.

## Tecnologías usadas
* [SpringBoot](https://spring.io/projects/spring-boot) 2 - Inversión de control
* [Hibernate](https://hibernate.org/) 5 - Mapeo relacional y servicios
* [HSQLDB](http://hsqldb.org/) - Base de datos
* [JavaFX](https://openjfx.io/) 8 - Para las vistas y controladores
* [JUnit](https://junit.org/junit5/) 4 - Test

## Próximas funcionalidades
* Ajustes generales
* Exportar/Importar conjuntos de imágenes
* Soporte de miniaturas (tumbnails)
* Análisis profundo de imágenes
* Sugerencia de etiquetas en búsquedas
* Estadísticas
* Más estilos
* Nueva disposición de los resultados de búsqueda
* Soporte de *Drag & Drop*
* ~~Administración de etiquetas~~


## Autor
[Basilio Cadaval Rodríguez](https://www.linkedin.com/in/bcadaval/).