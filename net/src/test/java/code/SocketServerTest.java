package code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 测试接受消息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-30 16:21
 **/
public class SocketServerTest {
    public static void main(String[] args) {
        int port = 8080; // 选择一个端口号
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务器已启动，等待客户端连接...");
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("客户端已连接: " + clientSocket.getInetAddress());
                    // 创建输入输出流
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("收到消息: " + inputLine);
                        out.println("Echo: " + inputLine);
                    }
                } catch (IOException e) {
                    System.err.println("客户端连接异常: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("服务器启动异常: " + e.getMessage());
        }
    }
}