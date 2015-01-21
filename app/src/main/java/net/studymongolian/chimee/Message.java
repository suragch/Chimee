package net.studymongolian.chimee;

public class Message {

	private long id;
	private long date;
	private String message;

	// constructor
	public Message() {
		id=0;
		date=0;
		message="";
	}

	// getters
	public long getId() {
		return id;
	}
	
	public long getDate() {
		return date;
	}
	
	public String getMessage() {
		return message;
	}


	// setters
	public void setId(long id) {
		this.id = id;
	}
	
	public void setDate(long date) {
		this.date = date;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}
