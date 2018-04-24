package com.fantasyunlimited.discord;

import java.io.Serializable;

public class MessageStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 10395390233736410L;
	private Name name;
	private boolean paginator;
	private int currentPage;
	private int maxPage;
	
	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public boolean isPaginator() {
		return paginator;
	}

	public void setPaginator(boolean paginator) {
		this.paginator = paginator;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}
	
	public enum Name {
		CREATE_CHAR_RACE_SELECTION,
		CREATE_CHAR_CLASS_SELECTION,
		CREATE_CHAR_CONFIRMATION,
		NONE
	}
}
