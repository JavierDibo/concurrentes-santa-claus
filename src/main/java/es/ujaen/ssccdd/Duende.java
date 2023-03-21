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
     * Constructor de la clase Duende
     *
     * @param id                  Identificador del duende
     * @param exm                 Semaforo para exclusion mutua
     * @param esperarAyuda        Semaforo para esperar la ayuda de Santa
     * @param esperaDuende        Semaforo para esperar a otros duendes que tengan problemas
     * @param descansoSanta       Semaforo para avisar a Santa de que todos los duendes tienen problemas
     * @param duendesConProblemas Contador de duendes con problemas
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
     * Implementacion del metodo run de la interfaz Runnable
     * El duende hace juguetes y solicita ayuda a Santa cuando tiene problemas
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

                // Santa ya ha ayudado a to.do el mundo
                exm.acquire();
                duendesConProblemas.decrementAndGet();
                if (duendesConProblemas.get() == SIN_PROBLEMAS) {
                    esperaDuende.release();
                }
                exm.release();
            }
        } catch (InterruptedException e) {
            System.out.println("(Hilo DUENDE: " + id + ") ha sido interrumpido");
        }
    }

    /**
     * Metodo que simula la accion de hacer un juguete
     */
    private void hacerJuguete() throws InterruptedException {
        System.out.println("(Hilo DUENDE: " + id + ") esta haciendo juguetes");
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(TIEMPO_MIN_FABRICAR_JUGUETE, TIEMPO_MAX_FABRICAR_JUGUETE));
    }

    /**
     * Metodo que simula la accion de recibir la ayuda de Santa
     */
    private void resolverAyuda() throws InterruptedException {
        System.out.println("(Hilo DUENDE: " + id + ") esta recibiendo ayuda de Santa");
        TimeUnit.SECONDS.sleep(TIEMPO_RECIBIR_AYUDA);
    }

    // Segundos
    private static final int TIEMPO_MIN_FABRICAR_JUGUETE = 1;
    private static final int TIEMPO_MAX_FABRICAR_JUGUETE = 5;
    private static final int TIEMPO_RECIBIR_AYUDA = 2;
    private static final int SIN_PROBLEMAS = 0;


}

