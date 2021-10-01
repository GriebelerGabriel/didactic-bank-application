package bank.ui.graphic.action;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;

import bank.business.AccountOperationService;
import bank.business.BusinessException;
import bank.business.domain.Branch;
import bank.business.domain.CurrentAccountId;
import bank.business.domain.Deposit;
import bank.business.domain.Transaction;
import bank.business.domain.Transfer;
import bank.business.domain.Withdrawal;
import bank.ui.TextManager;
import bank.ui.graphic.BankGraphicInterface;
import bank.ui.graphic.GUIUtils;
import bank.ui.graphic.action.StatementAction.StatementType;

public class PendentDepositsAction extends AccountAbstractAction {

	private class MonthYear {
		int month;
		int year;

		@Override
		public String toString() {
			return textManager.getText("month." + month) + "/" + year;
		}
	}

	public enum StatementType {
		MONTHLY, PERIOD;
	}

	private class StatementTypeListner implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			CardLayout cl = (CardLayout) (cards.getLayout());
			cl.show(cards, e.getActionCommand());
			type = StatementType.valueOf(e.getActionCommand());
		}
	}

	private class TransactionTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 2497950520925208080L;

		private CurrentAccountId id;
		private List<Transaction> transactions;

		public TransactionTableModel(CurrentAccountId id, List<Deposit> transactions) {
			this.id = id;
			this.transactions = new ArrayList<>(transactions);
		}

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public String getColumnName(int column) {
			String key = null;
			switch (column) {
			case 0:
				key = "date";
				break;
			case 1:
				key = "location";
				break;
			case 2:
				key = "operation.type";
				break;
			case 3:
				key = "details";
				break;
			case 4:
				key = "pendent.amount";
				break;
			case 5:
				key = "status";
				break;
			default:
				assert false;
				break;
			}
			return textManager.getText(key);
		}

		@Override
		public int getRowCount() {
			return transactions.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Transaction t = transactions.get(rowIndex);
			Object val = null;
			switch (columnIndex) {
			case 0:
				val = GUIUtils.DATE_TIME_FORMAT.format(t.getDate());
				break;
			case 1:
				val = t.getLocation();
				break;
			case 2:
				val = textManager.getText("operation." + t.getClass().getSimpleName());
				break;
			case 3:
				val = ((Deposit) t).getEnvelope();
				break;
			case 4:
				val = "+ " + ((Deposit) t).getPendentAmount();
				break;
			case 5:
				val = ((Deposit) t).getStatus();
				break;
			default:
				assert false;
				break;
			}
			return val;
		}

	}

	private static final int NUMBER_OF_POSSIBLE_MONTHS = 6;

	private static final long serialVersionUID = 5090183202921964451L;

	private JFormattedTextField beginDate;
	private JPanel cards;
	private JDialog dialog;
	private JFormattedTextField endDate;
	private JComboBox<MonthYear> month;
	private JTable transactions;
	private StatementType type;

	public PendentDepositsAction(BankGraphicInterface bankInterface, TextManager textManager,
			AccountOperationService accountOperationService) {
		super(bankInterface, textManager, accountOperationService);

		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		putValue(Action.NAME, textManager.getText("action.pendents"));
	}

	public void close() {
		dialog.dispose();
		dialog = null;
	}

	private JRadioButton createRadioButton(StatementType type, ButtonGroup btGroup, ActionListener al) {
		JRadioButton bt = new JRadioButton(textManager.getText(type.name()));
		bt.setActionCommand(type.name());
		bt.addActionListener(al);
		btGroup.add(bt);
		return bt;
	}

	@Override
	public void execute() {
		JPanel accountPanel = new JPanel(new GridLayout(2, 2, 5, 5));
		initAndAddAccountFields(accountPanel);

		// Cards
		JPanel radioBtPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		this.cards = new JPanel(new CardLayout());
		ButtonGroup btGroup = new ButtonGroup();
		ActionListener al = new StatementTypeListner();

		JPanel cardsPanel = new JPanel();
		cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.PAGE_AXIS));
		cardsPanel.add(accountPanel);
		cardsPanel.add(radioBtPanel);
		cardsPanel.add(cards);

		// Confirmation Buttons
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton cancelButton = new JButton(textManager.getText("button.close"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});
		buttonsPanel.add(cancelButton);
		JButton okButton = new JButton(textManager.getText("button.ok"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showPendentsDeposits();
			}
		});
		buttonsPanel.add(okButton);

		// Statement result
		JPanel transactionsPanel = new JPanel();
		transactionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		transactions = new JTable();
		JScrollPane scrollPane = new JScrollPane(transactions, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		transactionsPanel.add(scrollPane);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.add(cardsPanel, BorderLayout.CENTER);
		mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

		JPanel pane = new JPanel(new BorderLayout());
		pane.add(mainPanel, BorderLayout.NORTH);
		pane.add(transactionsPanel, BorderLayout.CENTER);

//		btM.doClick();

		this.dialog = GUIUtils.INSTANCE.createDialog(bankInterface.getFrame(), "action.statement", pane);
		this.dialog.setVisible(true);
	}

	private void showPendentsDeposits() {

		try {
			if (!checkAccountFields())
				return;

			Date begin = null;
			Date end = null;

			if (begin == null || end == null) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
				cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
				cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
				end = cal.getTime();

				cal.add(Calendar.DAY_OF_MONTH, -30);
				cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
				cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
				cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
				begin = cal.getTime();
			}

			List<Deposit> transactions = accountOperationService.getPendentDeposits(
					((Number) branch.getValue()).longValue(), ((Number) accountNumber.getValue()).longValue());
			this.transactions.setModel(
					new TransactionTableModel(new CurrentAccountId(new Branch(((Number) branch.getValue()).longValue()),
							((Number) accountNumber.getValue()).longValue()), transactions));
		} catch (BusinessException be) {
			GUIUtils.INSTANCE.showMessage(bankInterface.getFrame(), be.getMessage(), be.getArgs(),
					JOptionPane.WARNING_MESSAGE);
			log.warn(be);
		} catch (Exception exc) {
			GUIUtils.INSTANCE.handleUnexceptedError(bankInterface.getFrame(), exc);
		}
	}

}
