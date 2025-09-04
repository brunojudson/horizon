package com.suaempresa.processos.security;

import com.suaempresa.processos.dto.UsuarioSessaoDTO;
import com.suaempresa.processos.enums.PerfilUsuario;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"*.xhtml"})
public class AuthorizationFilter implements Filter {

    private static final Map<String, PerfilUsuario> pageRoles = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Definir quais perfis podem acessar quais páginas
        pageRoles.put("/usuarios/lista.xhtml", PerfilUsuario.ADMINISTRADOR);
        pageRoles.put("/relatorios/lista.xhtml", PerfilUsuario.ADMINISTRADOR);
        // Adicione outras páginas e seus perfis necessários aqui
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        String requestURI = req.getRequestURI();

        // Páginas que não exigem autenticação (já tratadas pelo LoginFilter)
        if (requestURI.contains("/login.xhtml") || requestURI.contains("/javax.faces.resource/")) {
            chain.doFilter(request, response);
            return;
        }

    UsuarioSessaoDTO currentUser = (session != null) ? (UsuarioSessaoDTO) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            // Se não estiver logado, redireciona para o login (redundante com LoginFilter, mas garante)
            res.sendRedirect(req.getContextPath() + "/login.xhtml");
            return;
        }

        // Verificar autorização baseada no perfil
        PerfilUsuario requiredRole = null;
        for (Map.Entry<String, PerfilUsuario> entry : pageRoles.entrySet()) {
            if (requestURI.contains(entry.getKey())) {
                requiredRole = entry.getValue();
                break;
            }
        }

    if (requiredRole != null && currentUser.getPerfil() != requiredRole) {
            // Usuário não tem o perfil necessário, redirecionar para uma página de acesso negado ou dashboard
            res.sendRedirect(req.getContextPath() + "/dashboard.xhtml"); // Redireciona para o dashboard
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpeza do filtro, se necessário
    }
}


