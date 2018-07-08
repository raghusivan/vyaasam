package com.note.beans;


import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Super Entity class
 * 
 * @author raghus
 */
@MappedSuperclass
public abstract class BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "UIDPK")
	private Long uidPk;

	public Long getUidPk() {
		return this.uidPk;
	}

	public void setUidPk(Long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (uidPk != null ? uidPk.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}
		else if (!(obj instanceof BaseEntity)) {
			return false;
		}
		else if (((BaseEntity) obj).getUidPk() != null && ((BaseEntity) obj).getUidPk().equals(this.getUidPk())) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "entity." + this.getClass() + "[ uidPk=" + uidPk + " ] ";
	}
}
