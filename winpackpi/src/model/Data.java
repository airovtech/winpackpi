package model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Data{

	private String xFieldName;
	private String xGroupName;
	private String yValueName;
	private String yGroupName;
	private GroupHeader[] groupHeaders;
	private String[] groupNames;
	private List<Map<String, Object>> values; 
	
	public String getyValueName() {
		return yValueName;
	}
	public void setyValueName(String yValueName) {
		this.yValueName = yValueName;
	}
	public String getxFieldName() {
		return xFieldName;
	}
	public void setxFieldName(String xFieldName) {
		this.xFieldName = xFieldName;
	}
	public String[] getGroupNames() {
		return groupNames;
	}
	public void setGroupNames(String[] groupNames) {
		this.groupNames = groupNames;
	}	
	public String getxGroupName() {
		return xGroupName;
	}
	public void setxGroupName(String xGroupName) {
		this.xGroupName = xGroupName;
	}
	public String getyGroupName() {
		return yGroupName;
	}
	public void setyGroupName(String yGroupName) {
		this.yGroupName = yGroupName;
	}
	public GroupHeader[] getGroupHeaders() {
		return groupHeaders;
	}
	public void setGroupHeaders(GroupHeader[] groupHeaders) {
		this.groupHeaders = groupHeaders;
	}
	public List<Map<String, Object>> getValues() {
		return values;
	}
	public void setValues(List<Map<String, Object>> values) {
		this.values = values;
	}
	public Data() {
		super();
	}	
	
	public String toString() {
		/*<data>
			<xFieldName></xFieldName>
			<xGroupName></xGroupName>
			<yValueName></yValueName>
			<yGroupName></yGroupName>
			<groupNames>
				<groupName></groupName>
				<groupName></groupName>
				<groupName></groupName>
			</groupNames>
			<groupHeaders>
				<groupHeader>
					<startColumnName></startColumnName>
					<startColumnIndex></startColumnIndex>
					<numberOfColumns></numberOfColumns>
					<titleText></titleText>
				</groupHeader>
				<groupHeader>
					<startColumnName></startColumnName>
					<startColumnIndex></startColumnIndex>
					<numberOfColumns></numberOfColumns>
					<titleText></titleText>
				</groupHeader>
			</groupHeaders>
			<values>
				<value>
					<valueMap key=""></valueMap>
					<valueMap key=""></valueMap>
					<valueMap key=""></valueMap>
				</value>
				<value>
					<valueMap key=""></valueMap>
					<valueMap key=""></valueMap>
					<valueMap key=""></valueMap>
				</value>
			</values>
		</data>*/
		
		StringBuffer strBuff = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		strBuff.append("<data>\r\n");
		strBuff.append("	<xFieldName>").append(this.xFieldName).append("</xFieldName>\r\n");
		strBuff.append("	<xGroupName>").append(this.xGroupName).append("</xGroupName>\r\n");
		strBuff.append("	<yValueName>").append(this.yValueName).append("</yValueName>\r\n");
		strBuff.append("	<yGroupName>").append(this.yGroupName).append("</yGroupName>\r\n");
		if (this.groupNames == null) {
			strBuff.append("	<groupNames/>\r\n");
		} else {
			strBuff.append("	<groupNames>\r\n");
			for (int i = 0; i < this.groupNames.length; i++) {
				strBuff.append("		<groupName>").append(this.groupNames[i]).append("</groupName>\r\n");
			}
			strBuff.append("	</groupNames>\r\n");
		}
		if (this.groupHeaders == null) {
			strBuff.append("	<groupHeaders/>\r\n");
		} else {
			strBuff.append("	<groupHeaders>\r\n");
			for (int i = 0; i < groupHeaders.length; i++) {
				GroupHeader gh = groupHeaders[i];
				strBuff.append("		<groupHeader>\r\n");
				strBuff.append("			<startColumnName>").append(gh.getStartColumnName()).append("</startColumnName>\r\n");
				strBuff.append("			<startColumnIndex>").append(gh.getStartColumnIndex()).append("</startColumnIndex>\r\n");
				strBuff.append("			<numberOfColumns>").append(gh.getNumberOfColumns()).append("</numberOfColumns>\r\n");
				strBuff.append("			<titleText>").append(gh.getTitleText()).append("</titleText>\r\n");
				strBuff.append("		</groupHeader>\r\n");
			}
			strBuff.append("	</groupHeaders>\r\n");
		}
		if (values == null) {
			strBuff.append("	<valueList/>\r\n");
		} else {
			
			strBuff.append("	<valueList>\r\n");
			for (int i = 0; i < this.values.size(); i++) {
				strBuff.append("		<valueMap>\r\n");
				Map valueMap = this.values.get(i);
				Iterator itr = valueMap.keySet().iterator();
				while (itr.hasNext()) {
					String key = (String)itr.next();
					String value = (String)valueMap.get(key);
					strBuff.append("			<value key=\"").append(key).append("\">").append(value).append("</value>\r\n");
				}
				strBuff.append("		</valueMap>\r\n");
			}
			strBuff.append("	</valueList>\r\n");
		}
		strBuff.append("</data>");
		return strBuff.toString();
	}
}
