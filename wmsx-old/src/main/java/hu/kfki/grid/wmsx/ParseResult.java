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
		iStream = stream;
		oStream = stream2;
		eStream = stream3;
		this.shadowpid = shadowpid;
	}

	public String getEStream() {
		return eStream;
	}

	public String getIStream() {
		return iStream;
	}

	public String getJobId() {
		return jobId;
	}

	public String getOStream() {
		return oStream;
	}

	public int getShadowpid() {
		return shadowpid;
	}

}
