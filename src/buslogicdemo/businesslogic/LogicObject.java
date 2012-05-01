package buslogicdemo.businesslogic;

import com.autobizlogic.abl.annotations.Constraint;
import com.autobizlogic.abl.annotations.LogicContextObject;
import com.autobizlogic.abl.logic.LogicContext;
import com.autobizlogic.abl.util.LogicLogger;
import com.autobizlogic.abl.util.LogicLogger.LoggerName;

/**
 * A trivial superclass for logic objects, demonstrating how logic can be inherited.
 */
public class LogicObject {

	@LogicContextObject
	protected LogicContext context;
	/**
	 * rarely enabled, just included here to illustrate debug options
	 */
	public static final LogicLogger _sysLog = LogicLogger.getLogger(LoggerName.RULES_ENGINE);
	
	@Constraint
	public void emptyConstraint() {
		if (_sysLog.isDebugEnabled())
			_sysLog.debug("emptyConstraint invoked for " + getClass(), context.getLogicRunner());
	}

}
