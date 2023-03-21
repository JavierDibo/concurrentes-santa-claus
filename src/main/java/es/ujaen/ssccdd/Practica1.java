package es.ujaen.ssccdd;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Resolucion de la primera practica basandose en el analisis proporcionado por el profesor
 *
 * @author Javier Francisco Dibo Gómez
 */
public class Practica1 {

    public static void main(String[] args) {

        System.out.println("(Hilo PRINCIPAL) ha comenzado");

        // Inicializacion de variables

        ExecutorService executor = Executors.newFixedThreadPool(NUM_RENOS + NUM_DUENDES + SANTA);

        Semaphore exm = new Semaphore(1);
        Semaphore repartoRegalos = new Semaphore(0);
        Semaphore esperarAyuda = new Semaphore(0);
        Semaphore esperaDuende = new Semaphore(1);
        Semaphore descansoSanta = new Semaphore(0);

        AtomicInteger regresoVacaciones = new AtomicInteger(0);
        AtomicInteger duendesConProblemas = new AtomicInteger(0);

        // Ejecucion de los hilos

        Runnable santa = new Santa(descansoSanta, exm, repartoRegalos, esperarAyuda, regresoVacaciones, duendesConProblemas);
        executor.execute(santa);

        for (int i = 0; i < NUM_RENOS; i++) {
            Runnable reno = new Reno(i, exm, repartoRegalos, descansoSanta, regresoVacaciones);
            executor.execute(reno);
        }

        for (int i = 0; i < NUM_DUENDES; i++) {
            Runnable duende = new Duende(i, exm, esperarAyuda, esperaDuende, descansoSanta, duendesConProblemas);
            executor.execute(duende);
        }

        // Tiempo para la finalizacion de la prueba

        try {
            TimeUnit.SECONDS.sleep(TIEMPO_PRUEBA);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Interrumpir los hilos

        executor.shutdownNow();

        System.out.println("(Hilo PRINCIPAL) ha finalizado");
    }

    public static final int NUM_RENOS = 9;
    public static final int NUM_DUENDES = 3;
    public static final int SANTA = 1;
    public static final int TIEMPO_PRUEBA = 30;
}