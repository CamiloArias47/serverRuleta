package servidorRuleta;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import java.awt.event.MouseEvent;

public class Server extends JFrame{

	private ServerSocket servidor;
	private Socket conexion;
	private ExecutorService ejecutarJuego; //Facilita la gesti�n de los hilos jugador
	private JTextArea areaSalida;
	private JPanel contenedorSur;
	private JButton parar;

	/** iterador que ira aumentando para facilitar el repaint de la img.*/
	private int iteradorImagen=0;
	/** Atributo que se toma como temporizzador*/
	private int timeRuleta = 400;

	/** Array con los jugadores de la ruleta*/
	//private ArrayList<Player> players = new ArrayList();
	private Player[] players;

	public Server() {
		players = new Player[2];
		ejecutarJuego = Executors.newFixedThreadPool(4); // crea el administrador de los hilos jugador

		Gui();
		createHost();
	}

	public void Gui() {
		areaSalida = new JTextArea(); // crea objeto JTextArea para mostrar la salida
		areaSalida.setEditable(false);
		add( areaSalida, BorderLayout.CENTER );
		areaSalida.setText( "Servidor esperando conexiones\n" );
		contenedorSur = new JPanel();

		EscuchaMouse evento = new EscuchaMouse();
		parar = new JButton("Parar servidor");
		parar.addMouseListener(evento);
		contenedorSur.add(parar);

		add(contenedorSur, BorderLayout.SOUTH);

		setSize( 300, 300 ); // establece el tama�o de la ventana
		setVisible( true ); // muestra la ventana
	}

	public void createHost() {
		// establece el servidor
		try{
			System.out.println("[server] Abriendo puerto");
			mostrarMensaje("[server] Abriendo puerto\n");
			servidor = new ServerSocket(12345);

		} // fin de try
		catch ( IOException excepcionES ){
			excepcionES.printStackTrace();
			System.out.println("[server] Error: Puerto no abierto");
			mostrarMensaje("[server] Error: Puerto no abierto\n");
			//System.exit(1); //cierra la aplicacion
		} // fin de catch

		 mostrarMensaje("[server] server address: "+servidor.getLocalSocketAddress()+"\n");
		 mostrarMensaje("[server] server puerto: "+servidor.getLocalPort()+"\n");

		conect();
		mostrarMensaje("[server] fin del metodo CreateHost\n");
	}

	public void conect() {
		// espera a que se conecte cada cliente
		mostrarMensaje("[server] Intentando conectar con clientes\n");
		for ( int i = 0; i < 4; i++ ) {
			try {
				mostrarMensaje("[server] Esperando conexion:"+i+"\n");
				//Socket jugador = servidor.accept();
				//mostrarMensaje("[server] Conectado con jugador:"+i+"\n");
				//players.add(new Player(jugador, i) ); // crea un jugador con la conexion de algun usuario que se conecte
				players[i] = new Player(servidor.accept(), i);
				mostrarMensaje("[server] Conectado con jugador:"+i+"\n");
				//players.get(i).run();
				mostrarMensaje("[server] total jugadores "+players.length+"\n");
				mostrarMensaje("[server] jugador "+i+" "+players[i]+"\n");
				//ejecutarJuego.execute(players.get(i)); // ejecuta el hilo jugador
				ejecutarJuego.execute(players[i]);
			} // fin de try
			catch ( IOException excepcionES ) {
				mostrarMensaje("[server] Error al conectar con el jugador\n");
				excepcionES.printStackTrace();
				//System.exit(1);
			} // fin de catch
		} // fin de for

		mostrarMensaje("[server] Todos los jugadores conectados \n");
	}

	public void closeServerSocket() {
		mostrarMensaje("[server] cerrando serverSocket\n");
		try {
			servidor.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			mostrarMensaje("[server] Errorn al cerrar serverSocket\n");
			e.printStackTrace();
		}
	}

	private void mostrarMensaje( final String mensajeAMostrar ){
		SwingUtilities.invokeLater( new Runnable() {
										 public void run() {
											 areaSalida.append( mensajeAMostrar ); // agrega el mensaje
										 } // fin del metodo run
									   } // fin de la clase interna
								  ); // fin de la llamada a SwingUtilities.invokeLater
	} // fin del metodo mostrarMensaje

  public void girarRuleta(){
		//esperar que la ruleta pare
		Thread delayGiro = new Thread(){
            public synchronized void run(){
							try{
								while(timeRuleta>0){
									timeRuleta-=1;
									iteradorImagen = iteradorImagen+1;
									players[0].sendMesageToClient(Integer.toString(iteradorImagen) );
									Thread.sleep(10);
								}

							}catch(InterruptedException e) {
								return;
							}
            }
		};

		timeRuleta = 400;
		delayGiro.start();
	}

	private class EscuchaMouse extends MouseAdapter{
		public void mouseClicked( MouseEvent e) {
			mostrarMensaje("[server] Boton clikeado\n");
			closeServerSocket();
		}
	}


	private class Player implements Runnable{

		private Socket socket;
		private int id;
		private Scanner entrada; // entrada del cliente
		private Formatter salida; // salida al cliente

		public Player(Socket socket, int id){
				this.socket = socket;
				this.id     = id;

				// obtiene los flujos del objeto Socket
				try {
					entrada = new Scanner( this.socket.getInputStream() );
					salida = new Formatter( this.socket.getOutputStream() );
					salida.flush();
				}
				catch ( IOException excepcionES ){
					mostrarMensaje("[server] Error al obtener canales de entrada y salida\n");
					excepcionES.printStackTrace();
					//System.exit( 1 );
				} // fin de catch
		}

		public void run(){
			mostrarMensaje("[server] run() = Jugador "+id+" conectado \n");
			salida.format( "%s\n", "Conectado al servidor" );
			salida.flush(); // vacia la salida

			girarRuleta();
		}

		public void sendMesageToClient(String msn){
			salida.format("%s\n", msn);
			salida.flush();
		}

		public void disconnect() {
			try {
				conexion.close();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}

	}

}
