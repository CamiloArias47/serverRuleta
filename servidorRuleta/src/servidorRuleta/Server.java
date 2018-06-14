package servidorRuleta;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
	
	private ServerSocket servidor; 
	
	public Server() {
		// establece el servidor
		try{
			System.out.println("[server] Abriendo puerto");
			servidor = new ServerSocket( 12345, 2 );
		} // fin de try
		catch ( IOException excepcionES ){
			excepcionES.printStackTrace();
			System.out.println("[server] Error: Puerto no abierto");
			System.exit(1); //cierra la aplicacion
		} // fin de catch
		
		System.out.println("[server] Puerto abierto");
	}
	
}
