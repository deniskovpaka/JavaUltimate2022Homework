package org.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@WebServlet(name = "EveningServlet", urlPatterns = "/evening")
public class EveningServlet extends HttpServlet {
    private static final String NAME_QUERY_PARAM = "name";
    private static final String DEFAULT_NAME = "buddy";
    private final String SESSION_ID = "SESSION_ID";
    private final ConcurrentMap<String, Map<String, String>> SESSION_MAP = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (var writer = resp.getWriter()) {
            Optional.ofNullable(req.getParameter(NAME_QUERY_PARAM))
                    .ifPresent(existingName -> setAttribute(req, resp, NAME_QUERY_PARAM, existingName));
            String name = getAttribute(req, NAME_QUERY_PARAM)
                    .map(String::valueOf)
                    .orElse(DEFAULT_NAME);
            writer.printf("Good evening, %s", name);
        }
    }

    public void setAttribute(HttpServletRequest req,
                             HttpServletResponse resp,
                             String name, String val) {
        String sessionId = getSessionId(req).orElseGet(() -> UUID.randomUUID().toString());
        SESSION_MAP.computeIfAbsent(sessionId, id -> new HashMap<>()).put(name, val);

        resp.addCookie(new Cookie(SESSION_ID, sessionId));
    }

    private Optional<String> getSessionId(HttpServletRequest req) {
        return Objects.isNull(req.getCookies())
                ? Optional.empty()
                : Arrays.stream(req.getCookies())
                .filter(c -> SESSION_ID.equalsIgnoreCase(c.getName()))
                .findAny()
                .map(Cookie::getValue);
    }

    public Optional<String> getAttribute(HttpServletRequest req, String name) {
        return getSessionId(req)
                .map(SESSION_MAP::get)
                .map(attributes -> attributes.get(name));
    }
}
