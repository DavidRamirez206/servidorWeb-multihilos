import java.io.*;
import java.net.Socket;

//This class is for handling each request from each client
public class HttpProcess extends Thread {

    private Socket socket;

    //We make a dependency injection
    public HttpProcess(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (   BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream outputStream = socket.getOutputStream()
        ) {

            String lineRequest = reader.readLine();
            if(lineRequest == null || lineRequest.isEmpty()) {
                return;
            }

            System.out.println("[" + Thread.currentThread().getName() + "] Request:");
            System.out.println(lineRequest);

            String requestHeaderLine = "";
            while ((requestHeaderLine = reader.readLine()) != null && !requestHeaderLine.isEmpty()) {
                System.out.println(requestHeaderLine);
            }

            String[] args = lineRequest.split(" ");
            if (args.length != 3) {
                System.out.println("Request is wrong");
                return;
            }

            String method = args[0];
            if(!method.equals("GET")) {
                System.out.println("501");
                return;
            }

            String version = args[2];
            if(!version.equals("HTTP/1.0") && !version.equals("HTTP/1.1")) {
                System.out.println("Version not supported");
                return;
            }

            //serving the resource
            String resource = args[1];
            if (resource.equals("/")) {
                resource = "/index.html"; //Should be /index.html
            }

            serverResource(outputStream, resource);


        } catch (IOException e) {
            System.err.println("Error manejando solicitud: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error cerrando solicitud: " + e.getMessage());
            }
        }
    }

    private void serverResource(OutputStream outputStream, String resource) throws IOException {
        File baseDir = new File("public");
        File file = new File(baseDir, resource.substring(1)); // "resource starts with '/', so, we have to use resource.substring(1)

        if(!file.exists() || !file.isFile()){
            send404(outputStream);
            return;
        }

        String contentType = "application/octet-stream";

        if (file.getName().endsWith(".html")) {
            contentType = "text/html";
        } else if (file.getName().endsWith(".jpg")) {
            contentType = "image/jpeg";
        } else if (file.getName().endsWith(".png")) {
            contentType = "image/png";
        } else if (file.getName().endsWith(".gif")) {
            contentType = "image/gif";
        } else if (file.getName().endsWith(".ico")) {
            contentType = "image/x-icon";
        }

        String headers = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "Content-Length: " + file.length() + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n";

        outputStream.write(headers.getBytes());

        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = fis.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        fis.close();
        outputStream.flush();

    }

    private void send404(OutputStream outputStream) throws IOException {
        File baseDir = new File("public");
        File file404 = new File(baseDir, "404.html");

        String body = "<h1>404 Not Found</h1>";
        byte[] bodyBytes;

        if (file404.exists() && file404.isFile()) {
            FileInputStream fis = new FileInputStream(file404);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            fis.close();
            bodyBytes = bos.toByteArray();
        } else {
            bodyBytes = body.getBytes();
        }

        String headers = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + bodyBytes.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n";

        outputStream.write(headers.getBytes());
        outputStream.write(bodyBytes);
        outputStream.flush();

    }
}
