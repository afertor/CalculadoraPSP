import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ClienteThread extends Thread {

    private Socket newSocket;

    public ClienteThread(Socket socket) {
        this.newSocket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream is = newSocket.getInputStream();
            OutputStream os = newSocket.getOutputStream();

            String mensajeCliente;
            int operacionCliente, decisionCliente = -1;
            double n1 = 0, n2, resultado = 0;
            String mensajeServidor = "";

            do {
                // Leer operación del cliente
                byte[] lengthBuffer = new byte[4];
                is.read(lengthBuffer);
                int messageLength = ByteBuffer.wrap(lengthBuffer).getInt();

                byte[] messageBuffer = new byte[messageLength];
                is.read(messageBuffer);
                mensajeCliente = new String(messageBuffer);

                try {
                    operacionCliente = Integer.parseInt(mensajeCliente);
                } catch (Exception e) {
                    break;
                }

                // Leer primer número
                if (decisionCliente != 0) {
                    lengthBuffer = new byte[4];
                    is.read(lengthBuffer);
                    messageLength = ByteBuffer.wrap(lengthBuffer).getInt();

                    messageBuffer = new byte[messageLength];
                    is.read(messageBuffer);
                    mensajeCliente = new String(messageBuffer);

                    n1 = Double.parseDouble(mensajeCliente);
                } else {
                    n1 = resultado;
                }

                // Leer segundo número
                lengthBuffer = new byte[4];
                is.read(lengthBuffer);
                messageLength = ByteBuffer.wrap(lengthBuffer).getInt();

                messageBuffer = new byte[messageLength];
                is.read(messageBuffer);
                mensajeCliente = new String(messageBuffer);

                n2 = Double.parseDouble(mensajeCliente);

                // Procesar operación
                switch (operacionCliente) {
                    case 1:
                        resultado = n1 + n2;
                        mensajeServidor = "Resultado de la suma: " + resultado;
                        break;
                    case 2:
                        resultado = n1 - n2;
                        mensajeServidor = "Resultado de la resta: " + resultado;
                        break;
                    case 3:
                        resultado = n1 * n2;
                        mensajeServidor = "Resultado de la multiplicación: " + resultado;
                        break;
                    case 4:
                        if (n2 == 0) {
                            mensajeServidor = "Resultado de la división: Infinito";
                        } else {
                            resultado = n1 / n2;
                            mensajeServidor = "Resultado de la división: " + resultado;
                        }
                        break;
                }

                // Enviar resultado al cliente
                os.write(ByteBuffer.allocate(4).putInt(mensajeServidor.getBytes().length).array());
                os.write(mensajeServidor.getBytes());

                // Leer decisión del cliente
                lengthBuffer = new byte[4];
                is.read(lengthBuffer);
                messageLength = ByteBuffer.wrap(lengthBuffer).getInt();

                messageBuffer = new byte[messageLength];
                is.read(messageBuffer);
                mensajeCliente = new String(messageBuffer);

                decisionCliente = Integer.parseInt(mensajeCliente);

                if (decisionCliente == 0) {
                    System.out.println("Seguimos utilizando el: " + resultado);
                } else {
                    System.out.println("Reiniciando calculadora");
                }

            } while (decisionCliente != 2);

            System.out.println("Socket cerrado");
            newSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
