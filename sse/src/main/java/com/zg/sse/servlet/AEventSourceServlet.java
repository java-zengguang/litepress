package com.zg.sse.servlet;


import com.zg.common.util.reflect.JsonUtils;
import com.zg.sse.event.AEventSource;
import com.zg.sse.event.AEventSourceAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;
import org.tinylog.Logger;



@WebServlet(name = "AEventSourceServlet", urlPatterns = "/sse")
public class AEventSourceServlet extends EventSourceServlet {


    @Override
    protected EventSource newEventSource(HttpServletRequest request) {
        String groupId = request.getParameter("groupId");
        String clientId = request.getParameter("clientId");
        Logger.info("创建SSEServlet  groupId: " + groupId + " clientId:" + clientId);
        return AEventSourceAdapter.createAEventSource(groupId, clientId);
    }



}
