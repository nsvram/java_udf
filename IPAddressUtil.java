package pacs.cp.crucible.util;

public class IPAddressUtil {

	/**
	 * @param ipv4
	 * @return The highest IP Number in the CIDR block
	 */
	public static String ipNumberToAddress(Long ipNumber) {
		//ensure ipNumber is not null and within the ip range
		if (ipNumber == null || ipNumber < 0 || ipNumber > 0xFFFFFFFFL) {
			return null;
		}
		
		return Long.toString((ipNumber >> 24) & 0xFFL) + "." + 
			   Long.toString((ipNumber >> 16) & 0xFFL) + "." + 
			   Long.toString((ipNumber >> 8) & 0xFFL) + "." + 
			   Long.toString(ipNumber & 0xFFL);
	}
	
	/**
	 * @param ipv4
	 * @return The highest IP Number in the CIDR block
	 */
	public static Long ipAddressToNumber(String ipv4) {
		if (ipv4 == null) {
			return null;
		}
		String[] fieldsStr = null;
		
		if((fieldsStr = ipv4.split("\\.")).length != 4) {
			//return null if we don't have a valid IP in XXX.XXX.XXX.XXX format
			return null;
		}
		
		int[] fieldsInt = new int[4]; //using int[] not byte[] because Java doesn't have unsigned byte
		
		try {
			if(
				(fieldsInt[0] = Integer.parseInt(fieldsStr[0])) > 255 || fieldsInt[0] < 0 ||
				(fieldsInt[1] = Integer.parseInt(fieldsStr[1])) > 255 || fieldsInt[1] < 0 ||
				(fieldsInt[2] = Integer.parseInt(fieldsStr[2])) > 255 || fieldsInt[2] < 0 ||
				(fieldsInt[3] = Integer.parseInt(fieldsStr[3])) > 255 || fieldsInt[3] < 0
			) {
				//ensure each group of number is <= 255
				return null; 
			}
		} catch (Exception e) {
			//handle number format exceptions and any other exception
			return null;
		}
				
		return (((long)fieldsInt[0])<<24) + (fieldsInt[1]<<16) + (fieldsInt[2]<<8) + (fieldsInt[3]);
	}

	public static Long highestIPv4LongInCIDR(Long ipNumber, Integer cidrLeadingBits) {
		if (ipNumber == null || cidrLeadingBits == null) {
			return null;
		}

		
		if (cidrLeadingBits > 32 || cidrLeadingBits < 1) {
			//check mask is between 1 and 32 inclusive
			return null;
		};
		
		long trailingBitMask = (1 << (32 - cidrLeadingBits)) -1;
		
		return ipNumber.longValue() | trailingBitMask;
	}
	
	/**
	 * @param cidr
	 * @return The highest IP Number in the CIDR block
	 *         null when CIDR is invalid
	 */
	public static Long highestIPv4LongInCIDR(String cidr) {
		if (cidr == null) {
			return null;
		}
		
		String[] ipAndLeadingBits = null;
		if((ipAndLeadingBits = cidr.split("/")).length != 2) {
			//return null if we don't have a valid CIDR in XXX.XXX.XXX.XXX/XX format
			return null;
		}
		
		int leadingBits;
		if ((leadingBits = Integer.parseInt(ipAndLeadingBits[1])) > 32 || leadingBits < 1) {
			//check mask is between 1 and 32 inclusive
			return null;
		};
		
		long trailingBitMask = (1 << (32 - leadingBits)) -1;
		Long ipNumber = IPAddressUtil.ipAddressToNumber(ipAndLeadingBits[0]);
		
		if(ipNumber == null) {
			return null;
		}
		
		return ipNumber.longValue() | trailingBitMask;
	}
	
	/**
	 * @param cidr
	 * @return The lowest IP Number in the CIDR block
	 *         null when CIDR is invalid
	 */
	public static Long lowestIPv4LongInCIDR(String cidr) {
		if (cidr == null) {
			return null;
		}
		
		String[] ipAndLeadingBits = null;
		if((ipAndLeadingBits = cidr.split("/")).length != 2) {
			//return null if we don't have a valid CIDR in XXX.XXX.XXX.XXX/XX format
			return null;
		}
		
		int leadingBits;
		if ((leadingBits = Integer.parseInt(ipAndLeadingBits[1])) > 32 || leadingBits < 1) {
			//check mask is between 1 and 32 inclusive
			return null;
		};
		
		long trailingBitMask = (1 << (32 - leadingBits)) -1;
		Long ipNumber = IPAddressUtil.ipAddressToNumber(ipAndLeadingBits[0]);
		
		if(ipNumber == null) {
			return null;
		}
		
		return ipNumber.longValue() & ~trailingBitMask; //bitwise AND with bit inverted mask
	}
	
	public static Long lowestIPv4LongInCIDR(Long ipNumber, Integer cidrLeadingBits) {
		if (ipNumber == null || cidrLeadingBits == null) {
			return null;
		}

		
		if (cidrLeadingBits > 32 || cidrLeadingBits < 1) {
			//check mask is between 1 and 32 inclusive
			return null;
		};
		
		long trailingBitMask = (1 << (32 - cidrLeadingBits)) -1;
		
		return ipNumber.longValue() & ~trailingBitMask; //bitwise AND with bit inverted mask
	}

	/**
	 * @param ip
	 * @param cidr
	 * @return	True if IP Address is within the CIDR block
	 *          False if IP Address is NOT within the CIDR block
	 *          null when either IP Address of CIDR block is invalid
	 */
	public static Boolean isIPAddressInCIDR(String ip, String cidr) {
		if (ip == null || cidr == null) {
			return null;
		}
		
		String[] ipAndLeadingBits = null;
		if((ipAndLeadingBits = cidr.split("/")).length != 2) {
			//return null if we don't have a valid CIDR in XXX.XXX.XXX.XXX/XX format
			return null;
		}
		
		int leadingBits;
		try {
			if ((leadingBits = Integer.parseInt(ipAndLeadingBits[1])) > 32 || leadingBits < 1) {
				//check mask is between 1 and 32 inclusive
				return null;
			};
		}
		catch (Exception e) {
			return null;
		}
		
		long trailingBitMask = (1 << (32 - leadingBits)) -1;
		long leadingBitMask = 0xFFFFFFFFL - trailingBitMask;
		
		Long cidrIPNumber = IPAddressUtil.ipAddressToNumber(ipAndLeadingBits[0]);
		if(cidrIPNumber == null) {
			return null;
		}
		
		Long ipNumber = IPAddressUtil.ipAddressToNumber(ip);
		if(ipNumber == null) {
			return null;
		}
		
		//check the leading bits are same, ignore the trailing bits
		return (cidrIPNumber.longValue() & leadingBitMask) == (ipNumber.longValue() & leadingBitMask);
	}
	
	public static Boolean isIPAddressInCIDR(String ip, String cidrIp, Integer cidrLeadingBits) {
		if (ip == null || cidrIp == null || cidrLeadingBits == null) {
			return null;
		}
		
		if (cidrLeadingBits > 32 || cidrLeadingBits < 1) {
			//check mask is between 1 and 32 inclusive
			return null;
		};
		
		long trailingBitMask = (1 << (32 - cidrLeadingBits)) -1;
		long leadingBitMask = 0xFFFFFFFFL - trailingBitMask;
		
		Long cidrIPNumber = IPAddressUtil.ipAddressToNumber(cidrIp);
		if(cidrIPNumber == null) {
			return null;
		}
		
		Long ipNumber = IPAddressUtil.ipAddressToNumber(ip);
		if(ipNumber == null) {
			return null;
		}
		
		//check the leading bits are same, ignore the trailing bits
		return (cidrIPNumber.longValue() & leadingBitMask) == (ipNumber.longValue() & leadingBitMask);
	}
	
	public static Boolean isIPInSameNetmask(String ip1, String ip2, String netmask) {
		if (ip1 == null || ip2 == null || netmask == null) {
			return null;
		}
		
		Long ip1Number = null, ip2Number = null, netmaskNumber;
		if((ip1Number = IPAddressUtil.ipAddressToNumber(ip1)) == null || 
		   (ip2Number = IPAddressUtil.ipAddressToNumber(ip2)) == null ||
		   (netmaskNumber = IPAddressUtil.ipAddressToNumber(netmask)) == null ) {
			//either ip1, ip2 or netmask is malformed
			return null;
		}
		
		//check the leading bits are same, ignore the trailing bits
		return (ip1Number & netmaskNumber) == (ip2Number & netmaskNumber);
	}

	
}
