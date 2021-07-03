package com.arraybase.lac;

public class LACActionProcess<T> {
		
		private final static String SUCCESS = "SUCCESS";
		private final static String FAILED = "FAILED";
		private final static String IN_PROGRESS = "IN_PROGRESS";
		
		
		private String msg = null;

		private String status = IN_PROGRESS;
		public LACActionProcess(String _message) {
			msg = _message;
		}
		public LACActionProcess() {
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public <T> Object getValues ()
		{
			return null;
		}

	}
