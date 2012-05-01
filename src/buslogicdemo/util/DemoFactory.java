package buslogicdemo.util;

import java.io.Serializable;
import java.util.List;

/**
 * An abstract interface for the JPA and JPA/JTA factories.
 * This allows us to switch between the two easily.
 */
public interface DemoFactory {

	public void beginTransaction();
	
	public void commitTransaction();
	
	public void rollbackTransaction();
	
	public <T> T getObjectById(Class<?> T, Serializable id);
	
	@SuppressWarnings("rawtypes")
	public List query(String query);
	
	public void saveObject(Object bean);
	
	public void deleteObject(Object bean);
	
	public void setCurrentUseCaseName(String s);
}
