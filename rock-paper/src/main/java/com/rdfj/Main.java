package com.rdfj;

import org.bytedeco.javacv.FrameGrabber.Exception;

public class Main {
    public static void main(String[] args) throws Exception {
        try {
            DeteccionMano.activate();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}