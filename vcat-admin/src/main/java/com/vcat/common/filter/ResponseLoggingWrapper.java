package com.vcat.common.filter;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ResponseLoggingWrapper extends HttpServletResponseWrapper {
    private static final Logger log = LoggerFactory.getLogger(ResponseLoggingWrapper.class);
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    /**
     * @param response  response from which we want to extract stream data
     */
    public ResponseLoggingWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        final ServletOutputStream servletOutputStream = ResponseLoggingWrapper.super.getOutputStream();
        return new ServletOutputStream() {
            private TeeOutputStream tee = new TeeOutputStream(servletOutputStream, bos);

            @Override
            public void write(byte[] b) throws IOException {
                tee.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                tee.write(b, off, len);
            }

            @Override
            public void flush() throws IOException {
                tee.flush();
                logRequest();
            }

            @Override
            public void write(int b) throws IOException {
                tee.write(b);
            }

            @Override
            public boolean isReady() {
                return servletOutputStream.isReady();
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                servletOutputStream.setWriteListener(writeListener);
            }


            @Override
            public void close() throws IOException {
                super.close();
                // do the logging
                logRequest();
            }
        };
    }

    public void logRequest() {
        byte[] toLog = toByteArray();
        if (toLog != null && toLog.length > 0)
            log.debug("HTTP响应: " + new String(toLog));
    }

    /**
     * this method will clear the buffer, so
     *
     * @return captured bytes from stream
     */
    public byte[] toByteArray() {
        byte[] ret = bos.toByteArray();
        bos.reset();
        return ret;
    }
}
