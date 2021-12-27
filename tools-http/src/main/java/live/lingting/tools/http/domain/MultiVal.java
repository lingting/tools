package live.lingting.tools.http.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author lingting
 */
public class MultiVal implements Iterable<Object> {

	private final List<Object> list = new ArrayList<>();

	public void add(Object obj) {
		list.add(obj);
	}

	public void addAll(Collection<Object> objects) {
		list.addAll(objects);
	}

	public List<Object> values() {
		return list;
	}

	@Override
	public Iterator<Object> iterator() {
		return list.iterator();
	}

}
