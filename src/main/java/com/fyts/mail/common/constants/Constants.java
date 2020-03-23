package com.fyts.mail.common.constants;

public interface Constants {
	/** 分割符 */
	String FILE_SEPARATOR = System.getProperty("file.separator");
	String LINE_SEPARATOR = System.getProperty("line.separator");
	String PATH_SEPARATOR = System.getProperty("path.separator");

	enum MAIL_CTYPE {
		PLAIN_TEXT("纯文本","1"),TEMPLATE("模板", "2"),RICH_TEXT("富文本","3");

		private String label;
		private String value;

		MAIL_CTYPE(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String value(){ return this.value; }
		public String label(){
			return this.label;
		}
	}

	enum MAIL_STATUS {
		DRAFT("草稿","1"), //可修改
		SENT("待发送","2"), //不可修改
		OK("发送成功","3"),
		ERROR("发送失败","4");

		private String label;
		private String value;

		MAIL_STATUS(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String value(){ return this.value; }
		public String label(){
			return this.label;
		}
	}


}
