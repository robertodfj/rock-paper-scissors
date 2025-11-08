package com.rdfj;

public class Juego {
    public static void iniciar() {
        System.out.println("Iniciando el juego de Piedra, Papel o Tijera...");
        
        String[] opciones = {"Piedra", "Papel", "Tijera"};
        // TODO: Implementar la seleccion del usuario por la camara dependiendo de los dedos detectados
        String eleccionMaquina = opciones[(int) (Math.random() * opciones.length)];
        String eleccionUsuario = "Piedra"; // Placeholder para la eleccion del usuario

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
