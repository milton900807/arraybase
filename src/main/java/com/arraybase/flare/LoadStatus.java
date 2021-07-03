package com.arraybase.flare;

public class LoadStatus implements ProcessReport {
		private int id = -1;
		private String msg = "";

		public LoadStatus() {

		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public void setJobId(int _id) {
			id = _id;
		}

		public int getJobId() {
			return id;
		}

		public String getCore() {
			// TODO Auto-generated method stub
			return null;
		}

	}
