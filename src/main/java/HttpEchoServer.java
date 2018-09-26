import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpEchoServer {

    private ServerSocket serverSocket;
    private String uri;
	private String statusCode;  
	private String location; 
	private String body;
    
    //Starts the server
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpEchoServer server = new HttpEchoServer(0);
        System.out.println("Go to localhost:" + server.getPort());
    }
    
    public HttpEchoServer(int port) throws IOException, InterruptedException {
    	
        this.serverSocket = new ServerSocket(port);
        
        Thread thread = new Thread(() -> runServer(serverSocket));
        thread.start();
    }

    public void runServer(ServerSocket serverSocket) {
        while (true) {
        	
            try {
                Socket socket = serverSocket.accept();
                InputStream input = socket.getInputStream();
                getInput(readLine(input));
                writeResponse(socket);
                socket.getOutputStream().flush();
                
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

	private void getInput(String input) throws IOException {
		
		if (!input.isEmpty()) {
			
			uri = input.split(" ")[1];
		
			HttpPath path = new HttpPath(uri);

			if (uri.contains("status")) {
				statusCode = path.getQuery().getParameter("status");
			}
			else {
				statusCode = "200";
			}
			if (uri.contains("body")) {
				body = path.getQuery().getParameter("body");
			}
			else {
				body = "Hello world";
			}
			if (uri.contains("Location")) {
				location = path.getQuery().getParameter("Location");
			}
		}
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

    public String readLine(InputStream input) throws IOException {
        
    	StringBuilder requestLine = new StringBuilder();

        int c;
        
        while ((c = input.read()) != -1) {
            if (c == '\r') {
                input.mark(1);
                c = input.read();
                if (c != '\n') {
                	input.reset();
                }
            }
            requestLine.append((char)c);
        }
        return requestLine.toString();
    }
    
    public int getPort() {
        return serverSocket.getLocalPort();
    }

}