package Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

import java.awt.Rectangle;

import java.util.Vector;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

public class MemManage {
	private static final int gSizeFrame = 4;
	private static final int gSizeInst = 320;
	private static final int gCapacityPage = 10; // 每个页面的容量为10，可以容纳10个指令

	// 构造函数
	public MemManage() {
		initData();
	}

	// 析构函数
	public void finalize() {
		/*
		 * 是Java中的一个方法，用于在对象被垃圾回收之前执行一些清理操作。 这个方法通常被子类重写以实现自定义的清理逻辑。当一个对象即将
		 * 被垃圾回收时，Java虚拟机会自动调用该对象的finalize()方法。但 是，由于finalize()方法的调用时机是不确定的，因此不建议在该方
		 * 法中执行重要的清理操作。相反，应该使用try-finally块或者Java 7
		 * 中引入的try-with-resources语句来确保资源的正确释放。希望这可 以帮助你更好地理解finalize()方法的作用和使用方式
		 */
		System.out.println("程序已结束！");
	}

	// 数据空间初始化
	public void initData() {
		sizeFrame = gSizeFrame;
		sizeInst = gSizeInst;
		capacityPage = gCapacityPage;

		// 初始化空间
		instArray = new Vector<Integer>();
		block = new Vector<Integer>();
		priorityLRU = new Vector<Integer>();
		pageTable = new Vector<Integer>();
		queueFIFO = new LinkedList<Integer>();

		vLcd = new Vector<JLabel>();
		vBlockList = new Vector<DefaultListModel<String>>();
		vLabelFrame = new Vector<JLabel>();

		// 已访问指令数量和缺页指令数量的初始化
		currentInst = 0;
		pageFaultCount = 0;

		instArray.setSize(sizeInst); // 指令大小初始化
		block.setSize(sizeFrame); // 内存块数量初始化
		priorityLRU.setSize(sizeFrame); // 各内存块优先级
		pageTable.setSize(sizeInst / capacityPage); // 页表数目，为一级页表

		// 指令地址初始化，也就是逻辑地址，我们假设指令顺序存放，那么就是以10个为单位顺序存放在一个页中
		for (int i = 0; i < sizeInst; i++)
			instArray.set(i, i);

		// 内存块尚未存放任何逻辑页面
		for (int i = 0; i < block.size(); i++)
			block.set(i, -1);

		// 页表内容初始化
		for (int i = 0; i < pageTable.size(); i++)
			pageTable.set(i, -1);

		// LRU内存块优先级初始化，全部设为0
		for (int i = 0; i < priorityLRU.size(); i++)
			priorityLRU.set(i, 0);
	}

	// 产生指令访问顺序
	public void instOrderGenerate() {
		Random rand = new Random();// 伪随机数生成器
		int count = sizeInst / 4; // 一共320条指令，一次循环生成4条指令，一共循环80次，该处4与内存块数量无关，取决于指令生成算法
		for (int i = 0; i < count; i++) {
			int m = rand.nextInt(sizeInst); // [0, sizeInst)；由于指令可重复指令并不考虑320条指令全不同
			while (m == 0) {
				m = rand.nextInt(sizeInst); // 如果m是0，后续取余不方便
			}
			int m1 = rand.nextInt(m);// 50%顺序执行,25%前地址部份，25%后地址部份
			instArray.set(i * 4, m1);
			instArray.set(i * 4 + 1, m1 + 1);

			int m2 = rand.nextInt(sizeInst - 1 - m1) + m1 + 2;// 后半部分
			m2 = (m2 >= sizeInst - 1 ? sizeInst - 2 : m2);// 防止指令越界
			instArray.set(i * 4 + 2, m2);
			instArray.set(i * 4 + 3, m2 + 1);
		}
	}

	// 显示指令序列
	public void instOrderShow() {
		for (int i = 0; i < sizeInst; i++) {
			String inst = String.format("%03d", instArray.get(i));
			String instPage = String.valueOf(instArray.get(i) / capacityPage);

			instListAll.addElement(inst + " (" + instPage + ")");
		}
	}

	// 延时函数
	public static void delay(int millis) {
		SwingUtilities.invokeLater(() -> {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				// 处理中断异常
				e.printStackTrace();
			}
		});
	}

	// 两种页面调度算法
	// LRU调度算法
	public void AlgoLRU(int inst) {
		for (int i = 0; i < sizeFrame; i++) {
			if (block.get(i) == inst / capacityPage) {
				priorityLRU.set(i, 0);// i号内存块使用，优先级设为0
				for (int j = 0; j < sizeFrame; j++) {
					if (j != i && block.get(j) != -1) {
						priorityLRU.set(j, priorityLRU.get(j) + 1);// 其他几个内存块的优先级全部降一级
					}
				}
				setBlock(i, inst); // 当前指令所在的页面已在内存内存块，设置
				return;
			}
		}
		// 显示缺页中断；下面是缺页中断的两种情况，需要换出与不需要
		updateFault(inst);
		// 第一种：刚开始有空闲内存
		for (int i = 0; i < sizeFrame; i++) {
			if (block.get(i) == -1) {
				priorityLRU.set(i, 0);
				for (int j = 0; j < sizeFrame; j++) {
					if (j != i && block.get(j) != -1) {
						priorityLRU.set(j, priorityLRU.get(j) + 1);
					}
				}
				setBlock(i, inst);
				return;
			}
		}
		// 第二种：缺页中断，换出最久未被访问内存
		int maxIndex = 0;
		int maxTime = -1;
		for (int i = 0; i < sizeFrame; i++) {
			if (priorityLRU.get(i) > maxTime) {
				maxTime = priorityLRU.get(i);
				maxIndex = i;
			}
		}
		// 使用最久未被访问的内存块
		priorityLRU.set(maxIndex, 0);
		for (int i = 0; i < sizeFrame; i++) {
			priorityLRU.set(i, priorityLRU.get(i) + 1);
		}
		setBlock(maxIndex, inst);
	}

	// FIFO调度算法
	public void AlgoFIFO(int inst) {
		for (int i = 0; i < sizeFrame; i++) {
			if (block.get(i) == inst / capacityPage) {
				setBlock(i, inst); // 当前指令所在的指令页面已在内存，设置后返回
				return;
			}
		}
		// 下面缺页中断；分两种情况，需要换出与不需要
		updateFault(inst);
		// 第一种：刚开始有空闲内存
		for (int i = 0; i < sizeFrame; i++) {
			if (block.get(i) == -1) {
				setBlock(i, inst);
				if (queueFIFO.offer(i) == false)
					System.out.println("内存块队列已满，添加失败！！！");
				return;
			}
		}
		// 第二种：需要替换某一个
		if (queueFIFO.size() == sizeFrame) {
			int freeBlockID = queueFIFO.remove();// 获取头部的内存块号并将其删除
			setBlock(freeBlockID, inst);
			if (queueFIFO.offer(freeBlockID) == false)// 将当前使用的内存块放到队列最后
				System.out.println("内存块队列已满，添加失败！！！");
		}
	}

	// 指令inst将要进入id为blockid的内存块中
	public void setBlock(int blockID, int inst) {
		updateLog(blockID, inst);
		// 不是目标页，需要替换出去
		int instPage = inst / capacityPage;
		if (instPage != block.get(blockID)) {
			block.set(blockID, instPage);
			vBlockList.get(blockID).clear();
			// 清除原有界面中的指令信息，实际中需要把这块复制到磁盘里面
			vBlockList.get(blockID).addElement("访问记录");
			vLcd.get(blockID).setText(Integer.toString(instPage));
		}
		String accessInfo = String.format("%03d", inst);
		vBlockList.get(blockID).addElement(accessInfo);
	}

	// 更新日志信息
	public void updateLog(int blockID, int inst) {
		String newPage = Integer.toString(inst / capacityPage);
		String LogString = "访问指令" + String.format("%03d", inst);
		LogString += " (";
		LogString += newPage;
		LogString += "). ";

		int instPage = inst / capacityPage;
		if (instPage == block.get(blockID)) {
			LogString += "在Frame" + Integer.toString(blockID) + "中";
		} else {
			LogString += "缺页中断. ";
			if (block.get(blockID) != -1) {
				LogString += "Frame" + Integer.toString(blockID) + "换出页" + Integer.toString(block.get(blockID))
						+ ", 装入页" + newPage;
				updatePageTable(block.get(blockID), Integer.toString(-1));// 换出页面的内存块变回空—— -1
			} else {
				LogString += "Frame" + Integer.toString(blockID) + "装入页" + newPage;
			}
			updatePageTable(inst / capacityPage, Integer.toString(blockID));// 装入页的内存块
		}

		logList.addElement(LogString);
		logData.setSelectedIndex(currentInst - 1);// 设置选中的项
		logData.ensureIndexIsVisible(currentInst - 1);
	}

	// 更新页表
	public void updatePageTable(int row, String newItem) {
		qPageTable.setValueAt(newItem, row, 1);
		pageTable.set(row, Integer.parseInt(newItem));// 更新页表的对应关系
		qPageTable.setRowSelectionInterval(row, row);// 该方法可以选中多行，设为相同值选中一行
		Rectangle rect = qPageTable.getCellRect(row, 0, true);
		qPageTable.scrollRectToVisible(rect);
	}

	// 更新缺页指令与缺页率
	public void updateFault(int inst) {
		String faultInst = String.format("%03d", inst);
		String faultPage = Integer.toString(inst / capacityPage);// 页面号

		instListFault.addElement(faultInst + " (" + faultPage + ")");

		pageFaultCount++;// 缺页数增加
		instFaultData.setSelectedIndex(pageFaultCount - 1);
		instFaultData.ensureIndexIsVisible(pageFaultCount - 1);// 设置跟随显示
	}

	// 刷新访问记录，便于对相同指令序列使用不同调度算法进行对比
	public void recover() {
		// 清空内存块
		for (var list : vBlockList) {
			list.clear();
			list.addElement("访问记录");
		}
		// 修改内存块内页号显示
		for (var pagenum : vLcd) {
			pagenum.setText("-1");
		}
		// 修改页表显示
		for (int row = 0; row < 32; row++)
			qPageTable.setValueAt("-1", row, 1);

		// 清空缺页清单
		instListFault.clear();
		// 清空访问信息
		logList.clear();

		// 刷新几个指标
		currentInst = 0;
		pageFaultCount = 0;

		// 内存块尚未存放任何逻辑页面
		for (int i = 0; i < block.size(); i++)
			block.set(i, -1);

		// 页表内容初始化
		for (int i = 0; i < pageTable.size(); i++)
			pageTable.set(i, -1);

		// LRU内存块优先级初始化，全部设为0
		for (int i = 0; i < priorityLRU.size(); i++)
			priorityLRU.set(i, 0);
		
		//FIFO队列清空
		queueFIFO.clear();
	}

	// 调度算法类型
	public AlgoType algo;

	// UIWidget, 各种输出信息的组件，如果是静态的组件直接在ui图中画好
	public Vector<JLabel> vLcd; // 显示当前内存块所对应页的页号
	public Vector<DefaultListModel<String>> vBlockList;// 显示当前内存块访问指令的历史
	public Vector<JLabel> vLabelFrame; // 显示当前内存块存放页号的历史

	public DefaultListModel<String> instListAll; // 所有的待访问指令序列
	public JList<String> instAllPage;// 装指令数据的容器
	public DefaultListModel<String> instListFault; // 发生缺页的指令序列
	public JList<String> instFaultData;// 装缺页指令数据的容器
	public DefaultListModel<String> logList; // 所有访问信息
	public JList<String> logData;// 装访问数据的容器

	public JTable qPageTable; // 一级页表

	public Vector<Integer> instArray; // 指令数组
	public Vector<Integer> block; // 记录各内存块当前所存页

	public int sizeFrame; // 内存块大小
	public int sizeInst; // 指令数量
	public int capacityPage; // 每个页能存放的指令数量

	public int currentInst; // 当前指令对应序号，我们假设指令顺序存放，那么就是以10个为单位顺序存在一个页中
	public int pageFaultCount; // 缺页数量

	private Vector<Integer> pageTable; // 页表中记录各页(1-32逻辑页)对应的物理内存页号
	private Queue<Integer> queueFIFO; // 记录FIFO算法中各内存块优先级，对应于相应页的存入时间
	private Vector<Integer> priorityLRU; // 记录LRU 算法中各内存块优先级，对应于未被使用时间

	// data and structure
}
