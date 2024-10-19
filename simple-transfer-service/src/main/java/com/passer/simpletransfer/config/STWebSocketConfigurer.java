package com.passer.simpletransfer.config;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.Enumeration;
import java.util.Map;

/**
 * websocket 配置信息
 */
@Configuration
public class STWebSocketConfigurer extends ServerEndpointConfig.Configurator {

    public static final String IP_ADDR = "IP.ADDR";

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        Map<String, Object> attributes = sec.getUserProperties();
        HttpSession session = (HttpSession) request.getHttpSession();
        if (session != null) {
            attributes.put(IP_ADDR, session.getAttribute(STWebSocketConfigurer.IP_ADDR));
            Enumeration<String> names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                attributes.put(name, session.getAttribute(name));
            }
        }
    }


}
