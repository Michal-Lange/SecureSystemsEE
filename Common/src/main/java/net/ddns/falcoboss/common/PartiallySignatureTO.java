package net.ddns.falcoboss.common;

public class PartiallySignatureTO {

	private String patiallySignedFileHash;
	private String fileHash;
	
	public PartiallySignatureTO(){
	}
	
	public PartiallySignatureTO(String patiallySignedFileHash, String fileHash){
		this.patiallySignedFileHash = patiallySignedFileHash;
		this.fileHash = fileHash;
	}

	public String getPatiallySignedFileHash() {
		return patiallySignedFileHash;
	}

	public void setPatiallySignedFileHash(String patiallySignedFileHash) {
		this.patiallySignedFileHash = patiallySignedFileHash;
	}

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}
}
