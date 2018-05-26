package bingoo;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

class Bingo extends Frame{

	final int SIZE = 5; // �������� ũ��
	int bingoCnt = 0; // �ϼ��� ������ ��
	boolean winCheck=false;
	Button[] btnArr = null;
	boolean[][] bArr = new boolean[SIZE][SIZE]; // ������ üũ���� Ȯ���� ���� �迭
	boolean isBingo = false;
	boolean click=true;
	// ������ ��ư�� ����� ���ڿ�, �������� ũ�⿡ ���� �̵��� �Ϻθ� ���� �� �ִ�.
	String[] values = { "�۽�", "��η�", "��â��", "��õ��ǥ", "���", "���ü�", "������", "�аԺ���", "�׶��ָ�", "�ٹ�", "��Ŭ����", "�鰳", "�𺧷�", "����",
			"���", "��������", "������", "����û��", "��õ��", "���", "���Ƿ�", "������", "��", "����", "����", "���ȣ", "�����", "������", "���̽�", "����",
			"���̳���", "����Ŭ��", "���ִ�", "�������", "������", "������", "����", "����", "ī��", "Ĳ��", "�¿�", "��Ƽ��", "�丣�Ҹ�", "��ǳ", "�ͺ�����",
			"��ũ�Ҵ�", "�ϴ���", "�Ϸ�", "�Ѱ���", "�淹����", "ȭ����", "���" };

	DataOutputStream out;

	Bingo(Socket socket) {
		this("Bingo Game Ver1.0", socket);
	}

	Bingo(String title, Socket socket) {

		super(title);

		setLayout(new GridLayout(SIZE, SIZE));

		MyEventHandler handler = new MyEventHandler();
		addWindowListener(handler);

		btnArr = new Button[SIZE * SIZE];

		shuffle();

		// Frame�� ��ư�� �߰��Ѵ�.
		for (int i = 0; i < SIZE * SIZE; i++) {

			btnArr[i] = new Button(values[i]); // ���ڿ��迭 values�� ���� ��ư�� Label�� �Ѵ�.
			add(btnArr[i]);
			btnArr[i].addActionListener(handler);
		}

		setBounds(500, 200, 300, 300);
		setVisible(true);

		try {
			out = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {}

	}

	void shuffle() {
		for (int i = 0; i < values.length * 2; i++) {
			int r1 = (int) (Math.random() * values.length);
			int r2 = (int) (Math.random() * values.length);

			String tmp = values[r1];
			values[r1] = values[r2];
			values[r2] = tmp;
		}
	}

	void print() { // �迭 bArr�� ����Ѵ�.
		for (int i = 0; i < bArr.length; i++) {
			for (int j = 0; j < bArr.length; j++) {
				System.out.print(bArr[i][j] ? "O" : "X");
			}
			System.out.println();
		}
		System.out.println("----------------");
		// System.out.println(bingoCnt);
	}

	boolean checkBingo() { // ���� �ϼ��Ǿ������� Ȯ���Ѵ�.
		bingoCnt = 0;
		int garoCnt = 0;
		int seroCnt = 0;
		int crossCnt1 = 0;
		int crossCnt2 = 0;

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (i + j == SIZE - 1 && bArr[i][j])
					crossCnt2++;
				if (i == j && bArr[i][j])
					crossCnt1++;
				if (bArr[i][j])
					garoCnt++;
				if (bArr[j][i])
					seroCnt++;
			}

			if (garoCnt == SIZE)
				bingoCnt++;
			if (seroCnt == SIZE)
				bingoCnt++;

			// if(bingoCnt>=SIZE) return true;
			garoCnt = 0;
			seroCnt = 0;
		}

		if (crossCnt1 == SIZE)
			bingoCnt++;
		if (crossCnt2 == SIZE)
			bingoCnt++;

		// System.out.println(bingoCnt);
		return bingoCnt >= SIZE;
	}

	class MyEventHandler extends WindowAdapter implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			Button btn = (Button) ae.getSource();

			String str = ae.getActionCommand();
			
			click = false;
			btn.setEnabled(false);
			
//			for (int i = 0; i < SIZE * SIZE; i++) {
//				btnArr[i].setEnabled(false);
//			}		
			
			
			
			// 2. ������ ��ư�� �����Ѵ�
			try {
				out.writeUTF("300|" + str);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// print();
			

		}
	}

	public void windowClosing(WindowEvent e) {
		e.getWindow().setVisible(false);
		e.getWindow().dispose();
		System.exit(0);
	}
	
	public void winCheck(String bingo) {
		String tmp;
		
		for (int i = 0; i < SIZE * SIZE; i++) {
			btnArr[i].setEnabled(false);
		}	
		
		if (winCheck) {
			tmp= "�¸�!";		
		}
		else{
			tmp= "�й�!";
		}
		
		int option = JOptionPane.showConfirmDialog(null, tmp+ " �ٽý���?", "title", JOptionPane.YES_NO_OPTION);

		// ��
		if (option == 0) {
			// ����� ������ �����
			setVisible(false);
			try {
				out.writeUTF("400|���������");
			} catch (IOException e) {}// �ٽý���
		} 
		// �ƴϿ�
		else if (option == 1) {// ����
			try {
				out.writeUTF("400|���Ӳ���");
			} catch (IOException e) {}
		}
	}

	public void bingoCheck(String bingo) {
		
		
		Button tmp = null;
		
		// 1. ���� ��ư üũ
		
		for (int i = 0; i < btnArr.length; i++) {
			if (btnArr[i].getLabel().equals(bingo)) {
				tmp = btnArr[i];
				bArr[i / SIZE][i % SIZE] = true;
				break;
			}
		}	
		
		try {
			tmp.setBackground(Color.RED);
			tmp.setEnabled(false);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		if (checkBingo()) {

			if(!isBingo) {
				isBingo=true;
			System.out.println("Bingo~!!!");

			Frame f = new Frame("Parent");
			f.setSize(300, 200);
			// parent Frame f , modal true Dialog . �� �� �ϰ� �� �� �ؼ� �ʼ����� �� ��
			final Dialog info = new Dialog(f, "Information", true);
			info.setSize(140, 90);
			info.setLocation(50, 50); // parent Frame , �� �ƴ� ȭ������� ��ġ
			info.setLayout(new FlowLayout());
			Label msg = new Label("���� ������ �̱�", Label.CENTER);
			Button ok = new Button("����");
			
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) { // OK . ��ư�� ������ �����
					// info.setVisible(false); // Dialog . �� �Ⱥ��̰� �Ѵ�
					winCheck=true;
					info.dispose(); // Dialog . �� �޸𸮿��� ���ش�
					f.setVisible(false);

					try {
						out.writeUTF("400|��������");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// �̱��� ���� ����??;;

					// ������ ��ư disable
						

				}
			});
			
			info.add(msg);
			info.add(ok);
			f.setVisible(true);
			info.setVisible(true); // Dialog . �� ȭ�鿡 ���̰� �Ѵ�
			}
		}
		

	}
	//@180526
	public void turnCheck(boolean turn) {
		setEnabled(turn);
	}

}
