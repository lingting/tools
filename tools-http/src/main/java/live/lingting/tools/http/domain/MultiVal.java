package live.lingting.tools.http.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingting
 */
public class MultiVal {

	private final List<Object> list = new ArrayList<>();

	public void add(Object obj) {
		list.add(obj);
	}

	public List<Object> values() {
		return list;
	}

}
