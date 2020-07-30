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

public class LoaderServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {

        try (InputStream in = request.getInputStream()) {        	
        	response.setContentType("application/string");
            response.setStatus(200);
            
            final ObjectMapper MAPPER = new ObjectMapper();
            RuleParameter requestParameters = MAPPER.readValue(in, RuleParameter.class);
            
            ActorRunner run = new ActorRunner(requestParameters);
           
        	response.setContentType("application/octet-stream");
            response.setStatus(200);
           
            try (OutputStream out = response.getOutputStream()) {
            	run.writeData(out,  0);
            }
            
            Constants.actorContexts.put(requestParameters.sessionId, run);
            
            System.out.println("\nStarted " + requestParameters.sessionId);
        } catch (Exception exception) {        	
        	exception.printStackTrace();

        	response.setContentType("application/text");
            response.setStatus(500);
            try (PrintWriter writer =
                 new PrintWriter(response.getOutputStream())) {
                exception.printStackTrace(writer);
            }
        }
    }

}
