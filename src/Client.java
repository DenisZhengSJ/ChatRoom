import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends Frame {
    TextField tf = new TextField();
    TextArea ta = new TextArea();
    Socket s = null;
    DataOutputStream dos = null;
    DataInputStream dis = null;
    private boolean connected = false;
    String ip;

    public static void main(String[] args){
        new Client().launchFrame();
    }

    private void launchFrame() {
        setLocation(400,300);
        this.setSize(300,300);
        this.setTitle("超级简陋的聊天室");
        ta.setEditable(false);
        ta.getScrollbarVisibility();
        ta.setCaretPosition(ta.getText().length());
        add(tf,BorderLayout.SOUTH);
        add(ta,BorderLayout.NORTH);
        pack();
        setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });
        tf.addActionListener(new tfListener());
        connect();

        new Thread(new ReceiveThread()).start();
    }

    public void connect(){
        try {
            s = new Socket("127.0.0.1",9999);
            dos = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        try {
            connected = false;
            dos.close();
            dis.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class tfListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String str = tf.getText();
            tf.setText("");

            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                ip = s.getInetAddress().getHostAddress();
                dos.writeUTF("时间:"+ df.format(new Date()) + "  ip:" + ip);
                dos.writeUTF(str);
                dos.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private class ReceiveThread implements Runnable{

        @Override
        public void run() {
            try {
                while(connected) {
                    String str = dis.readUTF();
                    ta.append(str + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
