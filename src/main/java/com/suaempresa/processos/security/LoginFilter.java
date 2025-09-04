package com.suaempresa.processos.security;

import com.suaempresa.processos.dto.UsuarioSessaoDTO;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "LoginFilter", urlPatterns = {"*.xhtml"})
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro, se necessário
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String requestURI = req.getRequestURI();

        // Permitir acesso à página de login e recursos públicos
        if (requestURI.contains("/login.xhtml") || requestURI.contains("/javax.faces.resource/")) {
            chain.doFilter(request, response);
        } else {
            // Verificar se o usuário está logado
            UsuarioSessaoDTO currentUser = (session != null) ? (UsuarioSessaoDTO) session.getAttribute("currentUser") : null;

            if (currentUser != null) {
                // Usuário logado, continuar com a requisição
                chain.doFilter(request, response);
            } else {
                // Usuário não logado, redirecionar para a página de login
                res.sendRedirect(req.getContextPath() + "/login.xhtml");
            }
        }
    }

    @Override
    public void destroy() {
        // Limpeza do filtro, se necessário
    }
}


