
package org.cytoscape.command.internal.tunables;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.BoundedLong;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BoundedLongTunableHandler extends AbstractStringTunableHandler {
    public BoundedLongTunableHandler(Field f, Object o, Tunable t) { super(f,o,t); }
    public BoundedLongTunableHandler(Method get, Method set, Object o, Tunable t) { super(get,set,o,t); }
	public Object processArg(String arg) throws Exception {
		long value = Long.parseLong(arg);
		BoundedLong bi = (BoundedLong)getValue();
		bi.setValue(value);
		return bi;
	}
}
