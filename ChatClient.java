//Joe Adams
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class ChatClient{

	//Class Data
	public boolean on;
	Thread input;
	OutgoingListener output;
	public ChatFrame frame;
	
	//Starts program
	public static void main(String[] args){
		new ChatClient();
        }



        public ChatClient(){
                frame = new ChatFrame();
                on = true;
		
		try{
			//Contact Socket and set up communications	

                        Socket socket = new Socket("000.00.00.000",8000);
			PrintWriter toServer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
			frame.print("Server connected to  at "+new Date());
			
			//make a new thread to listen to the server
			input = new Thread(new IncomingListener(socket, this));
			//connecst the gui to the server
			output =new OutgoingListener(toServer,this);
			ChatFrame.toServer = output;
			input.start();
                }
		catch(IOException ex){
			System.err.println(ex);
		}
        }
}


//listens to the server
class IncomingListener implements Runnable{


	private Socket socket;
	private ChatClient client;


	public IncomingListener(Socket socket, ChatClient client){
		this.socket = socket;
		this.client = client; 
	}
	
	//for the thread
	public void run(){


		try{
			
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			

			while(client.on){
				
				while(!input.ready()){
					if(!client.on){
						System.exit(0);
					}
				}
				String next = input.readLine();
				if(next == null){
					client.on = false;
				}
				else{
					client.frame.print(next);
				}
			}
		}
		catch(IOException ex){
			System.err.println(ex);
		}
	}
}	


//Actions to be taken when "enter" is pressed
class OutgoingListener {
	private ChatClient client;
	private PrintWriter toServer;
	
	public OutgoingListener(PrintWriter printer, ChatClient client){
		toServer = printer;
		this.client = client;
	}

	public void print(String messg){
               	toServer.println(messg);
	}
}


//GUI Stuff
class ChatFrame extends JFrame{
	private JTextField jtfInput;
	private JButton jbEnter;
	private JTextArea jlChat;
	private JPanel jpBottom;
	private JPanel jpChat;
	private JPanel jpAll;
	public static OutgoingListener toServer;
	private JScrollPane jsPane;

	public ChatFrame(){
		super();
		jtfInput = new JTextField("Enter Text Here",33);
		jbEnter = new JButton("Enter");
		toServer = null;
		
		//Listens for the "enter" button to get pushed
		jbEnter.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String text = jtfInput.getText();
					toServer.print(text);
					jtfInput.setText("");
				}
			});
					
		JRootPane rootPane = this.getRootPane();
		rootPane.setDefaultButton(jbEnter);
		
		jlChat = new JTextArea();
		jlChat.setLineWrap(true);
		jlChat.setWrapStyleWord(true);
		Color color = new Color(255,255,255);
		//Color color2 = new Color(255,100,0);
		//Color color2 = Color.GRAY;
		jlChat.setBackground(color);
		//jlChat.setForeground(color2);
		//jtfInput.setForeground(color2);
		jtfInput.setBackground(color);
		jtfInput.setBorder(BorderFactory.createEmptyBorder());
		//jtfInput.setCaretColor(color2);
		jbEnter.setVisible(false);
		Font font1 = new Font("Courier",Font.PLAIN,13);
		jtfInput.setFont(font1);
		jlChat.setFont(font1);

		jsPane = new JScrollPane(jlChat);
		jsPane.setVerticalScrollBar(jsPane.createVerticalScrollBar());
		jsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsPane.setOpaque(false);
		jlChat.setEditable(false);
		jpBottom = new JPanel();
		jpChat = new JPanel();
		jpAll = new JPanel();
		
		jpBottom.setLayout(new FlowLayout());
		jpBottom.add(jtfInput);
		jpBottom.add(jbEnter);
		jsPane.setBorder(BorderFactory.createEmptyBorder());
		jpAll.setLayout(new BorderLayout());
		jpAll.add(jsPane,BorderLayout.CENTER);
		jpAll.add(jpBottom, BorderLayout.PAGE_END);
		this.add(jpAll);
		add(jpAll);
		

		this.setTitle("Chat");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(400,400);
		this.setVisible(true);
		this.setBackground(color);
		this.setFont(font1);
	}
	
	//prints messg to jtextarea
	public void print(String messg){
		jlChat.append(messg+"\n");
		jsPane.getVerticalScrollBar().setValue(jsPane.getVerticalScrollBar().getMaximum());
	}
}


