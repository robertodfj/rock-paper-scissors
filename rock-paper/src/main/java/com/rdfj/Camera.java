package com.rdfj;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class Camera {
    public static OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0); // Seleccion de la camara por defecto

    public static void activate() {

        try {
            grabber.start();
            CanvasFrame canvas = new CanvasFrame("Piedra, papel o tijera");
            canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

            while (canvas.isVisible()) {  // Si se crea correctamente la ventana muestra el video
                Frame frame = grabber.grab();
                canvas.showImage(frame);
            }

            // Log de camara activa
            if (canvas.isVisible()) {
                System.out.println("Camara activa");
            }
        } catch (Exception e) {
            System.out.println("Error al activar la camara: " + e.getMessage());
        }
    }

    public static void close(){
        try {
            grabber.stop();
            System.out.println("Camara cerrada");
        } catch (Exception e) {
            System.out.println("Error al cerrar la camara: " + e.getMessage());
        }
    }
}
