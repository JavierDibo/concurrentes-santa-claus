package es.ujaen.ssccdd;

import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Reno implements Runnable {
    private final int id;
    private final Semaphore exm;
    private final Semaphore repartoRegalos;
    private final Semaphore descansoSanta;
    private final AtomicInteger regresoVacaciones;

    /**
     * Constructor de la clase Reno.
     *
     * @param id                El identificador del reno.
     * @param exm               Semaforo para exclusion mutua entre los renos.
     * @param repartoRegalos    Semaforo para controlar el reparto de regalos entre Santa Claus y los renos.
     * @param descansoSanta     Semaforo para controlar el descanso de Santa Claus.
     * @param regresoVacaciones Contador atomico para llevar la cuenta de los renos que han regresado de vacaciones.
     */
    public Reno(int id, Semaphore exm, Semaphore repartoRegalos, Semaphore descansoSanta, AtomicInteger regresoVacaciones) {
        this.id = id;
        this.exm = exm;
        this.repartoRegalos = repartoRegalos;
        this.descansoSanta = descansoSanta;
        this.regresoVacaciones = regresoVacaciones;
    }

    /**
     * Metodo run que implementa la tarea del reno.
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                vacaciones();

                exm.acquire();
                int renos = regresoVacaciones.incrementAndGet();
                if (renos == Practica1.NUM_RENOS) {
                    descansoSanta.release();
                }
                exm.release();

                repartoRegalos.acquire();
                repartirRegalos();
            }
        } catch (InterruptedException e) {
            System.out.println("(Reno " + id + ") ha sido interrumpido");
        }
    }

    /**
     * Metodo que simula las vacaciones del reno.
     *
     * @throws InterruptedException Si se produce una interrupcion durante la espera.
     */
    private void vacaciones() throws InterruptedException {
        System.out.println("(Reno " + id + ") esta de vacaciones");
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 5));
    }

    /**
     * Metodo que simula la entrega de regalos del reno junto a Santa Claus.
     *
     * @throws InterruptedException Si se produce una interrupcion durante la espera.
     */
    private void repartirRegalos() throws InterruptedException {
        System.out.println("(Reno " + id + ") esta repartiendo regalos con Santa Claus");
        TimeUnit.SECONDS.sleep(2);
    }
}
