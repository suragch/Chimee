package net.studymongolian.chimee;

public class Message {

	private long id;
	private long date;
	private String message;

    public Message(long id, long date, String message) {
        this.id = id;
        this.date = date;
        this.message = message;
    }

	public long getId() {
		return id;
	}
	
	public long getDate() {
		return date;
	}
	
	public String getMessage() {
		return message;
	}

}
