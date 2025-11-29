package com.example.codersfree.interceptor;

import com.example.codersfree.annotation.Guest;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        boolean isAuthenticated = isAuthenticated();
        
        // Verificar anotación @Guest y estado de autenticación
        if (hasGuestAnnotation(handlerMethod) && isAuthenticated) {
            response.sendRedirect("/");
            return false;
        }
        
        return true;
    }
    
    private boolean hasGuestAnnotation(HandlerMethod handlerMethod) {
        // Buscar en método
        Guest methodAnnotation = handlerMethod.getMethodAnnotation(Guest.class);
        if (methodAnnotation != null) {
            return true;
        }
        // Buscar en clase
        Guest classAnnotation = handlerMethod.getBeanType().getAnnotation(Guest.class);
        return classAnnotation != null;
    }
    
    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && 
               auth.isAuthenticated() && 
               !"anonymousUser".equals(auth.getName());
    }
}