package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import server.Conta;
/** 
 * Um objeto remoto � uma inst�ncia de uma classe que implementa uma interface remota. 
 * 
 * Uma interface remota estende a interface java.rmi.Remote e declara um conjunto de m�todos remotos. 
 * 
 * Cada m�todo remoto deve declarar java.rmi.RemoteException (ou uma superclasse de RemoteException) 
 * em sua cl�usula de throws, al�m de quaisquer exce��es espec�ficas da aplica��o.*/


// interface do objeto servidor

public interface Operacoes_banco extends Remote {
	
	// m�todo a ser implementado
    Conta fazer_login(String usuario, String senha) throws RemoteException;
    Conta criar_conta(Conta c) throws RemoteException;
    Conta editar_conta(Conta c) throws RemoteException;
    boolean remover_conta(long id) throws RemoteException;
   	String listar_contas() throws RemoteException;
    boolean sacar_valor(Conta c,double valor) throws RemoteException;
    boolean depositar_valor(Conta c,double valor) throws RemoteException;
    boolean transferir_saldo(Conta origem,double valor,long id_conta_alvo) throws RemoteException;
    double consultar_saldo (Conta c) throws RemoteException;
    boolean aderir_renda_fixa(Conta c) throws RemoteException;
    String listar_contas_a_serem_removidas() throws RemoteException;
    
}
