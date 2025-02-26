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
	 * @set the new amount
	 */
	public void setAmount(double newAmount) {
		this.amount = newAmount;
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

	public double getPendentAmount() {
		return pendentAmount;
	}

	public void setPendentAmount(double pendentAmount) {
		this.pendentAmount = pendentAmount;
	}

}
