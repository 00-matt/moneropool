package uk.offtopica.moneropool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Component
@Slf4j
public class NotifyServer {
    @Autowired
    private BlockTemplateNotifier blockTemplateNotifier;

    @Value("${notify.port}")
    private int port;

    private Thread thread;

    public void start() {
        if (thread != null) {
            throw new IllegalStateException("Already started");
        }

        thread = new Thread(() -> {
            try {
                log.info("Going to listen on :{}", port);
                ServerSocket serverSocket = new ServerSocket(port);
                while (true) {
                    Socket incoming = serverSocket.accept();
                    blockTemplateNotifier.update();
                    incoming.close();
                }
            } catch (IOException e) {
                log.error("Failed to start", e);
            }
        }, "notify-server");

        thread.start();
    }

    @PreDestroy
    public void stop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            log.error("Failed to stop", e);
        }
    }
}
