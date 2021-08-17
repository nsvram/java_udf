package pacs.cp.crucible.hive;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

import pacs.cp.crucible.util.IPAddressUtil;

public class IPAddressToNumber extends UDF {

	public IPAddressToNumber() {
	}

	public Long evaluate(String str) {
		return IPAddressUtil.ipAddressToNumber(str);
	}

}
