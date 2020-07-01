package server

import com.corundumstudio.socketio.listener.{ConnectListener, DataListener, DisconnectListener}
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}

class EchoServer {
  val config: Configuration = new Configuration {
    setHostname("localhost")
    setPort(8080)
  }

  val server: SocketIOServer = new SocketIOServer(config)

  server.addEventListener("send_back", classOf[String], new MessageListener())
  server.addEventListener("stop_server", classOf[Nothing], new StopListener(this))

  server.start()

  class MessageListener() extends DataListener[String] {
    override def onData(socket: SocketIOClient, data: String, ackRequest: AckRequest): Unit = {
      println("received message: " + data + " from " + socket)
      socket.sendEvent("echo", data)
    }
  }

  class StopListener(server: EchoServer) extends DataListener[Nothing] {
    override def onData(socket: SocketIOClient, data: Nothing, ackRequest: AckRequest): Unit = {
      server.server.getBroadcastOperations.sendEvent("server_stopped")
      println("stopping server")
      server.server.stop()
    }
  }

}
