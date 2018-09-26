import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpEchoServer {

    private ServerSocket serverSocket;
    private String uri;
	private String statusCode;  
	private String location; 
	private String body;
    
    //Starts the server
    public static void main(String[] args) throws IOException {
        HttpEchoServer server = new HttpEchoServer(0);
        System.out.println("Go to localhost:" + server.getPort());
    }
    
    public HttpEchoServer(int port) throws IOException {
    	
        this.serverSocket = new ServerSocket(port);
        
        Thread thread = new Thread(() -> runServer());
        thread.start();
    }

    public void runServer() {
        while (true) {
        	
        	Socket socket = null;
        	
            try {
                socket = serverSocket.accept();

                getInput(socket);

                writeResponse(socket);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	private void getInput(Socket socket) throws IOException {
		
		this.uri = readLine(socket).split(" ")[1];
		
		HttpPath path = new HttpPath(uri);

		this.statusCode = path.getQuery().getParameter("status");
		if (statusCode == null) {
		    statusCode = "200";
		}
		this.body = path.getQuery().getParameter("body");
		if (body == null) {
		    body = "Hello world";
		}
		this.location = path.getQuery().getParameter("Location");
	}

    
	private void writeResponse(Socket socket) throws IOException {
		
		socket.getOutputStream().write(("HTTP/1.1 " + statusCode + " OK\r\n").getBytes());
		socket.getOutputStream().write("Content-Type: text/html; charset=utf-8\r\n".getBytes());
		if (location != null) {
		    socket.getOutputStream().write(("Location: " + location + "\r\n").getBytes());
		}
		socket.getOutputStream().write("Server: Java Server!!\r\n".getBytes());
		socket.getOutputStream().write(("Content-Length: " + body.length() + "\r\n").getBytes());
		socket.getOutputStream().write("\r\n".getBytes());
		socket.getOutputStream().write((body + "\r\n").getBytes());
		socket.getOutputStream().flush();
	}

    public String readLine(Socket socket) throws IOException {
        StringBuilder requestLine = new StringBuilder();

        // Reads the first line
        int c;
        while ((c = socket.getInputStream().read()) != -1) {
            if (c == '\r') {
                break;
            }
            requestLine.append((char)c);
        }

        return requestLine.toString();
    }
    
    public int getPort() {
        return serverSocket.getLocalPort();
    }

}