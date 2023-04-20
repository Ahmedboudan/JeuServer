
package jeuserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.*;
import java.net.ServerSocket;
import java.net.*;
import java.util.*;

/**
 *
 * @author ahmed
 */
public class JeuServer extends Thread{
    private boolean active=true;
    int nbr_client;
    int nombreSectret;
    boolean fin=false;
    String gagnant;
    public static void main(String[] args) {
        // l'appel de la methode start() va executer la methode run()
        // et dans la methode run() on demarre le serveur
        new JeuServer().start();
    }

    @Override
    public void run() {
        ServerSocket ss;
        try {
            ss = new ServerSocket(1232); // tcp
            // generation d'un nombre entre 0 et 60
            nombreSectret = new Random().nextInt(60);
            System.out.println("Demarrage du serveur...");
            while(active){
           Socket socket = ss.accept();
           ++nbr_client;
           new Conversation(socket,nbr_client).start();
           // Pour chaque client qui vient de se conneceter je cree un thread : la Conversation herite de la classe Thread
                
         }
        } catch (IOException ex) {
            System.err.println("Erreur: "+ex.getMessage());
        }
        
    }
    class Conversation extends Thread{
        int num;
        private boolean connected=true;
        private Socket socket;
        public Conversation(Socket socket,int num){
            this.socket = socket; this.num = num;
        }
        @Override
        public void run() {
            try {
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader bfr = new BufferedReader(isr);
                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os,true);

                System.out.println("Connexion du client numero "+num);
               
                String ip = socket.getRemoteSocketAddress().toString();
                pw.println("Bienvenue vous etes le client numero "+num+" IP adresse : "+ip);
                pw.println("Devinez le nombre secret: ");
                String requete;
                while (connected && (requete = bfr.readLine()) != null) {
                    if(requete.equals("exit")){
                        connected = false;
                        System.out.println("Le client "+ip+" s'est deconnecte");
                    }
                    else{
                        System.out.println("Le client "+ip+" a envoye la requete "+requete);
                        System.out.println("Le nombre secret est "+nombreSectret);
                        int nombre = Integer.parseInt(requete);
                        if(fin==false){
                            if(nombre>nombreSectret){
                                pw.println("Votre nombre est superieur au nombre secret");
                            }
                            else if(nombre<nombreSectret){
                                pw.println("Votre nombre est inferieur au nombre secret");
                            }
                            else{
                               pw.println("Bravo ! vous avez gagné ");
                               pw.println("Tappe 'exit' pour sortir ");
                               gagnant = ip;
                                System.out.println("Bravo au gagnant "+ip);
                                fin = true;
                            }
                        }
                        else{
                            pw.println("Le jeu est terminé ! le gagnant est "+gagnant);
                        }
                    }
                }
                socket.close(); // fermer le socket une fois la communication terminée
            } catch (Exception e) {
                e.printStackTrace();
            }
          }
} 
}
