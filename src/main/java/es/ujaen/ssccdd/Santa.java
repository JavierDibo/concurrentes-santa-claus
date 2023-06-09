package es.ujaen.ssccdd;

import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Santa implements Runnable {
    private final Semaphore descansoSanta;
    private final Semaphore exm;
    private final Semaphore repartoRegalos;
    private final Semaphore esperarAyuda;
    private final AtomicInteger regresoVacaciones;
    private AtomicInteger duendesConProblemas;

    /**
     * Constructor de la clase Santa.
     *
     * @param descansoSanta       semaforo utilizado para que Santa descanse hasta que se active.
     * @param exm                 semaforo utilizado para garantizar la exclusion mutua en la variable compartida regresoVacaciones.
     * @param repartoRegalos      semaforo utilizado para que Santa inicie el reparto de regalos una vez que esten preparados.
     * @param esperarAyuda        semaforo utilizado por los duendes para solicitar ayuda de Santa.
     * @param regresoVacaciones   contador atomico que lleva la cuenta de cuantos renos han regresado de sus vacaciones.
     * @param duendesConProblemas contador atomico que lleva la cuenta de cuantos duendes necesitan ayuda.
     */
    public Santa(Semaphore descansoSanta, Semaphore exm, Semaphore repartoRegalos, Semaphore esperarAyuda, AtomicInteger regresoVacaciones, AtomicInteger duendesConProblemas) {
        this.descansoSanta = descansoSanta;
        this.exm = exm;
        this.repartoRegalos = repartoRegalos;
        this.esperarAyuda = esperarAyuda;
        this.regresoVacaciones = regresoVacaciones;
        this.duendesConProblemas = duendesConProblemas;
    }

    /**
     * Metodo run: implementacion de la interfaz Runnable
     * El metodo se encarga de coordinar el comportamiento de Santa con otros procesos concurrenes
     * En caso de que todos los renos hayan regresado de vacaciones, Santa prepara el trineo y reparte regalos
     * En caso contrario, Santa prepara la ayuda para los duendes que lo necesiten
     * Tabmien se encarga de ayudar a los duendes que lo soliciten
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                descansoSanta.acquire();

                boolean regalos;

                exm.acquire();
                if (regresoVacaciones.get() == Practica1.NUM_RENOS) {
                    regalos = true;
                    regresoVacaciones.set(0);
                } else {
                    regalos = false;
                }
                exm.release();

                if (regalos) {
                    prepararTrineo();
                    for (int i = 0; i < Practica1.NUM_RENOS; i++) {
                        repartoRegalos.release();
                    }
                    repartirRegalos();
                } else {
                    prepararAyuda();
                    for (int i = 0; i < Practica1.NUM_DUENDES; i++)
                        esperarAyuda.release();
                }
                resolverAyuda();
            }
        } catch (InterruptedException e) {
            System.out.println("(Hilo SANTA) ha sido interrumpido");
        }
    }

    /**
     * Metodo privado que simula la preparacion del trineo por parte de Santa
     */
    private void prepararTrineo() throws InterruptedException {
        System.out.println("(Hilo SANTA) esta preparando el trineo");
        TimeUnit.SECONDS.sleep(TIEMPO_PREPARAR_TRINEO);
    }

    /**
     * Metodo privado que simula el reparto de regalos por parte de Santa y los renos
     */
    private void repartirRegalos() throws InterruptedException {
        System.out.println("(Hilo SANTA) se estan repartiendo regalos");
        TimeUnit.SECONDS.sleep(TIEMPO_REPARTIR_REGALOS);
    }

    /**
     * Metodo privado que simula la preparacion de la ayuda por parte de Santa
     */
    private void prepararAyuda() throws InterruptedException {
        System.out.println("(Hilo SANTA) esta preparando la ayuda para los duendes");
        TimeUnit.SECONDS.sleep(TIEMPO_PREPARAR_AYUDA);
    }

    /**
     * Metodo privado que simula la ayuda de Santa a los duendes que lo necesiten
     */
    private void resolverAyuda() throws InterruptedException {
        System.out.println("(Hilo SANTA) esta ayudando a los duendes");
        TimeUnit.SECONDS.sleep(TIEMPO_AYUDAR_DUENDES);
    }

    // Segundos
    private static final int TIEMPO_AYUDAR_DUENDES = 2;
    private static final int TIEMPO_PREPARAR_AYUDA = 1;
    private static final int TIEMPO_REPARTIR_REGALOS = 2;
    private static final int TIEMPO_PREPARAR_TRINEO = 1;
}