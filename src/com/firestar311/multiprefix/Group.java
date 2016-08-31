package com.firestar311.multiprefix;

public class Group {
	private String name;
	private String prefix;
	private String suffix;
	private String format;
	private int priority;
	public Group(String name, String prefix, String suffix, String format, int priority) {
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
		this.format = format;
		this.priority = priority;
	}
	public String getName() {
		return name;
	}
	public String getPrefix() {
		return prefix;
	}
	public String getSuffix() {
		return suffix;
	}
	public String getFormat() {
		return format;
	}
	public int getPriority() {
		return priority;
	}
	@Override
	public String toString() {
		return "Group [name=" + name + ", prefix=" + prefix + ", suffix=" + suffix + ", format=" + format
				+ ", priority=" + priority + "]";
	}
	
	
}