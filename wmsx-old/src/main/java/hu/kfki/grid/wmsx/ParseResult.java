package hu.kfki.grid.wmsx;

public class ParseResult {
	final String jobId;

	final String iStream;

	final String oStream;

	final String eStream;

	final int shadowpid;

	public ParseResult(final String jobId, final String stream,
			final String stream2, final String stream3, final int shadowpid) {
		super();
		this.jobId = jobId;
		this.iStream = stream;
		this.oStream = stream2;
		this.eStream = stream3;
		this.shadowpid = shadowpid;
	}

	public String getEStream() {
		return this.eStream;
	}

	public String getIStream() {
		return this.iStream;
	}

	public String getJobId() {
		return this.jobId;
	}

	public String getOStream() {
		return this.oStream;
	}

	public int getShadowpid() {
		return this.shadowpid;
	}

}
