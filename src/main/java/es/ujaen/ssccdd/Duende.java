package es.ujaen.ssccdd;

import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Duende implements Runnable {
    private final int id;
    private final Semaphore exm;
    private final Semaphore esperarAyuda;
    private final Semaphore esperaDuende;
    private final Semaphore descansoSanta;
    private final AtomicInteger duendesConProblemas;

    /**
     * Constructor de la clase Duende.
     *
     * @param id                  Identificador del duende.
     * @param exm                 Semaforo para exclusion mutua.
     * @param esperarAyuda        Semaforo para esperar la ayuda de Santa Claus.
     * @param esperaDuende        Semaforo para esperar a otros duendes que tengan problemas.
     * @param descansoSanta       Semaforo para avisar a Santa Claus de que todos los duendes tienen problemas.
     * @param duendesConProblemas Contador de duendes con problemas.
     */
    public Duende(int id, Semaphore exm, Semaphore esperarAyuda, Semaphore esperaDuende, Semaphore descansoSanta, AtomicInteger duendesConProblemas) {
        this.id = id;
        this.exm = exm;
        this.esperarAyuda = esperarAyuda;
        this.esperaDuende = esperaDuende;
        this.descansoSanta = descansoSanta;
        this.duendesConProblemas = duendesConProblemas;
    }

    /**
     * Implementacion del metodo run de la interfaz Runnable.
     * El duende hace juguetes y solicita ayuda a Santa Claus cuando tiene problemas.
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                hacerJuguete();

                esperaDuende.acquire();
                exm.acquire();
                int duendes = duendesConProblemas.incrementAndGet();
                if (duendes == Practica1.NUM_DUENDES) {
                    descansoSanta.release(); // Se avisa a Santa
                } else {
                    esperaDuende.release();
                }
                exm.release();
                esperarAyuda.acquire();
                resolverAyuda();

                // Ya se ha completado la ayuda de Santa
                exm.acquire();
                duendesConProblemas.decrementAndGet();
                if (duendesConProblemas.get() == 0) {
                    esperaDuende.release();
                }
                exm.release();
            }
        } catch (InterruptedException e) {
            System.out.println("(Duende " + id + ") ha sido interrumpido");
        }
    }

    /**
     * Metodo que simula la accion de hacer un juguete.
     *
     * @throws InterruptedException si el hilo es interrumpido mientras esta durmiendo.
     */
    private void hacerJuguete() throws InterruptedException {
        System.out.println("(Duende " + id + ") esta haciendo juguetes");
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(1, 5));
    }

    /**
     * Metodo que simula la accion de recibir la ayuda de Santa Claus.
     *
     * @throws InterruptedException si el hilo es interrumpido mientras esta durmiendo.
     */
    private void resolverAyuda() throws InterruptedException {
        System.out.println("(Duende " + id + ") esta recibiendo ayuda de Santa Claus");
        TimeUnit.SECONDS.sleep(2);
    }
}

