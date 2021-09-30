package bank.business.domain;

import java.util.Date;

/**
 * @author Ingrid Nunes
 * 
 */
public abstract class Transaction {

	private CurrentAccount account;
	private double amount, pendentAmount = 0;
	private Date date;
	private OperationLocation location;
	private int status = 2; // 1 = finalizada, 2 = pendente e 3 = cancelada

	protected Transaction(OperationLocation location, CurrentAccount account, double amount) {
		this.location = location;
		this.date = new Date(System.currentTimeMillis());
		this.account = account;
		this.amount = amount;

	}
	

	/**
	 * @return the account
	 */
	public CurrentAccount getAccount() {
		return account;
	}

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the location
	 */
	public OperationLocation getLocation() {
		return location;
	}

	/**
	 * This method is here for initializing the database.
	 * 
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getPendentAmount() {
		return pendentAmount;
	}

	public void setPendentAmount(double pendentAmount) {
		this.pendentAmount = pendentAmount;
	}

}
