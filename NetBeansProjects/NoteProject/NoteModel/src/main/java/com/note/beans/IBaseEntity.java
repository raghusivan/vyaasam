package com.note.beans;

/**
 * This is a marker interface
 * 
 * @author reenas
 * 
 */
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public interface IBaseEntity {
	abstract public Long getUidPk();

	public void setUidPk(Long uidPk);
}