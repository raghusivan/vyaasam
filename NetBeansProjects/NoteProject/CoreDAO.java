package au.com.booktopia.cms.dao.impl;

import static au.com.booktopia.constants.DelimiterConstants.BACK_SLASH;
import static au.com.booktopia.constants.DelimiterConstants.FULLSTOP;
import static au.com.booktopia.constants.DelimiterConstants.PERCENT;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import au.com.booktopia.model.IBaseEntity;

/**
 * Base JPA DAO class for this persistence entity.
 *
 * @author raghus
 *
 */
@Scope("singleton")
@Repository(value = "coreDAO")
@SuppressWarnings("unchecked")
public class CoreDAO<T> {

	private final static Logger logger = Logger.getLogger(CoreDAO.class.getName());

	@Autowired
	@PersistenceContext(unitName = "booktopia"/*, type = PersistenceContextType.TRANSACTION*/)
	protected EntityManager em;

	private Class<T> type;

	/**
	 * Default constructor
	 *
	 * @param type entity class
	 */
	public CoreDAO(Class<T> type) {
		this.type = type;
		this.em.setFlushMode(FlushModeType.COMMIT);
	}

	public CoreDAO() {
	}

	protected EntityManager getEntityManager() {
		this.em.setFlushMode(FlushModeType.COMMIT);
		return em;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.em = entityManager;
		this.em.setFlushMode(FlushModeType.COMMIT);
	}

	public T persist(T t) {
		this.em.persist(t);
		this.em.flush();
		return t;
	}

	/**
	 * Stores an instance of the entity class in the database
	 * merge in JPA is similar to saveOrUpdate in hibernate
	 *
	 * @param T Object
	 * @return
	 */
	//@Transactional(propagation = Propagation.REQUIRED)
	public T saveOrUpdate(T t) {
		t = this.em.merge(t);
		this.em.flush();
		return t;
	}

	/**
	 * Retrieves an entity instance that was previously persisted to the database
	 *
	 * @param T Object
	 * @param id
	 * @return
	 */
	public T find(Object id) {
		return this.em.find(this.type, id);
	}

	/**
	 * Removes the record that is associated with the entity instance
	 *
	 * @param type
	 * @param id
	 */
	//@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Object id) {
		Object ref = this.em.getReference(this.type, id);
		this.em.remove(ref);
	}

	/**
	 * Deletes the entity instance
	 *
	 * @param <T>
	 * @param t
	 * @return the object that is updated
	 */
	//@Transactional(propagation = Propagation.REQUIRED)
	public boolean deleteObject(T entity) {
		//this.em.remove(entity);
		em.remove(em.contains(entity) ? entity : em.merge(entity));
		//		em.flush();
		return true;
	}

	/**
	 * Removes the number of entries from a table
	 *
	 * @param <T>
	 * @param items
	 * @return
	 */
	//@Transactional(propagation = Propagation.REQUIRED)
	public boolean deleteItems(List<T> items) {
		for (T item : items) {
			//em.remove(em.merge(item));
			em.remove(em.contains(item) ? item : em.merge(item));
		}
		em.flush();
		return true;
	}

	/**
	 * Updates the entity instance
	 *
	 * TODO remove
	 *
	 * @param <T>
	 * @param t
	 * @return the object that is updated
	 */
	//@Transactional(propagation = Propagation.REQUIRED)
	public T update(T item) {
		this.em.merge(item);
		em.flush();
		return item;
	}

	/**
	 * Returns the number of records that meet the criteria
	 *
	 * @param namedQueryName
	 * @return List
	 */
	//@Transactional(readOnly=true)
	public List<T> findWithNamedQuery(String namedQueryName) {
		return this.em.createNamedQuery(namedQueryName).getResultList();
	}

	/**
	 * Returns the number of records that meet the criteria
	 *
	 * @param namedQueryName
	 * @param parameters
	 * @return List
	 */
	public List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
		List<T> result = findWithNamedQuery(namedQueryName, parameters, 0);
		return result;
	}

	/**
	 * Returns the number of records with result limit
	 *
	 * @param queryName
	 * @param resultLimit
	 * @return List
	 */

	public List<T> findWithNamedQuery(String queryName, int resultLimit) {
		return this.em.createNamedQuery(queryName).setMaxResults(resultLimit).getResultList();
	}

	/**
	 * Returns the number of records that meet the criteria
	 *
	 * @param <T>
	 * @param sql
	 * @param type
	 * @return List
	 */
	public List<T> findByNativeQuery(String sql) {
		return this.em.createNativeQuery(sql, type).getResultList();
	}

	/**
	 * Returns the number of total records
	 *
	 * @param namedQueryName
	 * @return int
	 */
	public int countTotalRecord(String namedQueryName) {
		Query query = this.em.createNamedQuery(namedQueryName);
		Number result = (Number) query.getSingleResult();
		return result.intValue();
	}

	/**
	 * Returns the number of total records
	 *
	 * @param namedQueryName
	 * @return int
	 */
	public int countTotalRecord(String namedQueryName, Map<String, Object> parameters) {
		Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
		Query query = this.em.createNamedQuery(namedQueryName);
		for (Map.Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		Number result = (Number) query.getSingleResult();
		return result.intValue();
	}

	/**
	 * Returns the number of total records
	 *
	 * @param queryString
	 * @return int
	 */
	public int countTotalRecordWithTypedQuery(String queryString, Map<String, Object> parameters) {
		Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
		TypedQuery<Long> query = em.createQuery(queryString, Long.class);
		for (Map.Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		Number result = query.getSingleResult();
		return result.intValue();
	}

	/**
	 * Returns the number of records that meet the criteria with parameter map and
	 * result limit
	 *
	 * @param namedQueryName
	 * @param parameters
	 * @param resultLimit
	 * @return List
	 */
	public <S extends Object> List<S> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
		Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
		Query query = this.em.createNamedQuery(namedQueryName);
		if (resultLimit > 0) {
			query.setMaxResults(resultLimit);
		}
		for (Map.Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList();
	}

	/**
	 * Returns the number of records that meet the criteria with parameter map and
	 * result limit
	 *
	 * @param namedQueryName
	 * @param parameters
	 * @param resultLimit
	 * @return List
	 */
	public int executeNamedQuery(String namedQueryName, Map<String, Object> parameters) {
		Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
		Query query = this.em.createNamedQuery(namedQueryName);
		for (Map.Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.executeUpdate();
	}

	/**
	 * Returns the number of records that will be used with lazy loading / pagination
	 *
	 * @param namedQueryName
	 * @param start
	 * @param end
	 * @return List
	 */
	public <S extends Object> List<S> findWithNamedQuery(String namedQueryName, int start, int end) {
		Query query = this.em.createNamedQuery(namedQueryName);
		if (end - start > 0) {
			query.setMaxResults(end - start);
			query.setFirstResult(start);
		}

		return query.getResultList();
	}

	/**
	 * This method uses TypedQuery to return the number of records that meet the criteria with parameter map and
	 * result limit
	 *
	 * @param queryString
	 * @param clazz
	 * @param parameters
	 * @param start
	 * @param end
	 * @return
	 */
	@SuppressWarnings("hiding")
	public <T> List<T> findWithTypedQuery(String queryString, Class<T> clazz, Map<String, Object> parameters, int start, int end) {
		Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
		TypedQuery<T> query = em.createQuery(queryString, clazz);
		for (Map.Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		if (end - start > 0) {
			query.setMaxResults(end - start);
			query.setFirstResult(start);
		}
		return query.getResultList();
	}

	public <S extends Object> List<S> findWithNamedQuery(String namedQueryName, int start, int end, String sortField, String sortOrder) {
		String queryString = null;
		Query query = this.em.createNamedQuery(namedQueryName);

		if (!StringUtils.isBlank(sortField) && !StringUtils.isBlank(sortOrder)) {
			queryString = query.unwrap(org.hibernate.Query.class).getQueryString();
			queryString = queryString + " order by " + sortField + " " + sortOrder;
			query = this.em.createQuery(queryString);
		}

		if (end - start > 0) {
			query.setMaxResults(end - start);
			query.setFirstResult(start);
		}

		return query.getResultList();
	}

	/**
	 * Returns the number of records that will be used with lazy loading / pagination
	 *
	 * @param namedQueryName
	 * @param start
	 * @param end
	 * @return List
	 */
	public <S extends Object> List<S> findWithNamedQueryParams(String namedQueryName, Map<String, Object> parameters, int start, int end) {
		Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
		Query query = this.em.createNamedQuery(namedQueryName);
		for (Map.Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		if ((end - start) > 0) {
			query.setMaxResults(end - start);
			query.setFirstResult(start);
		}

		return query.getResultList();
	}

	public <S extends Object> List<S> findWithNamedQueryParams(String namedQueryName, Map<String, Object> parameters) {
		Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
		Query query = this.em.createNamedQuery(namedQueryName);
		for (Map.Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList();
	}

	public T findById(final Class<T> entityClass, final Long id) {
		return em.find(entityClass, id);
	}

	/**
	 * Adds a number of entries to the table
	 *
	 * @param <T>
	 * @param items
	 * @return
	 */
	public List<T> saveList(List<T> items) {
		for (T item : items) {
			item = saveOrUpdate(item, false);
		}
		em.flush();
		return items;
	}

	/**
	 * Stores an instance of the entity class in the database
	 * merge in JPA is similar to saveOrUpdate in hibernate with indicating whether to flush or not
	 *
	 * @param T Object
	 * @return
	 */
	//@Transactional(propagation = Propagation.REQUIRED)
	public T saveOrUpdate(T t, boolean isFlush) {
		t = this.em.merge(t);
		if (isFlush) {
			this.em.flush();
		}
		return t;
	}

	/**
	 * Reload the object from the database, reflects changes of the external source.
	 *
	 * @param t
	 * @return
	 */
	public <T> T refresh(T t) {
		Session s = em.unwrap(Session.class);
		s.refresh(t);
		return t;
	}

	/**
	 * This method is used to get path to the field and return Path<String>
	 *
	 * @param root
	 * @param fieldPath
	 * @return
	 */
	public Path<String> getPropertyPath(Root<? extends IBaseEntity> root, String fieldPath) {
		Path<String> path = null;
		if (StringUtils.isNotBlank(fieldPath)) {
			if (fieldPath.contains(FULLSTOP)) {
				String[] joinField = fieldPath.split(BACK_SLASH + FULLSTOP);
				if (joinField.length == 2) {
					path = root.join(joinField[0]).<String> get(joinField[1]);
				}
				else if (joinField.length == 3) {
					path = root.join(joinField[0]).join(joinField[1]).<String> get(joinField[2]);
				}
				else if (joinField.length == 4) {
					path = root.join(joinField[0]).join(joinField[1]).join(joinField[2]).<String> get(joinField[3]);
				}
			}
			else {
				path = root.<String> get(fieldPath);
			}
		}
		return path;
	}

	/**
	 * Add "%" to the start and end of a string.
	 *
	 * TODO: This method, and associated test cases, should be moved to BooktopiaUtils.DatabaseUtils
	 *
	 * @param text
	 * @return
	 */
	protected String addSqlPercentWildcards(String text) {
		if (StringUtils.isNotEmpty(text)) {
			if (!text.startsWith(PERCENT) && !text.endsWith(PERCENT)) {
				text = PERCENT + text + PERCENT;
			}
		}
		return text;
	}

	/**
	 * Remove the "%" from the start and end of a string.
	 *
	 * TODO: This method, and associated test cases, should be moved to BooktopiaUtils.DatabaseUtils
	 *
	 * @param text
	 * @return
	 */
	protected String removeSqlPercentWildcards(String text) {
		if (StringUtils.isNotEmpty(text)) {
			if (text.startsWith(PERCENT) && text.endsWith(PERCENT)) {
				int endIndex = text.length() == 1 ? 1 : text.length() - 1;
				text = text.substring(1, Math.min(text.length(), endIndex));
			}
		}
		return text;
	}

	/**
	 * Return the value of {@link EntityManager#contains(Object)}
	 * 
	 * @param entity the entity to check against the session
	 * @return true if exists in current session
	 */
	public boolean sessionContains(T entity) {
		if (entity != null) {
			return em.contains(entity);
		}
		return false;
	}

	public Class<T> getType() {
		return type;
	}

	public void setType(Class<T> type) {
		this.type = type;
	}

}
