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
En Eclipse, botón derecho sobre el proyecto. Run as -> Maven build... -> escribir "package" en Goals y seleccionar recuadro de "Skip Tests"-> Run.
En la carpeta /target/ de donde tengas el proyecto se habrá generado un jar.
### Eliminación
Si lo has ejecutado, en tu carpeta de usuario se habrá creado una carpeta llamada /memefinder3020/. Ahí se guardan todos los datos del programa y las imágenes. Con borrar la carpeta es suficiente.

## Tecnologías usadas
* [SpringBoot](https://spring.io/projects/spring-boot) 2 - Inversión de control
* [Hibernate](https://hibernate.org/) 5 - Mapeo relacional y servicios
* [HSQLDB](http://hsqldb.org/) - Base de datos
* [JavaFX](https://openjfx.io/) 8 - Para las vistas y controladores
* [JUnit](https://junit.org/junit5/) 4 - Test

## Próximas funcionalidades
* Administración de etiquetas
* Ajustes generales
* Exportar/Importar conjuntos de imágenes
* Análisis profundo de imágenes
* Sugerencia de etiquetas en búsquedas
* Estadísticas
* Más estilos
* Nueva disposición de los resultados de búsqueda


## Autor
[Basilio Cadaval Rodríguez](https://www.linkedin.com/in/bcadaval/).