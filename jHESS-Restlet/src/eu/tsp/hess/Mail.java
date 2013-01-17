package eu.tsp.hess;

public class Mail {
	private String status;

	private String subject;

	private String content;

	

	public String getContent() {
		return content;
	}

	public String getStatus() {
		return status;
	}

	public String getSubject() {
		return subject;
	}

	

	public void setContent(String content) {
		this.content = content;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}