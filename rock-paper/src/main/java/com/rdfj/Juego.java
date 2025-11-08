package com.rdfj;

public class Juego {

    public static void iniciar(String eleccionUsuario) {
        System.out.println("Iniciando el juego de Piedra, Papel o Tijera...");

        String[] opciones = {"Piedra", "Papel", "Tijera"};
        String eleccionMaquina = opciones[(int) (Math.random() * opciones.length)];

        System.out.println("Elección del usuario: " + eleccionUsuario);
        System.out.println("Elección de la máquina: " + eleccionMaquina);

        if (eleccionUsuario.equals(eleccionMaquina)) {
            System.out.println("🤷‍♂️ ¡Empate!");
        } else if ((eleccionUsuario.equals("Piedra") && eleccionMaquina.equals("Tijera")) ||
                   (eleccionUsuario.equals("Papel") && eleccionMaquina.equals("Piedra")) ||
                   (eleccionUsuario.equals("Tijera") && eleccionMaquina.equals("Papel"))) {
            System.out.println("✌️ ¡Ganaste!");
        } else {
            System.out.println("✊ ¡Perdiste!");
        }
    }
}