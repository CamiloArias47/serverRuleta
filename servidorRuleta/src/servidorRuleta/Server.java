package servidorRuleta;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

	public Server() {
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
			servidor = new ServerSocket( 12345, 2 );
			
		} // fin de try
		catch ( IOException excepcionES ){
			excepcionES.printStackTrace();
			System.out.println("[server] Error: Puerto no abierto");
			mostrarMensaje("[server] Error: Puerto no abierto\n");
			//System.exit(1); //cierra la aplicacion
		} // fin de catch

		System.out.println("[server] Puerto abierto");
		mostrarMensaje("[server] Puerto abierto\n");
		conect();
		
		//ejecutarJuego = Executors.newFixedThreadPool(1); // crea el administrador de los hilos jugador
		mostrarMensaje("[server] fin del metodo CreateHost\n");
	}

	
	public void conect() {
		mostrarMensaje("[server] Intentando conectar\n");
		try {
			conexion = servidor.accept(); 
			mostrarMensaje("[server] Esperando conexion\n");
		} // fin de try
		catch ( IOException excepcionES ) {
			mostrarMensaje("[server] Error en conexion\n");
			excepcionES.printStackTrace();
			//System.exit(1);
		} // fin de catch
		//disconnect();
		mostrarMensaje("[server] ... \n");
	}
	
	public void disconnect() {
		try {
			conexion.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
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
	
	private class EscuchaMouse extends MouseAdapter{
		public void mouseClicked( MouseEvent e) {
			mostrarMensaje("[server] Boton clikeado\n");
			closeServerSocket();
		}
	}
	
}
