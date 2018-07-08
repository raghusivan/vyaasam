package com.note.beans;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author reenas
 *
 */
@Entity
@Table(name = "note",schema="test")
/*
@NamedQueries({ 
	@NamedQuery(name = Comment.SELECT_COMMENT_BY_ENTITY_TYPE_ID_AND_ENTITY_UID_ORDER_BY_LAST_MODIFIED_DATE_DESC, 
			query = "SELECT com FROM Comment com WHERE com.entityTypeId = :entityTypeId and com.entityUid = :entityUid ORDER BY com.lastModifiedDate DESC")
})
 */
@org.hibernate.annotations.Entity(dynamicUpdate = true)
public class Note extends BaseEntity {

    private static final long serialVersionUID = 1L;

//	public static final String SELECT_COMMENT_BY_ENTITY_TYPE_ID_AND_ENTITY_UID_ORDER_BY_LAST_MODIFIED_DATE_DESC = "Comment.getCommentByEntityTypeAndEntityIdOrderByLastModifiedDate";
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @Column(name = "content")
    private String content;
    
    @Column(name = "type")
    private String type;    

    /**
     * @return the lastModifiedDate
     */
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * @param lastModifiedDate the lastModifiedDate to set
     */
    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
