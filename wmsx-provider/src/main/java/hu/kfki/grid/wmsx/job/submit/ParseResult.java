package hu.kfki.grid.wmsx.job.submit;

public class ParseResult {
	final String jobId;

	final String iStream;

	final String oStream;

	final String eStream;

	final int shadowpid;

	final int port;

	public ParseResult(final String jobId, final String stream,
			final String stream2, final String stream3, final int pid,
			final int portNum) {
		super();
		this.jobId = jobId;
		this.iStream = stream;
		this.oStream = stream2;
		this.eStream = stream3;
		this.shadowpid = pid;
		this.port = portNum;
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

	public int getPort() {
		return this.port;
	}

}
