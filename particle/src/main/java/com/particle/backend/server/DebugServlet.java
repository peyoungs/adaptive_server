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
import com.jme3.math.Vector3f;
import com.particle.backend.runner.Actor;
import com.particle.backend.runner.ActorRunner;
import com.particle.backend.runner.Constants;

public class DebugServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
    	
        try (InputStream is = request.getInputStream()) {        
            final ObjectMapper MAPPER = new ObjectMapper();
            DebugRequest debugParams = MAPPER.readValue(is, DebugRequest.class);
            
            ActorRunner runner = Constants.actorContexts.getIfPresent(debugParams.sessionId);
            
        	if (runner == null)
        		throw new RuntimeException("No Session found for " + debugParams.sessionId);
            
            Vector3f target = new Vector3f(debugParams.x, debugParams.y, debugParams.z);
            
            float dist = Float.MAX_VALUE;
            Actor min = runner.actors.get(0);
            for (Actor a: runner.actors) {
            	if (target.distanceSquared(a.getPosition()) < dist) {
            		min = a;
            		dist = target.distanceSquared(a.getPosition());
            	}
            }
            
        	response.setContentType("application/text");
            response.setStatus(200);
            
            OutputStream out = response.getOutputStream();
            try {
                MAPPER.writeValue(out, min);	
            } finally {
            	out.close();
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
