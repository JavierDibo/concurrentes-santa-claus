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
     * Constructor de la clase Reno
     *
     * @param id                El identificador del reno
     * @param exm               Semaforo para exclusion mutua entre los renos
     * @param repartoRegalos    Semaforo para controlar el reparto de regalos entre Santa y los renos
     * @param descansoSanta     Semaforo para controlar el descanso de Santa
     * @param regresoVacaciones Contador atomico para llevar la cuenta de los renos que han regresado de vacaciones
     */
    public Reno(int id, Semaphore exm, Semaphore repartoRegalos, Semaphore descansoSanta, AtomicInteger regresoVacaciones) {
        this.id = id;
        this.exm = exm;
        this.repartoRegalos = repartoRegalos;
        this.descansoSanta = descansoSanta;
        this.regresoVacaciones = regresoVacaciones;
    }

    /**
     * Metodo run que implementa la vida del reno
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
            System.out.println("(Hilo RENO: " + id + ") ha sido interrumpido");
        }
    }

    /**
     * Metodo que simula las vacaciones del reno
     */
    private void vacaciones() throws InterruptedException {
        System.out.println("(Hilo RENO: " + id + ") esta de vacaciones");
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(TIEMPO_MIN_VACACIONES, TIEMPO_MAX_VACACIONES));
    }

    /**
     * Metodo que simula la entrega de regalos del reno junto a Santa
     */
    private void repartirRegalos() throws InterruptedException {
        System.out.println("(Hilo RENO:" + id + ") esta repartiendo regalos con Santa");
        TimeUnit.SECONDS.sleep(TIEMPO_REPARTIR_REGALOS);
    }

    // Segundos
    private static final int TIEMPO_REPARTIR_REGALOS = 2;
    private static final int TIEMPO_MIN_VACACIONES = 1;
    private static final int TIEMPO_MAX_VACACIONES = 5;
}
