package apbiot.core.io;

import java.io.IOException;
import java.io.InputStream;

public class ClosedInputStream extends InputStream {

	@Override
	public int read() throws IOException {
		return -1;
	}

}
