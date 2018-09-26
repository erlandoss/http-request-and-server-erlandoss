import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpEchoServer {

    private ServerSocket serverSocket;
    private String statusCode;
    private String location; 
    private String body;

    //Starts the server and gives you a port to access it through
    public static void main(String[] args) throws IOException {
        HttpEchoServer server = new HttpEchoServer(0);
        System.out.println("Go to localhost:" + server.getPort());
    }

    //Sets serversocket with a port and starts thread
    public HttpEchoServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        Thread thread = new Thread(() -> runServer(serverSocket));
        thread.start();
    }

    //Main loop
    public void runServer(ServerSocket serverSocket) {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                String requestLine = readLine(input).split(" ")[1];
                HttpPath path = new HttpPath(requestLine);
                HttpQuery query = path.getQuery();
                setDefaultParameters();
                setQueryParameters(query);
                writeResponse(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Sets the parameters of the query
    private void setQueryParameters(HttpQuery query) {
        if(query != null) {
            this.statusCode = query.getParameter("status");
            if(this.statusCode == null) {
                statusCode = "200";
            }
            this.body = query.getParameter("body");
            if(body == null) {
                body = "java4ever";
            }
            this.location = query.getParameter("Location");
            }
        }
    
    //Sets some default parameters, experienced nullpointer exceptions without this
    private void setDefaultParameters() {
        statusCode = "200";
        body = "java4everrrrr";
        location = null;
    }

    //Writes the server response
    private void writeResponse(OutputStream output) throws IOException {
        output.write(("HTTP/1.1 " + statusCode + " OK\r\n").getBytes());
        output.write("Content-Type: text/html; charset=utf-8\r\n".getBytes());
        if (location != null) {
            output.write(("Location: " + location + "\r\n").getBytes());
        }
        output.write("Server: Java Server!!\r\n".getBytes());
        output.write(("Content-Length: " + body.length() + "\r\n").getBytes());
        output.write("\r\n".getBytes());
        output.write((body + "\r\n").getBytes());
        output.flush();
    }

    //Takes a inputstream and returns a string
    public String readLine(InputStream input) throws IOException {
        StringBuilder requestLine = new StringBuilder();
        // Reads the first line
        int c;
        while ((c = input.read()) != -1) {
            if (c == '\r') {
                input.read();
                break;
            }
            requestLine.append((char)c);
        }
        return requestLine.toString();
    }

    //Returns the port the server is running on
    public int getPort() {
        return serverSocket.getLocalPort();
    }
}
