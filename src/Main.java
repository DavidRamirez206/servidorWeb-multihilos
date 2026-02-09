import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private HttpProcess httpProcess;

    public static void main(String[] args) throws IOException {
        Main main = new Main();
    }

    public Main() throws IOException {

        try(ServerSocket serverSocket = new ServerSocket(8080)) {

            while (true){
                System.out.println("Waiting for connection...");
                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted");
                httpProcess = new HttpProcess(socket);
                httpProcess.start();

                //manejarSolicitudes(socket);

            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    private void manejarSolicitudes(Socket socket) throws IOException {

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        //Permite leer muy bien lo que venga a nivel de bytes
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));


        //Lo que manda el cliente

        String inputLine;
        while ((inputLine = reader.readLine()) != null && !inputLine.isEmpty()) {
            System.out.println(inputLine);
        }

        ///// Respuesta

        String outputLine = "<html><body>Hola Mundo</body></html>";

        writer.write("HTTP/1.1 200 OK\r\n");
        writer.write("Content-Type: text/html\r\n");
        writer.write("Content-Length: " + outputLine.length() +"\r\n");
        writer.write("\r\n");

        writer.write(outputLine);
        writer.flush();
        socket.close();
    }
}
