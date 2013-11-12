import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Data;

/* 
 * $Id$
 * created by    : yukm
 * creation-date : 2013. 11. 6.
 * =========================================================
 * Copyright (c) 2013 ManinSoft, Inc. All rights reserved.
 */

public class TestChart {

	public String getChartData(String date) throws Exception {
		Data data = new Data();
		
		data.setGroupNames(new String[]{"count"});
		data.setxFieldName("modifiedTime");
		data.setyValueName("count");
		
		List value = new ArrayList();
		Map map1 = new HashMap();
		map1.put("count", "19");
		map1.put("modifiedTime", "2013-10");
		Map map2 = new HashMap();
		map2.put("count", "6");
		map2.put("modifiedTime", "2013-11");
		value.add(map1);
		value.add(map2);
		data.setValues(value);
		
		return data.toString();
	}
}
