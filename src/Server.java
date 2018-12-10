import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    boolean started = false;
    ServerSocket ss = null;
    List<Client> clints = new ArrayList<Client>();

    public static void main(String[] args){
        new Server().start();
    }

    public void start(){
        try {
            ss = new ServerSocket(9999);
            started = true;
            while(started){
                Socket s = ss.accept();
                Client c = new Client(s);
                System.out.println("A client connect.");
                new Thread(c).start();
                clints.add(c);
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Client implements Runnable{
        private Socket s;
        private DataInputStream dis = null;
        private boolean connected = false;
        private DataOutputStream dos = null;

        public Client(Socket s){
            this.s = s;
            try {
                dis = new DataInputStream(s.getInputStream());
                dos = new DataOutputStream(s.getOutputStream());
                connected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void send(String str){
            try {
                dos.writeUTF(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (connected) {
                    String str = dis.readUTF();
                    for(int i = 0; i < clints.size(); i ++){
                        Client c = clints.get(i);
                        c.send(str);
                    }
                    System.out.println(str);
                }
            }catch (IOException e) {
                //e.printStackTrace();
            }finally {
                try {
                    dis.close();
                    dos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
