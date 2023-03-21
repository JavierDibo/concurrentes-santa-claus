[![logo](https://www.gnu.org/graphics/gplv3-127x51.png)](https://choosealicense.com/licenses/gpl-3.0/)
# Primera Práctica
Para la resolución de esta primera práctica se utilizará como herramienta de concurrencia los semáforos. Para el análisis y diseño el uso de semáforos es como se ha presentado en las clases de teoría. Para la implementación se hará uso de la clase [`Semaphore`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Semaphore.html) de JAVA. Hay diferentes ejemplos en este [guión](https://gitlab.com/ssccdd/materialadicional/-/blob/master/README.md) donde se demuestra el uso general de la clase. Para la implementación se se utilizará la factoría [`Executors`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Executors.html) y la interface [`ExecutorService`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/ExecutorService.html) para la ejecución de las tareas concurrentes que compondrán la solución de la práctica.

## Problema a resolver
Santa Claus pasa su tiempo de descanso, durmiendo, en su casa del Polo Norte. Para poder despertarlo, se ha de cumplir una de las dos condiciones siguientes:

1. Que todos los renos de los que dispone, nueve en total, hayan vuelto de vacaciones.
2.  Que algunos de sus duendes necesiten su ayuda para fabricar un juguete.

Para permitir que Santa Claus pueda descansar, los duendes han acordado despertarle si tres de ellos tienen problemas a la hora de fabricar un juguete. En el caso de que un grupo de tres duendes estén siendo ayudados por Santa, el resto de los duendes con problemas tendrán que esperar a que Santa termine de ayudar al primer grupo.

En caso de que haya duendes esperando y todos los renos hayan vuelto de vacaciones, entonces Santa Claus decidirá preparar el trineo y repartir los regalos, ya que su entrega es más importante que la fabricación de otros juguetes que podría esperar al año siguiente. El último reno en llegar ha de despertar a Santa mientras el resto de renos esperan antes de ser enganchados al trineo.

Para **solucionar** este problema hay que definir los siguientes procesos: 
- Santa Claus
- Duende
- Reno. 
- Hilo Principal para la realización de la prueba.

*Ayuda* : para los eventos de sincronización, será necesario disponer de mecanismos para despertar a Santa Claus, notificar a los renos que se han de enganchar al trineo y controlar la espera por parte de los duendes cuando otro grupo de duendes esté siendo ayudado por Santa Claus.

## Para la implementación

Deberán definirse tiempos que simulen las operaciones que tienen que hacer los diferentes procesos implicados en el problema. También deberá definirse un tiempo para la finalización de la prueba. Hay que mostrar información en la consola para seguir la ejecución de los procesos.

Los procesos que representan a los renos y a los duendes deberán estar en un ciclo infinito realizando su operación para no tener que crear procesos adicionales a los iniciales.

El hilo principal deberá crear un número de duendes y renos que estarán definidos en las constantes. 

Además debe implementarse un proceso de finalización que se lanzará pasado un tiempo que estará definido en las constantes. Por tanto todos los procesos presentes en la solución deberán implementar su interrupción como el método de finalización.

Para la sincronización de los diferentes procesos tenemos que tener presente lo siguiente:

- Para los renos utilizaremos un método vueltaVacaciones() que simulará un tiempo variable para cada reno y que vendrá controlado por una constante.
- Para los duende utilizaremos un método crearJuguete() que lanzará la excepción ProblemasException(), definida en el proyecto, para avisar a Santa que necesita ayuda.

## Solución


### Constantes

Para la solución del problema tenemos que tener presentes las siguientes constantes.

    NUM_RENOS : es la cantidad de renos que dispone Santa para repartir los regalos

    NUM_DUENDES : es el número de duendes con problemas para avisar a Santa
    
    AVISO : {REGALOS, PROBLEMAS} : permite priorizar a Santa el tipo de aviso 

### Análisis

El problema consiste en sincronizar correctamente los tres procesos implicados en el problema: Santa, Renos, Duendes.  Para ello tenemos los siguientes elementos de sincronización:

- *El Reno vuelve de vacaciones* : En esta condición se deben contabilizar los Renos que han vuelto de vacaciones. El último Reno que vuelve de vacaciones deberá avisar a Santa que están disponibles para el reparto de los regales. Para ello deberán contabilizarse en una variable compartida y así comprobar quién es el último y que deberá avisar a Santa.
- *Esperar a repartir los regalos* : Lo Renos esperan a que Santa monte el trineo y está disponible con todos los Renos para repartir los regalos.
- *Duendes con problemas* : Si un número establecido de Duendes tienen problemas hay que avisar a Santa para que los ayude. Se necesita una variable compartida para llevar la cuenta de los Duendes que tienen problemas para que el que se compute como el último avise a Santa.
- *Esperar a Santa* : Los Duendes deberán esperar a Santa a que les ayude a resolver los problemas. Una vez que esto ocurra deberán ir computando individualmente que han sido ayudados y así permitir que otros Duendes puedan recibir ayuda si la necesitan.
- *Esperar ayuda* : Si un grupo de Duendes están recibiendo ayuda de Santa los duendes tienen que esperar a que se complete la operación antes de que puedan ello solicitar ayuda a Santa.
- *Descanso de Santa* : Si los Renos no han vuelto de vacaciones o no se ha alcanzado el número requerido de Duendes con problemas Santa estará descansando.

Como restricción adicional Santa dará prioridad al reparto de los regalos antes de resolver problemas a los Duendes.

### Variables compartidas

Por el análisis de las condiciones de sincronización van a ser necesarias dos variables compartidas para que los Renos y Duendes puedan establecer sus condiciones de sincronización de forma apropiada:

    regresoVacaciones : número de Renos que han vuelto de vacaciones
    duendesConProblemas : número de duendes que tienen problemas hasta el momento

### Semáforos

Como herramienta de concurrencia para resolver el problema serán necesarios definir diferentes semáforos que garantices las condiciones de sincronización y el uso seguro de las variables compartidas.

- `exm` : Semáforo de exclusión mutua para garantizar el acceso a las variables compartidas. Estará inicializado a 1
- `repartoRegalos` :  El semáforo controla que los Renos esperan a Santa antes de empezar el reparto de los regalos. Este semáforo estará inicializado a 0.
- `esperarAyuda` : El semáforo controla que los Duendes con problemas esperan a Santa para recibir la ayuda que necesitan. Estará inicializado a 0.
- `esperaDuende` : El semáforo controla si un grupo establecido de duendes está recibiendo ayuda de Santa. Este semáforo se inicializa a 1.
- `descansoSanta` : El semáforo controla el descanso de Santa y hasta que los Renos no hayan vuelto de vacaciones o un grupo de Duendes tenga problemas Santa estará descansando. El semáforo estará inicializado a 0.

### Diseño

Para simular las operaciones de los diferentes proceso se utilizan los siguientes métodos:

- `vacaciones()` : Este método simula el tiempo que el Reno está de vacaciones.
- `prepararTrineo()` : Este método simula las operaciones necesarias que tendrá que realizar Santa antes de empezar el reparto de los regalos.
- `repartirRegalos()` : Este método estará disponible para Santa y los Renos para simular que están repartiendo los regalos.
- `hacerJuguete()` : Este método simula el trabajo de un Duende haciendo juguetes.
- `prepararAyuda()` : Este método simula las operaciones que hace Santa antes de prestar la ayuda a los duendes.
- `resolverAyuda()` : Este método estará disponible para Santa y los Duendes para completar la ayuda que cada uno de ellos precisa.

El diseño de los diferentes proceso será el siguientes:

#### Proceso Santa
	Variables
		AVISO aviso

Ejecución :
```
while ( Hasta ser interrumpido ) {
	descansoSanta.wait()

	// Resolver el aviso
	exm.wait()
	if ( regresoVacaciones = NUM_RENOS ) {
		aviso = REGALOS
		regresoVacaciones = NINGUNO
	} else {
		aviso = PROBLEMAS
	}
	exm.signal()

	if ( aviso = REGALOS ) {
		prepararTrineo()
		for( i = 0; i < NUM_RENOS; i++ )
			repartoRegalos.signal()
		repartirRegalos()
	} else {
		prepararAyuda()
		for( i = 0; i < NUM_DUENDES; i++ )
			esperarAyuda.signal()
		resolverAyuda()
	}
}
```

#### Proceso Reno(id)

Ejecución :
```
while ( Hasta ser interrumpido ) {
	vacaciones()
	
	exm.wait()
	regresoVacaciones++
	if ( regrosoVacaciones = NUM_RENOS )
		descansoSanta.signal() // Se avisa a Santa
	exm.signal()

	// Se espera a Santa
	repartoRegalos.wait()
	repartirRegalos()
}
```

#### Proceso Duende(id)

Ejecución :
```
while ( Hasta ser interrumpido ) {
	hacerJuguete()
	
	esperaDuende.wait()
	exm.wait()
	duendesConProblemas++
	if ( duendesConProblemas = NUM_DUENDES )
		descansoSanta.signal() // Se avisa a Santa
	else
		esperaDuende.signal()
	exm.signal()

	esperarAyuda.wait()
	resolverAyuda()

	// Ya se ha completado la ayuda de Santa
	exm.wait()
	duendesConProblemas--
	if ( duendesConProblemas = NINGUNO )
		esperarDuende.signal()
	exm.wait()
}
``` 

 
