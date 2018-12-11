package inciobot.bot_backend.model;

import java.io.File;

public class PhotoLink {
	private File file;
	private String link;

	public PhotoLink() {
		super();
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
