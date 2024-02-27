import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import javax.swing.JOptionPane;

public class ClienteCalculator {

    public static void main(String[] args) {
        try {
            System.out.println("Creando socket cliente");
            Socket clientSocket = new Socket();
            System.out.println("Estableciendo la conexión");

            InetSocketAddress addr = new InetSocketAddress("192.168.1.10", 6666);
            clientSocket.connect(addr);

            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();

            String mensajeServidor = "";
            String mensajeCliente = "";
            int mensajeClienteInt = 0, comprobacion = -1;

            do {
                do {
                    try {
                        mensajeCliente = JOptionPane.showInputDialog(null, "¡BIENVENIDO A LA CALCULADORA!"
                                + "\n\n Selecciona la operación que quieres realizar:"
                                + "\n1. Suma"
                                + "\n2. Resta"
                                + "\n3. Multiplicación"
                                + "\n4. División");
                        mensajeClienteInt = Integer.parseInt(mensajeCliente);

                        if (mensajeClienteInt < 1 || mensajeClienteInt > 4) {
                            JOptionPane.showMessageDialog(null, "Error: Operación no encontrada");
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Hasta pronto :)");
                        break;
                    }
                } while (mensajeClienteInt < 1 || mensajeClienteInt > 4);

                try {
                    os.write(ByteBuffer.allocate(4).putInt(mensajeCliente.getBytes().length).array());
                    os.write(mensajeCliente.getBytes());
                } catch (Exception e) {
                    break;
                }

                boolean validInput = false;

                if (comprobacion != 0) {
                    do {
                        mensajeCliente = JOptionPane.showInputDialog(null, "Introduce un valor:");
                        try {
                            Double.parseDouble(mensajeCliente);
                            validInput = true;
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "Error: Carácter no válido, asegúrate de que sea un número");
                        }
                    } while (!validInput);

                    os.write(ByteBuffer.allocate(4).putInt(mensajeCliente.getBytes().length).array());
                    os.write(mensajeCliente.getBytes());
                }

                validInput = false;

                do {
                    mensajeCliente = JOptionPane.showInputDialog(null, "Introduce el segundo valor:");
                    try {
                        Double.parseDouble(mensajeCliente);
                        validInput = true;
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Error: Ese caracter no es un número");
                    }
                } while (!validInput);

                os.write(ByteBuffer.allocate(4).putInt(mensajeCliente.getBytes().length).array());
                os.write(mensajeCliente.getBytes());

                byte[] lengthBuffer = new byte[4];
                is.read(lengthBuffer);
                int messageLength = ByteBuffer.wrap(lengthBuffer).getInt();

                byte[] messageBuffer = new byte[messageLength];
                is.read(messageBuffer);
                mensajeServidor = new String(messageBuffer);

                JOptionPane.showMessageDialog(null, mensajeServidor);

                comprobacion = JOptionPane.showConfirmDialog(null, "¿Quieres seguir operando con ese número?");
                mensajeCliente = String.valueOf(comprobacion);

                os.write(ByteBuffer.allocate(4).putInt(mensajeCliente.getBytes().length).array());
                os.write(mensajeCliente.getBytes());

                if (comprobacion == 2) {
                    JOptionPane.showMessageDialog(null, "Hasta pronto :)");
                }

            } while (comprobacion != 2);

            System.out.println("Cerrando el socket cliente");
            clientSocket.close();
            System.out.println("Terminado");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
