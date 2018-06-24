package com.ex.commanddemo.domain;

import javax.persistence.*;

/**
 * @author edison
 * On 2018/6/22 17:16
 */
@Entity
public class ReduceMoneyEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private long walletId;

	@Column
	private long value;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getWalletId() {
		return walletId;
	}

	public void setWalletId(long walletId) {
		this.walletId = walletId;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
}
