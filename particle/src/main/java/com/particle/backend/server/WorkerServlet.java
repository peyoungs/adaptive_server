package com.particle.backend.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.particle.backend.runner.ActorRunner;
import com.particle.backend.runner.Constants;

public class WorkerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
    	
        try (InputStream is = request.getInputStream()) {
            final ObjectMapper MAPPER = new ObjectMapper();
            WorkRequest workRequest = MAPPER.readValue(is, WorkRequest.class);
        	
        	ActorRunner runner = Constants.actorContexts.getIfPresent(workRequest.sessionId);
        	
        	if (runner == null) {
                response.setStatus(404);
                try (PrintWriter writer = new PrintWriter(response.getOutputStream())) {
                	writer.append("No session found for " +  workRequest.sessionId);
                }
                return;
        	}
        	
        	runner.update(workRequest.deltaTime);
            
        	response.setContentType("application/octet-stream");
            response.setStatus(200);
            
            try (OutputStream out = response.getOutputStream()) {
            	runner.writeData(out, 0);            	
            }
            
        } catch (Exception exception) {
        	exception.printStackTrace();

            response.setStatus(500);
            try (PrintWriter writer =
                 new PrintWriter(response.getOutputStream())) {
                exception.printStackTrace(writer);
            }
        }
    }

}
