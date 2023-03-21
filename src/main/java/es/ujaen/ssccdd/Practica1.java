package es.ujaen.ssccdd;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Javier Francisco Dibo Gomez
 */
public class Practica1 {
    public static final int NUM_RENOS = 9;
    public static final int NUM_DUENDES = 3;
    public static final int TIEMPO_PRUEBA = 30;

    public static void main(String[] args) {

        System.out.println("(Hilo PRINCIPAL) ha comenzado");

        ExecutorService executor = Executors.newFixedThreadPool(NUM_RENOS + NUM_DUENDES + 1);

        Semaphore exm = new Semaphore(1);
        Semaphore repartoRegalos = new Semaphore(0);
        Semaphore esperarAyuda = new Semaphore(0);
        Semaphore esperaDuende = new Semaphore(1);
        Semaphore descansoSanta = new Semaphore(0);

        AtomicInteger regresoVacaciones = new AtomicInteger(0);
        AtomicInteger duendesConProblemas = new AtomicInteger(0);

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

        try {
            TimeUnit.SECONDS.sleep(TIEMPO_PRUEBA); // Tiempo para la finalizacion de la prueba
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdownNow();

        System.out.println("(Hilo PRINCIPAL) ha finalizado");
    }
}