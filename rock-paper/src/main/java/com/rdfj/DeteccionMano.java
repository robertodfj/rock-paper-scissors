package com.rdfj;

import java.awt.event.KeyEvent;

import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import static org.bytedeco.opencv.global.opencv_core.inRange;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

public class DeteccionMano {

    public static void activate() throws FrameGrabber.Exception, InterruptedException {

        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();

        CanvasFrame canvas = new CanvasFrame("Piedra Papel Tijera - Detección de Mano");
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

        boolean capturado = false;
        String jugadaUsuario = "";
        String jugadaMaquina = "";
        String resultado = "";

        while (canvas.isVisible()) {
            Frame frame = grabber.grab();
            if (frame == null) continue;

            Mat mat = converter.convert(frame);

            // Centrar rectángulo (ZONA DE DETECCIÓN)
            int frameWidth = mat.cols();
            int frameHeight = mat.rows();
            int rectWidth = (int)(400);
            int rectHeight = (int)(400);
            Point topLeft = new Point((frameWidth - rectWidth) / 2, (frameHeight - rectHeight) / 2);
            Point bottomRight = new Point(topLeft.x() + rectWidth, topLeft.y() + rectHeight);
            
            // Dibujar rectángulo de detección
            opencv_imgproc.rectangle(mat, topLeft, bottomRight, new Scalar(0, 255, 0, 0),
                    3, opencv_imgproc.LINE_8, 0);

            // Detectar con ESPACIO - FORMA CORRECTA
            try {
                KeyEvent key = canvas.waitKey(20);
                if (key.getKeyCode() == KeyEvent.VK_SPACE && !capturado) {
                    Mat manoRegion = new Mat(mat, new Rect(topLeft, bottomRight));
                    Mat mask = detectarMano(manoRegion);
                    int dedos = contarDedos(mask);
                    jugadaUsuario = deducirJugada(dedos);

                    // Generar jugada de la máquina
                    String[] opciones = {"Piedra", "Papel", "Tijera"};
                    jugadaMaquina = opciones[(int)(Math.random() * opciones.length)];

                    // Determinar resultado
                    if (jugadaUsuario.equals(jugadaMaquina)) {
                        resultado = "Empate!";
                    } else if ((jugadaUsuario.equals("Piedra") && jugadaMaquina.equals("Tijera")) ||
                               (jugadaUsuario.equals("Papel") && jugadaMaquina.equals("Piedra")) ||
                               (jugadaUsuario.equals("Tijera") && jugadaMaquina.equals("Papel"))) {
                        resultado = "¡Ganaste!";
                    } else {
                        resultado = "¡Perdiste!";
                    }

                    capturado = true;
                }
            } catch (Exception e) {
                System.out.println("Error en detección: " + e.getMessage());
            }

            // Mostrar información en pantalla
            opencv_imgproc.putText(mat, "Tu jugada: " + jugadaUsuario, new Point(30, 50),
                    opencv_imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(0, 255, 0, 0),
                    2, opencv_imgproc.LINE_AA, false);

            opencv_imgproc.putText(mat, "Maquina: " + jugadaMaquina, new Point(30, 100),
                    opencv_imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(0, 0, 255, 0),
                    2, opencv_imgproc.LINE_AA, false);

            opencv_imgproc.putText(mat, "Resultado: " + resultado, new Point(30, 150),
                    opencv_imgproc.FONT_HERSHEY_SIMPLEX, 1.0, new Scalar(255, 0, 0, 0),
                    2, opencv_imgproc.LINE_AA, false);

            // Instrucción
            opencv_imgproc.putText(mat, "Presiona ESPACIO para jugar", new Point(30, frameHeight - 30),
                    opencv_imgproc.FONT_HERSHEY_SIMPLEX, 0.7, new Scalar(255, 255, 255, 0),
                    2, opencv_imgproc.LINE_AA, false);

            // Mostrar el frame
            canvas.showImage(converter.convert(mat));

            // Reset después de 2 segundos
            if (capturado) {
                Thread.sleep(2000);
                capturado = false;
                jugadaMaquina = "";
                resultado = "";
            }
        }

        grabber.stop();
        canvas.dispose();
    }

    public static Mat detectarMano(Mat frame) {
        Mat hsv = new Mat();
        opencv_imgproc.cvtColor(frame, hsv, opencv_imgproc.COLOR_BGR2HSV);

        // Usar límites HSV y convertir a Mats 1x1 para la sobrecarga de inRange(Mat, Mat, Mat, Mat)
        Scalar lower = new Scalar(0, 30, 60, 0);
        Scalar upper = new Scalar(20, 150, 255, 0);
        Mat mask = new Mat();

        Mat lowerMat = new Mat(1, 1, hsv.type(), lower);
        Mat upperMat = new Mat(1, 1, hsv.type(), upper);
        inRange(hsv, lowerMat, upperMat, mask);
        opencv_imgproc.GaussianBlur(mask, mask, new Size(7, 7), 0);

        return mask;
    }

    public static int contarDedos(Mat mask) {
        MatVector contours = new MatVector();
        Mat hierarchy = new Mat();
        opencv_imgproc.findContours(mask, contours, hierarchy,
                opencv_imgproc.RETR_EXTERNAL,
                opencv_imgproc.CHAIN_APPROX_SIMPLE);

        if (contours.size() == 0) return 0;

        int maxIndex = 0;
        double maxArea = 0;
        for (int i = 0; i < contours.size(); i++) {
            double area = opencv_imgproc.contourArea(contours.get(i));
            if (area > maxArea) {
                maxArea = area;
                maxIndex = i;
            }
        }

        Mat contour = contours.get(maxIndex);
        if (opencv_imgproc.contourArea(contour) < 5000 || contour.rows() < 3)
            return 0;

        Mat hull = new Mat();
        opencv_imgproc.convexHull(contour, hull, false, true);
        if (hull.rows() < 3) return 0;

        int count = 0;
        try {
            Mat defects = new Mat();
            opencv_imgproc.convexityDefects(contour, hull, defects);
            if (!defects.empty()) {
                IntRawIndexer indexer = defects.createIndexer();
                for (int i = 0; i < defects.rows(); i++) {
                    int depth = indexer.get(i, 3);
                    if (depth > 10000) count++;
                }
                indexer.release();
            }
        } catch (Exception e) {
            System.out.println("Error en conteo de dedos: " + e.getMessage());
        }

        return count + 1;
    }

    public static String deducirJugada(int dedos) {
        switch (dedos) {
            case 0:
            case 1:
                return "Piedra";
            case 2:
            case 3:
                return "Tijera";
            default:
                return "Papel";
        }
    }
}