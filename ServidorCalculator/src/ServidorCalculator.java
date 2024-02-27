import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class ServidorCalculator {

    public static void main(String[] args) {

        try{
            System.out.println("Creando socket servidor");

            ServerSocket serverSocket=new ServerSocket();

            System.out.println("Realizando el bind");

            InetSocketAddress addr=new InetSocketAddress("192.168.1.10",6666);
            serverSocket.bind(addr);

            System.out.println("Conexiones aceptadas");

            while(true){
                System.out.println("Recibiendo conexión");
                Socket newSocket= serverSocket.accept();
                ClienteThread clienteThread=new ClienteThread(newSocket);
                clienteThread.start();
            }

        }catch (IOException e) {
            System.out.println("ERROR DE CONEXION");
        }
    }

}