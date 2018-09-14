package com.filebox.api.websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;

import com.filebox.common.kit.RetKit;

/**
 * @Description:TODO(长连接)
 * @author 作者 : jinghui.su
 * @date 创建时间：2017年5月11日
 */
@ServerEndpoint("/websocket/{userid}")
public class WebSocketController {
	private static final Logger log = Logger.getLogger(WebSocketController.class);
	public static ConcurrentHashMap<String, WebSocketController> webSocketSet = new ConcurrentHashMap<String, WebSocketController>();
	private Session session;
	private String userId;

	public void index() {
		System.out.println("Hello World!");
	}

	@OnOpen
	public void onOpen(Session session, @PathParam("userid") String userId) {
		log.info("WebSocket连接 -----------------------" + userId);
		System.out.println("连接 -------" + userId);
		this.session = session;
		this.userId = userId;
		webSocketSet.put(userId, this);
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("关闭连接 -------" + userId);
		webSocketSet.remove(userId); // 从set中删除
	}


	@OnMessage
	public void onMessage(String requestJson, Session session) {
		try {
			this.session.getBasicRemote().sendText(requestJson);
		} catch (IOException e) {
			log.debug(e);
		}
		/*
		 * //群发消息 for(WebSocketController item: webSocketSet){ try {
		 * item.sendMessage(requestJson); } catch (IOException e) {
		 * e.printStackTrace(); continue; } }
		 */
	}

	public static RetKit sendMsgToUser(String userId, String message) {
		if (webSocketSet.get(userId) == null) {
			return RetKit.fail("设备不在线");
		}
		// 群发消息
		try {
			webSocketSet.get(userId).sendMessage(message);
		} catch (IOException e) {
			log.debug(e);
			return RetKit.fail(e.getMessage());
		}
		return RetKit.ok();
	}

	public void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
	}

}
