package com.rdfj;

public class Main {
    public static void main(String[] args) {
        // TODO: Selecionar iniciar juego, repetir o salir

        // 1 Llamamos a la funciona para activar la camara
        Camera.activate();

        // Cerramos camara y salimos
        Camera.close();
        System.out.println("Saliendo del programa...");
    }
}