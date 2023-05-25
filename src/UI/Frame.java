package UI;

import Component.MemManage;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.*;

public class Frame extends JFrame {
	// 在java中serialVersionUID是唯一控制着能否反序列化成功的标志， 只要这个值不一样，就无法反序列化成功。
	private static final long serialVersionUID = 7794841109938881742l;// 强制修改Uid删除警告

	// 主视图容器
	private JPanel contentPane;

	// 创建一个内存管理
	private MemManage MM;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame frame = new Frame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// 初始化UI界面
	@SuppressWarnings("deprecation")
	public void InitUI() {
		MM = new MemManage();
		// UI界面设置
		for (int i = 0; i < MM.sizeFrame; i++) {
			MM.vLabelFrame.add(new JLabel("Frame" + String.valueOf(i)));
			MM.vLabelFrame.get(i).move(10 + i * 140, 20);
			MM.vLabelFrame.get(i).setSize(70, 20);
			contentPane.add(MM.vLabelFrame.get(i));
		}
		// 数值
		for (int i = 0; i < MM.sizeFrame; i++) {
			MM.vLcd.add(new JLabel(String.valueOf(-1)));
			MM.vLcd.get(i).move(10 + i * 140, 30);
			MM.vLcd.get(i).resize(60, 60);
			contentPane.add(MM.vLcd.get(i));
		}
		// 显示器
		for (int i = 0; i < MM.sizeFrame; i++) {
			MM.vBlockList.add(new DefaultListModel<String>());
			MM.vBlockList.get(i).addElement("访问记录");
			JList<String> list = new JList<String>(MM.vBlockList.get(i));
			JScrollPane midScPane = new JScrollPane(list);
			midScPane.setViewportView(list);
			midScPane.move(60 + i * 140, 20);
			midScPane.resize(60, 150);
			contentPane.add(midScPane);
		}
		{ // 输出详细指令信息初始化UI
			// 输出详细指令信息初始化UI
			MM.instListAll = new DefaultListModel<String>();
			MM.instAllPage = new JList<String>(MM.instListAll);
			JScrollPane midScPane = new JScrollPane(MM.instAllPage);
			midScPane.setViewportView(MM.instAllPage);
			midScPane.move(30, 220);
			midScPane.resize(100, 200);
			contentPane.add(midScPane);
			// 缺页指令
			MM.instListFault = new DefaultListModel<String>();
			MM.instFaultData = new JList<String>(MM.instListFault);
			JScrollPane midScPane1 = new JScrollPane(MM.instFaultData);
			midScPane1.setViewportView(MM.instFaultData);
			midScPane1.move(180, 220);
			midScPane1.resize(100, 200);
			contentPane.add(midScPane1);
			// 访问日志
			MM.logList = new DefaultListModel<String>();
			MM.logData = new JList<String>(MM.logList);
			JScrollPane midScPane2 = new JScrollPane(MM.logData);
			midScPane2.setViewportView(MM.logData);
			midScPane2.move(570, 335);
			midScPane2.resize(250, 320);
			contentPane.add(midScPane2);
			// 页表,并进行初始化，由于这里未使用如Tablemodel的表数据模型，只能将所有的页行添加，JTable不能直接添加行数据
			String[] columnNames = { "逻辑页号", "内存块号" };
			String[][] data = { { "0", "-1" }, { "1", "-1" }, { "2", "-1" }, { "3", "-1" }, { "4", "-1" },
					{ "5", "-1" }, { "6", "-1" }, { "7", "-1" }, { "8", "-1" }, { "9", "-1" }, { "10", "-1" },
					{ "11", "-1" }, { "12", "-1" }, { "13", "-1" }, { "14", "-1" }, { "15", "-1" }, { "16", "-1" },
					{ "17", "-1" }, { "18", "-1" }, { "19", "-1" }, { "20", "-1" }, { "21", "-1" }, { "22", "-1" },
					{ "23", "-1" }, { "24", "-1" }, { "25", "-1" }, { "26", "-1" }, { "27", "-1" }, { "28", "-1" },
					{ "29", "-1" }, { "30", "-1" }, { "31", "-1" }, { "32", "_avoid_out_" } };// 添加32号行防止越界
			MM.qPageTable = new JTable(data, columnNames);
			MM.qPageTable.setEnabled(false);
			JScrollPane midScPane3 = new JScrollPane(MM.qPageTable);
			midScPane3.setViewportView(MM.qPageTable);
			midScPane3.move(570, 40);
			midScPane3.resize(250, 250);
			contentPane.add(midScPane3);
		}
	}

	/*
	 * 下面几个是事件监听函数，处理按钮点击事件，实现用户交互
	 */
	// 点击开始按钮
	public void start_clicked(ActionEvent e) {
		MM.instOrderGenerate();
		MM.instOrderShow();
		choose_label.setEnabled(true);
		fifoButton.setEnabled(true);
		lruButton.setEnabled(true);
	}

	// 点击退出按钮，终止程序
	public void exit_clicked(ActionEvent e) {
		System.exit(0);
	}

	// 点击FIFO选择调度方式FIFO
	public void fifo_clicked(ActionEvent e) {
		startButton.setEnabled(false);
		lruButton.setEnabled(false);
		MM.algo = Component.AlgoType.FIFO;

		f1_Button.setEnabled(true);
		f10_Button.setEnabled(true);
		fAll_Button.setEnabled(true);
	}

	// 点击LRU选择调度方式为LRU
	public void lru_clicked(ActionEvent e) {
		startButton.setEnabled(false);
		fifoButton.setEnabled(false);
		MM.algo = Component.AlgoType.LRU;

		f1_Button.setEnabled(true);
		f10_Button.setEnabled(true);
		fAll_Button.setEnabled(true);
	}

	// 单步访问一条指令

	// 访问一条指令
	public void OneStep() {
		// 指令已经访问完
		if (MM.currentInst == MM.sizeInst) {
			fifoButton.setEnabled(false);
			lruButton.setEnabled(false);
			startButton.setEnabled(false);
			f1_Button.setEnabled(false);
			f10_Button.setEnabled(false);
			fAll_Button.setEnabled(false);
			return;
		}

		fedinstnum.setText(String.valueOf(MM.currentInst));
		MM.instAllPage.setSelectedIndex(MM.currentInst);
		MM.instAllPage.ensureIndexIsVisible(MM.currentInst);// 设置跟随显示

		if (MM.algo == Component.AlgoType.FIFO)
			MM.AlgoFIFO(MM.instArray.get(MM.currentInst++));
		else if (MM.algo == Component.AlgoType.LRU)
			MM.AlgoLRU(MM.instArray.get(MM.currentInst++));
		// 刷新显示
		float faultRate = (float) MM.pageFaultCount / (float) MM.currentInst;
		fedinstnum.setText(String.valueOf(MM.currentInst));// 更新已经访问指令数量
		queinstnum.setText(String.valueOf(MM.pageFaultCount));// 更新缺页数量
		querate.setText(String.valueOf(faultRate));// 更新缺页率
	}

	// 点击访问1条指令、10条指令、全部指令
	public void f1_clicked(ActionEvent e) {
		OneStep();
	}

	// 点击访问10条指令
	public void f10_clicked(ActionEvent event) {
		// 访问过程中不能点击
		f1_Button.setEnabled(false);
		fAll_Button.setEnabled(false);

//		for (int i = 0; i < 10 && MM.currentInst < MM.sizeInst; i++) {
//			OneStep();
//		}
		// 设定可以看到每次的更新，避免直接看到最后结果
		Timer timer = new Timer(30, null);
		int nowInst=MM.currentInst;//记录当前指令位置，便于判断后面定时器执行了几次
		ActionListener taskPerformer = e -> {
			OneStep();
			if (MM.currentInst == nowInst + 10 ||MM.currentInst >= MM.sizeInst)
				timer.stop();
		};
		timer.addActionListener(taskPerformer);
		timer.start();

		f1_Button.setEnabled(true);
		fAll_Button.setEnabled(true);

		// 指令已经访问完
		if (MM.currentInst == MM.sizeInst) {
			fifoButton.setEnabled(false);
			lruButton.setEnabled(false);
			startButton.setEnabled(false);
			f1_Button.setEnabled(false);
			f10_Button.setEnabled(false);
			fAll_Button.setEnabled(false);
			return;
		}
	}

	// 点击访问所有剩余指令
	public void fAll_clicked(ActionEvent event) {
		// 访问过程中不能点击
		f1_Button.setEnabled(false);
		f10_Button.setEnabled(false);

//		while (MM.currentInst < MM.sizeInst) {
//			OneStep();
//		}
		// 设定可以看到每次的更新，避免直接看到最后结果
		Timer timer = new Timer(30, null);
		ActionListener taskPerformer = e -> {
			OneStep();
			if (MM.currentInst == MM.sizeInst) {// 指令已经访问完
				timer.stop();
				fifoButton.setEnabled(false);
				lruButton.setEnabled(false);
				startButton.setEnabled(false);
				f1_Button.setEnabled(false);
				f10_Button.setEnabled(false);
				fAll_Button.setEnabled(false);
			}
		};
		timer.addActionListener(taskPerformer);
		timer.start();
	}

	// 构造函数，创建整体的UI
	public Frame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		// 设置窗口标题
		setTitle("Memory Management");

		InitUI();

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("指令序列");
		lblNewLabel.setBounds(55, 200, 58, 15);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("缺页指令序列");
		lblNewLabel_1.setBounds(172, 200, 95, 15);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("已访问指令数量");
		lblNewLabel_2.setBounds(55, 450, 95, 15);
		contentPane.add(lblNewLabel_2);

		JLabel lblNewLabel_3 = new JLabel("缺页指令数量");
		lblNewLabel_3.setBounds(55, 496, 95, 15);
		contentPane.add(lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("缺页率");
		lblNewLabel_4.setBounds(55, 543, 71, 15);
		contentPane.add(lblNewLabel_4);

		fedinstnum = new JLabel("0");
		fedinstnum.setBounds(192, 450, 58, 15);
		contentPane.add(fedinstnum);

		queinstnum = new JLabel("0");
		queinstnum.setBounds(192, 496, 58, 15);
		contentPane.add(queinstnum);

		querate = new JLabel("\\");
		querate.setBounds(192, 543, 58, 15);
		contentPane.add(querate);

		choose_label = new JLabel("请选择页面调度算法");
		choose_label.setBounds(305, 239, 127, 15);
		choose_label.setEnabled(false);
		contentPane.add(choose_label);

		JLabel lblNewLabel_9 = new JLabel("访问指令日志");
		lblNewLabel_9.setBounds(559, 318, 95, 15);
		contentPane.add(lblNewLabel_9);

		JLabel lblNewLabel_10 = new JLabel("页表");
		lblNewLabel_10.setBounds(559, 20, 58, 15);
		contentPane.add(lblNewLabel_10);

		startButton = new JButton("开始");
		startButton.addActionListener(this::start_clicked);// 绑定事件监听
		startButton.setBounds(305, 196, 97, 23);
		contentPane.add(startButton);

		exitButton = new JButton("退出");
		exitButton.setBounds(424, 196, 97, 23);
		exitButton.addActionListener(this::exit_clicked);// 绑定事件监听
		contentPane.add(exitButton);

		fifoButton = new JButton("FIFO");
		fifoButton.setBounds(305, 264, 97, 23);
		fifoButton.addActionListener(this::fifo_clicked);// 绑定事件监听
		fifoButton.setEnabled(false);
		contentPane.add(fifoButton);

		lruButton = new JButton("LRU");
		lruButton.setBounds(424, 264, 97, 23);
		lruButton.addActionListener(this::lru_clicked);// 绑定事件监听
		lruButton.setEnabled(false);
		contentPane.add(lruButton);

		f1_Button = new JButton("访问 1 条指令");
		f1_Button.setBounds(316, 374, 127, 23);
		f1_Button.addActionListener(this::f1_clicked);// 绑定事件监听
		f1_Button.setEnabled(false);
		contentPane.add(f1_Button);

		f10_Button = new JButton("访问 10 条指令");
		f10_Button.setBounds(316, 416, 127, 23);
		f10_Button.addActionListener(this::f10_clicked);// 绑定事件监听
		f10_Button.setEnabled(false);
		contentPane.add(f10_Button);

		fAll_Button = new JButton("访问全部指令");
		fAll_Button.setBounds(316, 466, 127, 23);
		fAll_Button.addActionListener(this::fAll_clicked);// 绑定事件监听
		fAll_Button.setEnabled(false);
		contentPane.add(fAll_Button);

		ways_label = new JLabel("访问指令方式");
		ways_label.setBounds(316, 339, 127, 15);
		ways_label.setEnabled(false);
		contentPane.add(ways_label);
	}

	// 原来是在design页面创建的，现调整为私有成员方便设置值

	// 私有成员
	private JLabel fedinstnum;
	private JLabel queinstnum;
	private JLabel querate;
	private JLabel ways_label;
	private JLabel choose_label;
	private JButton startButton;
	private JButton exitButton;
	private JButton fifoButton;
	private JButton lruButton;
	private JButton f1_Button;
	private JButton f10_Button;
	private JButton fAll_Button;
}
