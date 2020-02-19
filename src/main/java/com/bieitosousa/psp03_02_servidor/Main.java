/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bieitosousa.psp03_02_servidor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bieito
 */
public class Main {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                if (".".equals(inputLine)) {
//                    out.println("good bye");
//                    break;
//                }
//                out.println(inputLine);
//            }
        } catch (IOException e) {

        }

    }

    private boolean fileProgram() {
        String inputLine;
        File f;
        try {
            start(1500);
//             out.println("Instrucciones Para descargar archivos:"
//                     + " /n deveras envia un mesaje en el cual inclullas cabecera"
//                     + "  \n cabecera { [CLI] mensaje:}"
//                     + " \n mensaje { del nombre del archivo} "
//                     + "\n request : "
//                     + "/n si {operacion realizada con exito} "
//                     + "/n no : {send,search,reader}_error  ");
            while ((inputLine = in.readLine()) != null) {
                System.out.println("[SERVER_RECIBE]" + inputLine);
                if (inputLine.indexOf("[CLI] mensaje:") != -1) {
                    // separamos [CLI] mensaje:
                    String[] parts = inputLine.split(":");
                    String cabecera = parts[0]; // "[CLI] mensaje:"
                    String mensaje = parts[1]; // nombre de archivo
                    if ((f = new File("."+File.separator + mensaje)).exists()) { // si existe el archivo
                        System.out.println("[SERVER_INTENTA ENVIAR_ARCHIVO]");
                        out.println("ok");
                        if (sendFile(f)) { // si se envia con exito
                            System.out.println("EXITO: enviando archivo " + f.toString() + "");

                            return true;
                        } else {
                            System.out.println("ERROR: enviando archivo " + f.toString() + "");
                            out.println("send_error");
                            return false; //no se a enviado con exito
                        }
                    } else {
                        System.out.println("ERROR: enviando archivo " + f.toString() + "");
                        out.println("search_error");
                        return false; //no se a enviado con exito
                    }
                } else {
                    System.out.println("ERROR: leyendo mensaje{" + inputLine + "}");
                    out.println("reader_error");
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            stop();
        }
        return false;
    }

    private void stop() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {

        }

    }

    public static void main(String[] args) {
        Main m = new Main();
        m.fileProgram();

    }

    private boolean sendFile(File f) {
        try {
            while (true) {
                //Aceptar conexiones
//                connection = server.accept();
                DataInputStream input;
                BufferedInputStream bis;
                BufferedOutputStream bos;
                int in;
                byte[] byteArray;
                //Fichero a transferir
//                final String filename = ".\\2EvaBie.pdf";
//
//                final File localFile = new File(f);

                bis = new BufferedInputStream(new FileInputStream(f));
                bos = new BufferedOutputStream(clientSocket.getOutputStream());
                //Enviamos el nombre del fichero
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                dos.writeUTF(f.getName());
                //Enviamos el fichero
                byteArray = new byte[8192];
                while ((in = bis.read(byteArray)) != -1) {
                    bos.write(byteArray, 0, in);
                }
                bis.close();
                bos.close();

            }
        } catch (Exception e) {
            System.err.println(e);
            return false;

        }
    }

}
